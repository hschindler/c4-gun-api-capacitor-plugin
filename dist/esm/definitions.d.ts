export interface C4GunApiCapacitorPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    /**
    * Gets RFID UHF reader firmware.
    *
    * @since 1.0.0
    */
    getFirmware(): Promise<{
        firmware: number[];
    }>;
    /**
    * Starts RFID UHF inventory.
    *
    * @since 1.0.0
    */
    startInventory(): Promise<{
        uhfData: string[];
    }>;
    /**
    * Stops RFID UHF inventory.
    *
    * @since 1.0.0
    */
    stopInventory(): Promise<boolean>;
    /**
    * Sets RFID UHF output power.
    *
    * @since 1.0.0
    */
    setOutputPower(): Promise<boolean>;
}
