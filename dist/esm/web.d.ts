import { WebPlugin } from '@capacitor/core';
import { C4GunApiCapacitorPlugin } from './definitions';
export declare class C4GunApiCapacitorPluginWeb extends WebPlugin implements C4GunApiCapacitorPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    getFirmware(): Promise<{
        firmware: string;
    }>;
    startInventory(): Promise<{
        uhfData: string[];
    }>;
    stopInventory(): Promise<boolean>;
    setOutputPower(): Promise<number>;
    getOutputPower(): Promise<number>;
    private throwUnimplementedError;
}
declare const C4GunApiCapacitorPlugin: C4GunApiCapacitorPluginWeb;
export { C4GunApiCapacitorPlugin };
