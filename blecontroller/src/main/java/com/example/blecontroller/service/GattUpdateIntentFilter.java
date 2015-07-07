package com.example.blecontroller.service;

import android.content.IntentFilter;

import com.example.blecontroller.utils;

/**
 * Created by kuyen on 2015/7/7.f
 */
public class GattUpdateIntentFilter extends IntentFilter{

    //
    public  GattUpdateIntentFilter() {
        final  IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(utils.STRING.ACTION_GATT_CONNECTED);
        intentFilter.addAction(utils.STRING.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(utils.STRING.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(utils.STRING.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(utils.STRING.ACTION_STOP_GEOBLESERVICE);
        intentFilter.addAction(utils.STRING.ACTION_SERVICE_STATE);
        //return intentFilter;
    }
}
