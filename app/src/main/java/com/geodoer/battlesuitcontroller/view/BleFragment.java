package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.geodoer.battlesuitcontroller.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BleFragment
        extends
        Fragment
        implements
        View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button btnBleDialog;
    private TextView txtBleStatus;

    //private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mServiceExisting = false;


    public static BleFragment newInstance(String param1, String param2) {
        BleFragment fragment = new BleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setComponents();
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
             //   Dialog dialog = new Dialog(getActivity());//指定自定義樣式
             //   dialog.setContentView(R.layout.fragment_bledialog);//指定自定義layout

                //可自由調整佈局內部元件的屬性
//                LinearLayout ll = (LinearLayout)dialog.findViewById(R.id.ly);
 //               ll.getLayoutParams().width=360;

//                Window dialogWindow = dialog.getWindow();
//                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//                //dialogWindow.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
//                lp.x = 500; // 新位置X坐標
//                lp.y = 450; // 新位置Y坐標
//                lp.width = 100; // 寬度
//                lp.height = 100; // 高度
//                lp.alpha = 0.7f; // 透明度

                //新增自定義按鈕點擊監聽
                //Button btn = (Button)dialog.findViewById(R.id.button_scanstart);
                //btn.setOnClickListener(this);

                //顯示dialog
                //dialog.show();


                new BleCustomDialog(this.getActivity()).show();
                break;

            case R.id.button_scanstart:
//                mScanning = true;
//                text_scan.setText(text_scan_on);
//                button_scan.setText(buttontext_scanstop);
//
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
                break;

            case R.id.button_stopservice:
//                mScanning = false;
//                text_scan.setText(text_scan_off);
//                button_scan.setText(buttontext_scanstart);
//
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
