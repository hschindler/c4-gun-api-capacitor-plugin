import { WebPlugin } from '@capacitor/core';
import { C4GunApiCapacitorPluginPlugin } from './definitions';
export declare class C4GunApiCapacitorPluginWeb extends WebPlugin implements C4GunApiCapacitorPluginPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
declare const C4GunApiCapacitorPlugin: C4GunApiCapacitorPluginWeb;
export { C4GunApiCapacitorPlugin };
