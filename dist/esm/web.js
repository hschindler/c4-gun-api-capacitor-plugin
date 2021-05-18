import { WebPlugin } from '@capacitor/core';
export class C4GunApiCapacitorPluginWeb extends WebPlugin {
    // constructor() {
    //   super({
    //     name: ' C4GunApiCapacitorPlugin',
    //     platforms: ['web'],
    //   });
    // }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
    async getFirmware() {
        // logic here
        this.throwUnimplementedError();
    }
    async startInventory() {
        // logic here
        this.throwUnimplementedError();
    }
    async stopInventory() {
        // logic here
        this.throwUnimplementedError();
    }
    async setOutputPower() {
        // logic here
        this.throwUnimplementedError();
    }
    async scanBarcode() {
        // logic here
        this.throwUnimplementedError();
    }
    throwUnimplementedError() {
        throw this.unimplemented('Not implemented on web.');
    }
}
const C4GunApiCapacitorPlugin = new C4GunApiCapacitorPluginWeb();
export { C4GunApiCapacitorPlugin };
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(C4GunApiCapacitorPlugin);
//# sourceMappingURL=web.js.map