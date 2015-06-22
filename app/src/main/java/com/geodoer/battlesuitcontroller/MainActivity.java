package com.geodoer.battlesuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.view.HostFragment;
import com.geodoer.battlesuitcontroller.view.JoinFragment;
import com.geodoer.battlesuitcontroller.view.SettingsActivity;
import com.geodoer.bluetoothcontroler.BcUtils;
import com.geodoer.bluetoothcontroler.controller.BleActionReceiver;
import com.geodoer.bluetoothcontroler.controller.BleController;
import com.geodoer.bluetoothcontroler.service.GeoBleService;

import at.markushi.ui.CircleButton;

import static com.geodoer.battlesuitcontroller.BscUtils.switchFragment;


public class MainActivity
        extends
        AppCompatActivity
        implements
        View.OnClickListener,
        BleActionReceiver.whenReceivedBleAction,
        BleController.whenRunningBleService{

    private CircleButton btnHost;
    private CircleButton btnJoin;
    private CircleButton cbMainLogo;
    private Toolbar toolbar;
    private TextView txtWaitingIndicator;

    private Handler handler, handlerForUi;
    private Runnable rCheckBle,rConnectBle,rWaitingForConnection,rAutoConnectToDevice;

    //
    private static boolean isCancel = false, isBreathing = true;
    private final String bleDevice1 = "8C:DE:52:34:C6:2F";
    private final String bleDevice2 = "8C:DE:52:34:C6:34";

    //
    //
    //

    private BleActionReceiver mBleActionReceiver;

    private BleController bc;


    //
    // AppCompatActivity Overrides
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //if (navigationView != null) setupDrawerContent(navigationView);

        setComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intentSetting= new Intent();
                intentSetting.setClass(this, SettingsActivity.class);
                this.startActivity(intentSetting);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 註冊廣播接收器
        this.registerReceiver(mBleActionReceiver, BleActionReceiver.BleIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除廣播接收器
        unregisterReceiver(mBleActionReceiver);

        // 傳送意圖以停止 Ble 服務
        final Intent intent = new Intent(GeoBleService.mAction_stopself);
        sendBroadcast(intent);
    }

    //
    // class methods
    //

    private void setComponents() {
        Log.wtf(BscUtils.logTag, "---------- APP START ----------");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(false);

        btnHost = (CircleButton) findViewById(R.id.btnHost);
        btnJoin = (CircleButton) findViewById(R.id.btnJoin);
        btnHost.setOnClickListener(this);
        btnJoin.setOnClickListener(this);

        cbMainLogo = (CircleButton) findViewById(R.id.cbMainLogo);
        cbMainLogo.setOnClickListener(this);

        txtWaitingIndicator = (TextView) findViewById(R.id.txtWaitingIndicator);
        txtWaitingIndicator.setText("...");

        mBleActionReceiver = new BleActionReceiver();
        mBleActionReceiver.setWhenReceivedBleActionTarget(this);

        bc = new BleController(getApplicationContext());
        bc.setBleServiceRunningTarget(this);

        handlerForUi = new Handler();
        handlerForUi.postDelayed(setWaiting(), 300);

        handler = new Handler();
        handler.post(setCheckBle());
    }

    private void setComponentsVisible(){
        //cbMainLogo.setVisibility(View.INVISIBLE);
        //cbMainLogo.setColor(getResources().getColor(R.color.c_blue_green));
        //toolbar.setVisibility(View.VISIBLE);
        //ivMainLogo.setVisibility(View.INVISIBLE);
        // btnHost.setVisibility(View.VISIBLE);
        //btnJoin.setVisibility(View.VISIBLE);
        cbMainLogo.setColor(getResources().getColor(R.color.c_brick_red));
        cbMainLogo.setAlpha((float) 1);
        cbMainLogo.setClickable(true);
    }

    private void setComponentsInVisible(){
        cbMainLogo.setClickable(false);
        toolbar.setVisibility(View.GONE);
        btnHost.setVisibility(View.INVISIBLE);
        btnJoin.setVisibility(View.INVISIBLE);
    }

    private AlertDialog showExitAlertDialog(){
        return new AlertDialog.Builder(this)
                .setTitle("發現問題")
                .setMessage("你的裝置不支援藍牙4.0！")
                .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).show();
    }

    //
    // Runnable(s)
    //

    // 搜尋Ble裝置時做動
    private Runnable setWaiting(){
        return rWaitingForConnection = new Runnable() {
            @Override
            public void run() {
                if (txtWaitingIndicator.getText().equals("..."))
                    txtWaitingIndicator.setText("....");
                else
                if (txtWaitingIndicator.getText().equals("...."))
                    txtWaitingIndicator.setText(".....");
                else
                if (txtWaitingIndicator.getText().equals("....."))
                    txtWaitingIndicator.setText("...");

                if(isBreathing)
                {
                    handlerForUi.postDelayed(this, 300);
                    // setComponentsInVisible();
                }
                else
                {
                    txtWaitingIndicator.setText("OK");
                    handlerForUi.removeCallbacks(this);
                }
            }
        };
    }

    // 檢查ble支援狀態
    private Runnable setCheckBle(){
        return rCheckBle = new Runnable() {
            @Override
            public void run() {
                // run start
                int bleStateCode = bc.checkBleState();

                if (bleStateCode==BleController.BLE_STATE_OK)
                {
                    bc.triggerScan();
                    handler.removeCallbacks(rCheckBle);
                    handler.postDelayed(setConnectBle(), 5000);
                }
                else if (bleStateCode==BleController.BLE_STATE_OFF)
                {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BcUtils.REQUEST_ENABLE_BT);
                    handler.postDelayed(rCheckBle,5000);
                }
                else if(bleStateCode == BleController.BLE_STATE_UNSUPPORTED)
                {
                    showExitAlertDialog();
                }
                // run end
            }
        };
    }

    // 如果發現預設兩組ble-address, 則自動連線
    private Runnable setConnectBle() {
        return rConnectBle = new Runnable() {
            @Override
            public void run() {
                // run start
                isBreathing = false;
                setComponentsVisible();
                // run end
            }
        };
    }

    private Runnable setAutoConnectToDevice(final BluetoothDevice thisDevice){
        return rAutoConnectToDevice = new Runnable(){

            @Override
            public void run() {
                // run end
                final Intent connectBle = new Intent(getApplicationContext(),
                        GeoBleService.class);
                connectBle.putExtra(GeoBleService.EXTRAS_DEVICE_NAME,
                        thisDevice.getName());
                connectBle.putExtra(GeoBleService.EXTRAS_DEVICE_ADDRESS,
                        thisDevice.getAddress());
                startService(connectBle);
                // run end
            }
        };
    }

    //
    // interface callbacks
    //

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnHost:
                switchFragment(this,HostFragment.newInstance("", ""));
                break;
            case R.id.btnJoin:
                switchFragment(this,JoinFragment.newInstance("", ""));
                break;
        }
        Toast.makeText(this, v.getId() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionDataAvailable(String actionData) {

    }

    @Override
    public void onBleServiceNotConnecting() {

    }

    @Override
    public void onBleServiceConnecting() {

    }

    @Override
    public void onServiceIsRunningNow() {

    }

    @Override
    public void onServiceIsStopped() {

    }

    @Override
    public void startScanning() {

    }

    @Override
    public void stopScanning() {

    }

    @Override
    public void onGotBleDevices(BluetoothDevice thisDevice, int rssi, byte[] scanRecord) {

        if((thisDevice.getAddress().equals(bleDevice1) ||
                (thisDevice.getAddress().equals(bleDevice2))))
        {
            handler.postDelayed(rAutoConnectToDevice,1500);
        }

    }
}
