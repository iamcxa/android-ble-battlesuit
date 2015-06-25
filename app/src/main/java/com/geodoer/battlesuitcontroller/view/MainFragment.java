package com.geodoer.battlesuitcontroller.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.geodoer.battlesuitcontroller.uiCard.SuggestedCard;
import com.geodoer.battlesuitcontroller.uiCard.SuggestedCardHeader;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.controller.PHPController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.gmariotti.cardslib.library.Constants;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;

import static com.geodoer.battlesuitcontroller.util.BscUtils.logTag;


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
        Card.OnCardClickListener,
        Card.OnLongCardClickListener,
        ListView.OnItemClickListener{

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

    //private ListView listView;

    //private ParseController PC;

    private PHPController pc;

    private GameController gc;

    private ArrayAdapter<Long> adapter;

    //private PullToRefreshView mPullToRefreshView;

    protected ScrollView
            mScrollView;

    private CardViewNative
            cardView,cardView2;

    private long gTime=0;

    public static int
            gameMode;

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
        super.onCreate(null);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // pc = new PHPController(getActivity());
        //gc = new GameController(getActivity());
        //gc.setWhenSucceedTarget(this);
        //pc = gc.getPc();
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
            cardView
                    = (CardViewNative) getActivity().findViewById(R.id.carddemo_suggested);

            cardView2
                    = (CardViewNative) getActivity().findViewById(R.id.carddemo_suggested2);


            //SuggestedCard.setWhenClcikedTarget(this);

            //  CircleButton btnBack = (CircleButton) getView().findViewById(R.id.btnBack);
            //  CircleButton btnDone = (CircleButton) getView().findViewById(R.id.btnDone);

//            btnBack.setOnClickListener(this);
            // btnDone.setOnClickListener(this);

            mScrollView = (ScrollView) getActivity().findViewById(R.id.card_scrollview);
            //init_card_animation_shadow();

            initCardSuggested();
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
                if (result) {
                    Log.wtf(TAG, "connectGame success");
                    joinGame(playerId, playerName);
                } else {
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

        //
        CardThumbnail cardThumbnail = new CardThumbnail(getActivity());
        cardThumbnail.setDrawableResource(R.drawable.ic_ring_hp);

        //Create a CardHeader
        SuggestedCardHeader header = new SuggestedCardHeader(getActivity(),"團隊搶旗");
        header.setsHeaderText("團隊搶旗");

        //
        SuggestedCard card = new SuggestedCard(getActivity());
        card.setsTittle("搶旗");
        card.setsHeaderText("搶旗");
        card.setShadow(true);
        card.setCardElevation(getResources().getDimension(R.dimen.carddemo_shadow_elevation));
        card.setsDescription("●勝利條件（任一項）：\n" +
                "> 最快得到搶旗分數上限\n" +
                "> 限時內得分最高隊\n" +
                "●起始條件：\n" +
                ">  10 生命\n" +
                "> 120 彈藥 "+
                "> 沒有彈藥包（透過NFC）"+
                "> 有補血包（透過NFC）");
        card.setsSubTitle("遊戲開始需要至少 2 人");
        card.setsPurpose("開始搶旗！");
        card.addCardThumbnail(cardThumbnail);
        card.addCardHeader(header);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                gameMode = 1;
                GameListDialog gameListDialog = new GameListDialog(getActivity());
                gameListDialog.show();
                //onGameModeCardClicked(1);

            }
        });
        cardView.setCard(card);

        //
        CardThumbnail cardThumbnail2 = new CardThumbnail(getActivity());
        cardThumbnail2.setDrawableResource(R.drawable.ic_ring_ammo);

        //Create a CardHeader
        SuggestedCardHeader header2 = new SuggestedCardHeader(getActivity(),"團隊死鬥");
        header2.setsHeaderText("團隊死鬥");

        SuggestedCard card2 = new SuggestedCard(getActivity());
        card2.setTitle("死鬥");
        card2.setsHeaderText("團隊死鬥");
        card2.setShadow(true);
        card2.setCardElevation(getResources().getDimension(R.dimen.carddemo_shadow_elevation));
        card2.addCardThumbnail(cardThumbnail);
        card2.setsDescription("●勝利條件（任一項）：\n" +
                "> 最先殺人數達到殺人上限\n" +
                "> 限時內殺人數最多\n" +
                "●起始條件：\n" +
                ">  20 生命\n" +
                "> 400 彈藥\n" +
                "> 沒有補血包（透過NFC）"+
                "> 有彈藥包（透過NFC）");
        card2.setsSubTitle("遊戲開始需要至少 2 人");
        card2.setsPurpose("開始死鬥！");
        card2.addCardHeader(header2);
        card2.addCardThumbnail(cardThumbnail2);
        card2.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {

                gameMode = 2 ;

                GameListDialog gameListDialog = new GameListDialog(getActivity());

                gameListDialog.show();
//                onGameModeCardClicked(2);

            }
        });
        cardView2.setCard(card2);

        // ArrayList<Card> cards = new ArrayList<>();
//        cards.add(card);
//        cards.add(card2);
//        cards.add(card);
//
//        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
//
//        CardListView listView
//                = (CardListView) getActivity().findViewById(R.id.carddemo_list_base1);
//        if (listView!=null){
//            listView.setAdapter(mCardArrayAdapter);
//        }
    }

    private void onGameModeCardClicked(final int gameMode){
        this.gameMode = gameMode;

        // go check
        pc.getOnlineGames(new PHPController.getOnlineGamesCallback() {

            @Override
            public void run(boolean result, List<Map<String, Long>> list) {

                if(result) {
                    if (list.size() > 0) {
                        long nowTime = System.currentTimeMillis();
                        ArrayList<Long> arrayGameList = new ArrayList<Long>();
                        long gameTime;

                        for (Map i : list) {
                            gameTime = Long.valueOf(i.get(PHPcolumn.game.gameId).toString());
                            if (nowTime - gameTime < 300000)
                                arrayGameList.add(gameTime);
                        }

                        if(arrayGameList.size()>0) {
                            Log.wtf(logTag, "i got those games available=" + arrayGameList.toString());

                            for (long l : arrayGameList) {
                                gc.connect(l);
                            }
                        }else
                            showDialogWhenFindNoEmptyOnlineGame(gameMode);
                    }
                    else
                    {
                        Log.wtf(logTag, "i got no suitable games.");
                        showDialogWhenFindNoEmptyOnlineGame(gameMode);
                    }
                }
                else
                {
                    Log.wtf(logTag, "something goes wrong." );
                }

            }
        });
    }

    //==============================//
    //           Dialog(s)          //
    //==============================//

    private AlertDialog showDialogWhenFindNoEmptyOnlineGame(final int gameMode){
        return new AlertDialog.Builder(getActivity())
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("系統找不到可以配對或是正在進行的遊戲，您要自己開一場嗎？")
                .setCancelable(false)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gc.hostGameMode(gameMode);
                    }
                })
                .setNegativeButton("再等等", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private AlertDialog showDialogWhenSelectGameMode(final int gameMode){
        return new AlertDialog.Builder(getActivity())
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("系統找不到可以配對或是正在進行的遊戲，您要自己開一場嗎？")
                .setCancelable(false)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //hostGame();
                        gc.hostGameMode(gameMode);
                    }
                })
                .setNegativeButton("再等等", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
    public void onClick(Card card, View view) {

    }

    @Override
    public boolean onLongClick(Card card, View view) {
        return false;
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
