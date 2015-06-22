package com.geodoer.bluetoothcontroler.controller;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.geodoer.bluetoothcontroler.service.GeoBleService;
import com.geodoer.bluetoothcontroler.BcUtils;

/**
 * Created by iamcx_000 on 2015/6/22.g
 */
public class BleController {

    public static final int BLE_STATE_OK = 0;
    public static final int BLE_STATE_OFF = 1;
    public static final int BLE_STATE_UNSUPPORTED = 9;

    private int bleScanInterval;

    private String logTag;

    private boolean mServiceExisting = false;

    private boolean mScanning;

    private Context context;

    private Handler handler;

    private BluetoothAdapter mBluetoothAdapter;

    private whenRunningBleService mWhenRunningBleService;

    public BleController(Context context) {
        this.context = context;
        this.logTag = BcUtils.logTag;
        this.bleScanInterval = 5000;
    }

    public void setBleScanInterval(int scanInterval){
        this.bleScanInterval = scanInterval;
    }

    public void setBleServiceRunningTarget(whenRunningBleService mWhenRunningBleService){
        this.mWhenRunningBleService=mWhenRunningBleService;
    }

    // 1
    public int checkBleState() {
        // 取得系統藍牙服務
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 檢查藍牙支援
        if (mBluetoothAdapter == null) {
            // 沒有藍芽
            Log.wtf(logTag, BcUtils.BLUETOOTH_NOT_SUPPORTED);
            return BLE_STATE_UNSUPPORTED;
        } else {
            // 檢查BLE支援
            Log.wtf(logTag, BcUtils.BLUETOOTH_SUPPORTED);
            if (!context.getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                // 沒有BLE
                Log.wtf(logTag, BcUtils.BLUETOOTH_LE_NOT_SUPPORTED);
                return BLE_STATE_UNSUPPORTED;
            } else {
                // 有
                Log.wtf(logTag, BcUtils.BLUETOOTH_LE_SUPPORTED);
                if(mBluetoothAdapter.isEnabled()) {
                    Log.wtf(logTag, BcUtils.BLUETOOTH_ENABLED);
                    check_service();
                    return BLE_STATE_OK;
                }else{
                    Log.wtf(logTag, BcUtils.BLUETOOTH_NOT_ENABLED);
                    return BLE_STATE_OFF;
                }
            }
        }
    }

    //
    public Boolean isServiceRunning(String serviceName)
    {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo
                runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // if 1, then this
    private void check_service()
    {
        if(isServiceRunning(BcUtils.SERVICE_NAME))
        {
            Log.wtf(logTag," Ble Service Checked. Its on. ");

            mWhenRunningBleService.onServiceIsRunningNow();

            final Intent intent = new Intent(GeoBleService.mAction_servicestate);
            context.sendBroadcast(intent);
        }
        else
        {
            Log.wtf(logTag," Ble Service Checked. Its off. ");

            mWhenRunningBleService.onServiceIsStopped();

            mScanning = false;
        }
    }

    // private call
    public void triggerScan() {
        Log.wtf(logTag, " Triggered Ble Scan start ");
        if (checkBleState()==BLE_STATE_OK) {
            if (!mScanning)
                start_scanning();
            else
                stop_scanning();
        }
    }

    //
    public void triggerStop()
    {
        Log.wtf(logTag," Triggered Ble Scan stop ");

        if(!mServiceExisting) return;

        mServiceExisting = false;
        final Intent serviceintent = new Intent(context,GeoBleService.class);
        context.stopService(serviceintent);
    }

    //
    private void start_scanning()
    {
        mScanning = true;

        mWhenRunningBleService.startScanning();

        mBluetoothAdapter.startLeScan(mLeScanCallback);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    stop_scanning();
                }
            }
        }, bleScanInterval);
    }

    //
    private void stop_scanning()
    {
        mScanning = false;

        mWhenRunningBleService.stopScanning();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(
                        final BluetoothDevice thisDevice,
                        int rssi,
                        byte[] scanRecord) {

                    Log.wtf(logTag,
                            " Ble Devices found: "+
                                    thisDevice.getAddress()+
                                    " ");

                    mWhenRunningBleService.onGotBleDevices(
                            thisDevice,
                            rssi,
                            scanRecord
                    );
                }
            };

    //
    public interface whenRunningBleService{

        void onServiceIsRunningNow();

        void onServiceIsStopped();

        void startScanning();

        void stopScanning();

        void onGotBleDevices(
                final BluetoothDevice thisDevice,
                int rssi,
                byte[] scanRecord
        );
    }
}
