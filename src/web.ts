import { WebPlugin } from '@capacitor/core';
import { C4GunApiCapacitorPlugin } from './definitions';

export class C4GunApiCapacitorPluginWeb extends WebPlugin implements C4GunApiCapacitorPlugin {
  // constructor() {
  //   super({
  //     name: ' C4GunApiCapacitorPlugin',
  //     platforms: ['web'],
  //   });
  // }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async getFirmware(): Promise<{ firmware: number[] }> {
    // logic here
    this.throwUnimplementedError();
  }

  async startInventory(): Promise<{ uhfData: string[] }> {
    // logic here
    this.throwUnimplementedError();
  }

  async stopInventory(): Promise<boolean> {
    // logic here
    this.throwUnimplementedError();
  }

  async setOutputPower(): Promise<boolean> {
    // logic here
    this.throwUnimplementedError();
  }

  async scanBarcode(): Promise<{ barcodeData: string }> {
    // logic here
    this.throwUnimplementedError();
  }

  private throwUnimplementedError(): never {
    throw this.unimplemented('Not implemented on web.');
  }
}

const C4GunApiCapacitorPlugin = new C4GunApiCapacitorPluginWeb();

export { C4GunApiCapacitorPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(C4GunApiCapacitorPlugin);
