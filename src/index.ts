import { registerPlugin } from '@capacitor/core';

import type { CapCallKeepPlugin } from './definitions';

const CapCallKeep = registerPlugin<CapCallKeepPlugin>('CapCallKeep', {
  web: () => {
    throw Error('not implemented on web');
  },
});

export * from './definitions';
export { CapCallKeep };
