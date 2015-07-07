package com.example.blecontroller.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.blecontroller.utils;

/**
 * Created by kuyen on 2015/7/7.r
 */
public class GattUpdateReceiver extends  BroadcastReceiver{

    private whenReceivedActions
            whenReceivedActions;

    public GattUpdateReceiver(GattUpdateReceiver.whenReceivedActions whenReceivedActions) {
        this.whenReceivedActions = whenReceivedActions;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();

        //Log.wtf(logTag,"Onreceive"+action);
        if (utils.STRING.ACTION_GATT_CONNECTED.equals(action))
        {
            whenReceivedActions.whenReceivedGattConnected();
            //mConnected = true;
            //Log.wtf(logTag,"GATT_SERVICE_CONNECTED");
        }
        else if (utils.STRING.ACTION_GATT_DISCONNECTED.equals(action))
        {
            whenReceivedActions.whenReceivedGattDisConnected();
            //mConnected = false;
            //Log.wtf(logTag,"GATT_SERVICE_DISCONNECTED");
        }
        else if (utils.STRING.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
        {

            whenReceivedActions.whenReceivedGattServiceDiscovered();
            //if(!mConnected)return;
            // Show all the supported services and characteristics on the user interface.
            //displayGattServices(mBluetoothLeService.getSupportedGattServices());

        }
        else if (utils.STRING.ACTION_DATA_AVAILABLE.equals(action))
        {
            whenReceivedActions.whenReceivedActionData();
                /*
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                if(mConnected == false )return;

                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA) ;
                data = new String(data.substring(0,2));
                //Log.wtf(logTag,BluetoothLeService.ACTION_DATA_AVAILABLE);
                Log.wtf(logTag,"dispaly DATA:"+ data );
                */
        }
        else if(utils.STRING.ACTION_SERVICE_STATE.equals(action))
        {
            whenReceivedActions.whenReceivedActionServiceState();
//                broadcastUpdate(BleActivity.mAction_servicestate, device_name +
//                        ": \"" +
//                        device_address + "\"");

//            new BroadcastUpdater(getBaseContext(),
//                    utils.STRING.ACTION_SERVICE_STATE,
//                    device_name +
//                            ": \"" +
//                            device_address + "\""
//            );
        }
        else if(utils.STRING.ACTION_STOP_GEOBLESERVICE.equals(action))
        {
            whenReceivedActions.whenReceivedActionStopGeoBleService();
         //   stopSelf();
        }
    }


    public interface whenReceivedActions {

        void whenReceivedGattConnected();

        void whenReceivedGattDisConnected();

        void whenReceivedGattServiceDiscovered();

        void whenReceivedActionServiceState();

        void whenReceivedActionData();

        void whenReceivedActionStopGeoBleService();

    }
}
