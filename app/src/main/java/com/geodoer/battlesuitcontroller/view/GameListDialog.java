package com.geodoer.battlesuitcontroller.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geodoer.battlesuitcontroller.R;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.controller.PHPController;
import com.yalantis.taurus.PullToRefreshView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.markushi.ui.CircleButton;

/*
    kuyen
 */
public class GameListDialog extends AlertDialog
        implements
        DialogInterface.OnClickListener,
        OnShowListener,
        View.OnClickListener
{

    private View
            dialogLayout;

    private LayoutInflater
            mLayoutInflater;

    private ListView
            lvGameList;

    private PullToRefreshView
            mPullToRefreshView;

    private PHPController
            pc;

    private ArrayAdapter<Map<String, Long>>
            arrayAdapter;

    private CircleButton
            cbDialogOk;

    private ArrayList<Integer>
            arrayColors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_game_list);

        // set view components
        setComponent();
    }

    public GameListDialog(Context context) {
        super(context);

        setWindow();

        pc=new PHPController(context);

        arrayColors = new ArrayList<>();
        //arrayColors.add(R.color.c_coral_red);
        arrayColors.add(R.color.c_blue_ryb);
        arrayColors.add(R.color.c_dark_green_x11);
        arrayColors.add(R.color.c_blue_gray);
        arrayColors.add(R.color.c_blue_munsell);
    }

    private void setWindow(){
        // get things
        mLayoutInflater = getWindow().getLayoutInflater();
        dialogLayout = mLayoutInflater.inflate(R.layout.dialog_show_game_list, null);

        // set custom dialog layout
        setView(dialogLayout);

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
        cbDialogOk = (CircleButton) findViewById(R.id.cbDialogOk);
        cbDialogOk.setOnClickListener(this);

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
    }

    private void setGameListAdapter(){
        pc.getOnlineGames(new PHPController.getOnlineGamesCallback() {
            @Override
            public void run(boolean result, List<Map<String, Long>> list) {
                //
                if (result) {
                    Log.wtf("pc", "get Onlining Games success");

                    //-------get ID from list--------------------
                    if (list == null)
                        Log.wtf("pc", "getOnlining list is null");
                    else {

                        ArrayList<Long> arrayList = new ArrayList<>();
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

                    }
                    //---------------------------------------------

                } else
                    Log.wtf("PARSE", "get Onlining Games fail");
            }
        });
    }

//    private Button getBanNeutral() {
//        return getButton(DialogInterface.BUTTON_NEUTRAL);
//    }

//    private void setDialogShowing(DialogInterface dialog) {
//        try {
//            //不關閉
//            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
//            field.setAccessible(true);
//            field.set(dialog, false);
//            //  MyDebug.MakeLog(1, "setDialogShowing");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

//    /**
//     * This is called when a long press occurs on our listView02 items.
//     */
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        menu.setHeaderTitle("Context Menu");
//        menu.add(0, v.getId(), 0, "Delete");
//    }

//    /**
//     * This is called when an item in our context menu is clicked.
//     */
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        return true;
//    }
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                viewHolder.textViewName =
                        (TextView) convertView.findViewById(R.id.text_view_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String data = String.valueOf(mData.get(position).get(PHPcolumn.game.gameId));
            String showText = "ID:" +
                    data +
                    "\nTime=" +
                    getDate(Long.valueOf(data), "yyyy/MM/dd, hh:mm:ss");

            if(mData.get(position).get(PHPcolumn.game.gPcount) > 0)
            {
                convertView.setBackgroundResource(R.color.c_coral_red);
                showText = showText + " (可加入)";
            }
            else
            {
                Random seed = new Random();
                int colorSelected = seed.nextInt(arrayColors.size() - 1);
                convertView.setBackgroundResource(arrayColors.get(colorSelected));
            }

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
            case R.id.cbDialogOk:

                this.dismiss();
                setDialogDismiss(this);

                break;
        }
    }


}