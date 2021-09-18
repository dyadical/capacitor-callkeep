const admin = require('firebase-admin');
const { registrationToken, firebaseConfig } = require('./firebase-config.js');

console.log({ firebaseConfig });
admin.initializeApp(firebaseConfig);

// This registration token comes from the client FCM SDKs.

const message = {
  // NOTE: 'data' goes through to app but 'notification' does not
  // See https://stackoverflow.com/a/38795553/4941530
  data: {
    title: 'hello world',
    body: 'have a good day',
    type: 'ring', // our app will check this value
  },
  token: registrationToken,
};

// Send a message to the device corresponding to the provided
// registration token.
admin
  .messaging()
  .send(message)
  .then(response => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch(error => {
    console.log('Error sending message:', error);
  });
