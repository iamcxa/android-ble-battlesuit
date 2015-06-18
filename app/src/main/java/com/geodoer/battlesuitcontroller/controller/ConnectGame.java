package com.geodoer.battlesuitcontroller.controller;

import android.content.Context;
import android.util.Log;

import com.geodoer.parsecontroller.controller.ParseController;

/**
 * Created by iamcx_000 on 2015/6/18.
 */
public class ConnectGame {

    private ParseController PC;
    private boolean thisResult;


    public boolean ConnectGame(Context context, int gameID) {
        this.PC = new ParseController(context);
        return connect(gameID);
    }

    private boolean connect(int gameID) {
        PC.connectGame(new ParseController.connectGameCallback(gameID) {
            @Override
            public void run(boolean result) {
                if (result) {
                    connectOk();
                    thisResult=true;
                }
                else {
                    connectFailed();
                    thisResult=false;
                }
            }
        });
        return thisResult;
    }

    private void connectOk(){
        Log.wtf("PARSE", "connect success");
    }

    private void connectFailed(){
        Log.wtf("PARSE", "connect success");

    }

    public long getConnectedGameId(){
        if(thisResult)
            return PC.getGameId();
        else
            return -1;
    }
}
