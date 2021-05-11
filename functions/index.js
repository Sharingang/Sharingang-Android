const functions = require("firebase-functions");

const secret_key = functions.config().stripe.secret_key;
const publishable_key = functions.config().stripe.publishable_key;
const stripe = require("stripe")(secret_key);

const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();
const auth = admin.auth();

exports.checkout = functions.https.onCall(async (data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called while authenticated.');
    }

    const itemPromise = db.collection('items').doc(data.itemId).get();

    const user = await auth.getUser(context.auth.uid)

    const customers = (await stripe.customers.list({ email: user.email })).data;
    var customer;
    if (customers.length > 0) {
        customer = customers[0];
    } else {
        customer = await stripe.customers.create({
            name: user.displayName,
            email: user.email,
            metadata: {
                userId: user.uid
            }
        });
    }

    // Create an ephemeral key for the Customer; this allows the app to display saved payment methods and save new ones
    const ephemeralKeyPromise = stripe.ephemeralKeys.create(
        { customer: customer.id },
        { apiVersion: '2020-08-27' }
    );

    const item = (await itemPromise).data();
    // Stripe expects price in cents
    const price = Math.round(item.price * 100);

    const paymentIntent = await stripe.paymentIntents.create({
        amount: price,
        currency: "chf",
        customer: customer.id,
        description: item.title,
        metadata: {
            itemId: data.itemId,
            buyerUserId: user.uid,
            sellerUserId: item.userId
        }
    });

    console.log("Created payment intent", { email: user.email, customerId: customer.id, itemId: data.itemId, price: price });

    // Send the object keys to the client
    return {
        publishableKey: publishable_key,
        paymentIntent: paymentIntent.client_secret,
        customer: customer.id,
        ephemeralKey: (await ephemeralKeyPromise).secret
    }
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
