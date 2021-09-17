import { CapCallKeep } from 'capacitor-callkeep';
import { addPushListeners } from './push.js';
addPushListeners();
function log(x) {
  console.log(JSON.stringify(x));
}
function error(x) {
  console.error(JSON.stringify(x));
}

window.foo = async function foo() {
  await tc(
    CapCallKeep.setupAndroid({
      selfManaged: false,
      imageName: 'imageNameIdk',
    }),
    'setupAndroid',
  );
  await tc(CapCallKeep.checkPermissions(), 'checkPermissions');
  await tc(CapCallKeep.echo({ value: 'bob' }), 'echo');
  await tc(
    CapCallKeep.displayIncomingCall({
      uuid: '1234',
      number: '5678',
      callerName: 'tom johnson',
    }),
    'displayIncomingCall',
  );
};

async function tc(func, name) {
  try {
    const res = await func();
    console.log(name, ':', JSON.stringify(res));
    return res;
  } catch (e) {
    console.error(name, ':', JSON.stringify(e));
    return e;
  }
}
