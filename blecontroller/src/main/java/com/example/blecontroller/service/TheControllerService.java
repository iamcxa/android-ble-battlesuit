package com.example.blecontroller.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.blecontroller.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by MurasakiYoru on 2015/6/2.s
 */
public class TheControllerService
        extends
        Service
        implements
        GattUpdateReceiver.whenReceivedActions
{

    private String
            logTag = utils.STRING.TAG_PREFIX + "GBS",
            device_name,
            device_address;

    private Thread
            serviceDiscoveryThread;

    private BluetoothGattCharacteristic
            mNotifyCharacteristic = null;

    private BleService
            mBleService;

    private boolean
            mConnected = false;


    //-----------------> added by kuyen for setting interface call.
    private static whenServiceStateChanged whenServiceStateChanged;

    @Override
    public void onCreate()
    {
        super.onCreate();

        this.logTag = "GBS";

        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.wtf(logTag, "service start");
        Bundle b = intent.getExtras();

        device_name = b.getString(utils.STRING.EXTRAS_DEVICE_NAME);
        device_address = b.getString(utils.STRING.EXTRAS_DEVICE_ADDRESS);
        Log.wtf(logTag,"device name = "+ device_name);
        Log.wtf(logTag,"device address = "+device_address);
        registerReceiver(new GattUpdateReceiver(this), new GattUpdateIntentFilter());


        if (mBleService != null)
        {
            final boolean result = mBleService.connect(device_address);
            Log.wtf(logTag,"Connect request result=" + result);
        }

        //broadcastUpdate(BleActivity.mAction_servicestate,device_name);

        new BroadcastUpdater(getBaseContext(),utils.STRING.ACTION_SERVICE_STATE,device_name);


        //-----------------------------> added by kuyen
        whenServiceStateChanged.onServiceStart();

        //return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.wtf(logTag, "service destroyed");

        if(mNotifyCharacteristic!=null)
        {
            mBleService.setCharacteristicNotification(mNotifyCharacteristic, false);
            mNotifyCharacteristic = null;
        }

        unregisterReceiver(new GattUpdateReceiver(this));

        unbindService(mServiceConnection);
        mBleService.disconnect();
        mBleService = null;

        //  broadcastUpdate(BleActivity.mAction_servicestate, "null");


        new BroadcastUpdater(getBaseContext(),
                utils.STRING.ACTION_SERVICE_STATE,"null");


        //-----------------------------> added by kuyen
        whenServiceStateChanged.onServiceDestroyed();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //---------------------------------------------------------------------------------------------
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (!mBleService.initialize())
            {
                Log.wtf(logTag,"Unable to initialize Bluetooth");
                stopSelf();

                //-----------------------------> added by kuyen
                whenServiceStateChanged.onServiceUnableToInitialized();
            }

            // Automatically connects to the device upon successful start-up initialization.
            if(mBleService.connect(device_address)) {

                //-----------------------------> added by kuyen
                whenServiceStateChanged.onServiceConnected(device_address);

                Log.wtf(logTag, "onServiceConnected to this: " + device_address);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.wtf(logTag,"onServiceDisConnected");
            mBleService = null;

            //-----------------------------> added by kuyen
            whenServiceStateChanged.onServiceDisConnected(componentName);
        }
    };


    private void displayGattServices(List<BluetoothGattService> gattServices)
    {
        if (gattServices == null) return;

        String uuid ;
        String unknownServiceString = "unKnownService";
        String unknownCharaString = "unknownCharString";

        // Loops through available GATT Services.

        for (BluetoothGattService gattService : gattServices)
        {
            uuid = gattService.getUuid().toString();
            //Log.wtf(logTag,"Service Name:" +lookup(uuid, unknownServiceString));
            //Log.wtf(logTag,"Service UUid:" +uuid );

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                int name = gattCharacteristic.getProperties();
                uuid = gattCharacteristic.getUuid().toString();

                //Log.wtf(logTag, "-Char Name:" + lookup(uuid, unknownCharaString + ":" + name));
                //Log.wtf(logTag, "-Char uuid:" + uuid);


                if (utils.STRING.targetUUID.equals(uuid))
                {
                    //Log.wtf(logTag,"find success= "+targetUUID);
                    mBleService.setCharacteristicNotification(gattCharacteristic,true);
                }
            }
        }

    }


    //---------------------------------------------------------------------------------------------
    private static HashMap<String, String> attributes = new HashMap<>();
    static {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002902-0000-1000-8000-00805f9b34fb", "CLIENT_CHARACTERISTIC_CONFIG");
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }


    //-----------------> added by kuyen for setting interface call.
    public void setServiceStateChangedTarget(whenServiceStateChanged target){
        whenServiceStateChanged = target;
    }



    @Override
    public void whenReceivedGattConnected() {

    }

    @Override
    public void whenReceivedGattDisConnected() {

    }

    @Override
    public void whenReceivedGattServiceDiscovered() {

    }

    @Override
    public void whenReceivedActionServiceState() {

    }

    @Override
    public void whenReceivedActionData() {

    }

    @Override
    public void whenReceivedActionStopGeoBleService() {

    }






    //-----------------> added by kuyen.
    public interface whenServiceStateChanged{
        void onServiceConnected(String device_address);

        void onServiceDisConnected(ComponentName componentName);

        void onServiceDestroyed();

        void onServiceStart();

        void onServiceUnableToInitialized();
    }


    /*
        multi device connect hack
     */
    private Queue<BluetoothGatt> serviceDiscoveryQueue = new LinkedList<BluetoothGatt>();

    private void initServiceDiscovery(){
        if(serviceDiscoveryThread == null){
            serviceDiscoveryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    serviceDiscovery();

                    serviceDiscoveryThread.interrupt();
                    serviceDiscoveryThread = null;
                }
            });

            serviceDiscoveryThread.start();
        }
    }

    private void serviceDiscovery(){
        while(!serviceDiscoveryQueue.isEmpty()){
            serviceDiscoveryQueue.poll().discoverServices();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e){}
        }
    }
}
