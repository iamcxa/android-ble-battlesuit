package com.geodoer.bluetoothcontroler.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.bluetoothcontroler.BcUtils;

/**
 * Created by kuyen on 2015/6/22.f
 */
public class BleActionReceiver extends BroadcastReceiver {

    private whenReceivedBleAction mWhenReceivedBleAction;

    public BleActionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        // [(action string)]-->(extra data)
        if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
        {
            // (action string)-->[(extra data)]
            String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            if(data!=null)
            {
                Log.wtf(BcUtils.BarLogTag,
                                " Received : \""+
                        data.substring(0, 2)+"\"");

                mWhenReceivedBleAction.onReceivedActionData(data.substring(0, 2));
            }
        }
        // [(action string)]-->(extra data)
        else if (BcUtils.SERVICE_STATE.equals(action))
        {
            // (action string)-->[(extra data)]
            String data = intent.getStringExtra(BcUtils.EXTRA_DATA);

            if(data.equals("null"))
            {
                Log.wtf(BcUtils.BarLogTag, " Received null.");

                mWhenReceivedBleAction.onReceivedNull();
                BcUtils.IS_SERVICE_EXSTING = false;
            }
            else
            {
                Log.wtf(BcUtils.BarLogTag, " Received sth: \""+action+"\"");

                mWhenReceivedBleAction.onReceivedSomething(data);
                BcUtils.IS_SERVICE_EXSTING = true;
            }
        }
    }

    public void setWhenReceivedBleActionTarget(whenReceivedBleAction whenReceivedBleActionTarget){
        this.mWhenReceivedBleAction = whenReceivedBleActionTarget;
    }

    // IntentFilter
    public static IntentFilter BleIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BcUtils.SERVICE_STATE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // interface
    public interface whenReceivedBleAction{
        void onReceivedActionData(String actionData);

        void onReceivedNull();

        void onReceivedSomething(String data);
    }
}
