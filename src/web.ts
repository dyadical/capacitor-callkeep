import { WebPlugin } from '@capacitor/core';

import type { CapCallKeepPlugin } from './definitions';

export class CapCallKeepWeb extends WebPlugin implements CapCallKeepPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
