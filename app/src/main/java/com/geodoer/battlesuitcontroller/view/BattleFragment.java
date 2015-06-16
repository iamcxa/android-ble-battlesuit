package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.db.circularcounter.CircularCounter;
import com.geodoer.battlesuitcontroller.R;
import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.circularseekbar.CircularSeekBar;
import com.geodoer.parsecontroller.controller.GameIdmaker;
import com.geodoer.parsecontroller.controller.ParseController;


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

    private ParseController PC;

    private CircularCounter meterHp;
    private CircularCounter meterAmmo;

    private String[] colorsAmmo;
    private String[] colorsHp;

    private Handler handler;

    private Runnable r,hideWarring;

    private Button btnMore;
    private Button btnLess;
    private Button btnAuto;

    private boolean isAutoRun;

    private CircularSeekBar barAmmo;
    private CircularSeekBar barHp;

    private TextView txtGetPname,txtBleState;

    private ImageView ivWarning;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String arg1 = "sHp";
    private static final String arg2 = "sAmmo";
    private static final String arg3 = "gTime";
    private static final String arg4 = "pName";

    // TODO: Rename and change types of parameters
    private int mArg1,mArg2,mArg3;
    private String mArg4;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static BattleFragment newInstance(int sHp,
                                             int sAmmo,
                                             int gTime,
                                             String pName) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putInt(arg1, sHp);
        args.putInt(arg2, sAmmo);
        args.putInt(arg3, gTime);
        args.putString(arg4, pName);
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
                meterHp.setValues(currV, currV*2, currV*3);
                handler.postDelayed(this, 50);
            }
        };

        hideWarring=new Runnable(){

            @Override
            public void run() {
                ivWarning.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArg1 = getArguments().getInt(arg1);
            mArg2 = getArguments().getInt(arg2);
            mArg3 = getArguments().getInt(arg3);
            mArg4 = getArguments().getString(arg4);

            Log.wtf("args","hp="+mArg1+",ammo="+mArg2+",time="+mArg3+",name="+mArg4);

            PC =new ParseController(getActivity().getApplicationContext());
            PC.setGame(2, mArg1, mArg2, new ParseController.setGameCallback(GameIdmaker.newId()) {
                @Override
                public void run(boolean result) {
                    if (result) Log.wtf("PARSE", "set Game success");
                    else Log.wtf("PARSE", "set Game fail");
                }
            });
        }

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
        getActivity()
                .registerReceiver(ble_activity_receiver,
                        ble_activity_receiverIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(ble_activity_receiver);
        isAutoRun=false;
        handler.removeCallbacks(r);
    }

    private void setupcompements() {
        if (getView() != null) {
            ivWarning=(ImageView)getView().findViewById(R.id.ivWarning);
            ivWarning.setVisibility(View.INVISIBLE);

            txtGetPname = (TextView)getView().findViewById(R.id.txtGetPname);
            txtGetPname.setText(mArg4);

            txtBleState=(TextView)getView().findViewById(R.id.txtBleState);
            txtBleState.setText("no data");

            barAmmo = (CircularSeekBar) getView().findViewById(R.id.barAmmo);
            barAmmo.setMaxProgress(mArg2);
            barAmmo.setProgress(0);
            barAmmo.setBarWidth(35);
            barAmmo.invalidate();
            barAmmo.setProgressColor(getResources().getColor(R.color.bar_color_ammo));
            barAmmo.setSeekBarChangeListener(this);

            barHp = (CircularSeekBar) getView().findViewById(R.id.barHp);
            barHp.setMaxProgress(mArg1);
            barHp.setProgress(0);
            barHp.setBarWidth(35);
            barHp.invalidate();
            barHp.setProgressColor(getResources().getColor(R.color.bar_color_hp));
            barHp.setSeekBarChangeListener(this);

            isAutoRun = false;

            btnMore = (Button) getView().findViewById(R.id.btnMore);
            btnLess = (Button) getView().findViewById(R.id.btnLess);
            btnAuto = (Button) getView().findViewById(R.id.btnAuto);

            btnMore.setOnClickListener(this);
            btnLess.setOnClickListener(this);
            btnAuto.setOnClickListener(this);

            colorsAmmo = getResources().getStringArray(R.array.colors_AMMO);
            colorsHp = getResources().getStringArray(R.array.colors_HP);

            meterHp = (CircularCounter) getView().findViewById(R.id.meter);
            meterAmmo = (CircularCounter) getView().findViewById(R.id.meter_ammo);

            meterAmmo.setValues(mArg2,mArg2*2,mArg2*3);
            meterAmmo.setRange(mArg2);

            meterHp.setValues(mArg1, mArg1 * 2, mArg1 * 3);
            meterHp.setRange(mArg1)

                    .setFirstWidth(getResources().getDimension(R.dimen.first))
                    .setFirstColor(Color.parseColor(colorsHp[0]))

                    .setSecondWidth(getResources().getDimension(R.dimen.second))
                    .setSecondColor(Color.parseColor(colorsHp[1]))

                    .setThirdWidth(getResources().getDimension(R.dimen.third))
                    .setThirdColor(Color.parseColor(colorsHp[2]))

                    .setBackgroundColor(Color.parseColor(colorsHp[3]));

            meterAmmo.setFirstWidth(getResources().getDimension(R.dimen.first))
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
        switch (v.getId()){
            case R.id.btnLess:
                reduceMeter(meterAmmo);
                reduceMeter(meterHp);
                break;

            case R.id.btnMore:
                addMeter(meterAmmo);
                addMeter(meterHp);
                break;

            case R.id.btnAuto:
                if(!isAutoRun){
                    isAutoRun=true;
                    handler.postDelayed(r,50);
                }else{
                    isAutoRun=false;
                    handler.removeCallbacks(r);
                }
                break;
        }
    }

    @Override
    public void onProgressChange(CircularSeekBar view, int newProgress) {
        switch (view.getId()){
            case R.id.barAmmo:
                addMeter(meterAmmo,newProgress,newProgress*2,newProgress*3);
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


    private final BroadcastReceiver ble_activity_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            final String bString = BleCustomDialog.mAction_servicestate;
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(data!=null)
                {
                    String temp = data.substring(0, 2);
                    txtBleState.setText(temp+System.currentTimeMillis()/(60*60*60));

                    // 被瞄準
                    if(temp.equals("AA")){
                        ivWarning.setVisibility(View.VISIBLE);
                        handler.postDelayed(hideWarring,1500);

                        // 被擊中
                    }else if(temp.equals("BB")){
                        if(meterHp.getValue1()>0) {
                            reduceMeter(meterHp);

                            PC.Player.updateInfo(new ParseController.updateInfoCallback(PC.Player.HP, -1) {
                                @Override
                                public void run(boolean result) {
                                    if (result) Log.wtf("PARSE", "update HP -1 success");
                                    else Log.wtf("PARSE", "update HP -1 fail");
                                }
                            });
                        }else
                            Toast.makeText(getActivity(),
                                    "你死啦！",
                                    Toast.LENGTH_SHORT).show();

                        // 開槍
                    }else if(temp.equals("CC")){
                        if(meterAmmo.getValue1()>0) {
                            reduceMeter(meterAmmo);

                            PC.Player.updateInfo(new ParseController.updateInfoCallback(PC.Player.AMMO, -1) {
                                @Override
                                public void run(boolean result) {
                                    if (result)
                                        Log.wtf("PARSE", "update Ammo -1 success");
                                    else Log.wtf("PARSE", "update Ammo -1 fail");
                                }
                            });

                        }else
                            Toast.makeText(getActivity(),
                                    "你沒子彈啦！",
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private static IntentFilter ble_activity_receiverIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleCustomDialog.mAction_servicestate);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}

