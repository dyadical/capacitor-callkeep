import { CapCallKeep } from 'capacitor-callkeep';

function log(x) {
  console.log(JSON.stringify(x));
}

window.foo = async function foo() {
  const setupRes = await CapCallKeep.setupAndroid({
    selfManaged: false,
    imageName: 'imageNameIdk',
  });
  log({ setupRes });
  const perms = await CapCallKeep.checkPermissions();
  log({ perms });
  const echoResult = await CapCallKeep.echo({ value: 'bob' });
  log({ echoResult });
  const callResult = CapCallKeep.displayIncomingCall({
    uuid: '1234',
    number: '5678',
    callerName: 'tom johnson',
  });
  log({ callResult });
};
