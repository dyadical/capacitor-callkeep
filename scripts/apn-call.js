const admin = require('firebase-admin');
// const {v5: uuid} = require('uuid')
const { registrationToken, firebaseConfig } = require('./firebase-config.js');
admin.initializeApp(firebaseConfig);

const type = process.argv[2];
console.log({ type });
if (type !== 'r' && type !== 'n') {
  console.error(
    "must provide argument, either 'r' for ring or 'n' for notification",
  );
  process.exit(1);
}
// This registration token comes from the client FCM SDKs.

const message =
  type === 'r'
    ? {
        // NOTE: 'data' goes through to app but 'notification' does not
        // See https://stackoverflow.com/a/38795553/4941530
        data: {
          type: 'ring', // our app will check this value
          uuid: Math.random().toString().slice(2), //uuid(),
          number: '5558881234',
          callerName: 'Alice Smith',
        },
        token: registrationToken,
      }
    : {
        notification: {
          title: 'test notification steve',
          body: 'oy mate',
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
