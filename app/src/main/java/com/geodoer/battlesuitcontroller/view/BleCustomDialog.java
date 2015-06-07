package com.geodoer.battlesuitcontroller.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.geodoer.battlesuitcontroller.R;

import java.lang.reflect.Field;

/**
 * This is a custom dialog class that will hold a tab view with 2 tabs.
 * Tab 1 will be a list view. Tab 2 will be a list view.
 */
public class BleCustomDialog extends AlertDialog
        implements
        DialogInterface.OnClickListener,
        OnShowListener
{


    static class ViewHolder{
        View dialogLayout;
        LayoutInflater mLayoutInflater;
    }

    private String selectedDate;
    private String selectedTime;
    private ViewHolder v=new ViewHolder();

    public BleCustomDialog(Context context) {
        super(context);

        v.mLayoutInflater = getWindow().getLayoutInflater();
        v.dialogLayout = v.mLayoutInflater.inflate(R.layout.fragment_bledialog, null);

        // set custom dialog layout
        setView(v.dialogLayout);

        // remove window title
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // get this window's layout parameters so we can change the position
        WindowManager.LayoutParams params = getWindow().getAttributes();

        // change the position. 0,0 is center
        params.x = 0;
        params.y = 50;
        params.height = -2;
        params.width = -2;
        this.getWindow().setAttributes(params);

        // set dialog Buttons
        this.setButton(BUTTON_POSITIVE,
                "1", this);
        this.setButton(BUTTON_NEUTRAL,
                "2", this);
        this.setButton(BUTTON_NEGATIVE,
                "3", this);
        this.setCanceledOnTouchOutside(false);

        // set Show Listener - in case to hide BUTTON_NEUTRAL.
        this.setOnShowListener(this);
    }

    /**
     *
     */
    private void getSelectedTime() {
        if ((getBtnNutral().getVisibility()) == (View.VISIBLE)) {


        }
    }
    private Button getBtnNutral() {
        return getButton(DialogInterface.BUTTON_NEUTRAL);
    }


    private void setDialogShowing(DialogInterface dialog) {
        try {
            //不關閉
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
          //  MyDebug.MakeLog(1, "setDialogShowing");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setBtnAction_Positive(DialogInterface dialog) {

    }


    private void setBtnAction_Neutral() {
        selectedTime = "";


        // 隱藏按鈕
        getBtnNutral().setVisibility(View.GONE);
    }

    /**
     * This is called when a long press occurs on our listView02 items.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Delete");
    }

    /**
     * This is called when an item in our context menu is clicked.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }

    /**
     * Dialog On-Show-Listener
     * 從 TaskEditorMain 讀取日期時間  / 隱藏放棄時間按鈕
     */
    @Override
    public void onShow(DialogInterface dialog) {
        // 啟動先隱藏放棄時間按鈕
        getBtnNutral().setVisibility(View.GONE);

    }

    /**
     * 建立三個按鈕的監聽式
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //which可以用來分辨是按下哪一個按鈕
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:    // save selected date/time

                setBtnAction_Positive(dialog);

                break;
            case DialogInterface.BUTTON_NEUTRAL:        // 取消時間
                setDialogShowing(dialog);
                setBtnAction_Neutral();

                break;
            case DialogInterface.BUTTON_NEGATIVE:    // 取消全部

                setDialogDismiss(dialog);

                break;
        }
    }
}