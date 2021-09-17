import { PushNotifications } from '@capacitor/push-notifications';
import { log, error } from './util.js';

export function addPushListeners() {
  console.log('starting push listeners');
  PushNotifications.requestPermissions().then(result => {
    if (result.receive === 'granted') {
      PushNotifications.register();
    } else {
      error('push notification permission not granted');
    }
  });
  PushNotifications.addListener('registration', token => {
    log('Push registration success, token: ' + token.value);
  });
  PushNotifications.addListener('registrationError', error => {
    error('Error on registration: ' + JSON.stringify(error));
  });
  PushNotifications.addListener('pushNotificationReceived', notification => {
    log('Push received: ' + JSON.stringify(notification));
  });
  PushNotifications.addListener(
    'pushNotificationActionPerformed',
    notification => {
      log('Push action performed: ' + JSON.stringify(notification));
    },
  );
}
