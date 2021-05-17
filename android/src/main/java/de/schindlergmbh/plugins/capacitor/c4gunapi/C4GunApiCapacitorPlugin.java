package de.schindlergmbh.plugins.capacitor.c4gunapi;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.TimeZone;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings;

import android.nfc.Tag;
import android.util.Log;
import android.content.Intent;
import android.content.IntentFilter;


import com.pda.uhfm.UHFManager;
import com.pda.uhfm.VersionInfo;

import cn.pda.serialport.Tools;


@CapacitorPlugin(name = "C4GunApiCapacitorPlugin")
public class  C4GunApiCapacitorPlugin extends Plugin {
    
    private static final String TAG = C4GunApiCapacitorPlugin.class.getName();

    private UHFManager _uhfManager;
    private Boolean _readerInitialized;
    private String _errorLog;
    private boolean startFlag = false;
    

    private String readMode = "tid"; // tid / epc

    private int _outputPower = 0;

    private Thread _scanThread;


    private KeyReceiver _keyReceiver;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }


    @PluginMethod()
    public void getFirmware(PluginCall call) {
       
        Log.d(TAG, "getFirmware");

        this.initKeyReceiver();

        this.initializeUHFManager();

        if (_uhfManager == null) {
            call.reject("UHF API not installed");
            return;
        }

        com.pda.uhfm.VersionInfo firmwareVersion = this._uhfManager.getVersion();
        // final byte[] firmwareVersion = _uhfManager.getFirmware();
        String returnVersion = "";

        if (firmwareVersion == null) {
            returnVersion = "get version is null";
        } else {
            Log.d(TAG, "firmwareVersion");
            Log.d(TAG, firmwareVersion.SoftwareVersion);
            returnVersion = firmwareVersion.SoftwareVersion;
        }     
        
        this.disposeUHFManager();

        JSObject ret = new JSObject();
        ret.put("firmware", returnVersion);
        call.resolve(ret);
    }

    @PluginMethod()
    public void startInventory(PluginCall call) {
       
        Log.d(TAG, "startInventory");

        Boolean result = true;
        
        // _listEPCObject = new ArrayList<EPC>();

        saveCall(call);

        // this.StartInventoryThread();
        
    }

    @PluginMethod()
    public void stopInventory(PluginCall call) {
       
        Boolean result = true;

        // this.StopInventoryThread();

        JSObject ret = new JSObject();
        ret.put("value", result);
        call.resolve(ret);
    }


    @PluginMethod()
    public void setOutputPower(PluginCall call) {

        Boolean result = true;       

        JSObject ret = new JSObject();
        ret.put("value", result);
        call.resolve(ret);
    }


    // @PluginMethod()
    // public void scanBarcode(PluginCall call) {
       
    //     Boolean result = true;
    //     JSObject ret = new JSObject();

    //     try {
    //         // this._barcodeCallBackContext = callbackContext;

    //         Barcode1DManager.BaudRate = _barcodeBaudrate;
    //         Barcode1DManager.Port = _barcodePort;
    //         Barcode1DManager.Power = _barcodePower;
    
    //         try {
    //             if (_barcodeManager == null) {
    //                 _barcodeManager = new Barcode1DManager();
    //             }
    //         } catch (Exception e) {
    //             _errorLog = e.getMessage();
    //             e.printStackTrace();
    //         }

    //         BarcodeHandler barcodeHandler = new BarcodeHandler(call, new closeBarcodeCallback() {
    //             @Override
    //             public void closeBarcodeManager() {
    //                 if (_barcodeManager != null) {
    //                     Log.d(TAG, "closeBarcodeManager");

    //                     try {
    //                         _barcodeManager.Close();
    //                     } catch (Exception e) {
    //                         _errorLog = e.getMessage();
    //                     }

    //                 }
    //             }
    //         });

    //         _barcodeManager.Open(barcodeHandler);

    //         try {
    //             Thread.sleep(500);
    //         } catch (InterruptedException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }

    //         _barcodeManager.Scan();

    //     } catch (Exception e) {
    //         _errorLog = e.getMessage();

    //         // PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, _errorLog);
    //         // pluginResult.setKeepCallback(true);
    //         // _barcodeCallBackContext.sendPluginResult(pluginResult);

    //         call.reject(e.getMessage());
    //         return;
    //     }

    // }

    // public interface closeBarcodeCallback {
    //     void closeBarcodeManager();
    // }


    private void initializeUHFManager() {
        Log.d(TAG, "initializeUHFManager C4GunApiCordovaPlugin");
        if (this._uhfManager == null) {

            try {
                this._uhfManager = UHFManager.getInstance();
                this._readerInitialized = this._uhfManager.initRfid();

                this._uhfManager.setProtocol(_uhfManager.PROTOCOL_ISO_18000_6C);
                this._uhfManager.setFreBand(com.pda.uhfm.FreRegion.TMR_REGION_Europea_Union_3);

                if (this._outputPower > 0) {
                    boolean result = this._uhfManager.setReadPower(this._outputPower);
                } else {
                    this._uhfManager.setReadPower(27); // 0-30
                }

                this._uhfManager.setWritePower(27);
            } catch (Exception e) {
                _errorLog = e.getMessage();
                e.printStackTrace();
                // Log.d(TAG, "Error: " + e.getMessage());
            }

        }

    }

    private void disposeUHFManager() {

        if (this._uhfManager != null) {
            Log.d(TAG, "disposeUHFManager");

            try {
                this._uhfManager.close();
            } catch (Exception e) {
                _errorLog = e.getMessage();
            }

            this._uhfManager = null;
        }
    }

    private void initKeyReceiver() {
        if (this._keyReceiver == null) {
            this._keyReceiver = new KeyReceiver(getContext());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        getContext().registerReceiver(this._keyReceiver, filter);
        
    }

    private void unregisterReceiver() {
        try {
            getContext().unregisterReceiver(this._keyReceiver);
        } catch (Exception e) {

        }

    }
}
