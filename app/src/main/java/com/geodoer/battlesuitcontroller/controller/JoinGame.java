package com.geodoer.battlesuitcontroller.controller;

import android.content.Context;
import android.util.Log;

import com.geodoer.parsecontroller.controller.ParseController;

/**
 * Created by iamcx_000 on 2015/6/18.
 */
public class JoinGame {

    private ParseController PC;
    private boolean thisResult;


    public boolean JoinGame(Context context, int playerId, String playerName) {
        this.PC = new ParseController(context);
        return join(playerId,playerName);
    }

    private boolean join(int playerId, String playerName) {
        PC.joinGame(new ParseController.joinGameCallback(
                playerId,
                playerName) {
            @Override
            public void run(boolean result) {
                if (result) {
                    joinOk();
                    thisResult=true;
                }
                else {
                    joinFailed();
                    thisResult=false;
                }
            }
        });
        return thisResult;
    }

    private void joinOk(){
        Log.wtf("PARSE", "join success");
        thisResult=true;
    }

    private void joinFailed(){
        Log.wtf("PARSE", "join fail");

    }

    public long getJoinedGameId(){
        if(thisResult)
            return PC.getGameId();
        else
            return -1;
    }
}
