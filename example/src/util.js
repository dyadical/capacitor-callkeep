import { Notyf } from 'notyf';

export const toast = new Notyf();
toast.success('toasting library works?');
console.log('GVQE1 test log');

export function log(x, prefix = '') {
  const s = JSON.stringify(x);
  toast.success(prefix + s);
  console.log(prefix + s);
}

export function error(x) {
  const s = errToString(x);
  toast.error(s);
  console.error(s);
}

function errToString(err) {
  return JSON.stringify(err, Object.getOwnPropertyNames(err));
}

export async function attempt(func, name = null) {
  if (name == null) {
    name = func.name;
  }
  try {
    const res = await func();
    const message = `${name}: ${JSON.stringify(res)}`;
    console.log(message);
    toast.success(message);
    return res;
  } catch (e) {
    const message = `${name} ERROR: ${errToString(e)}`;
    console.error(message);
    toast.error(message);
    return e;
  }
}
