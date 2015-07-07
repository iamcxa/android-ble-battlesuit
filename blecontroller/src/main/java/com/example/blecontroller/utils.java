package com.example.blecontroller;

/**
 * Created by kuyen on 2015/7/6.
 */
public class utils {

    /*
        Int
     */
    public final static class INT {

        public static final int
                BLE_STATE_OK = 1,
                BLE_STATE_OFF = 0,
                BLE_STATE_UNSUPPORTED = -1,
                BLE_STATE_DISCONNECTED = 999,
                BLE_STATE_CONNECTING = 888,
                BLE_STATE_CONNECTED = 777;

    }


    /*
        String
     */
    public final static class STRING {

        public final static String
                PREFIX =
                "com.geodoer.bluetooth.le.",

        TAG_PREFIX =
                "Geo_",

        ACTION_GATT_CONNECTED =
                PREFIX + "ACTION_GATT_CONNECTED",

        ACTION_GATT_DISCONNECTED =
                PREFIX + "ACTION_GATT_DISCONNECTED",

        ACTION_GATT_SERVICES_DISCOVERED =
                PREFIX + "ACTION_GATT_SERVICES_DISCOVERED",

        ACTION_DATA_AVAILABLE =
                PREFIX + "ACTION_DATA_AVAILABLE",

        EXTRA_DATA =
                PREFIX + "EXTRA_DATA",

        EXTRA_MAC =
                PREFIX + "EXTRA_MAC",

        ACTION_SERVICE_NAME=
                 PREFIX + ".GeoBleService",

        ACTION_SERVICE_STATE =
                PREFIX + ACTION_SERVICE_NAME + ".State",

        ACTION_STOP_GEOBLESERVICE =
                        PREFIX + ACTION_SERVICE_NAME + "StopSelf",

        targetUUID =
                "0000fff1-0000-1000-8000-00805f9b34fb",

        //datafrom startservice
        EXTRAS_DEVICE_NAME
                = "DEVICE_NAME",

        EXTRAS_DEVICE_ADDRESS
                = "DEVICE_ADDRESS",

        LIST_NAME
                = "NAME",

        DESC_BLUETOOTH_NOT_SUPPORTED
                =" Bluetooth not supported.",

        DESC_BLUETOOTH_SUPPORTED
                =" Bluetooth supported.",

        DESC_BLUETOOTH_LE_SUPPORTED
                =" BLE supported.",

        DESC_BLUETOOTH_LE_NOT_SUPPORTED
                =" BLE not supported.",

        DESC_BLUETOOTH_ENABLED
                =" Bluetooth enabled.",

        DESC_BLUETOOTH_NOT_ENABLED
                =" Bluetooth not enabled.";

    }
}
