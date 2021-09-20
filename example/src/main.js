import { CapCallKeep, CapCallKeep as cck, CONSTANTS } from 'capacitor-callkeep';
import { addPushListeners } from './push.js';
import { attempt, log } from './util.js';

addPushListeners();

window.attempt = attempt;
window.CCK = cck;

const uuids = [];

function updateIdsDiv() {
  document.getElementById('uuids').innerText = JSON.stringify(uuids);
}
updateIdsDiv();

async function setup() {
  await attempt(
    () =>
      cck.setupAndroid({
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
  await attempt(() => cck.requestPermissions(), 'requestPermissions');
}
setup();

function addCapCallKeepListeners() {
  const events = [
    'endCall', // two listeners
    'answerCall', // two listeners
    'toggleHold',
    'setMutedCall',
    'DTMFAction',
    'startCall', // two listeners
    'activateAudioSession',
    'checkReachability',
    'showIncomingCallUi', // two listeners
    'silenceIncomingCall',
  ];
  for (const event of events) {
    cck.addListener(event, data => log(data, `Event ${event} received `));
  }
  cck.addListener('endCall', ({ callUUID }) => {
    console.log('updating uuids because endCall');
    const i = uuids.indexOf(callUUID);
    if (i >= 0) {
      uuids.splice(i, 1);
    }
    updateIdsDiv();
  });
  cck.addListener('answerCall', ({ callUUID }) => {
    console.log('updating uuids because answerCall');
    uuids.push(callUUID);
    updateIdsDiv();
  });
  cck.addListener('startCall', ({ callUUID }) => {
    console.log('updating uuids because startCall');
    uuids.push(callUUID);
    updateIdsDiv();
  });
  cck.addListener('showIncomingCallUi', ({ callUUID, handle, name }) => {
    console.log('updating uuids because showIncomingCallUi');
    uuids.push(callUUID);
    updateIdsDiv();
  });
}
addCapCallKeepListeners();

const buttonFunctions = [
  function checkPermissions() {
    attempt(() => cck.checkPermissions(), 'checkPermissions');
  },

  function supportConnectionService() {
    attempt(() => cck.supportConnectionService(), 'supportConnectionService');
  },
  function registerPhoneAccount() {
    attempt(() => cck.registerPhoneAccount(), 'registerPhoneAccount');
  },
  function hasPhoneAccount() {
    attempt(() => cck.hasPhoneAccount(), 'hasPhoneAccount');
  },
  function hasDefaultPhoneAccount() {
    attempt(() => cck.hasDefaultPhoneAccount(), 'hasDefaultPhoneAccount');
  },
  function checkPhoneAccountEnabled() {
    attempt(() => cck.checkPhoneAccountEnabled(), 'checkPhoneAccountEnabled');
  },
  function toggleAudioRouteSpeaker_random() {
    attempt(
      () =>
        cck.toggleAudioRouteSpeaker({
          uuid: uuids[0],
          routeSpeaker: Math.random() > 0.5,
        }),
      'toggleAudioRouteSpeaker',
    );
  },
  function setAvailable_random() {
    attempt(
      () => cck.setAvailable({ active: Math.random() > 0.5 }),
      'setAvailable',
    );
  },
  function registerAndroidEvents() {
    attempt(() => cck.registerAndroidEvents(), 'registerAndroidEvents');
  },
  function isConnectionServiceAvailable() {
    attempt(
      () => cck.isConnectionServiceAvailable(),
      'isConnectionServiceAvailable',
    );
  },
  function rejectCall() {
    attempt(() => cck.rejectCall({ uuid: uuids[0] }), 'rejectCall');
  },
  function hasOutgoingCall() {
    attempt(() => cck.hasOutgoingCall(), 'hasOutgoingCall');
  },
  function setForegroundServiceSettings() {
    attempt(
      () =>
        cck.setForegroundServiceSettings({
          channelId: 'hmm',
          channelName: 'whatisthis',
          notificationTitle: 'titlehereidk',
          // notificationIcon: "doesnotexist.png",
        }),
      'setForegroundServiceSettings',
    );
  },
  function canMakeMultipleCalls_random() {
    attempt(
      () => cck.canMakeMultipleCalls({ allow: Math.random() > 0.5 }),
      'canMakeMultipleCalls',
    );
  },
  function setCurrentCallActive() {
    attempt(
      () => cck.setCurrentCallActive({ callUUID: uuids[0] }),
      'setCurrentCallActive',
    );
  },
  function backToForeground() {
    attempt(() => cck.backToForeground(), 'backToForeground');
  },
  function answerIncomingCall() {
    attempt(
      () => cck.answerIncomingCall({ uuid: uuids[uuids.length - 1] }),
      'answerIncomingCall',
    );
  },
  // TODO: different args for below three functions between ios and android
  function displayIncomingCall() {
    attempt(
      () =>
        cck.displayIncomingCall({
          uuid: uuid(),
          number: '5557771234',
          callerName: 'tom johnson',
          // handleType: HandleType,
          // hasVideo: boolean,
          // options: IncomingCallOptions,
        }),
      'displayIncomingCall',
    );
  },
  function startCall() {
    attempt(
      () =>
        cck.startCall({
          uuid: uuid(),
          handle: 'Alice Smith',
          // contactIdentifier: string,
          // handleType: HandleType,
          // hasVideo: boolean,
        }),
      'startCall',
    );
  },
  function updateDisplay() {
    attempt(
      () =>
        updateDisplay({
          uuid: uuids[0],
          displayName: 'Updated Nameson',
          handle: 'upname',
          // options: IncomingCallOptions,
        }),
      'updateDisplay',
    );
  },
  function reportEndCallWithUUID() {
    attempt(
      () =>
        cck.reportEndCallWithUUID({
          uuid: uuids[0],
          reason: CONSTANTS.END_CALL_REASONS.FAILED,
        }),
      'reportEndCallWithUUID',
    );
  },
  function endCall() {
    attempt(() => cck.endCall({ uuid: uuids[0] }), 'endCall');
  },
  function endAllCalls() {
    attempt(() => cck.endAllCalls(), 'endAllCalls');
  },
  function setReachable() {
    attempt(() => cck.setReachable(), 'setReachable');
  },
  function getAudioRoutes() {
    attempt(() => cck.getAudioRoutes(), 'getAudioRoutes');
  },
  function setAudioRoute() {
    attempt(
      () => cck.setAudioRoute({ uuid: uuids[0], inputName: 'TODO' }),
      'setAudioRoute',
    );
  },
  function setOnHold_random() {
    attempt(
      () => cck.setOnHold({ uuid: uuids[0], held: Math.random() > 0.5 }),
      'setOnHold',
    );
  },
  // /** sendDTMF is used to send DTMF tones to the PBX. */
  function sendDTMF() {
    attempt(() => cck.sendDTMF({ uuid: uuids[0], key: '7' }), 'sendDTMF');
  },
];

const buttonsDiv = document.getElementById('buttons');
function makeButtons() {
  for (const f of buttonFunctions) {
    const button = document.createElement('button');
    button.innerText = f.name;
    button.onclick = f;
    buttonsDiv.appendChild(button);
  }
}
makeButtons();

function uuid() {
  return Math.random().toString().slice(2);
}
