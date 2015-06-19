package com.geodoer.bluetoothcontroler.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.geodoer.bluetoothcontroler.R;
import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.bluetoothcontroler.service.GeoBleService;

import java.util.ArrayList;


public class BleActivity extends ListActivity
{
    private static final String LOG="BT";
    private static final long SCAN_PERIOD = 5000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String service_name ="com.geodoer.geobluetooth_example.GeoBleService";
    public  static final String mAction_servicestate
            = "com.geodoer.geobluetooth_example.BleActivity.servicestate";
    public  static final String EXTRA_DATA = "extra";

    private static final String buttontext_scanstart = "Scan Start";
    private static final String buttontext_scanstop = "Scan  Stop";
    private static final String text_scan_on = "state:Scanning";
    private static final String text_scan_off = "state:Not Scanning";
    private static final String text_connect_on = "Service is Running";
    private static final String text_connect_off = "No Service";

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning ;
    private Handler mHandler;

    private Button button_scan,stop_service;
    private TextView text_scan,text_connect,text_display;

    private boolean mServiceExisting = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.wtf("BLE","------------------APP START---------------------");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bleactivity);

        text_connect = (TextView)findViewById(R.id.textView_connect);
        text_connect.setText(text_connect_off);

        text_scan = (TextView)findViewById(R.id.textView_scanning);
        text_scan.setText(text_scan_off);

        text_display = (TextView)findViewById(R.id.text_display);

        button_scan = (Button)findViewById(R.id.button_scanstart);
        button_scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                scanLeDevice(!mScanning);
            }
        });
        button_scan.setText(buttontext_scanstart);


        stop_service = (Button)findViewById(R.id.button_stopservice);
        stop_service.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Stopservice();
            }
        });



        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Log.wtf(LOG,"NO BLE support");
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.wtf(LOG,"Bluetooth not supported");
            finish();
            return;
        }


        check_service();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        registerReceiver(ble_activity_receiver,ble_activity_receiverIntentFilter());
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        unregisterReceiver(ble_activity_receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //---------------------------------------------------------------------------------------------
    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            mLeDeviceListAdapter.clear();
            start_scanning();


            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(mScanning)
                    {
                        stop_scanning();
                    }
                }
            }, SCAN_PERIOD);
        }
        else
        {
            stop_scanning();
        }
    }
    private void start_scanning()
    {
        mScanning = true;
        text_scan.setText(text_scan_on);
        button_scan.setText(buttontext_scanstop);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    private void stop_scanning()
    {
        mScanning = false;
        text_scan.setText(text_scan_off);
        button_scan.setText(buttontext_scanstart);

        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
        {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        //--------------------------
        if(mServiceExisting) return;
        //---------------------------

        //stop scanning
        if (mScanning)scanLeDevice(false);

        //-------------------------------------------------------------
        final Intent serviceintent = new Intent(this,GeoBleService.class);
        serviceintent.putExtra(GeoBleService.EXTRAS_DEVICE_NAME,device.getName());
        serviceintent.putExtra(GeoBleService.EXTRAS_DEVICE_ADDRESS,device.getAddress());
        startService(serviceintent);

    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter
    {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter()
        {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = BleActivity.this.getLayoutInflater();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }
        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null)
            {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    private void Stopservice()
    {
        if(!mServiceExisting)return;

        mServiceExisting = false;
        final Intent serviceintent = new Intent(this,GeoBleService.class);
        stopService(serviceintent);
        text_connect.setText(text_connect_off);
    }

    private Boolean isServiceRunning(String serviceName)
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void check_service()
    {
        if(isServiceRunning(service_name))
        {
            //text_connect.setText(text_connect_on);
            final Intent intent = new Intent(GeoBleService.mAction_servicestate);
            sendBroadcast(intent);
        }
        else
        {
            mScanning = false;
            text_connect.setText(text_connect_off);
        }
    }

    //-----------------------------------------------------------------------------
    private final BroadcastReceiver ble_activity_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(data!=null)
                {
                    String temp = text_display.getText().toString();
                    if(temp.length() > 18)
                        text_display.setText(new String(data.substring(0, 2)));
                    else
                        text_display.setText(temp+"-"+new String(data.substring(0, 2)));
                }
            }
            else if (mAction_servicestate.equals(action))
            {
                String data = intent.getStringExtra(EXTRA_DATA);

                if(data.equals("null"))
                {
                    text_connect.setText(text_connect_off);
                    mServiceExisting = false;
                }
                else
                {
                    text_connect.setText(data);
                    mServiceExisting = true;
                }

            }
        }
    };

    private static IntentFilter ble_activity_receiverIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(mAction_servicestate);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}