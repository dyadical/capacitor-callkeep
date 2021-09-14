import { registerPlugin } from '@capacitor/core';

import type { CapCallKeepPlugin } from './definitions';

const CapCallKeep = registerPlugin<CapCallKeepPlugin>('CapCallKeep', {
  // web: () => import('./web').then(m => new m.CapCallKeepWeb()),
});

export * from './definitions';
export { CapCallKeep };
