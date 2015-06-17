package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.parsecontroller.controller.ParseController;

import at.markushi.ui.CircleButton;

import static com.geodoer.battlesuitcontroller.util.utils.switchFragment;

public class HostFragment
        extends
        Fragment
        implements
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText etxtPname;

    private int vHp,vAmmo,vTime;

    private ParseController PC;

    public static HostFragment newInstance(String param1, String param2) {
        HostFragment fragment = new HostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host, container, false);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDone:
                if(!BleFragment.getDevice().equals("[device_status]")) {

                    String pName = etxtPname.getText().toString();
                    if (pName.isEmpty())
                        pName = "player_host";

                    // 切換
                    switchFragment(getActivity(), BattleFragment
                            .newInstance(vHp,
                                    vAmmo,
                                    vTime,
                                    pName,
                                    1,
                                    0,
                                    PC));
                }else
                    Toast.makeText(getActivity(),"請先選擇裝置!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnBack:
                switchFragment(getActivity(),MainFragment.newInstance("",""));
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // if(fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBarAmmo:
                vAmmo=progress;
                break;
            case R.id.seekBarHp:
                vHp=progress;
                break;
            case R.id.seekBarGTL:
                vTime=progress;
                break;
        }
        Log.wtf("seekbar", "raw=" + progress +
                ",vhp=" + vHp +
                ",vAmmo=" + vAmmo +
                ",vTime=" + vTime);
        //}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setComponents();
    }

    private void setComponents(){
        if(getView()!=null){
            etxtPname=(EditText)getView().findViewById(R.id.etxtPname);

            CircleButton btnBack = (CircleButton) getView().findViewById(R.id.btnBack);
            CircleButton btnDone = (CircleButton) getView().findViewById(R.id.btnDone);

            btnBack.setOnClickListener(this);
            btnDone.setOnClickListener(this);

            SeekBar seekBarAmmo = (SeekBar) getView().findViewById(R.id.seekBarAmmo);
            SeekBar seekBarHp = (SeekBar) getView().findViewById(R.id.seekBarHp);
            SeekBar seekBarGTL = (SeekBar) getView().findViewById(R.id.seekBarGTL);

            vAmmo=120;
            vTime=120;
            vHp=10;

            seekBarAmmo.setMax(vAmmo);
            seekBarGTL.setMax(vTime);
            seekBarHp.setMax(vHp);

            seekBarAmmo.setProgress(120);
            seekBarGTL.setProgress(10);
            seekBarHp.setProgress(120);

            seekBarAmmo.setOnSeekBarChangeListener(this);
            seekBarHp.setOnSeekBarChangeListener(this);
            seekBarGTL.setOnSeekBarChangeListener(this);
        }
    }


    //
    private boolean hostGame(){

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
