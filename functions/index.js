const functions = require("firebase-functions");

const secret_key = functions.config().stripe.secret_key;
const publishable_key = functions.config().stripe.publishable_key;
const stripe = require("stripe")(secret_key);

const admin = require('firebase-admin');
if (process.env.FUNCTIONS_EMULATOR === 'true') {
    const serviceAccount = require("./serviceAccountKey.json");
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
    });
} else {
    admin.initializeApp();
}
const db = admin.firestore();
const auth = admin.auth();

const region = 'europe-west6'; // Zurich
const deeplink = "https://sharingang.page.link/item?id="

exports.checkout = functions.region(region).https.onCall(async (data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called while authenticated.');
    }

    const itemPromise = db.collection('items').doc(data.itemId).get()
    const quantity = data.quantity
    const user = await auth.getUser(context.auth.uid)
    const customer = await getOrCreateCustomer(user)

    // Create an ephemeral key for the Customer; this allows the app to display saved payment methods and save new ones
    const ephemeralKeyPromise = stripe.ephemeralKeys.create(
        { customer: customer.id },
        { apiVersion: '2020-08-27' }
    );

    const item = (await itemPromise).data();
    const paymentIntent = await createPaymentIntent(customer, user, item, quantity);

    return {
        publishableKey: publishable_key,
        paymentIntent: paymentIntent.client_secret,
        customer: customer.id,
        ephemeralKey: (await ephemeralKeyPromise).secret
    };
});

exports.newItemNotificationCreate = functions.region(region).firestore.document('items/{itemId}').onCreate((change, context) => onNewItem(change, context));
exports.newItemNotificationUpdate = functions.region(region).firestore.document('items/{itemId}').onUpdate((change, context) => onNewItem(change.after, context));

function onNewItem(change, context) {
    const newItem = change.data();

    var message = {
        data: {
            userId: newItem.userId,
            deeplink: deeplink + context.params.itemId,
            notificationType: "new_item"
        },
        notification: {
            body: newItem.title
        }
    };

    pushMessage(message, newItem.categoryString);
    return true;
}
exports.chatNotificationCreate = functions.region(region).firestore.document('users/{userId}/chats/{chatId}/messages/{message}').onCreate((change, context) => onNewChat(change, context));
//exports.chatNotificationUpdate = functions.region(region).firestore.document('users/{userId}/chats/{chatId}/messages/{message}').onUpdate((change, context) => onNewChat(change.after, context));

function onNewChat(change, context) {
    const newChat = change.data();
    db.collection("users").doc(newChat.from).get().then((user) => {
        var message = {
            data: {
                deeplink: deeplink + context.params.chatId,
                notificationType: "chat",
                toId: newChat.to
            },
            notification: {
                body: user.data().name + ": " + newChat.message
            }
        };
        pushMessage(message, "chat");
    });
    return true;
}
/**
 * Send a notification to users subscribed to the topic
 *
 * @param payload the content of the notification
 * @param topic the topic to send the notification to
 */
function pushMessage(payload, topic) {
    admin.messaging().sendToTopic(topic, payload)
        .then(function (response) {
            console.log("Successfully sent notification!");
        })
        .catch(function (error) {
            console.log("Error sending notification:", error);
        });
}

/**
 * Search for an existing Stripe customer, if it doesn't exist, create a new one
 * 
 * @param {admin.auth.UserRecord} user - FirebaseAuth user
 * @returns {object} Stripe customer
 */
async function getOrCreateCustomer(user) {
    const customers = (await stripe.customers.list({ email: user.email })).data;
    if (customers.length > 0) {
        return customers[0];
    } else {
        return await stripe.customers.create({
            name: user.displayName,
            email: user.email,
            metadata: {
                userId: user.uid
            }
        });
    }
}

/**
 * Create a Stripe payment intent for the given item and customer
 * 
 * @param {object} customer - Stripe customer
 * @param {admin.auth.UserRecord} user - FirebaseAuth user
 * @param {FirebaseFirestore.DocumentData} item - item being sold
 * @param {int} quantity - Number of items
 * @returns {object} Stripe payment intent
 */
async function createPaymentIntent(customer, user, item, quantity) {
    // Stripe expects price in cents
    var price = 0;
    if (item.discount) {
        price = Math.round(item.discountPrice * 100) * quantity;
    } else {
        price = Math.round(item.price * 100) * quantity;
    }

    const paymentIntent = await stripe.paymentIntents.create({
        amount: price,
        currency: "chf",
        customer: customer.id,
        description: item.title,
        metadata: {
            itemId: item.id,
            buyerUserId: user.uid,
            sellerUserId: item.userId
        }
    });

    console.log("Created payment intent", { email: user.email, customerId: customer.id, itemId: item.id, price: price });

    return paymentIntent;
}

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
