package com.example.blecontroller_demo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.geodoer.bluetoothcontroler.adapter.BleDeviceListAdapder;

/**
 * Created by kuyen on 2015/6/22.f
 */
public class BleDeviceSelectionDialog extends AlertDialog
    implements
        DialogInterface.OnClickListener
{

    private BleDeviceListAdapder mBleDeviceListAdapder;

    protected BleDeviceSelectionDialog(Context context) {
        super(context);
        //setContentView(R.layout.dialog_b);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }


}
