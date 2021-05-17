import { registerPlugin } from '@capacitor/core';
const C4GunApiCapacitor = registerPlugin('C4GunApiCapacitorPlugin', {
    web: () => import('./web').then(m => new m.C4GunApiCapacitorPluginWeb()),
});
export { C4GunApiCapacitor };
//# sourceMappingURL=index.js.map