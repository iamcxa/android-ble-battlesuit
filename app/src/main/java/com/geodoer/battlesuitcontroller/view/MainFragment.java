package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.battlesuitcontroller.controller.GameController;
import com.geodoer.battlesuitcontroller.gameItem.aPlayer;
import com.geodoer.battlesuitcontroller.uiCard.SuggestedCard;
import com.geodoer.parsecontroller.controller.ParseController;
import com.geodoer.phpcontroller.controller.PHPController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import it.gmariotti.cardslib.library.Constants;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;

import static com.geodoer.battlesuitcontroller.util.BscUtils.switchFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment
        extends
        Fragment
        implements
        View.OnClickListener,
        ListView.OnItemClickListener,
        GameController.whenSucceed{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String
            TAG = "pc",
            mParam1,
            mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listView;

    private ParseController PC;

    private PHPController pc;

    private GameController gc;

    private ArrayAdapter<Long> adapter;

    //private PullToRefreshView mPullToRefreshView;

    protected ScrollView mScrollView;

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
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
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
        return inflater.inflate(R.layout.fragment_main, container, false);
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
                //switchFragment(getActivity(),MainFragment.newInstance("",""));
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


            //
//            mPullToRefreshView = (PullToRefreshView) getActivity().findViewById(R.id.pull_to_refresh);
//            mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    mPullToRefreshView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            mPullToRefreshView.setRefreshing(false);
//                        }
//                    }, 500);
//                }
//            });
//            mPullToRefreshView.setBackgroundColor(
//                    getResources().getColor(R.color.c_half_transparent));

            //

            CircleButton btnBack = (CircleButton) getView().findViewById(R.id.btnBack);
            CircleButton btnDone = (CircleButton) getView().findViewById(R.id.btnDone);

            btnBack.setOnClickListener(this);
            btnDone.setOnClickListener(this);

            listView=(ListView)getView().findViewById(R.id.listView);
            listView.setOnItemClickListener(this);


            mScrollView = (ScrollView) getActivity().findViewById(R.id.card_scrollview);
            init_card_animation_shadow();
            initCardSuggested();


            Map<String, Integer> map;
            List<Map<String, Integer>> sampleList = new ArrayList<>();


//            int[] colors = {
//                    R.color.saffron,
//                    R.color.eggplant,
//                    R.color.sienna};
//
//            int[] tripNames = {
//                    R.string.trip_to_india,
//                    R.string.trip_to_italy,
//                    R.string.trip_to_indonesia};
//
//            for (int i = 0; i < tripNames.length; i++) {
//                map = new HashMap<>();
//                map.put(SampleAdapter.KEY_NAME, tripNames[i]);
//                map.put(SampleAdapter.KEY_COLOR, colors[i]);
//                sampleList.add(map);
//            }

//            pc=new PHPController(getActivity());
//            pc.getOnlineGames(new PHPController.getOnlineGamesCallback() {
//                @Override
//                public void run(boolean result, ArrayList<Long> list) {
//                    //
//                    if (result) {
//                        Log.wtf("pc", "get Onlining Games success");
//
//                        //-------get ID from list--------------------
//                        if (list == null)
//                            Log.wtf("pc", "getOnlining list is null");
//                        else {
//                            Log.wtf("pc", "getOnlining list size :" + list.size());
//                            for (long i : list) {
//                                Log.wtf("pc", "Onlining Games ID : " + i);
//                            }
//                            adapter = new SampleAdapter(
//                                    getActivity(),
//                                    R.layout.list_item,
//                                    list);
//                            //new SampleAdapter(this, R.layout.list_item, sampleList)
//                            listView.setAdapter(adapter);
//                        }
//                        //---------------------------------------------
//                    } else
//                        Log.wtf("PARSE", "get Onlining Games fail");
//                    //
//                }
//            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
        if(!BleFragment.getDevice().equals("[device_status]")) {

            long gameId=adapter.getItem(position);
            // gc.connect(gameId);

            // join時暫時固定id為2 Chris Kyle->美國狙擊手
            connectGame(gameId,2,"Chris Kyle");

            view.setPressed(true);

        }else
            Toast.makeText(getActivity(), "請先選擇裝置!", Toast.LENGTH_SHORT).show();
    }

    private void connectGame(long gameId, final int playerId, final String playerName){
        pc.connectGame(new PHPController.connectGameCallback(gameId) {
            @Override
            public void run(boolean result) {
                if (result)
                {
                    Log.wtf(TAG, "connectGame success");
                    joinGame(playerId, playerName);
                }
                else
                {
                    Log.wtf(TAG, "connectGame fail");
                }
            }
        });
    }

    private void joinGame(int playerId, String playerName){
        pc.joinGame(new PHPController.joinGameCallback(playerId, playerName) {
            @Override
            public void run(boolean result) {
                if (result) {

                    Log.wtf(TAG, "joinGame success");
                } else {
                    Log.wtf(TAG, "joinGame fail");

                }
            }
        });
    }

    /**
     * This method builds a suggested card example
     */
    private void initCardSuggested() {

        CardThumbnail cardThumbnail = new CardThumbnail(getActivity());
        cardThumbnail.setDrawableResource(R.drawable.ic_ring_ammo);

        //Create a CardHeader
        CardHeader header = new CardHeader(getActivity());
        header.setTitle("header tittle");

        SuggestedCard card = new SuggestedCard(getActivity());
        CardViewNative cardView
                = (CardViewNative) getActivity().findViewById(R.id.carddemo_suggested);

        card.setTitle("card title");
        card.setShadow(true);
        card.setCardElevation(getResources().getDimension(R.dimen.carddemo_shadow_elevation));
        //card.addCardHeader(header);
        //card.addCardThumbnail(cardThumbnail);


        cardView.setCard(card);
    }

    /**
     * This method builds a card with an animation
     */
    private void init_card_animation_shadow() {

        //Create a Card
        Card card = new Card(getActivity());

        //Create a CardHeader
        CardHeader header = new CardHeader(getActivity());

        //Set the header title
        header.setTitle(getString(R.string.demo_suggested_title));

        card.addCardHeader(header);

        card.setCardElevation(getResources().getDimension(R.dimen.carddemo_shadow_elevation));

        //Set card in the cardView
        final CardViewNative cardView
                = (CardViewNative) getActivity().findViewById(R.id.carddemo_shadow_animation);
        cardView.setCard(card);

        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Constants.API_L) {
                            cardView.animate().setDuration(100).scaleX(1.1f).scaleY(1.1f).translationZ(10);
                        } else {
                            cardView.animate().setDuration(100).scaleX(1.1f).scaleY(1.1f);
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (Build.VERSION.SDK_INT >= Constants.API_L) {
                            cardView.animate().setDuration(100).scaleX(1).scaleY(1).translationZ(0);
                        } else {
                            cardView.animate().setDuration(100).scaleX(1).scaleY(1);
                        }
                        return true;
                }
                return false;
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
    }

    @Override
    public void hostSucceed() {

    }

    @Override
    public void connectSucceed() {
        Toast.makeText(getActivity(),"connectSucceed",Toast.LENGTH_SHORT).show();

        aPlayer aPlayer = new aPlayer();
        aPlayer.setPlayerId(2);
        aPlayer.setPlayerName("joiner");

        gc.join(aPlayer);
    }

    @Override
    public void joinSucceed() {
        Toast.makeText(getActivity(),"joinSucceed",Toast.LENGTH_SHORT).show();
        switchFragment(getActivity(),
                BattleFragment.newInstance(
                        PC.getGameId(),
                        2,
                        PC.getSetHP(),
                        PC.getSetAMMO(),
                        120,
                        gc.getPlayer().getPlayerName()

//                        gc.getGame().getSetHp(),
//                        gc.getGame().getSetAmmo(),
//                        gc.getGame().getGameTime(),
//                        gc.getPlayer().getPlayerName()
                ));
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

    //
    //
    //



}
