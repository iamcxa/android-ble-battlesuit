package com.geodoer.bluetoothcontroler.service;

/**
 * Created by kuyen on 2015/7/6.
 */
public class utils {

    /*
        Int
     */
    
    public int mConnectionState = STATE_DISCONNECTED;

    public final static int STATE_DISCONNECTED = 0;

    public final static int STATE_CONNECTING = 1;

    public final static int STATE_CONNECTED = 2;


    /*
        String
     */

    public final static String TAG = BluetoothLeService.class.getSimpleName();

    public final static String GROUP_NAME =
            "com.geodoer";

    public final static String ACTION_GATT_CONNECTED =
            GROUP_NAME + "bluetooth.le.ACTION_GATT_CONNECTED";

    public final static String ACTION_GATT_DISCONNECTED =
            GROUP_NAME + "bluetooth.le.ACTION_GATT_DISCONNECTED";

    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            GROUP_NAME + "bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

    public final static String ACTION_DATA_AVAILABLE =
            GROUP_NAME + "bluetooth.le.ACTION_DATA_AVAILABLE";

    public final static String EXTRA_DATA =
            GROUP_NAME + "bluetooth.le.EXTRA_DATA";

    public final static String EXTRA_MAC=
            GROUP_NAME + "bluetooth.le.EXTRA_MAC";

}
