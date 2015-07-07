package com.example.blecontroller.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;

import com.example.blecontroller.utils;

/**
 * Created by kuyen on 2015/7/7. d
 */
public class BroadcastUpdater {

    //private Context context;

    public BroadcastUpdater(final Context context,
                            final String action,
                            final BluetoothGatt gatt) {

        final Intent intent = new Intent(action);

        intent.putExtra(utils.EXTRA_MAC, gatt.getDevice().getAddress());

        context.sendBroadcast(intent);
    }

    public BroadcastUpdater(final Context context,
                            final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }


    public BroadcastUpdater(final Context context,
                            final String action,
                            final String s)
    {
        final Intent intent = new Intent(action);
        intent.putExtra(utils.EXTRA_DATA,s);
        context.sendBroadcast(intent);
    }


    public BroadcastUpdater(final Context context,
                            final String action,
                            final BluetoothGattCharacteristic characteristic)
    {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0)
        {
            final StringBuilder stringBuilder = new StringBuilder(data.length);

            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));

            final StringBuilder stringBuilder2 = new StringBuilder(data.length);

            for (Byte i : data)
                stringBuilder2.append(Integer.toString(i.intValue(), 16));

            intent.putExtra(utils.EXTRA_DATA,
                    stringBuilder.toString() +
                            "\n" +
                            stringBuilder2.toString());
        }

        context.sendBroadcast(intent);
    }
}
