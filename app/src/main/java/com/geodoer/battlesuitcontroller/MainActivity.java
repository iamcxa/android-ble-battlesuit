package com.geodoer.battlesuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static com.geodoer.battlesuitcontroller.BscUtils.logTag;
import static com.geodoer.battlesuitcontroller.BscUtils.switchFragment;


public class MainActivity
        extends
        AppCompatActivity
        implements
        View.OnClickListener,
        BleActionReceiver.whenReceivedBleAction,
        BleController.whenRunningBleService,
        GeoBleService.whenServiceStateChanged{

    private CircleButton
            btnHost,
            btnJoin,
            cbMainLogo;

    private Toolbar
            toolbar;

    private TextView
            txtWaitingIndicator;

    private Handler
            handler,
            handlerForMainLogoFading,
            handlerForUi;

    private Runnable
            rCheckBle,
            rCheckConnectionAlive,
            rWaitingForConnection,
            rAutoConnectToDevice,
            rMainLogoFading,
            rMainLogoFadeOut;

    //
    private boolean
            isMainLogoFading = true,
            isWaiting = true,
            hasDevice1 = false,
            hasDevice2 = false,
            doConnect = false;

    private final String
            bleDevice1 = "8C:DE:52:34:C6:2F",
            bleDevice2 = "8C:DE:52:34:C6:34";

    private int
            findDeviceFailedCount = 0;

    private ArrayList<String>
            arrayBleDevices = null;

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
        //this.recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除廣播接收器
        unregisterReceiver(mBleActionReceiver);

        isMainLogoFading = false;

        removeAllRunnableFromQueue();

        // 傳送意圖以停止 Ble 服務
        final Intent intent = new Intent(GeoBleService.mAction_stopself);
        sendBroadcast(intent);
        Log.wtf(BscUtils.logTag, "---------- APP END ----------");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }else
        if(keyCode == KeyEvent.KEYCODE_HOME)
        {

            return true;
        }else
        if(keyCode == KeyEvent.KEYCODE_APP_SWITCH)
        {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.isTracking() &&
                !event.isCanceled()) {

            showDialogWhenPressedBackKey();

            return true;
        }
        return super.onKeyUp(keyCode, event);
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

        mBleActionReceiver = new BleActionReceiver();
        mBleActionReceiver.setWhenReceivedBleActionTarget(this);

        bc = new BleController(getApplicationContext());
        bc.setBleServiceRunningTarget(this);

        GeoBleService gbs = new GeoBleService();
        gbs.setServiceStateChangedTarget(this);

        arrayBleDevices = new ArrayList<>();
        arrayBleDevices.clear();

        handlerForUi = new Handler();
        handler = new Handler();
        handlerForMainLogoFading = new Handler();

        start();
    }

    private void start(){
        // 還原預設值
        setComponentsBackDefaultValue();

        // 清空queue
        removeAllRunnableFromQueue();

        // 塞入runnable
        handlerForUi.postDelayed(setRunWaiting(), 300);
        handler.post(setRunCheckBle());
        handlerForMainLogoFading.postDelayed(setRunFading(), 100);
    }

    private void removeAllRunnableFromQueue(){
        if(handler!=null){
            if(rCheckBle!=null)
                handler.removeCallbacks(rCheckBle);
            if(rAutoConnectToDevice!=null)
                handler.removeCallbacks(rAutoConnectToDevice);
        }
        if(handlerForUi!=null) {
            if (rWaitingForConnection != null)
                handlerForUi.removeCallbacks(rWaitingForConnection);
            if (rCheckConnectionAlive != null)
                handlerForUi.removeCallbacks(rCheckConnectionAlive);
        }
        if(handlerForMainLogoFading!=null){
            if(rMainLogoFading!=null)
                handlerForMainLogoFading.removeCallbacks(rMainLogoFading);
            if(rMainLogoFadeOut!=null)
                handlerForMainLogoFading.removeCallbacks(rMainLogoFadeOut);
        }

        if(bc!=null)
            bc.triggerStopService();
    }

    private void setComponentsBackDefaultValue(){
        Log.wtf(BscUtils.logTag, "----------- reSTART -----------");

        BscUtils.deviceName = "";
        BscUtils.ConnectedBleDeviceAddress = "";
        BscUtils.ConnectedBleDevice = null;

        arrayBleDevices.clear();

        findDeviceFailedCount = 0;
        isMainLogoFading = true;
        isWaiting = true;
        hasDevice1 = false;
        hasDevice2 = false;
        doConnect = false;

        setComponentsWhenNeededToConnectBleDevices();
    }

    private void setComponentsWhenBleDeviceConnected(){
        cbMainLogo.setColor(getResources().getColor(R.color.c_brick_red));
        cbMainLogo.setAlpha((float) 1);
        cbMainLogo.setClickable(false);
        cbMainLogo.setPressed(true);

        isMainLogoFading = false;
    }

    private void setComponentsWhenNeededToConnectBleDevices(){
        txtWaitingIndicator.setVisibility(View.VISIBLE);
        txtWaitingIndicator.setText("...");
        txtWaitingIndicator.setAlpha((float) 1);

        toolbar.setVisibility(View.GONE);
        btnHost.setVisibility(View.INVISIBLE);
        btnJoin.setVisibility(View.INVISIBLE);

        cbMainLogo.setVisibility(View.VISIBLE);
        cbMainLogo.setColor(getResources().getColor(R.color.c_deep_sky_blue));
        cbMainLogo.setAlpha((float) 0.5);
        cbMainLogo.setClickable(false);
        cbMainLogo.setPressed(false);
    }

    private void setSubMenuShow(){
        txtWaitingIndicator.setVisibility(View.GONE);
        txtWaitingIndicator.setText("...");

        toolbar.setVisibility(View.VISIBLE);
        btnHost.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.VISIBLE);

        cbMainLogo.setClickable(false);
        cbMainLogo.setVisibility(View.GONE);
    }


    //
    // dialogs
    //

    private AlertDialog showDialogWhenGetNoBleSupport(){
        return new AlertDialog.Builder(this)
                .setTitle("發現問題")
                .setIcon(R.drawable.warning)
                .setMessage("你的裝置不支援藍牙4.0！")
                .setCancelable(false)
                .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenFindNoDevices(){
        return new AlertDialog.Builder(this)
                .setTitle("發現問題")
                .setIcon(R.drawable.warning)
                .setMessage("找不到裝置！")
                .setCancelable(false)
                .setNeutralButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("再多試幾次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        start();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("用模擬器好了...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllRunnableFromQueue();
                        handlerForUi.postDelayed(setRunFadingOut(), 1000);
                        dialog.dismiss();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenPressedBackKey(){
        return new AlertDialog.Builder(this)
                .setTitle("你確定？")
                .setIcon(R.drawable.ic_forum)
                .setMessage("你按下返回鍵了。你要離開B.S.C嗎？")
                .setCancelable(false)
                .setNegativeButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("沒事！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenLostConnection(){
        bc.triggerStopService();
        return new AlertDialog.Builder(this)
                .setTitle("發現問題")
                .setIcon(R.drawable.warning)
                .setMessage("裝置斷線！")
                .setCancelable(false)
                .setNegativeButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("重新連線", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.recreate();
                    }
                }).show();
    }

    //==============================//
    //         Runnable(s)          //
    //==============================//

    // 搜尋Ble裝置時做動
    private Runnable setRunWaiting(){
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

                if(isWaiting)
                {
                    handlerForUi.postDelayed(this, 300);
                }
                else
                {
                    handlerForUi.removeCallbacks(this);
                    setComponentsWhenBleDeviceConnected();
                    handlerForUi.postDelayed(setRunCheckConnectionAlive(),2500);
                }
            }
        };
    }

    // 檢查ble支援狀態
    private Runnable setRunCheckBle(){
        return rCheckBle = new Runnable() {
            @Override
            public void run() {
                // run start
                int bleStateCode = bc.checkBleState();

                if (bleStateCode==BleController.BLE_STATE_OK)
                {
                    bc.triggerScan();
                    handler.removeCallbacks(this);
                }
                else if (bleStateCode==BleController.BLE_STATE_OFF)
                {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, BcUtils.REQUEST_ENABLE_BT);
                    handler.postDelayed(rCheckBle,5000);
                }
                else if(bleStateCode == BleController.BLE_STATE_UNSUPPORTED)
                {
                    showDialogWhenGetNoBleSupport();
                }
                // run end
            }
        };
    }

    // 自動檢查連線狀態
    private Runnable setRunCheckConnectionAlive() {
        return rCheckConnectionAlive = new Runnable() {
            int bleConnectionState = 0;
            String bleConnectionStateString;
            @Override
            public void run() {
                // run start
                bleConnectionState =
                        bc.keepConnection(BscUtils.ConnectedBleDeviceAddress);

                switch (bleConnectionState) {
                    case -1: // not ready yet

                        break;

                    case 0: // disconnected
                        showDialogWhenLostConnection();
                        bleConnectionStateString = "DISCONNECTION.";
                        break;

                    case 2: // connected
                        handlerForUi.removeCallbacks(this);
                        handlerForUi.postDelayed(this, 2500);
                        bleConnectionStateString = "ALIVE.";
                        break;
                }

                Log.wtf(logTag, "Connection state with Board "+
                        BscUtils.deviceName +
                        " is " +
                        bleConnectionStateString);

                // run end
            }
        };
    }

    private Runnable setRunAutoConnectToDevice(final BluetoothDevice thisDevice){
        return rAutoConnectToDevice = new Runnable(){
            @Override
            public void run() {
                // run start
                if(doConnect) {
                    final Intent connectBle = new Intent(getApplicationContext(),
                            GeoBleService.class);
                    connectBle.putExtra(GeoBleService.EXTRAS_DEVICE_NAME,
                            thisDevice.getName());
                    connectBle.putExtra(GeoBleService.EXTRAS_DEVICE_ADDRESS,
                            thisDevice.getAddress());
                    startService(connectBle);
                }
                else
                    handler.postDelayed(setRunAutoConnectToDevice(thisDevice), 1000);
                // run end
            }
        };
    }

    private Runnable setRunFadingOut(){
        setComponentsWhenBleDeviceConnected();
        return rMainLogoFadeOut = new Runnable() {
            int currV = ((int)cbMainLogo.getAlpha())*100;
            float alpha;
            boolean go = true;
            @Override
            public void run() {
                // run start
                alpha = ((float) currV) / 100;

                if(go) currV--;

                if (currV >= 1)
                    go = true;
                else
                if (currV == 0 )
                    go = false;

                cbMainLogo.setAlpha(alpha);
                txtWaitingIndicator.setAlpha(alpha);
                //Log.wtf(logTag, "fadout currV=" + currV + " currAlpha=" + cbMainLogo.getAlpha());
                if(go)
                    handlerForMainLogoFading.postDelayed(this, 20);
                else {
                    handlerForMainLogoFading.removeCallbacks(this);
                    cbMainLogo.callOnClick();
                }
                // run end
            }
        };
    }

    private Runnable setRunFading(){
        return rMainLogoFading = new Runnable(){
            int currV = 0;
            boolean go = true;
            @Override
            public void run() {
                // run start
                if (currV == 110 && go)
                    go = false;
                else
                if (currV <= 20 && !go)
                    go = true;

                if (go)
                    currV++;
                else
                    currV--;

                cbMainLogo.setAlpha(((float) currV) / 100);
                //Log.wtf(logTag, "fading currV=" + currV + " currAlpha=" + cbMainLogo.getAlpha());
                if(isMainLogoFading)
                    handlerForMainLogoFading.postDelayed(this, 20);
                // run end
            }
        };
    }

    //==============================//
    //      interface callbacks     //
    //==============================//

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnHost:
                switchFragment(this,HostFragment.newInstance("", ""));
                break;

            case R.id.btnJoin:
                switchFragment(this,JoinFragment.newInstance("", ""));
                break;

            case R.id.cbMainLogo:
                setSubMenuShow();
                break;
        }
        Toast.makeText(this, v.getId() + "", Toast.LENGTH_SHORT).show();
    }


    // BleController

    @Override
    public void onBcServiceIsRunningNow() {

    }

    @Override
    public void onBcServiceIsStopped() {

    }

    @Override
    public void BcStartScanning() {

    }

    @Override
    public void BcStopScanning() {

        if(hasDevice1 || hasDevice2)
        {
            doConnect = true;
        }
        else
        {
            findDeviceFailedCount++;
            Log.wtf(logTag," device finding failed count is " + findDeviceFailedCount);
        }

        if(findDeviceFailedCount >= 1){
            showDialogWhenFindNoDevices();
        }
    }

    @Override
    public void onBcFoundBleDevices(
            BluetoothDevice thisDevice,
            int rssi, byte[] scanRecord) {

        if(arrayBleDevices !=null) {
            arrayBleDevices.trimToSize();
            if (!hasDevice1)
                if (thisDevice.getAddress().equals(bleDevice1)) {
                    hasDevice1 = true;
                    arrayBleDevices.add(bleDevice1);
                    handler.postDelayed(setRunAutoConnectToDevice(thisDevice), 1500);
                }

            if (!hasDevice2)
                if (thisDevice.getAddress().equals(bleDevice2)) {
                    hasDevice2 = true;
                    arrayBleDevices.add(bleDevice2);
                    handler.postDelayed(setRunAutoConnectToDevice(thisDevice), 1500);
                }
        }else
            Log.wtf("","arraylist is null!");
    }


    // GeoBleService

    @Override
    public void onServiceConnected(String device_address) {

        Log.wtf(logTag,"connected with "+device_address);

        if(device_address.equals(bleDevice1)||
                device_address.equals(bleDevice2)){
            //
            isWaiting = false;

            BscUtils.deviceName = device_address.subSequence(15,17).toString();

            BscUtils.ConnectedBleDeviceAddress = device_address;

            txtWaitingIndicator.setText(
                    "OK with Board " + BscUtils.deviceName);

            handlerForUi.postDelayed(setRunFadingOut(),1000);
        }
    }

    @Override
    public void onServiceDisConnected(ComponentName componentName) {
        Log.wtf(logTag, " Connection Lost with " +
                componentName.getPackageName() + "/" +
                componentName.getShortClassName());
        // showDialogWhenLostConnection();
    }

    @Override
    public void onServiceDestroyed() {

    }

    @Override
    public void onServiceStart() {

    }

    @Override
    public void onServiceUnableToInitialized() {

    }


    // receiver

    @Override
    public void onReceivedActionData(String actionData) {

    }

    @Override
    public void onReceivedNull() {

    }

    @Override
    public void onReceivedSomething() {

    }
}
