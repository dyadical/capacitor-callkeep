const admin = require('firebase-admin');

// TODO: pull out into .firebaserc
var firebaseConfig = {
  credential: admin.credential.applicationDefault(),
  authDomain: 'TODO',
  databaseURL: 'TODO',
  projectId: 'TODO',
  messagingSenderId: 'TODO',
  appId: 'TODO',
  // measurementId: "TODO",
};

admin.initializeApp(firebaseConfig);

// This registration token comes from the client FCM SDKs.
const registrationToken = 'TODO';

const message = {
  notification: {
    title: 'hello world',
    body: new Date().toLocaleString(),
  },
  token: registrationToken,
  // topic: "callattempt.voip",
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
