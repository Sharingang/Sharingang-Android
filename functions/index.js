const functions = require("firebase-functions");

const secret_key = functions.config().stripe.secret_key;
const publishable_key = functions.config().stripe.publishable_key;
const stripe = require("stripe")(secret_key);

const admin = require('firebase-admin');
admin.initializeApp();

exports.checkout = functions.https.onCall(async (data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called while authenticated.');
    }

    const itemId = data.itemId;
    console.log(itemId);
    console.log(context.auth.uid);

    // Create or retrieve the Stripe Customer object associated with your user.
    let customer = await stripe.customers.create(); // This example just creates a new Customer every time

    // Create an ephemeral key for the Customer; this allows the app to display saved payment methods and save new ones
    const ephemeralKey = await stripe.ephemeralKeys.create(
        { customer: customer.id },
        { apiVersion: '2020-08-27' }
    );

    // Create a PaymentIntent with the payment amount, currency, and customer
    const paymentIntent = await stripe.paymentIntents.create({
        amount: 973,
        currency: "chf",
        customer: customer.id
    });

    // Send the object keys to the client
    return {
        publishableKey: publishable_key,
        paymentIntent: paymentIntent.client_secret,
        customer: customer.id,
        ephemeralKey: ephemeralKey.secret
    }

    // const writeResult = await admin.firestore().collection('messages').add({ original: original });
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
