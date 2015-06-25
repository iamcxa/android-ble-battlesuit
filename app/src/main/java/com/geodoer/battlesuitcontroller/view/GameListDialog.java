package com.geodoer.battlesuitcontroller.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.MainActivity;
import com.geodoer.battlesuitcontroller.R;
import com.geodoer.battlesuitcontroller.controller.GameController;
import com.geodoer.battlesuitcontroller.util.BscUtils;
import com.geodoer.bluetoothcontroler.BcUtils;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.controller.PHPController;
import com.yalantis.taurus.PullToRefreshView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.markushi.ui.CircleButton;

/*
    kuyen
 */
public class GameListDialog extends AlertDialog
        implements
        View.OnClickListener,
        DialogInterface.OnClickListener,
        OnShowListener,
        GameController.whenSucceed,
        AdapterView.OnItemClickListener
{
    private View
            dialogLayout;

    private LayoutInflater
            mLayoutInflater;

    private ListView
            lvGameList;

    private PullToRefreshView
            mPullToRefreshView;

    private ArrayAdapter<Map<String, Long>>
            arrayAdapter;

    private CircleButton
            cbDialogBack,
            cbDialogNewGame;

    private ArrayList<Integer>
            arrayColors;

    private int
            gameMode;

    //private PHPController
    //        pc;

    //private GameController
    //       gc;

    //
    //
    //

    public GameListDialog(Context context) {
        super(context);
        //
        setWindow();
        //
        setVars();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_game_list);

        // set view components
        setComponent();
    }

    private void setWindow(){
        // get things
        mLayoutInflater = getWindow().getLayoutInflater();
        dialogLayout = mLayoutInflater.inflate(R.layout.dialog_show_game_list, null);

        // set custom dialog layout
        setView(dialogLayout);

        //setContentView(R.layout.dialog_show_game_list);

        // remove window title
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // get this window's layout parameters so we can change the position
        WindowManager.LayoutParams params = getWindow().getAttributes();

        // change the position. 0,0 is center
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        this.getWindow().setAttributes(params);

        // set dialog Buttons
        this.setButton(BUTTON_POSITIVE, "1", this);
        // this.setButton(BUTTON_NEUTRAL,"2", this);
        // this.setButton(BUTTON_NEGATIVE, "3", this);
        this.setCanceledOnTouchOutside(false);

        // set Show Listener - in case to hide BUTTON_NEUTRAL.
        this.setOnShowListener(this);
    }

    private void setComponent(){
        cbDialogBack = (CircleButton) findViewById(R.id.cbDialogBack);
        cbDialogBack.setOnClickListener(this);

        cbDialogNewGame = (CircleButton) findViewById(R.id.cbDialogNewGame);
        cbDialogNewGame.setOnClickListener(this);
        cbDialogNewGame.setEnabled(false);

        // mPullToRefreshView
        mPullToRefreshView
                = (PullToRefreshView) findViewById(R.id.pull_to_refresh);

        // 設定下拉更新
        mPullToRefreshView.setOnRefreshListener(
                new PullToRefreshView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mPullToRefreshView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // run start
                                setGameListAdapter();
                                Toast.makeText(getContext(), "更新線上遊戲清單中...", Toast.LENGTH_SHORT).show();
                                mPullToRefreshView.setRefreshing(false);
                                // run end
                            }
                        }, 700);
                    }
                });

        //
        mPullToRefreshView.setBackgroundColor(
                getContext().getResources().getColor(R.color.c_half_transparent));

        // listView
        lvGameList = (ListView) findViewById(R.id.lvGameList);
        lvGameList.setOnItemClickListener(this);

    }

    private void setVars(){
        // gc
        MainActivity.getThisGC().setWhenSucceedTarget(this);
        // pc = gc.getPc();

        // random colors
        arrayColors = new ArrayList<Integer>();
        //arrayColors.add(R.color.c_coral_red);
        arrayColors.add(R.color.c_blue_ryb);
        arrayColors.add(R.color.c_dark_green_x11);
        arrayColors.add(R.color.c_blue_gray);
        arrayColors.add(R.color.c_blue_munsell);
    }

    private void setGameListAdapter(){
        MainActivity.getThisGC().getPc().getOnlineGames(new PHPController.getOnlineGamesCallback() {
            @Override
            public void run(boolean result, List<Map<String, Long>> list) {
                //
                if (result) {
                    Log.wtf("pc", "get Onlining Games success");

                    //-------get ID from list--------------------
                    if (list == null)
                        Log.wtf("pc", "getOnlining list is null");
                    else {

                        ArrayList<Long> arrayList = new ArrayList<Long>();
                        arrayList.clear();

                        Log.wtf("pc", "getOnlining list size :" + list.size());

                        for (Map i : list) {
                            Log.wtf("pc",
                                    "Onlining Games ID : " + i.get(PHPcolumn.game.gameId) +
                                            " ,its people count : " + i.get(PHPcolumn.game.gPcount));
                        }

                        //for (int j=list.size()-1;j>=0;j--){
                        //     arrayList.add(list.get(j).get(PHPcolumn.game.gameId));
                        //}
                        //arrayList.trimToSize();
                        //arrayAdapter =
                        arrayAdapter = new SampleAdapter(
                                getContext(),
                                R.layout.list_item,
                                //android.R.layout.simple_list_item_1,
                                list);

                        lvGameList.setAdapter(arrayAdapter);

                        cbDialogNewGame.setEnabled(true);
                    }
                    //---------------------------------------------

                } else {
                    Log.wtf("dialog", "get Onlining Games fail");
                    // showDialogWhenGetNoOnlineGame();
                }
            }
        });
    }

    private void setDialogDismiss(DialogInterface dialog) {
        try {
            //不關閉
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
            //MyDebug.MakeLog(1, "setDialogDismiss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertDialog showDialogWhenGetNoOnlineGame(){
        return new AlertDialog.Builder(getContext())
                .setTitle("問題")
                .setIcon(R.drawable.question_mark)
                .setMessage("更新遊戲清單沒有成功。\n \n" +
                        "請嘗試檢查網路狀態，或是滑動清單重新讀取一次。\n" +
                        "*如果讀取遊戲清單一直失敗，將不能開啟一局新遊戲。")
                .setCancelable(false)
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).show();
    }

    @Override
    public void hostSucceed() {
        Log.wtf("dialog", "dialog:hostSucceed");
    }

    @Override
    public void connectSucceed() {
        Log.wtf("dialog", "dialog:connectSucceed");
    }

    @Override
    public void joinSucceed() {
        Log.wtf("dialog", "dialog:joinSucceed");

        //
        final Intent intent = new Intent(BcUtils.SERVICE_STATE);
        intent.putExtra(BcUtils.EXTRA_DATA,
                "START_TO_SWITCH_FRAGMENT_TO_BATTLE");
        getContext().sendBroadcast(intent);
        //

        this.dismiss();
    }

    @Override
    public void hostFailed() {

        Toast.makeText(getContext(),
                "開遊戲失敗，可能是網路原因或其他問題。",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void connectFailed() {

        Toast.makeText(getContext(),
                "連接失敗！可能是網路問題，或是這場遊戲已經滿了。",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void joinFailed() {

        Toast.makeText(getContext(),
                "連接成功，但加入遊戲失敗！可能是網路問題，或是這場遊戲已經滿了。",Toast.LENGTH_SHORT).show();

    }

    /*

     */
    class SampleAdapter extends ArrayAdapter<Map<String, Long>> {

        public static final String KEY_NAME = "name";
        public static final String KEY_COLOR = "color";

        private final LayoutInflater mInflater;
        private final List<Map<String, Long>> mData;
        //private final List<Long> mData;

        public SampleAdapter(Context context,
                             int layoutResourceId,
                             List<Map<String, Long>> data) {
            super(context, layoutResourceId, data);
            mData = data;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position,
                            View convertView,
                            @NonNull ViewGroup parent) {
            //
            final ViewHolder viewHolder;
            //
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                viewHolder.textViewName =
                        (TextView) convertView.findViewById(R.id.text_view_name);
                convertView.setTag(R.layout.list_item,viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.layout.list_item);
            }

            long nowTime = System.currentTimeMillis();
            String data = String.valueOf(mData.get(position).get(PHPcolumn.game.gameId));
            String showText = "ID:" +
                    data +
                    "\nTime=" +
                    BscUtils.getDate(Long.valueOf(data), "yyyy/MM/dd, hh:mm:ss");

            if(nowTime - mData.get(position).get(PHPcolumn.game.gameId) < 1800000)
            {
                convertView.setBackgroundResource(R.color.c_coral_red);
                showText = showText + " (可加入)";
            }
            else
            {
                Random seed = new Random();
                int colorSelected = seed.nextInt(arrayColors.size() - 1);
                convertView.setBackgroundResource(arrayColors.get(colorSelected));
                showText = showText + " (已逾期)";
            }

            convertView.setTag(data);
            viewHolder.textViewName.setTextSize((float)16);
            viewHolder.textViewName.setText(showText);

            //viewHolder.textViewName.setText(mData.get(position).get(KEY_NAME));
            //convertView.setBackgroundResource(mData.get(position).get(KEY_COLOR));

            return convertView;
        }

        class ViewHolder {
            TextView textViewName;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        long gameId = Long.valueOf(view.getTag().toString());
        long nowTime = System.currentTimeMillis();
        Log.wtf("list",
                "view.getTag="+gameId+
                        ", position="+position+
                        ", id"+id);

        if(nowTime - gameId > 1800000)
        {
            Toast.makeText(getContext(),"不能加入逾期遊戲，請重新開啟新局。"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity.getThisGC().connect(gameId);
    }

    /**
     * Dialog On-Show-Listener
     */
    @Override
    public void onShow(DialogInterface dialog) {
        // 啟動先隱藏放棄時間按鈕
        // getBanNeutral().setVisibility(View.GONE);

        setGameListAdapter();
    }

    /**
     * 建立三個按鈕的監聽式
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: // ok button

                this.dismiss();
                setDialogDismiss(this);

                break;

//            case DialogInterface.BUTTON_NEUTRAL:
//                setDialogShowing(dialog);
//
//                break;
//
//            case DialogInterface.BUTTON_NEGATIVE:
//
//                setDialogDismiss(dialog);
//
//                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cbDialogBack:

                this.dismiss();
                setDialogDismiss(this);

                break;

            case R.id.cbDialogNewGame:

                MainActivity.getThisGC().hostGameMode(MainFragment.gameMode);

                break;
        }
    }
}