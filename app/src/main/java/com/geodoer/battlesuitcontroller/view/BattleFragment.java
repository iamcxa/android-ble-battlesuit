package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.db.circularcounter.CircularCounter;
import com.geodoer.battlesuitcontroller.MainActivity;
import com.geodoer.battlesuitcontroller.R;
import com.geodoer.battlesuitcontroller.controller.GameController;
import com.geodoer.battlesuitcontroller.util.BscUtils;
import com.geodoer.circularseekbar.CircularSeekBar;


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
        GameController.whenPlayerAct,
        GameController.whenSucceed{

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

//    private static PHPController PC;
//    private GameController gc;

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


            }
        };
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.setIsBattling(true);
        MainActivity.getThisGC().setWhenPlayerActTarget(this);
        MainActivity.getThisGC().setWhenSucceedTarget(this);

//        gc = new GameController(getActivity());
//        gc.setWhenPlayerActTarget(this);
//        gc.setWhenSucceedTarget(this);
//        //PC = MainActivity.getThisGC().getPc();
//
//        PC=gc.getPc();

        // 震動元件
        mVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);

        if (getArguments() != null) {
            mGameId = getArguments().getLong(aGameId);
            mPlayerId = getArguments().getInt(aPlayerId);
            mSetHP = getArguments().getInt(aSetHP);
            mSetAmmo = getArguments().getInt(aSetAmmo);
            mGameTime = getArguments().getInt(aGameTime);
            mPlayerName = getArguments().getString(aPlayerName);

            // PC =new ParseController(getActivity().getApplicationContext());
        }else
        {
            mGameId = MainActivity.getThisGC().getGame().getGameId();
            mSetHP = MainActivity.getThisGC().getGame().getSetHp();
            mSetAmmo =MainActivity.getThisGC().getGame().getSetAmmo();
            mGameTime = MainActivity.getThisGC().getGame().getGameTime();
            mPlayerId = MainActivity.getThisGC().getPlayer().getPlayerId();
            mPlayerName = MainActivity.getThisGC().getPlayer().getPlayerName();

//            gc.getGame().setGameId(mGameId);
//            gc.getGame().setSetHp(mSetHP);
//            gc.getGame().setSetAmmo(mSetAmmo);
//            gc.getGame().setGameTime(mGameTime);
//            gc.getPlayer().setPlayerId(mPlayerId);
//            gc.getPlayer().setPlayerName(mPlayerName);
        }

        Log.wtf("args", "=====================");
        Log.wtf("args", "gameID=" + mGameId);
        Log.wtf("args", "mPlayerId=" + mPlayerId);
        Log.wtf("args", "mSetHP=" + mSetHP);
        Log.wtf("args", "mSetAmmo=" + mSetAmmo);
        Log.wtf("args", "mGameTime=" + mGameTime);
        Log.wtf("args", "mPlayerName=" + mPlayerName);
        Log.wtf("args", "=====================");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        setupompements();
        //
        handler.postDelayed(stateUpdate,500);
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // getActivity().unregisterReceiver(ble_activity_receiver);
        isAutoRun=false;
        MainActivity.getThisGC().stopPcService();
        handler.removeCallbacks(r);
        MainActivity.setIsBattling(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setupompements() {
        if (getView() != null) {
            battleLL=(LinearLayout)getView().findViewById(R.id.battleLL);
            battleLL.setVisibility(View.INVISIBLE);

            ivWarning=(ImageView)getView().findViewById(R.id.ivWarning);
            ivWarning.setVisibility(View.INVISIBLE);

            txtGetPname = (TextView) getView().findViewById(R.id.txtGetPname);
            txtGetPname.setText("[" + mPlayerName + "]");

            txtBleState=(TextView)getView().findViewById(R.id.txtBleState);
            txtBleState.setText("no data yet");

            txtLoading=(TextView)getView().findViewById(R.id.txtLoading);
            txtLoading.setText("Loading");
            txtLoading.setVisibility(View.VISIBLE);

            CircularSeekBar barAmmo = (CircularSeekBar) getView().findViewById(R.id.barAmmo);
            barAmmo.setMaxProgress(mSetAmmo);
            barAmmo.setProgress(0);
            barAmmo.setBarWidth(35);
            barAmmo.invalidate();
            barAmmo.setProgressColor(getResources().getColor(R.color.bar_color_ammo));
            //barAmmo.setSeekBarChangeListener(this);
            barAmmo.setVisibility(View.GONE);

            CircularSeekBar barHp = (CircularSeekBar) getView().findViewById(R.id.barHp);
            barHp.setMaxProgress(mSetHP);
            barHp.setProgress(0);
            barHp.setBarWidth(35);
            barHp.invalidate();
            barHp.setProgressColor(getResources().getColor(R.color.bar_color_hp));
            // barHp.setSeekBarChangeListener(this);
            barHp.setVisibility(View.GONE);

            isAutoRun = false;

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

            meterAmmo.setValues(mSetAmmo, mSetAmmo * 2, mSetAmmo * 3);
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
    public void changeHP(int value) {
        if(value>0)
            this.meterHp.setValues(value,value*2,value*3);
        else if((value<=0)) {
            showDialogWhenPlayerDie();
            MainActivity.getThisGC().getPc().clearSCListener();
        }

    }

    @Override
    public void changeAmmo(int value) {

        if(value>0)
            this.meterAmmo.setValues(value,value*2,value*3);
        else {
            showDialogWhenPlayerOutOfAmmo();
            MainActivity.getThisGC().getPc().clearSCListener();
        }
    }

    @Override
    public void hostSucceed() {

    }

    @Override
    public void connectSucceed() {

    }

    @Override
    public void joinSucceed() {
        MainActivity.getThisGC().getPc().startService(getActivity());
        MainActivity.getThisGC().setWhenPlayerActTarget(this);
    }

    @Override
    public void hostFailed() {

    }

    @Override
    public void connectFailed() {

    }

    @Override
    public void joinFailed() {

    }

    private AlertDialog showDialogWhenPlayerOutOfAmmo(){
        return new AlertDialog.Builder(getActivity())
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("你沒子彈！")
                .setCancelable(false)
                .setNegativeButton("離開遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BattleFragment.this.onDestroy();
                        getActivity().getParent().finish();
                    }
                })
                .setPositiveButton("再玩一場", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BscUtils.switchFragment(getActivity(), MainFragment.newInstance("", ""));
                        BattleFragment.this.onDestroy();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenPlayerDie(){
        return new AlertDialog.Builder(getActivity())
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("你陣亡啦！")
                .setCancelable(false)
                .setNegativeButton("離開遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BattleFragment.this.onDestroy();
                        getActivity().getParent().finish();
                    }
                })
                .setPositiveButton("再玩一場", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BscUtils.switchFragment(getActivity(), MainFragment.newInstance("", ""));
                        BattleFragment.this.onDestroy();
                    }
                }).show();
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
}

