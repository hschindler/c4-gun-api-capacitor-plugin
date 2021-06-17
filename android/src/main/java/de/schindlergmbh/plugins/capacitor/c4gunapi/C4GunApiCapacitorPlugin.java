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
public class C4GunApiCapacitorPlugin extends com.getcapacitor.Plugin {

   
    private static final String TAG = C4GunApiCapacitorPlugin.class.getName();

    private UHFManager _uhfManager;
    private Boolean _readerInitialized;
    private String _errorLog;
    private boolean startFlag = false;
    

    private String _readMode = "tid"; // tid / epc

    private int _outputPower = 0;

    private Thread _scanThread;


    private KeyReceiver _keyReceiver;

    public void load() {

        Log.d(TAG, "C4GunApiCapacitorPlugin - load");

        this.initKeyReceiver();

    }

    @Override
    protected void handleOnDestroy() {
     this.dispose();   
    }

    @PluginMethod
    public void exitApp(PluginCall call) {
        
    }

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
        
        String value = call.getString("value", "tid");

        Log.d(TAG, "startInventory value=" + value);
        
        if (value.equals("tid") || value.equals("epc")) {
            this._readMode = value;
        }
        
        saveCall(call);

        this.StartInventoryThread();
        
    }

    @PluginMethod()
    public void stopInventory(PluginCall call) {
       
        Boolean result = true;

        this.StopInventoryThread();

        JSObject ret = new JSObject();
        ret.put("value", result);
        call.resolve(ret);
    }


    @PluginMethod()
    public void setOutputPower(PluginCall call) {
        // 0-30
        Integer value = call.getInt("value", 30);

        if (value != null) {
            Log.d(TAG, "Power value = " + value);
            this._outputPower = value;
            Log.d(TAG, "outputPower value = " + new Integer(this._outputPower).toString());
        } else {
            Log.d(TAG, "Power value = null");
        }
        
        JSObject ret = new JSObject();
        ret.put("value", this._outputPower);
        call.resolve(ret);
    }

    @PluginMethod()
    public void getOutputPower(PluginCall call) {   

        JSObject ret = new JSObject();
        ret.put("value", this._outputPower);
        call.resolve(ret);
    }


    private void dispose() {
        this.StopInventoryThread();
        this.disposeUHFManager();

        // if (getContext().)
        // getContext().unregisterReceiver(this._keyReceiver);

        this._keyReceiver = null;

    }
   


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

                    if (result == true) {
                        Log.d(TAG, "initializeUHFManager setReadPower success");
                    } else {
                        Log.d(TAG, "initializeUHFManager setReadPower failed");
                    }
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


    class CallBackImpl implements KeyReceiverCallback {          //class that implements the method to callback defined in the interface
        public void onReceiveCallback() {
            System.out.println("I've been called back");
            JSObject ret = new JSObject();
            // ret.put("value", "some value");
            notifyListeners("scanButtonPressed", ret);
        }
    }


    private void initKeyReceiver() {

        Log.d(TAG, "initKeyReceiver");

        if (this._keyReceiver == null) {
            Log.d(TAG, "createReceiver");    
            this._keyReceiver = new KeyReceiver(getContext(),  new CallBackImpl());
        } else {
            // Log.d(TAG, "search receiver");   
            // var existingReceiver= getContext().registerReceiver(null, filter);
            
            Log.d(TAG, "unregisterReceiver");   
            
            getContext().unregisterReceiver(this._keyReceiver);
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


    private void StartInventoryThread() {

        Log.d(TAG, "StartInventoryThread");

        // start inventory thread
        startFlag = true;

        if (this._scanThread == null || this._scanThread.getState() == Thread.State.TERMINATED) {
            Log.d(TAG, "StartInventoryThread - create new thread");
            this._scanThread = new InventoryThread();
        }

        Log.d(TAG, "StartInventoryThread - start thread");

        if (this._scanThread.getState() == Thread.State.NEW) {
            this._scanThread.start();
        }

    }

    private void StopInventoryThread() {
        // runFlag = false;
        startFlag = false;
    }

    private void PauseInventoryThread() {
        startFlag = false;
    }


    private JSONArray ConvertArrayList(ArrayList<String> list) {
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        for (String value : list) {
            jsonArray.put(value);
        }

        return jsonArray;
    }

    // add TIDs to view
    private void returnCurrentTIDs(final ArrayList<String> tidList, PluginCall call) {
        if (call != null) {
            if (tidList != null || tidList.isEmpty() == false) {
                JSObject ret = new JSObject();
                ret.put("uhfData", ConvertArrayList(tidList));
                call.resolve(ret);
            }

        }
    }


    /**
     * Inventory Thread
     */
    class InventoryThread extends Thread {
        
        // private List<byte[]> epcList;
        // private ArrayList<String> tidList;

        private ArrayList<String> dataList;

        @Override
        public void run() {
            super.run();

            Log.d(TAG, "InventoryThread starting...");

            PluginCall savedCall = getSavedCall();

            if (savedCall == null) {
                Log.d("Test", "No stored plugin call for startInventory request result");
                return;
            }

            initializeUHFManager();

            if (_uhfManager == null) {
                Log.d(TAG, "InventoryThread failed creating uhfManager");
                savedCall.reject("InventoryThread failed creating uhfManager");
                return;
            }

            Log.d(TAG, "InventoryThread startflag = " + String.valueOf(startFlag));

            while (startFlag) {

                Log.d(TAG, "Waiting for timeout..");

                if (_uhfManager != null) {

                    if ("tid".equals(_readMode)) {

                        Log.d(TAG, "ReadMode is TID...");

                        try {
                            dataList = new ArrayList<String>();

                            List<com.pda.uhfm.TagDataModel> tagDataModels = _uhfManager.ReadData(UHFManager.TID, 0, 6,
                                    new byte[4]);

                            if (tagDataModels != null) {

                                for (com.pda.uhfm.TagDataModel tagDataModel : tagDataModels) {
                                    // String tid = tagDataModel.DATA;
                                    String tid = tagDataModel.DATA;

                                    if (tid.length() == 0) {
                                        byte[] bEpc = Tools.HexString2Bytes(tagDataModel.Epc);

                                        byte[] bTid = _uhfManager.readDataByEPC(bEpc, new byte[4], 2, 0, 4);

                                        if (bTid != null) {
                                            tid = Tools.Bytes2HexString(bTid, bTid.length);
                                            if (tid.length() > 1) {
                                                Log.d(TAG, "TID found:" + tid);
                                                dataList.add(tid);
                                            } else {
                                                tid = "epc: " + tagDataModel.Epc;
                                                dataList.add(tid);
                                            }
                                        }
                                        // } else {
                                        // tid = "bTid = null - epc: " + tagDataModel.Epc + " bEpc ="
                                        // + Tools.Bytes2HexString(bEpc, bEpc.length);
                                        // dataList.add(tid);
                                        // }

                                    } else {
                                        Log.d(TAG, "TID found length > 0:" + tid);
                                        dataList.add(tid);
                                    }
                                }
                            } else {
                                Log.d(TAG, "tagDataModels = null");
                            }

                            if ((dataList != null) && (!dataList.isEmpty())) {
                                if (dataList.size() > 0) {
                                    returnCurrentTIDs(dataList, savedCall);
                                    startFlag = false;
                                }
                            }
                            
                        } catch (Exception ex) {
                            Log.e(TAG, "GetTID Exception: " + ex.getMessage());
                            savedCall.reject("Fehler-GetTID: " + ex.getMessage());
                        }

                    } else if ("epc".equals(_readMode)) {
                        try {

                            dataList = new ArrayList<String>();

                            _uhfManager.startInventory(false); // multiMode

                            while (startFlag) {

                                byte[] bytess = _uhfManager.getEPCByteBuff();
                                if (bytess != null) {
                                    com.pda.uhfm.EPCDataModel epcDataModel = _uhfManager.getEPC(bytess);
                                    if (epcDataModel != null) {
                                        byte[] epcdata = epcDataModel.EPC;
                                        int rssi = epcDataModel.RSSI;
                                        String epc = Tools.Bytes2HexString(epcdata, epcdata.length);
                                        // Log.e("inventoryTask", epc) ;
                                        if (dataList == null) {
                                            dataList = new ArrayList<String>();
                                        }
                                        dataList.add(epc);
                                    }
                                }

                                if ((dataList != null) && (!dataList.isEmpty())) {
                                    if (dataList.size() > 0) {
                                        returnCurrentTIDs(dataList, savedCall);
                                        startFlag = false;
                                    }
                                }

                            }

                            
                            _uhfManager.stopInventory();
                     
                        } catch (Exception ex) {
                            Log.e(TAG, "GetEPC Exception: " + ex.getMessage());
                            savedCall.reject("Fehler-GetEPC: " + ex.getMessage());
                        }
                        
                    }

                } else {
                    // returnCurrentTIDs(null);
                    savedCall.reject("UHFManager is not initialized!");
                }                

               

                // epcList = null;
                dataList = null;

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    // Thread.currentThread().interrupt();
                    e.printStackTrace();
                    // return;
                }

                // }
            } // while

            Log.d(TAG, "InventoryThread is closing...");

            disposeUHFManager();


        } // run

    } // end class InventoryThread
}
