package com.geodoer.bluetoothcontroler.service;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.geodoer.bluetoothcontroler.view.BleActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MurasakiYoru on 2015/6/2.
 */
public class GeoBleService extends Service
{
    private static final String TAG = "GBS";
    private static final String targetUUID = "0000fff1-0000-1000-8000-00805f9b34fb";
    public  static final String mAction_servicestate ="com.geodoer.geobluetooth_example.GeoBleService.servicestate";
    public  static final String mAction_stopself = "com.geodoer.geobluetooth_example.GeoBleService.stopself";

    //datafrom startservice
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic mNotifyCharacteristic = null;
    private BluetoothLeService mBluetoothLeService;

    private String device_name;
    private String device_address;
    private boolean mConnected = false;



    //-----------------> added by kuyen for setting interface call.
    private static whenServiceStateChanged whenServiceStateChanged;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.wtf(TAG,"service start");
        Bundle b = intent.getExtras();

        device_name = b.getString(EXTRAS_DEVICE_NAME);
        device_address = b.getString(EXTRAS_DEVICE_ADDRESS);
        Log.wtf(TAG,"device name = "+ device_name);
        Log.wtf(TAG,"device address = "+device_address);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


        if (mBluetoothLeService != null)
        {
            final boolean result = mBluetoothLeService.connect(device_address);
            Log.d(TAG, "Connect request result=" + result);
        }

        broadcastUpdate(BleActivity.mAction_servicestate,device_name);


        //-----------------------------> added by kuyen
        whenServiceStateChanged.onServiceStart();

        //return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.wtf(TAG, "service destroyed");

        if(mNotifyCharacteristic!=null)
        {
            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
            mNotifyCharacteristic = null;
        }

        unregisterReceiver(mGattUpdateReceiver);

        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService = null;

        broadcastUpdate(BleActivity.mAction_servicestate, "null");


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
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize())
            {
                Log.e(TAG, "Unable to initialize Bluetooth");
                stopSelf();

                //-----------------------------> added by kuyen
                whenServiceStateChanged.onServiceUnableToInitialized();
            }

            // Automatically connects to the device upon successful start-up initialization.
            if(mBluetoothLeService.connect(device_address)) {

                //-----------------------------> added by kuyen
                whenServiceStateChanged.onServiceConnected(device_address);

                Log.wtf(TAG, "onServiceConnected to this: " + device_address);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.wtf(TAG,"onServiceDisConnected");
            mBluetoothLeService = null;

            //-----------------------------> added by kuyen
            whenServiceStateChanged.onServiceDisConnected(componentName);
        }
    };
    //---------------------------------------------------------------------------------------------

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            //Log.wtf(TAG,"Onreceive"+action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))
            {
                mConnected = true;
                //Log.wtf(TAG,"GATT_SERVICE_CONNECTED");
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                mConnected = false;
                //Log.wtf(TAG,"GATT_SERVICE_DISCONNECTED");
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {

                if(!mConnected)return;
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                /*
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                if(mConnected == false )return;

                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA) ;
                data = new String(data.substring(0,2));
                //Log.wtf(TAG,BluetoothLeService.ACTION_DATA_AVAILABLE);
                Log.wtf(TAG,"dispaly DATA:"+ data );
                */
            }
            else if(mAction_servicestate.equals(action))
            {
                broadcastUpdate(BleActivity.mAction_servicestate,device_name+
                        ": \""+
                        device_address+"\"");
            }
            else if(mAction_stopself.equals(action))
            {
                stopSelf();
            }
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(mAction_stopself);
        intentFilter.addAction(mAction_servicestate);
        return intentFilter;
    }

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
            //Log.wtf(TAG,"Service Name:" +lookup(uuid, unknownServiceString));
            //Log.wtf(TAG,"Service UUid:" +uuid );

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                int name = gattCharacteristic.getProperties();
                uuid = gattCharacteristic.getUuid().toString();

                //Log.wtf(TAG, "-Char Name:" + lookup(uuid, unknownCharaString + ":" + name));
                //Log.wtf(TAG, "-Char uuid:" + uuid);


                if (targetUUID.equals(uuid))
                {
                    //Log.wtf(TAG,"find success= "+targetUUID);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                }
            }
        }

    }


    //---------------------------------------------------------------------------------------------
    private static HashMap<String, String> attributes = new HashMap();
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

    //---------------------------------------------------------------------------------------------
    private void broadcastUpdate(final String action,String s)
    {
        final Intent intent = new Intent(action);
        intent.putExtra(BleActivity.EXTRA_DATA,s);
        sendBroadcast(intent);
    }

    //-----------------> added by kuyen for setting interface call.
    public void setServiceStateChangedTarget(whenServiceStateChanged target){
        whenServiceStateChanged = target;
    }


    //-----------------> added by kuyen.
    public interface whenServiceStateChanged{
        void onServiceConnected(String device_address);

        void onServiceDisConnected(ComponentName componentName);

        void onServiceDestroyed();

        void onServiceStart();

        void onServiceUnableToInitialized();
    }
}
