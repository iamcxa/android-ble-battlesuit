package com.geodoer.bluetoothcontroler;

/**
 * Created by iamcx_000 on 2015/6/22.
 */
public class BcUtils {

    public static final String logTag
            = "BC";

    public static final String BarLogTag
            = "BAR";

    public static final int REQUEST_ENABLE_BT
            = 1;

    public static final String SERVICE_NAME
            ="com.geodoer.geobluetooth_example.GeoBleService";

    public static final String SERVICE_STATE
            = "com.geodoer.geobluetooth_example.BleActivity.servicestate";

    public static final String EXTRA_DATA
            = "extra";

    public static boolean IS_SERVICE_EXSTING
            = false;

    public static final String BLUETOOTH_NOT_SUPPORTED
            =" Bluetooth not supported.";

    public static final String BLUETOOTH_SUPPORTED
            =" Bluetooth supported.";

    public static final String BLUETOOTH_LE_SUPPORTED
            =" BLE supported.";

    public static final String BLUETOOTH_LE_NOT_SUPPORTED
            =" BLE not supported.";

    public static final String BLUETOOTH_ENABLED
            =" Bluetooth enabled.";

    public static final String BLUETOOTH_NOT_ENABLED
            =" Bluetooth not enabled.";

    public static final String BLUETOOTH_DEVICE_NOT_FOUND
            =" BLE devices not found.";

    public static final String buttontext_scanstart = "Scan Start";
    public static final String buttontext_scanstop = "Scan  Stop";
    public static final String text_scan_on = "state:Scanning";
    public static final String text_scan_off = "state:Not Scanning";
    public static final String text_connect_on = "Service is Running";
    public static final String text_connect_off = "No Service";

}
