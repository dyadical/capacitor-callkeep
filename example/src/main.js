import { CapCallKeep } from 'capacitor-callkeep';
import { addPushListeners } from './push.js';
import { attempt } from './util.js';

addPushListeners();

window.callStuff = async function callStuff() {
  await attempt(
    CapCallKeep.setupAndroid({
      selfManaged: false,
      imageName: 'imageNameIdk',
    }),
    'setupAndroid',
  );
  await attempt(CapCallKeep.checkPermissions(), 'checkPermissions');
  await attempt(CapCallKeep.echo({ value: 'bob' }), 'echo');
  await attempt(
    CapCallKeep.displayIncomingCall({
      uuid: '1234',
      number: '5678',
      callerName: 'tom johnson',
    }),
    'displayIncomingCall',
  );
};
