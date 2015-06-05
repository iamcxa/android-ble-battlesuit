package com.geodoer.bluetoothcontroler.controller;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.geodoer.bluetoothcontroler.service.GeoBleService;
import com.geodoer.bluetoothcontroler.view.BleActivity;


/**
 * Created by MurasakiYoru on 2015/6/4.
 */
public class GeoBleController
{
    private static final String service_name ="com.geodoer.geobluetooth_example.GeoBleService";
    private static final String TAG = "GeoBle";
    public GeoBleController()
    {

    }
    public static void startBleActivity(Context context)
    {
        Intent intent = new Intent(context, BleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }
    public static void destroyService(Context context)
    {
        Log.wtf(TAG,"destroyService1");
        if(isServiceRunning(context,service_name) )
        {
            final Intent intent = new Intent(GeoBleService.mAction_stopself);
            context.sendBroadcast(intent);
        }
    }

    private static Boolean isServiceRunning(Context context,String serviceName)
    {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
