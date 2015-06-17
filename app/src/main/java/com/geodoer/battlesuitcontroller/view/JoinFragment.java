package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.parsecontroller.controller.ParseController;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static com.geodoer.battlesuitcontroller.util.utils.switchFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JoinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JoinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JoinFragment
        extends
        Fragment
        implements
        View.OnClickListener,
        ListView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listView;

    private ParseController PC;

    private ArrayAdapter<Long> adapter;

    private long gTime=0;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JoinFragment newInstance(String param1, String param2) {
        JoinFragment fragment = new JoinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public JoinFragment() {
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
        return inflater.inflate(R.layout.fragment_join, container, false);
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

                break;
            case R.id.btnBack:
                switchFragment(getActivity(),MainFragment.newInstance("",""));
                break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setComponents();
    }

    private void setComponents(){
        if(getView()!=null){
            CircleButton btnBack = (CircleButton) getView().findViewById(R.id.btnBack);
            CircleButton btnDone = (CircleButton) getView().findViewById(R.id.btnDone);

            btnBack.setOnClickListener(this);
            btnDone.setOnClickListener(this);

            listView=(ListView)getView().findViewById(R.id.lvJoin);
            listView.setOnItemClickListener(this);

            PC =new ParseController(getActivity().getApplicationContext());
            PC.getOnlineGames(new ParseController.getOnlineGamesCallback() {
                @Override
                public void run(boolean result, ArrayList<Long> list) {
                    //
                    if (result) {
                        Log.wtf("PARSE", "get Onlining Games success");

                        //-------get ID from list--------------------
                        if (list == null)
                            Log.wtf("PARSE", "getOnlining list is null");
                        else {
                            Log.wtf("PARSE", "getOnlining list size :" + list.size());
                            for (long i : list) {
                                Log.wtf("PARSE", "Onlining Games ID : " + i);
                            }
                            adapter = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_list_item_1, list);
                            listView.setAdapter(adapter);
                        }
                        //---------------------------------------------
                    } else
                        Log.wtf("PARSE", "get Onlining Games fail");
                    //
                }
            });

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
        if(!BleFragment.getDevice().equals("[device_status]")) {
            //
            PC.connectGame(new ParseController.connectGameCallback(
                    adapter.getItem(position)){
                @Override
                public void run(boolean result) {
                    if (result) {
                        Log.wtf("PARSE", "connect success");

                        switchFragment(getActivity(), BattleFragment
                                .newInstance(0,
                                        PC.getSetHP(),
                                        PC.getSetAMMO(),
                                        "join",
                                        2,
                                        PC.getGameId(),
                                        PC));


//                        PC.joinGame(new ParseController.joinGameCallback(2, "joiner") {
//                            @Override
//                            public void run(boolean result) {
//
//                                if (result) {
//                                    Log.wtf("PARSE", "join success");
//
//                                }
//                                else
//                                    Log.wtf("PARSE", "join fail");
//                            }
//                        });

                    } else Log.wtf("PARSE", "connect fail");
                }
            });

        }else
            Toast.makeText(getActivity(), "請先選擇裝置!", Toast.LENGTH_SHORT).show();
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
