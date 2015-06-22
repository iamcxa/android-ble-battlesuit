package com.geodoer.battlesuitcontroller.view;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.bluetoothcontroler.service.GeoBleService;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * This is a custom dialog class that will hold a tab view with 2 tabs.
 * Tab 1 will be a list view. Tab 2 will be a list view.
 */
public class BleCustomDialog extends AlertDialog
        implements
        DialogInterface.OnClickListener,
        OnShowListener,
        ListView.OnItemClickListener
{
    private static final String LOG="BT";
    private static final long SCAN_PERIOD = 5000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String service_name ="com.geodoer.geobluetooth_example.GeoBleService";
    public  static final String mAction_servicestate = "com.geodoer.geobluetooth_example.BleActivity.servicestate";
    public  static final String EXTRA_DATA = "extra";

    private static final String buttontext_scanstart = "Scan Start";
    private static final String buttontext_scanstop = "Scan  Stop";
    private static final String text_scan_on = "state:Scanning";
    private static final String text_scan_off = "state:Not Scanning";
    private static final String text_connect_on = "Service is Running";
    private static final String text_connect_off = "No Service";
    private Button button_scan,stop_service;
    private TextView text_scan,text_connect,text_display;

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning ;
    private Handler mHandler;


    private boolean mServiceExisting = false;

    View dialogLayout;
    LayoutInflater mLayoutInflater;

    private String selectedDate;
    private String selectedTime;
    //private ViewHolder v=new ViewHolder();
    private ListView lvBleDevice;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.wtf("BLE", "------------------APP START---------------------");

        setContentView(R.layout.dialog_ble);

        lvBleDevice=(ListView)findViewById(R.id.lvBleDevice);

        text_connect = (TextView)findViewById(R.id.txtConnect);
        text_connect.setText(text_connect_off);

        text_scan = (TextView)findViewById(R.id.txtScanning);
        text_scan.setText(text_scan_off);

        text_display = (TextView)findViewById(R.id.txtShowMsg);

        button_scan = (Button)findViewById(R.id.btnScan);
        button_scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                scanLeDevice(!mScanning);
            }
        });
        button_scan.setText(buttontext_scanstart);

        stop_service = (Button)findViewById(R.id.btnDisconnect);
        stop_service.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Stopservice();
            }
        });

        mHandler = new Handler();

        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Log.wtf(LOG,"NO BLE support");
            cancel();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.wtf(LOG,"Bluetooth not supported");
            cancel();
            return;
        }

        check_service();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getOwnerActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        getContext().registerReceiver(ble_activity_receiver, ble_activity_receiverIntentFilter());
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        lvBleDevice.setAdapter(mLeDeviceListAdapter);
        lvBleDevice.setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        getContext().unregisterReceiver(ble_activity_receiver);
    }

    @Override
    public void cancel() {
        super.cancel();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
//        getContext().unregisterReceiver(ble_activity_receiver);
        if (mHandler != null) {
        mHandler.removeCallbacks(runnable);
        }
    }

    public BleCustomDialog(Context context) {
        super(context);

        mLayoutInflater = getWindow().getLayoutInflater();
        dialogLayout = mLayoutInflater.inflate(R.layout.dialog_ble, null);

        // set custom dialog layout
        setView(dialogLayout);

        // remove window title
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // get this window's layout parameters so we can change the position
        WindowManager.LayoutParams params = getWindow().getAttributes();

        // change the position. 0,0 is center
        params.x = 0;
        params.y = 50;
        params.height = -2;
        params.width = -2;
        this.getWindow().setAttributes(params);

        // set dialog Buttons
        this.setButton(BUTTON_POSITIVE,
                "1", this);
        this.setButton(BUTTON_NEUTRAL,
                "2", this);
        this.setButton(BUTTON_NEGATIVE,
                "3", this);
        this.setCanceledOnTouchOutside(false);

        // set Show Listener - in case to hide BUTTON_NEUTRAL.
        this.setOnShowListener(this);
    }

    private Button getBtnNutral() {
        return getButton(DialogInterface.BUTTON_NEUTRAL);
    }

    private void setDialogShowing(DialogInterface dialog) {
        try {
            //不關閉
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
            //  MyDebug.MakeLog(1, "setDialogShowing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDialogDismiss(DialogInterface dialog) {
        try {
            //不關閉
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
            //MyDebug.MakeLog(1, "setDialogDismiss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBtnAction_Positive(DialogInterface dialog) {

    }


    private void setBtnAction_Neutral() {
        selectedTime = "";


        // 隱藏按鈕
        getBtnNutral().setVisibility(View.GONE);
    }

    /**
     * This is called when a long press occurs on our listView02 items.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Delete");
    }

    /**
     * This is called when an item in our context menu is clicked.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }

    /**
     * Dialog On-Show-Listener
     * 從 TaskEditorMain 讀取日期時間  / 隱藏放棄時間按鈕
     */
    @Override
    public void onShow(DialogInterface dialog) {
        // 啟動先隱藏放棄時間按鈕
        getBtnNutral().setVisibility(View.GONE);

    }

    /**
     * 建立三個按鈕的監聽式
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //which可以用來分辨是按下哪一個按鈕
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:    // save selected date/time

                setBtnAction_Positive(dialog);

                break;
            case DialogInterface.BUTTON_NEUTRAL:        // 取消時間
                setDialogShowing(dialog);
                setBtnAction_Neutral();

                break;
            case DialogInterface.BUTTON_NEGATIVE:    // 取消全部

                setDialogDismiss(dialog);

                break;
        }
    }

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void start_scanning()
    {
        mScanning = true;
        text_scan.setText(text_scan_on);
        button_scan.setText(buttontext_scanstop);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stop_scanning()
    {
        mScanning = false;
        text_scan.setText(text_scan_off);
        button_scan.setText(buttontext_scanstart);

        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        //--------------------------
        if(mServiceExisting) return;
        //---------------------------

        //stop scanning
        if (mScanning)scanLeDevice(false);

        //-------------------------------------------------------------
        final Intent serviceintent = new Intent(getContext(),GeoBleService.class);
        serviceintent.putExtra(GeoBleService.EXTRAS_DEVICE_NAME, device.getName());
        serviceintent.putExtra(GeoBleService.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        getContext().startService(serviceintent);
        cancel();
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
            mInflator = getLayoutInflater();
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
                view = mInflator.inflate(com.geodoer.bluetoothcontroler.R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(com.geodoer.bluetoothcontroler.R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(com.geodoer.bluetoothcontroler.R.id.device_name);
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
                viewHolder.deviceName.setText(com.geodoer.bluetoothcontroler.R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }


    private BluetoothDevice device;


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice thisDevice, int rssi, byte[] scanRecord) {
                    device=thisDevice;
                    mHandler = new Handler();
                    mHandler.post(runnable);
                }
            };

    final Runnable runnable = new Runnable() {
        public void run() {
            mLeDeviceListAdapter.addDevice(device);
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    };

    private void Stopservice()
    {
        if(!mServiceExisting)return;

        mServiceExisting = false;
        final Intent serviceintent = new Intent(getContext(),GeoBleService.class);
        getContext().stopService(serviceintent);
        text_connect.setText(text_connect_off);
    }

    private Boolean isServiceRunning(String serviceName)
    {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
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
            getContext().sendBroadcast(intent);
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


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}