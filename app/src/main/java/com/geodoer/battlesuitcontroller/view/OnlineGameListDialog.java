package com.geodoer.battlesuitcontroller.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;


public class OnlineGameListDialog extends AlertDialog {


    protected OnlineGameListDialog(Context context) {
        super(context);
    }

    protected OnlineGameListDialog(Context context,
                                   boolean cancelable,
                                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
