const admin = require('firebase-admin');
const registrationToken = 'TODO-apn-token-from-device';
const firebaseConfig = {
  credential: admin.credential.applicationDefault(),
  authDomain: 'TODO',
  databaseURL: 'TODO',
  projectId: 'TODO',
  messagingSenderId: 'TODO',
  appId: 'TODO',
  // measurementId: "G-MEASUREMENT_ID",
};

exports.registrationToken = registrationToken;
exports.firebaseConfig = firebaseConfig;
