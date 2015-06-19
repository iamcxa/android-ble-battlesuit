package com.geodoer.battlesuitcontroller.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.geodoer.battlesuitcontroller.item.aGame;
import com.geodoer.battlesuitcontroller.item.aPlayer;
import com.geodoer.parsecontroller.controller.ParseController;

/**
 * Created by iamcx_000 on 2015/6/18.
 */
public class GameController {

    private static ParseController PC;
    private aGame thisGame;
    private aPlayer thisPlayer;
    private long gameId;
    private boolean thisResult;
    private Context context;

    private Runnable r;
    private Handler handler;

    private whenSucceed whenSucceed;

    //
    public GameController(Context context) {
        this.PC = new ParseController(context);
        this.context = context;
        this.gameId = 0;
    }

    //
    public void host(aGame aGame,aPlayer aPlayer) {
        if (aGame.getGameId() != 0) {
            if (aPlayer.getPlayerId() != 0) {
                this.thisGame = aGame;
                this.thisPlayer = aPlayer;
                this.gameId = aGame.getGameId();
                PC.setGame(aGame.getPlayerCount(),
                        aGame.getSetHp(),
                        aGame.getSetAmmo(),
                        new ParseController.setGameCallback(aGame.getGameId()) {
                            @Override
                            public void run(boolean result) {
                                if (result) {
                                    whenSucceed.hostSucceed();
                                    connect();
                                    Log.wtf("PARSE", "set Game Succeed");
                                } else {
                                    whenSucceed.hostFailed();
                                    Log.wtf("PARSE", "set Game Failed");
                                }
                            }
                        });
            }else
                Log.wtf("PARSE", "Player is null!");
        }else
            Log.wtf("PARSE", "Game Id is null!");
    }

    //
    public void connect() {
        privateConnect();
    }

    //
    public void connect(long gameId){
        this.gameId = gameId;
        privateConnect();
    }

    //
    private void privateConnect(){
        PC.connectGame(new ParseController.connectGameCallback(this.gameId) {
            @Override
            public void run(boolean result) {
                if (result) {
                    whenSucceed.connectSucceed();
                    join();
                    Log.wtf("PARSE", "connect Game Succeed");
                } else {
                    whenSucceed.connectFailed();
                    Log.wtf("PARSE", "connect Game Failed");
                }
            }
        });
    }

    //
    public void join() {
        privateJoin();
    }

    //
    public void join(aPlayer aplayer){
        thisPlayer = aplayer;
        privateJoin();
    }

    //
    private void privateJoin(){
        PC.joinGame(new ParseController.joinGameCallback(
                thisPlayer.getPlayerId(),
                thisPlayer.getPlayerName()) {
            @Override
            public void run(boolean result) {
                if (result) {
                    whenSucceed.joinSucceed();
                    Log.wtf("PARSE", "join Game Succeed");
                } else {
                    whenSucceed.joinFailed();
                    Log.wtf("PARSE", "join Game Failed");
                }
            }
        });
    }

    //
    public void setWhenSucceedTarget(whenSucceed whenSucceed){
        this.whenSucceed=whenSucceed;
    }

    //
    public aGame getGame() {
        return thisGame;
    }

    //
    public aPlayer getPlayer() {
        return thisPlayer;
    }

    //
    public static ParseController getPc(){
        if(PC!=null)
            return PC;
        else
            return null;
    }

    //
    public interface whenSucceed {
        void hostSucceed();

        void connectSucceed();

        void joinSucceed();

        void hostFailed();

        void connectFailed();

        void joinFailed();
    }
}