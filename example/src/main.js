import { CapCallKeep } from 'capacitor-callkeep';
import { addPushListeners } from './push.js';
import { attempt, log } from './util.js';

addPushListeners();

window.attempt = attempt;
window.CCK = CapCallKeep;

async function setup() {
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
setup();

function addCapCallKeepListeners() {
  const events = [
    'endCall',
    'answerCall',
    'toggleHold',
    'setMutedCall',
    'DTMFAction',
    'startCall',
    'activateAudioSession',
    'checkReachability',
    'showIncomingCallUi',
    'silenceIncomingCall',
  ];
  for (const event of events) {
    CapCallKeep.addListener(event, data =>
      log(data, `Event ${event} received `),
    );
  }
}
addCapCallKeepListeners();
