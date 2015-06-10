package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import com.geodoer.battlesuitcontroller.R;

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

    private SeekBar seekBarGTL,seekBarHp,seekBarAmmo;

    private CircleButton btnDone,btnBack;

    private EditText etxtPname;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
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
                switchFragment(getActivity(),BattleFragment.newInstance("",""));
                break;
            case R.id.btnBack:
                switchFragment(getActivity(),MainFragment.newInstance("",""));
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
            btnBack=(CircleButton)getView().findViewById(R.id.btnBack);
            btnDone=(CircleButton)getView().findViewById(R.id.btnDone);

            btnBack.setOnClickListener(this);
            btnDone.setOnClickListener(this);

            seekBarAmmo=(SeekBar)getView().findViewById(R.id.seekBarAmmo);
            seekBarHp=(SeekBar)getView().findViewById(R.id.seekBarHp);
            seekBarGTL=(SeekBar)getView().findViewById(R.id.seekBarGTL);

            seekBarAmmo.setMax(120);
            seekBarGTL.setMax(120);
            seekBarHp.setMax(10);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
