package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.db.circularcounter.CircularCounter;
import com.geodoer.battlesuitcontroller.MainActivity;
import com.geodoer.battlesuitcontroller.R;
import com.geodoer.battlesuitcontroller.controller.GameController;
import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.circularseekbar.CircularSeekBar;
import com.geodoer.phpcontroller.controller.PHPController;
import com.geodoer.phpcontroller.utils.StatusChangeListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BattleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BattleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BattleFragment
        extends
        Fragment
        implements
        View.OnClickListener,
        CircularSeekBar.OnSeekChangeListener {

    private static PHPController PC;

    private CircularCounter meterHp,meterAmmo;

    private Handler handler,stateUpdateHandle;

    private Runnable r,hideWarring,stateUpdate,setShottingFlag;

    private boolean isAutoRun;

    private LinearLayout battleLL;

    private TextView txtBleState,txtLoading,txtGetPname;

    private ImageView ivWarning;

    private Vibrator mVibrator;

    private MediaPlayer mPlayer;

    private int tVibrattion=80;

    private OnFragmentInteractionListener mListener;

    private GameController gc;

    private static final String aGameId = "aGameId";
    private static final String aPlayerId = "aPlayerId";
    private static final String aSetHP = "aSetHP";
    private static final String aSetAmmo = "aSetAmmo";
    private static final String aGameTime = "aGameTime";
    private static final String aPlayerName = "aPlayerName";

    long mGameId;
    int mPlayerId;
    int mSetHP;
    int mSetAmmo;
    int mGameTime;
    String mPlayerName;
    long tNow;

    public static BattleFragment newInstance(
    ) {
        BattleFragment fragment = new BattleFragment();
        return fragment;
    }


    public static BattleFragment newInstance(
            long gameId,
            int playerId,
            int sHp,
            int sAmmo,
            int gTime,
            String pName) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putLong(aGameId, gameId);
        args.putInt(aPlayerId, playerId);
        args.putInt(aSetHP, sHp);
        args.putInt(aSetAmmo, sAmmo);
        args.putInt(aGameTime, gTime);
        args.putString(aPlayerName, pName);
        fragment.setArguments(args);
        return fragment;
    }

    public BattleFragment() {
        // Required empty public constructor


        handler = new Handler();
        r = new Runnable(){
            int currV = 0;
            boolean go = true;
            public void run(){
                if(currV == 60 && go)
                    go = false;
                else if(currV == -60 && !go)
                    go = true;

                if(go)
                    currV++;
                else
                    currV--;

                meterAmmo.setValues(currV, currV*2, currV*3);
                meterHp.setValues(currV, currV * 2, currV * 3);
                handler.postDelayed(this, 50);
            }
        };

        hideWarring=new Runnable(){
            @Override
            public void run() {
                ivWarning.setVisibility(View.INVISIBLE);
                mVibrator.vibrate(tVibrattion);
            }
        };

        stateUpdate = new Runnable() {
            @Override
            public void run() {
                battleLL.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.GONE);
            }
        };

        setShottingFlag= new Runnable(){

            @Override
            public void run() {


                setShootingFlag();

            }
        };
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.setIsBattling(true);

        // 震動元件
        mVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        if (getArguments() != null) {
            mGameId = getArguments().getLong(aGameId);
            mPlayerId = getArguments().getInt(aPlayerId);
            mSetHP = getArguments().getInt(aSetHP);
            mSetAmmo = getArguments().getInt(aSetAmmo);
            mGameTime = getArguments().getInt(aGameTime);
            mPlayerName = getArguments().getString(aPlayerName);

            Log.wtf("args", "=====================");
            Log.wtf("args", "gameID=" + mGameId);
            Log.wtf("args", "mPlayerId=" + mPlayerId);
            Log.wtf("args", "mSetHP=" + mSetHP);
            Log.wtf("args", "mSetAmmo=" + mSetAmmo);
            Log.wtf("args", "mGameTime=" + mGameTime);
            Log.wtf("args", "mPlayerName=" + mPlayerName);
            Log.wtf("args", "=====================");

            // PC =new ParseController(getActivity().getApplicationContext());
        }

        PC=new PHPController();

        StatusChangeListener SCL = new StatusChangeListener()
        {
            @Override
            public void onHPChanged(int value)
            {
                Log.wtf("bat","HP have been changed to "+value);
                meterHp.setValues(value,value*2,value*3);
            }

            @Override
            public void onAMMOChanged(int value)
            {
                Log.wtf("bat","AMMO have been changed to "+value);
                meterAmmo.setValues(value,value*2,value*3);
            }
        };

        PC.addSCListener(SCL);
        PC.startService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupcompements();

        //   connectGame.connect(mGameId);

        //if(joinGame.join(mPlayerId,mPlayerName))
        handler.postDelayed(stateUpdate,500);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity()
//                .registerReceiver(ble_activity_receiver,
//                        ble_activity_receiverIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(ble_activity_receiver);
        isAutoRun=false;
        handler.removeCallbacks(r);
        MainActivity.setIsBattling(false);
    }

    private void setupcompements() {
        if (getView() != null) {
            battleLL=(LinearLayout)getView().findViewById(R.id.battleLL);
            battleLL.setVisibility(View.INVISIBLE);

            ivWarning=(ImageView)getView().findViewById(R.id.ivWarning);
            ivWarning.setVisibility(View.INVISIBLE);

            txtGetPname = (TextView) getView().findViewById(R.id.txtGetPname);
            txtGetPname.setText(mPlayerName+"["+ PC.getGameId()+"]");

            txtBleState=(TextView)getView().findViewById(R.id.txtBleState);
            txtBleState.setText("no data");

            txtLoading=(TextView)getView().findViewById(R.id.txtLoading);
            txtLoading.setText("Loading");
            txtLoading.setVisibility(View.VISIBLE);

            CircularSeekBar barAmmo = (CircularSeekBar) getView().findViewById(R.id.barAmmo);
            barAmmo.setMaxProgress(mSetAmmo);
            barAmmo.setProgress(0);
            barAmmo.setBarWidth(35);
            barAmmo.invalidate();
            barAmmo.setProgressColor(getResources().getColor(R.color.bar_color_ammo));
            barAmmo.setSeekBarChangeListener(this);

            CircularSeekBar barHp = (CircularSeekBar) getView().findViewById(R.id.barHp);
            barHp.setMaxProgress(mSetHP);
            barHp.setProgress(0);
            barHp.setBarWidth(35);
            barHp.invalidate();
            barHp.setProgressColor(getResources().getColor(R.color.bar_color_hp));
            barHp.setSeekBarChangeListener(this);

            isAutoRun = false;

          //  Button btnMore = (Button) getView().findViewById(R.id.btnMore);
          //  Button btnLess = (Button) getView().findViewById(R.id.btnLess);
           // Button btnAuto = (Button) getView().findViewById(R.id.btnAuto);

          //  btnMore.setOnClickListener(this);
          //  btnLess.setOnClickListener(this);
         //  btnAuto.setOnClickListener(this);

            String[] colorsAmmo = getResources().getStringArray(R.array.colors_AMMO);
            String[] colorsHp = getResources().getStringArray(R.array.colors_HP);

            meterHp = (CircularCounter) getView().findViewById(R.id.meter);
            meterAmmo = (CircularCounter) getView().findViewById(R.id.meter_ammo);

            meterHp.setValues(mSetHP, mSetHP * 2, mSetHP * 3);
            meterHp.setRange(mSetHP)
                    .setFirstWidth(getResources().getDimension(R.dimen.first))
                    .setFirstColor(Color.parseColor(colorsHp[0]))
                    .setSecondWidth(getResources().getDimension(R.dimen.second))
                    .setSecondColor(Color.parseColor(colorsHp[1]))
                    .setThirdWidth(getResources().getDimension(R.dimen.third))
                    .setThirdColor(Color.parseColor(colorsHp[2]))
                    .setBackgroundColor(Color.parseColor(colorsHp[3]));

            meterAmmo.setValues(mSetAmmo, mSetAmmo * 2,mSetAmmo*3);
            meterAmmo.setRange(mSetAmmo)
                    .setFirstWidth(getResources().getDimension(R.dimen.first))
                    .setFirstColor(Color.parseColor(colorsAmmo[0]))
                    .setSecondWidth(getResources().getDimension(R.dimen.second))
                    .setSecondColor(Color.parseColor(colorsAmmo[1]))
                    .setThirdWidth(getResources().getDimension(R.dimen.third))
                    .setThirdColor(Color.parseColor(colorsAmmo[2]))
                    .setBackgroundColor(Color.parseColor(colorsAmmo[3]));
        }
    }

    private void reduceMeter(CircularCounter meter){
        meter.setValues(meter.getValue1() - 1,
                meter.getValue2() - 2,
                meter.getValue3() - 3);
    }

    private void addMeter(CircularCounter meter){
        meter.setValues(meter.getValue1() + 1,
                meter.getValue2() + 2,
                meter.getValue3() + 3);
    }

    private void addMeter(CircularCounter meter,int v1,int v2,int v3){
        meter.setValues(v1,
                v2,
                v3);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btnLess:
//                reduceMeter(meterAmmo);
//                reduceMeter(meterHp);
//                break;
//
//            case R.id.btnMore:
//                addMeter(meterAmmo);
//                addMeter(meterHp);
//                break;
//
//            case R.id.btnAuto:
//                if(!isAutoRun){
//                    isAutoRun=true;
//                    handler.postDelayed(r,50);
//                }else{
//                    isAutoRun=false;
//                    handler.removeCallbacks(r);
//                }
//                break;
//        }
    }

    @Override
    public void onProgressChange(CircularSeekBar view, int newProgress) {
        switch (view.getId()){
            case R.id.barAmmo:
                addMeter(meterAmmo, newProgress, newProgress * 2, newProgress * 3);
                break;
            case R.id.barHp:
                addMeter(meterHp,newProgress,newProgress*2,newProgress*3);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setShootingFlag(){

//        if(PC.getAmmo_t()==1)
//            // 開槍狀態=0
//            PC.Player.updateInfo(
//                    new ParseController
//                            .updateInfoCallback(
//                            ParseController
//                                    .PlayerStatus.AMMO_t
//                            , 0) {
//                        @Override
//                        public void run(boolean result) {
//                            if (result){
//                                Log.wtf("PARSE", "update Ammo_t 0 success");
//                            }
//                            else Log.wtf("PARSE", "update Ammo_t 0 fail");
//                        }
//                    });
//        handler.removeCallbacks(setShottingFlag);
    }

    private void reduceHp(){
        // 血環扣血
//        if(meterHp.getValue1()>0)
//            // 雲端扣血
//            PC.Player
//                    .updateInfo(new ParseController
//                            .updateInfoCallback(ParseController
//                            .PlayerStatus.HP, -1) {
//                        @Override
//                        public void run(boolean result) {
//                            if (result) {
//                                Log.wtf("PARSE", "update HP -1 success");
//                                mVibrator.vibrate(tVibrattion);
//                                reduceMeter(meterHp);
//                            } else Log.wtf("PARSE", "update HP -1 fail");
//                        }
//                    });
    }

    // 接收BLE廣播
    private final BroadcastReceiver ble_activity_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            //final String bString = BleCustomDialog.mAction_servicestate;
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(data!=null)
                {
                    String temp = data.substring(0, 2);
                    txtBleState.setText(temp+System.currentTimeMillis()/(60*60*60));

                    // 被瞄準
                    if(temp.equals("AA")) {
                        ivWarning.setVisibility(View.VISIBLE);
                        handler.postDelayed(hideWarring, 1500);

                        tNow=System.currentTimeMillis();

//                        // 檢查誰開槍
//                        PC.getWhoShooting(new ParseController.getWhoShootCallback() {
//                            @Override
//                            public void run(boolean result, ArrayList<Integer> list) {
//                                if(result){
//                                    if(!list.isEmpty()){
//                                        Log.wtf("PARSE", list.toString()+"Now="+tNow+" this is pId="+mPlayerId);
//                                        if(list.contains(mPlayerId)){
//                                            int i;
//                                            for(i=0;i<list.size();i++){
//                                                //Log.wtf("list", "list item=" + list.get(i));
//                                                if(list.get(i)==mPlayerId){
//                                                    list.remove(i);
//                                                    //Log.wtf("list", "remove itself.");
//                                                }}
//                                        }else {
//
//                                            Log.wtf("PARSE", "attacked by pId=" + list.toString());
//
//
//                                            //reduceHp();
//
//                                        }
//
//                                    }else
//                                        Log.wtf("PARSE", "沒有配對到槍手");
//                                }
//                            }
//                        });

                        // 被擊中
                    }else if(temp.equals("BB")){

                        // 如果還有血
                        if(meterHp.getValue1()>0) {


                            reduceHp();

                        }else
                            // 沒血
                            Toast.makeText(getActivity(),
                                    "你死啦！",
                                    Toast.LENGTH_SHORT).show();
                        // 開槍
                    }else if(temp.equals("CC")){
                        //
                        if(meterAmmo.getValue1()>0) {

                            mVibrator.vibrate(tVibrattion);
                            reduceMeter(meterAmmo);

//                            if(PC.Player.getAmmo_t()==0) {
//                                // 開槍狀態=1
//                                PC.Player
//                                        .updateInfo(
//                                                new ParseController
//                                                        .updateInfoCallback(
//                                                        ParseController
//                                                                .PlayerStatus.AMMO_t
//                                                        , 1) {
//                                                    @Override
//                                                    public void run(boolean result) {
//                                                        if (result)
//                                                            Log.wtf("PARSE", "update Ammo_t 1 success");
//                                                        else
//                                                            Log.wtf("PARSE", "update Ammo_t 1 fail");
//                                                    }
//                                                });
                        }else{

                            handler.postDelayed(setShottingFlag,10);
                        }
                        Log.wtf("","");
                        // 更新子彈量
//                            PC.Player
//                                    .updateInfo(
//                                            new ParseController
//                                                    .updateInfoCallback(
//                                                    ParseController
//                                                            .PlayerStatus.AMMO
//                                                    , -1) {
//                                                @Override
//                                                public void run(boolean result) {
//                                                    if (result) {
//                                                        Log.wtf("PARSE", "update Ammo -1 success");
//                                                    } else Log.wtf("PARSE", "update Ammo -1 fail");
//                                                }
//                                            });

                    }else
                        Toast.makeText(getActivity(),
                                "你沒子彈啦！",
                                Toast.LENGTH_SHORT).show();
                }
            }
        }

    };

//    //
//private static IntentFilter ble_activity_receiverIntentFilter()
//        {
//final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BleCustomDialog.mAction_servicestate);
//        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
//        return intentFilter;
//        }
}

