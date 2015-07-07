package com.example.blecontroller;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.example.blecontroller.service.TheControllerService;


/**
 * Created by iamcx_000 on 2015/6/22.g
 */
public class BleController {

    private String
            logTag;

    private boolean
            mScanning,
            mServiceExisting = false;

    private int
            bleScanInterval,
            bleKeepingAliveInterval;

    private Context
            context;

    private Handler
            handler;

    private final BluetoothManager
            bluetoothManager;

    private BluetoothAdapter
            mBluetoothAdapter;

    private whenRunningBleService
            mWhenRunningBleService;


    public BleController(Context context) {
        this.context = context;
        this.logTag = utils.STRING.TAG_PREFIX + "BleC";
        this.bleScanInterval = 5000;
        this.bleKeepingAliveInterval = 10000;
        this.handler = new Handler();
        this.bluetoothManager = (BluetoothManager) context.
                getSystemService(Context.BLUETOOTH_SERVICE);
    }

    // 設定介面目標
    public void setBleServiceRunningTarget(whenRunningBleService mWhenRunningBleService){
        this.mWhenRunningBleService=mWhenRunningBleService;
    }

    // 外部呼叫-檢查服務狀態/藍芽支援/是否開啟
    public int checkBleState() {
        // 取得系統藍牙服務
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 檢查藍牙支援
        if (mBluetoothAdapter == null) {
            // 沒有藍芽
            Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_NOT_SUPPORTED);
            return utils.INT.BLE_STATE_UNSUPPORTED;

        } else {
            // 檢查BLE支援
            Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_SUPPORTED);
            if (!context.getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                // 沒有BLE
                Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_LE_NOT_SUPPORTED);
                return utils.INT.BLE_STATE_UNSUPPORTED;

            } else {
                // 有
                Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_LE_SUPPORTED);
                if(mBluetoothAdapter.isEnabled()) {
                    // 藍芽有開
                    Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_ENABLED);
                    check_service();
                    return utils.INT.BLE_STATE_OK;

                }else{
                    // 沒開
                    Log.wtf(logTag, utils.STRING.DESC_BLUETOOTH_NOT_ENABLED);
                    return utils.INT.BLE_STATE_OFF;
                }
            }
        }
    }

    // 檢查服務狀態
    private void check_service()
    {
        if(isServiceRunning(utils.STRING.ACTION_SERVICE_NAME))
        {
            Log.wtf(logTag," GeoBle service state checked. Its on. ");

            mWhenRunningBleService.onBcServiceIsRunningNow();

            final Intent intent = new Intent(utils.STRING.ACTION_SERVICE_STATE);
            context.sendBroadcast(intent);
        }
        else
        {
            Log.wtf(logTag," GeoBle service state checked. Its off. ");

            mWhenRunningBleService.onBcServiceIsStopped();

            mScanning = false;
        }
    }

    // 檢查服務是否正在執行
    private Boolean isServiceRunning(String serviceName)
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


    // 外部呼叫啟動掃描
    public void triggerScan() {
        Log.wtf(logTag, " Triggered Ble Scan start ");
        if (checkBleState() == utils.INT.BLE_STATE_OK) {
            if (!mScanning)
                start_scanning();
            else
                stop_scanning();
        }
    }

    // 外部呼叫停止"服務"
    public void triggerStopService()
    {
        Log.wtf(logTag," Triggered Ble Scan stop ");

        if(!mServiceExisting) return;

        mServiceExisting = false;
        final Intent serviceIntent = new Intent(context,TheControllerService.class);
        context.stopService(serviceIntent);
    }

    // 連線檢查是否裝置斷線
    public int keepConnection(String address){
        if(!address.isEmpty())
            return bluetoothManager.getConnectionState(
                    mBluetoothAdapter.getRemoteDevice(address),
                    BluetoothProfile.GATT);
        else
            return -1;
    }

    // 內部用開始掃描
    private void start_scanning()
    {
        mScanning = true;

        mWhenRunningBleService.BcStartScanning();

        mBluetoothAdapter.startLeScan(mLeScanCallback);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    stop_scanning();
                }
            }
        }, bleScanInterval);
    }

    // 內部用停止掃描
    private void stop_scanning()
    {
        mScanning = false;

        mWhenRunningBleService.BcStopScanning();
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
                                    " ,rssi: "+
                                    rssi);

                    mWhenRunningBleService.onBcFoundBleDevices(
                            thisDevice,
                            rssi,
                            scanRecord
                    );
                }
            };

    // 設定掃描間隔
    public void setBleScanInterval(int scanInterval){
        this.bleScanInterval = scanInterval;
    }

    // 設定檢查連線狀態間隔
    public void setBleKeepingAliveInterval(int keepingAliveInterval){
        this.bleKeepingAliveInterval = keepingAliveInterval;
    }

    // interface
    public interface whenRunningBleService{

        void onBcServiceIsRunningNow();

        void onBcServiceIsStopped();

        void BcStartScanning();

        void BcStopScanning();

        void onBcFoundBleDevices(
                final BluetoothDevice thisDevice,
                int rssi,
                byte[] scanRecord
        );
    }
}
