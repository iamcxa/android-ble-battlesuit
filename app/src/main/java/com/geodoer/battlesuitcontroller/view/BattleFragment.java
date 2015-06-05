package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.db.circularcounter.CircularCounter;
import com.geodoer.battlesuitcontroller.R;
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
        CircularSeekBar.OnSeekChangeListener {

    private CircularCounter meterHp;
    private CircularCounter meterAmmo;

    private String[] colorsAmmo;
    private String[] colorsHp;

    private Handler handler;

    private Runnable r;

    private Button btnMore;
    private Button btnLess;
    private Button btnAuto;

    private boolean isAutoRun;

    private CircularSeekBar barAmmo;
    private CircularSeekBar barHp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BattleFragment newInstance(String param1, String param2) {
        BattleFragment fragment = new BattleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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


    private void setupcompements(){
        barAmmo=(CircularSeekBar)getView().findViewById(R.id.barAmmo);
        barAmmo.setMaxProgress(60);
        barAmmo.setProgress(0);
        barAmmo.setBarWidth(35);
        barAmmo.invalidate();
        barAmmo.setProgressColor(getResources().getColor(R.color.bar_color_ammo));
        barAmmo.setSeekBarChangeListener(this);

        barHp=(CircularSeekBar)getView().findViewById(R.id.barHp);
        barHp.setMaxProgress(5);
        barHp.setProgress(0);
        barHp.setBarWidth(35);
        barHp.invalidate();
        barHp.setProgressColor(getResources().getColor(R.color.bar_color_hp));
        barHp.setSeekBarChangeListener(this);

        isAutoRun=false;

        btnMore=(Button)getView().findViewById(R.id.btnMore);
        btnLess=(Button)getView().findViewById(R.id.btnLess);
        btnAuto=(Button)getView().findViewById(R.id.btnAuto);

        btnMore.setOnClickListener(this);
        btnLess.setOnClickListener(this);
        btnAuto.setOnClickListener(this);

        colorsAmmo = getResources().getStringArray(R.array.colors_AMMO);
        colorsHp = getResources().getStringArray(R.array.colors_HP);

        meterHp = (CircularCounter) getView().findViewById(R.id.meter);
        meterAmmo = (CircularCounter) getView().findViewById(R.id.meter_ammo);

        meterHp.setFirstWidth(getResources().getDimension(R.dimen.first))
                .setFirstColor(Color.parseColor(colorsHp[0]))

                .setSecondWidth(getResources().getDimension(R.dimen.second))
                .setSecondColor(Color.parseColor(colorsHp[1]))

                .setThirdWidth(getResources().getDimension(R.dimen.third))
                .setThirdColor(Color.parseColor(colorsHp[2]))

                .setBackgroundColor(Color.parseColor(colorsAmmo[3]));

        meterAmmo.setFirstWidth(getResources().getDimension(R.dimen.first))
                .setFirstColor(Color.parseColor(colorsAmmo[0]))

                .setSecondWidth(getResources().getDimension(R.dimen.second))
                .setSecondColor(Color.parseColor(colorsAmmo[1]))

                .setThirdWidth(getResources().getDimension(R.dimen.third))
                .setThirdColor(Color.parseColor(colorsAmmo[2]))

                .setBackgroundColor(Color.parseColor(colorsAmmo[3]));
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
    public void onPause() {
        super.onPause();
        isAutoRun=false;
        handler.removeCallbacks(r);
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

}
