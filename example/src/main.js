import { CapCallKeep } from 'capacitor-callkeep';
import { addPushListeners } from './push.js';
import { attempt } from './util.js';

addPushListeners();

async function start() {
  await attempt(
    () =>
      CapCallKeep.setupAndroid({
        selfManaged: false,
        imageName: 'imageNameIdk',
        foregroundService: {
          channelId: 'hmm',
          channelName: 'whatisthis',
          notificationTitle: 'titlehereidk',
          // notificationIcon: "doesnotexist.png",
        },
      }),
    'setupAndroid',
  );
  await attempt(() => CapCallKeep.requestPermissions(), 'requestPermissions');
}
start();

window.callStuff = async function callStuff() {
  await attempt(() => CapCallKeep.checkPermissions(), 'checkPermissions');
  await attempt(
    () =>
      CapCallKeep.displayIncomingCall({
        uuid: '1234',
        number: '5678',
        callerName: 'tom johnson',
      }),
    'displayIncomingCall',
  );
};

// setInterval(() => { console.log('logging alive as of ' + new Date().toLocaleTimeString()); }, 2000);
