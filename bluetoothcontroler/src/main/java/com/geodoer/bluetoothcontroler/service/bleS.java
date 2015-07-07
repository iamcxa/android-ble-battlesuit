package com.geodoer.bluetoothcontroler.service;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by kuyen on 2015/7/5.
 */
public class bleS extends Service {

    private Thread connectionThread;

    private Context context;

    public bleS( Context context) {
        super();
        this.context = context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Queue<BluetoothDevice> connectionQueue = new LinkedList<BluetoothDevice>();


    public void initConnection(){
        if(connectionThread == null){
            connectionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    connectionLoop();
                    connectionThread.interrupt();
                    connectionThread = null;
                }
            });

            connectionThread.start();
        }
    }

    private void connectionLoop(){
        while(!connectionQueue.isEmpty()){
            connectionQueue.poll()
                    .connectGatt(context, false, bleInterface.mGattCallback);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {}
        }
    }

}
