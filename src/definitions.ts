declare module '@capacitor/core' {
  interface PluginRegistry {
     C4GunApiCapacitorPlugin:  C4GunApiCapacitorPluginPlugin;
  }
}

export interface  C4GunApiCapacitorPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
