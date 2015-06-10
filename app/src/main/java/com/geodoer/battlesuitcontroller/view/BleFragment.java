package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.bluetoothcontroler.service.BluetoothLeService;

public class BleFragment
        extends
        Fragment
        implements
        View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String deviceName,deviceId;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button btnBleDialog;
    private static TextView txtBleStatus;

    public static BleFragment newInstance(String param1, String param2) {
        BleFragment fragment = new BleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setDevice(String thisDeviceName,String thisDeviceId){
        deviceName=thisDeviceName;
        deviceId=thisDeviceId;
        if(txtBleStatus!=null)
            txtBleStatus.setText(thisDeviceName+","+thisDeviceId);
    }

    public static String getDevice(){
        return txtBleStatus.getText().toString();
    }

    public BleFragment() {
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setComponents();
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
    }

    private void setComponents(){
        if(getView()!=null) {
            btnBleDialog = (Button) getView().findViewById(R.id.btnBleDialog);
            txtBleStatus = (TextView) getView().findViewById(R.id.txtBleStatus);
            btnBleDialog.setOnClickListener(this);
        }
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
            case R.id.btnBleDialog:
                new BleCustomDialog(this.getActivity()).show();
                break;
        }
    }

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
            if (bString.equals(action))
            {
                String data = intent.getStringExtra(BleCustomDialog.EXTRA_DATA);

                if(!data.equals("null"))
                {
                    txtBleStatus.setText(data);
                }
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(data!=null)
                {
                    String temp = txtBleStatus.getText().toString();
                    if(temp.length() > 18)
                        txtBleStatus.setText(new String(data.substring(0, 2)));
                    else
                        txtBleStatus.setText(temp+"-"+new String(data.substring(0, 2)));
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
