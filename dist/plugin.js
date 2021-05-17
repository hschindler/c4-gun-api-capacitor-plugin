var capacitorC4GunApiCapacitorPlugin = (function (exports, core) {
    'use strict';

    const C4GunApiCapacitor = core.registerPlugin('C4GunApiCapacitorPlugin', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.C4GunApiCapacitorPluginWeb()),
    });

    class C4GunApiCapacitorPluginWeb extends core.WebPlugin {
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
    core.registerWebPlugin(C4GunApiCapacitorPlugin);

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        C4GunApiCapacitorPluginWeb: C4GunApiCapacitorPluginWeb,
        C4GunApiCapacitorPlugin: C4GunApiCapacitorPlugin
    });

    exports.C4GunApiCapacitor = C4GunApiCapacitor;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}, capacitorExports));
//# sourceMappingURL=plugin.js.map
