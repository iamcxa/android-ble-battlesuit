package com.example.blecontroller.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.example.blecontroller.utils;

/**
 * Created by kuyen on 2015/7/7.d
 */
public class GattCallback extends BluetoothGattCallback {

    public int
            mConnectionState;

    private String
            logTag = utils.STRING.TAG_PREFIX + "GCB";

    private Context
            context;

    private BluetoothGatt
            mBluetoothGatt;


    public GattCallback(Context context,
                        BluetoothGatt mBluetoothGatt) {
        super();
        this.context = context;
        this.mBluetoothGatt = mBluetoothGatt;
        this.mConnectionState = 0;
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch(status){
            case BluetoothGatt.GATT_SUCCESS:
                //String intentAction;

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //intentAction = ACTION_GATT_CONNECTED;
                    mConnectionState = utils.INT.BLE_STATE_CONNECTED;
                    //broadcastUpdate(intentAction);
                    new BroadcastUpdater(context,utils.STRING.ACTION_GATT_CONNECTED, gatt);
                    Log.i(logTag, "Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    Log.i(logTag,"Attempting to start service discovery:" +
                            mBluetoothGatt.discoverServices());

                } else
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = utils.INT.BLE_STATE_DISCONNECTED;
                    Log.i(logTag,"Disconnected from GATT server.");
                    //broadcastUpdate(intentAction);
                    new BroadcastUpdater(context,utils.STRING.ACTION_GATT_DISCONNECTED, gatt);
                }
                break;

            default:
                Log.wtf(logTag,"GATT state:"+status);
                break;

        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        //Log.wtf(TAG,"onServicesDiscovered");
        if (status == BluetoothGatt.GATT_SUCCESS) {
            new BroadcastUpdater(context,utils.STRING.ACTION_GATT_SERVICES_DISCOVERED);
        } else {
            Log.wtf(logTag,"onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status)
    {
        //Log.wtf(TAG,"onCharacteristicRead");
        if (status == BluetoothGatt.GATT_SUCCESS) {
            new BroadcastUpdater(context, utils.STRING.ACTION_DATA_AVAILABLE, characteristic);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic)
    {
        //Log.wtf(TAG,"onCharacteristicChanged");
        new BroadcastUpdater(context, utils.STRING.ACTION_DATA_AVAILABLE, characteristic);
    }
}
