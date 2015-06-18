package com.geodoer.battlesuitcontroller.controller;

import android.content.Context;
import android.util.Log;

import com.geodoer.parsecontroller.controller.GameIdmaker;
import com.geodoer.parsecontroller.controller.ParseController;

/**
 * Created by iamcx_000 on 2015/6/18.
 */
public class HostGame {

    private ParseController PC;
    private boolean thisResult;


    public boolean HostGame(Context context,
                    int setPlayerCount,
                    int setHp,
                    int setAmmo,
                    long setTime) {
        this.PC = new ParseController(context);
        return host(setPlayerCount,setHp,setAmmo,setTime);
    }

    private boolean host(
            int setPlayerCount,
            int setHp,
            int setAmmo,
            long setTime) {
        PC.setGame(setPlayerCount,
                setHp,
                setAmmo,
                new ParseController.setGameCallback(GameIdmaker.newId()) {
                    @Override
                    public void run(boolean result) {
                        if (result) {
                            connectOk();
                            thisResult = true;
                        } else {
                            connectFailed();
                            thisResult = false;
                        }
                    }
                });
        return thisResult;
    }

    private void connectOk(){
        Log.wtf("PARSE", "set Game success");
    }

    private void connectFailed(){
        Log.wtf("PARSE", "set Game fail");
    }

    public long getHostedGameId(){
        if(thisResult)
            return PC.getGameId();
        else
            return -1;
    }
}
