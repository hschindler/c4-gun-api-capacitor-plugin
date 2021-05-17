import { registerPlugin } from '@capacitor/core';

import type { C4GunApiCapacitorPlugin } from './definitions';

const C4GunApiCapacitor = registerPlugin<C4GunApiCapacitorPlugin>('C4GunApiCapacitorPlugin', {
    web: () => import('./web').then(m => new m.C4GunApiCapacitorPluginWeb()),
});

export * from './definitions';
export { C4GunApiCapacitor };