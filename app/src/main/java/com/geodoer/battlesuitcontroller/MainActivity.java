package com.geodoer.battlesuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.controller.GameController;
import com.geodoer.battlesuitcontroller.util.BscUtils;
import com.geodoer.battlesuitcontroller.view.BattleFragment;
import com.geodoer.battlesuitcontroller.view.HostFragment;
import com.geodoer.battlesuitcontroller.view.MainFragment;
import com.geodoer.battlesuitcontroller.view.SettingsActivity;
import com.geodoer.bluetoothcontroler.BcUtils;
import com.geodoer.bluetoothcontroler.controller.BleActionReceiver;
import com.geodoer.bluetoothcontroler.controller.BleController;
import com.geodoer.bluetoothcontroler.service.GeoBleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import at.markushi.ui.CircleButton;

import static com.geodoer.battlesuitcontroller.util.BscUtils.logTag;
import static com.geodoer.battlesuitcontroller.util.BscUtils.switchFragment;


public class MainActivity
        extends
        AppCompatActivity
        implements
        View.OnClickListener,
        BleActionReceiver.whenReceivedBleAction,
        BleController.whenRunningBleService,
        GeoBleService.whenServiceStateChanged,
        MainFragment.OnFragmentInteractionListener,
        BattleFragment.OnFragmentInteractionListener{

    private final static int
            MODE_CODE_COMPONENT_PARPARE_TO_WAITING = 0,
            MODE_CODE_COMPONENT_WAISTING_IS_OVER = 1,
            MODE_CODE_COMPONENT_FADING_OUT_END = 2;

    private CircleButton
            //btnHost,
            //btnJoin,
            cbMainLogo;

    private FrameLayout
            container;

    private Toolbar
            toolbar;

    private TextView
            txtWaitingIndicator,
            txtShowFakeLoadingMsg;

    private Handler
            handler,
            handlerForMainLogoFading,
            handlerForUi;

    private Runnable
            rCheckBle,
            rCheckConnectionAlive,
            rWaitingForConnection,
            rFakeWaitingMsg,
            rAutoConnectToDevice,
            rMainLogoFading,
            rMainLogoFadeOut;

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
            arrayListFakeMsg = null,
            arrayBleDevices = null;

    private static boolean
            isBattling = false;

    private BleActionReceiver
            mBleActionReceiver;

    private BleController
            bc;

    protected static GameController
            gc;

    //
    // AppCompatActivity Overrides
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);

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
            //moveTaskToBack(true);
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

        container = (FrameLayout) findViewById(R.id.container);

//        btnHost = (CircleButton) findViewById(R.id.btnHost);
//        btnJoin = (CircleButton) findViewById(R.id.btnJoin);
//        btnHost.setOnClickListener(this);
//        btnJoin.setOnClickListener(this);

        cbMainLogo = (CircleButton) findViewById(R.id.cbMainLogo);
        cbMainLogo.setOnClickListener(this);

        txtWaitingIndicator = (TextView) findViewById(R.id.txtWaitingIndicator);
        txtShowFakeLoadingMsg =(TextView) findViewById(R.id.txtShowFakeLoadingMsg);

        mBleActionReceiver = new BleActionReceiver();
        mBleActionReceiver.setWhenReceivedBleActionTarget(this);

        bc = new BleController(getApplicationContext());
        bc.setBleServiceRunningTarget(this);

        GeoBleService gbs = new GeoBleService();
        gbs.setServiceStateChangedTarget(this);

        initAllThings();
    }

    private void initAllThings(){
        // 還原預設值
        Log.wtf(BscUtils.logTag, "----------- initAllThings -----------");

        gc = new GameController(this);

        arrayBleDevices = new ArrayList<>();
        arrayBleDevices.clear();

        handlerForUi = new Handler();
        handler = new Handler();
        handlerForMainLogoFading = new Handler();

        // 塞入假訊息
        initFakeLoadingMsg();

        // 重設變數
        initVariable();

        // 設定ui物件屬性
        initComponentsWhenFindingDevicesStart();

        // 清空queue
        removeAllRunnableFromQueue();

        // 塞入runnable
        handler.post(setRunCheckBle());
        handlerForUi.postDelayed(setRunWaiting(), 300);
        handlerForUi.postDelayed(setRunFakeWaitingMsg(), 500);
        handlerForMainLogoFading.postDelayed(setRunFading(), 100);
    }

    private void initVariable(){
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
    }

    private void initComponentsWhenFindingDevicesStart(){

        // 藏起 toolbar
        toolbar.setVisibility(View.GONE);

        // 藏起frame
        container.setVisibility(View.INVISIBLE);

        //  btnHost.setVisibility(View.INVISIBLE);
        //  btnJoin.setVisibility(View.INVISIBLE);

        controlTxtView(MODE_CODE_COMPONENT_PARPARE_TO_WAITING);

        controlMainLogo(MODE_CODE_COMPONENT_PARPARE_TO_WAITING);
    }

    private void initFakeLoadingMsg(){
        //
        String[] fakeMsgs
                = getResources().getStringArray(R.array.array_fake_loading_msg);
        Log.wtf(logTag, "size=" + fakeMsgs.length +
                "msg=" + Arrays.toString(fakeMsgs));
        arrayListFakeMsg = new ArrayList<>();
        arrayListFakeMsg.clear();
        Collections.addAll(arrayListFakeMsg, fakeMsgs);
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
            if(rFakeWaitingMsg!=null)
                handlerForUi.removeCallbacks(rFakeWaitingMsg);
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


    private void setComponentsWhenFindingDevicesEnd(){
//
        controlMainLogo(MODE_CODE_COMPONENT_WAISTING_IS_OVER);
        //
        isMainLogoFading = false;
    }

    private void setComponentsWhenFadingOutEnd(){
        //
        toolbar.setVisibility(View.GONE);

        //
        // btnHost.setVisibility(View.VISIBLE);
        // btnJoin.setVisibility(View.VISIBLE);


        // fading out 結束才顯示frame
        container.setVisibility(View.VISIBLE);
        switchFragment(this, MainFragment.newInstance("", ""));

        controlTxtView(MODE_CODE_COMPONENT_FADING_OUT_END);

        controlMainLogo(MODE_CODE_COMPONENT_FADING_OUT_END);
    }

    private void controlTxtView(int mode){
        if (mode == MODE_CODE_COMPONENT_PARPARE_TO_WAITING) {
            // 顯示 "..."
            txtWaitingIndicator.setVisibility(View.VISIBLE);
            txtWaitingIndicator.setText("...");
            txtWaitingIndicator.setAlpha((float) 1);
            //
            txtShowFakeLoadingMsg.setVisibility(View.VISIBLE);
            txtShowFakeLoadingMsg.setText("正在準備");
            txtShowFakeLoadingMsg.setAlpha((float)1);
        }else
        if(mode == MODE_CODE_COMPONENT_WAISTING_IS_OVER){

            txtShowFakeLoadingMsg.setText("開啟作弊模式");

        }else
        if(mode == MODE_CODE_COMPONENT_FADING_OUT_END) {
            //
            // 不顯示任何東西
            txtWaitingIndicator.setVisibility(View.GONE);
            txtWaitingIndicator.setText("...");

            txtShowFakeLoadingMsg.setVisibility(View.GONE);
            txtShowFakeLoadingMsg.setText("正在準備");
        }
    }

    private void controlMainLogo(int mode) {
        if (mode == MODE_CODE_COMPONENT_PARPARE_TO_WAITING) {
            // 設定logo->顯示/藍色/半透明/不能按/未按下
            cbMainLogo.setVisibility(View.VISIBLE);
            cbMainLogo.setColor(getResources().getColor(R.color.c_deep_sky_blue));
            cbMainLogo.setAlpha((float) 0.5);
            cbMainLogo.setClickable(false);
            cbMainLogo.setPressed(false);
        } else
        if(mode == MODE_CODE_COMPONENT_WAISTING_IS_OVER){
            // 設定logo->顯示/紅色/不透明/不能按/已按下(發光)
            cbMainLogo.setVisibility(View.VISIBLE);
            cbMainLogo.setColor(getResources().getColor(R.color.c_brick_red));
            cbMainLogo.setAlpha((float) 1);
            cbMainLogo.setClickable(false);
            cbMainLogo.setPressed(true);
        }else
        if(mode == MODE_CODE_COMPONENT_FADING_OUT_END) {
            // 設定logo->消失/
            cbMainLogo.setVisibility(View.GONE);
        }
    }

    public static GameController getThisGC(){
        return gc;
    }

    public static boolean isBattling() {
        return isBattling;
    }

    public static void setIsBattling(boolean isBattling) {
        MainActivity.isBattling = isBattling;
    }

    //==============================//
    //           Dialog(s)          //
    //==============================//

    private AlertDialog showDialogWhenGetNoBleSupport(){
        return new AlertDialog.Builder(this)
                .setTitle("問題")
                .setIcon(R.drawable.warning)
                .setMessage("你的裝置不支援藍牙4.0！")
                .setCancelable(false)
                .setPositiveButton("離開遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenFindNoDevices(){
        return new AlertDialog.Builder(this)
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("找不到裝置。\n\n" +
                        "請確定藍芽裝置都有開啟、並且在手機附近，或是將藍芽裝置重啟。")
                .setCancelable(false)
                .setNegativeButton("離開遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("再試一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initAllThings();
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("用模擬器", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllRunnableFromQueue();
                        controlTxtView(MODE_CODE_COMPONENT_WAISTING_IS_OVER);
                        handlerForUi.postDelayed(setRunFadingOut(), 1000);
                        dialog.dismiss();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenPressedBackKey(){
        return new AlertDialog.Builder(this)
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("你按下返回鍵了。你要離開B.S.C嗎？")
                .setCancelable(false)
                .setNegativeButton("離開遊戲", new DialogInterface.OnClickListener() {
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
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("偵測到裝置斷線！\n\n" +
                        "請確定BLE裝置有接上電源、而且在手機附近。")
                .setCancelable(false)
                .setNegativeButton("離開遊戲", new DialogInterface.OnClickListener() {
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

    //
    private Runnable setRunFakeWaitingMsg(){
        return rFakeWaitingMsg = new Runnable(){
            @Override
            public void run() {
                // run initAllThings
                if(isWaiting) {
                    Random seed = new Random();
                    int position = seed.nextInt(arrayListFakeMsg.size());
                    txtShowFakeLoadingMsg.setText(arrayListFakeMsg.get(position));
                    //Log.wtf(logTag, "Seed=" + position + ",msg=" + arrayListFakeMsg.get(position));
                    handlerForUi.postDelayed(this, 700);
                }else
                    handlerForUi.removeCallbacks(this);
                // run end
            }
        };

    }

    // 搜尋Ble裝置時做動
    private Runnable setRunWaiting(){
        return rWaitingForConnection = new Runnable() {
            @Override
            public void run() {
                // run initAllThings
                //
                if (txtWaitingIndicator.getText().equals("..."))
                    txtWaitingIndicator.setText("....");
                else
                if (txtWaitingIndicator.getText().equals("...."))
                    txtWaitingIndicator.setText(".....");
                else
                if (txtWaitingIndicator.getText().equals("....."))
                    txtWaitingIndicator.setText("...");

                //
                if(isWaiting)
                {
                    handlerForUi.postDelayed(this, 300);
                }
                else
                {
                    handlerForUi.removeCallbacks(this);
                    if(rFakeWaitingMsg!=null)
                        handlerForUi.removeCallbacks(rFakeWaitingMsg);
                    setComponentsWhenFindingDevicesEnd();
                    handlerForUi.postDelayed(setRunCheckConnectionAlive(),2500);
                }
                // run end
            }
        };
    }

    // 檢查ble支援狀態
    private Runnable setRunCheckBle(){
        return rCheckBle = new Runnable() {
            boolean isStartedIntent = false;
            @Override
            public void run() {
                // run initAllThings
                int bleStateCode = bc.checkBleState();

                if (bleStateCode==BleController.BLE_STATE_OK)
                {
                    bc.triggerScan();
                    handler.removeCallbacks(this);
                }
                else if (bleStateCode==BleController.BLE_STATE_OFF)
                {
                    if(!isStartedIntent) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, BcUtils.REQUEST_ENABLE_BT);
                        isStartedIntent = true;
                    }
                    handler.postDelayed(this,10000);
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
                // run initAllThings
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
                        handlerForUi.postDelayed(this, 5000);
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
                // run initAllThings
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
        setComponentsWhenFindingDevicesEnd();
        return rMainLogoFadeOut = new Runnable() {
            int currV = ((int)cbMainLogo.getAlpha())*100;
            float alpha;
            boolean go = true;
            @Override
            public void run() {
                // run initAllThings
                alpha = ((float) currV) / 100;

                if(go) currV--;

                if (currV >= 1)
                    go = true;
                else
                if (currV == 0 )
                    go = false;

                cbMainLogo.setAlpha(alpha);
                txtWaitingIndicator.setAlpha(alpha);
                txtShowFakeLoadingMsg.setAlpha(alpha);
                //Log.wtf(logTag, "fadout currV=" + currV + " currAlpha=" + cbMainLogo.getAlpha());
                if(go)
                    // 仍在fading
                    handlerForMainLogoFading.postDelayed(this, 20);
                else {
                    // 結束
                    handlerForMainLogoFading.removeCallbacks(this);
                    setComponentsWhenFadingOutEnd();
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
                // run initAllThings
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
                switchFragment(this, MainFragment.newInstance("", ""));
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

            //
            txtWaitingIndicator.setText(
                    "OK with Board " + BscUtils.deviceName);

            //
            txtShowFakeLoadingMsg.setText("準備完成");

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
        //Log.wtf(logTag, "onReceived=" + actionData);

        if(isBattling()) {
            //Log.wtf("MA", "now is Battling!");
            switch (actionData) {
                case "AA":
                    //Log.wtf("MA", "Battling received AA");
                    final Intent intent = new Intent(BscUtils.ACTION_DATA_AVAILABLE);
                    intent.putExtra(BscUtils.EXTRA_DATA, "AA");
                    sendBroadcast(intent);

                    break;
                case "BB":
                    //Log.wtf("MA", "Battling received BB");
                    final Intent intent1 = new Intent(BscUtils.ACTION_DATA_AVAILABLE);
                    intent1.putExtra(BscUtils.EXTRA_DATA, "BB");
                    sendBroadcast(intent1);

                    break;
                case "CC":
                    //Log.wtf("MA", "Battling received CC");
                    final Intent intent2 = new Intent(BscUtils.ACTION_DATA_AVAILABLE);
                    intent2.putExtra(BscUtils.EXTRA_DATA, "CC");
                    sendBroadcast(intent2);
                    break;
            }
        }
    }

    @Override
    public void onReceivedNull() {

    }

    @Override
    public void onReceivedSomething(String data) {

        if(data.equals("START_TO_SWITCH_FRAGMENT_TO_BATTLE")) {
            switchFragment(this,
                    BattleFragment.newInstance());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
