import { WebPlugin } from '@capacitor/core';
import {  C4GunApiCapacitorPluginPlugin } from './definitions';

export class  C4GunApiCapacitorPluginWeb extends WebPlugin implements  C4GunApiCapacitorPluginPlugin {
  constructor() {
    super({
      name: ' C4GunApiCapacitorPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const  C4GunApiCapacitorPlugin = new  C4GunApiCapacitorPluginWeb();

export {  C4GunApiCapacitorPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin( C4GunApiCapacitorPlugin);
