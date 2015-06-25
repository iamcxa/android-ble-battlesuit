package com.geodoer.battlesuitcontroller.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.geodoer.battlesuitcontroller.gameItem.aGame;
import com.geodoer.battlesuitcontroller.gameItem.aPlayer;
import com.geodoer.phpcontroller.controller.GameIdmaker;
import com.geodoer.phpcontroller.controller.PHPController;
import com.geodoer.phpcontroller.utils.StatusChangeListener;

/**
 * Created by iamcx_000 on 2015/6/18.GC
 */
public class GameController {

    protected static PHPController PC;
    private static aGame thisGame;
    private static aPlayer thisPlayer;
    private long gameId;
    private boolean thisResult;
    protected Context context;

    private Runnable r;
    private Handler handler;

    private whenSucceed whenSucceed;

    private whenPlayerAct whenPlayerAct;

    //
    public GameController(Context aContext) {
        this.context = aContext;
        this.gameId = 0;
        thisGame = new aGame();

        thisPlayer = new aPlayer();
        PC = new PHPController(aContext);
        //
        StatusChangeListener SCL = new StatusChangeListener()
        {
            @Override
            public void onHPChanged(int value)
            {
                if (value > 0) {
                    Log.wtf("bat","HP have been changed to "+value);
                    whenPlayerAct.changeHP(value);
                }else{
                    PC.clearSCListener();
                }
            }

            @Override
            public void onAMMOChanged(int value) {
                if (value > 0) {
                    Log.wtf("bat", "AMMO have been changed to " + value);
                    whenPlayerAct.changeAmmo(value);
                }else{
                    PC.clearSCListener();
                }
            }
        };
        //
        PC.addSCListener(SCL);
    }

    @Deprecated
    public void host(aGame aGame, aPlayer aPlayer){

    }

    public void hostGameMode(int gameMode){

        aGame aGame = new aGame();
        aGame.setGameId(GameIdmaker.newId());
        aGame.setGameTime(999);
        aGame.setPlayerCount(2);

        switch (gameMode){
            case 1:
                aGame.setSetAmmo(120);
                aGame.setSetHp(10);
                break;

            case 2:
                aGame.setSetAmmo(400);
                aGame.setSetHp(20);
                break;
        }

        aPlayer aPlayer = new aPlayer();
        aPlayer.setPlayerId(1);
        aPlayer.setPlayerName("host");

        hostCustomGame(aGame, aPlayer);
    }

    //
    public void hostCustomGame(aGame aGame,aPlayer aPlayer) {
        if (aGame.getGameId() != 0) {
            if (aPlayer.getPlayerId() != 0) {
                thisGame = aGame;
                thisPlayer = aPlayer;
                this.gameId = aGame.getGameId();
                PC.setGame(aGame.getPlayerCount(),
                        aGame.getSetHp(),
                        aGame.getSetAmmo(),
                        new PHPController.setGameCallback(gameId) {
                            @Override
                            public void run(boolean result) {
                                if (result) {
                                    whenSucceed.hostSucceed();
                                    connect();
                                    Log.wtf("GC", "set Game Succeed");
                                } else {
                                    whenSucceed.hostFailed();
                                    Log.wtf("GC", "set Game Failed");
                                }
                            }
                        });

            }else
                Log.wtf("GC", "Player is null!");
        }else
            Log.wtf("GC", "Game Id is null!");
    }

    //
    private void connect() {
        privateConnect(true);
    }

    //
    public void connect(long gameId){
        this.gameId = gameId;
        privateConnect(false);
    }

    //
    private void privateConnect(final boolean isCallByInside) {
        PC.connectGame(new PHPController.connectGameCallback(gameId) {
            @Override
            public void run(boolean result) {
                if (result) {
                    //
                    whenSucceed.connectSucceed();
                    //
                    if (!isCallByInside) {
                        thisGame.setGameId(gameId);
                        thisGame.setSetHp(PC.getSetHP());
                        thisGame.setSetAmmo(PC.getSetAMMO());
                        thisGame.setPlayerCount(2);
                        thisGame.setGameTime(999);
                        //
                        thisPlayer.setPlayerId(2);
                        thisPlayer.setPlayerName("join");
                    }
                    //
                    join();
                    //
                    Log.wtf("GC", "connect Game Succeed");
                } else {
                    whenSucceed.connectFailed();
                    Log.wtf("GC", "connect Game Failed");
                }
            }
        });
    }

    //
    private void join() {
        privateJoin();
    }

    //
    public void join(aPlayer aplayer){
        thisPlayer = aplayer;
        privateJoin();
    }

    //
    private void privateJoin(){
        PC.joinGame(new PHPController.joinGameCallback(
                thisPlayer.getPlayerId(),
                thisPlayer.getPlayerName()) {
            @Override
            public void run(boolean result) {
                if (result) {

                    whenSucceed.joinSucceed();

                    PC.startService();

                    Log.wtf("GC", "join Game Succeed");
                } else {
                    whenSucceed.joinFailed();
                    Log.wtf("GC", "join Game Failed");
                }
            }
        });
    }

    //
    public void startPcService(Context c){

        PC.startService(c);
    }

    //
    public void stopPcService(){
        PC.stopService();
    }

    //
    public void setWhenSucceedTarget(whenSucceed whenSucceed){
        this.whenSucceed=whenSucceed;
    }

    //
    public void setWhenPlayerActTarget(whenPlayerAct whenPlayerAct){
        this.whenPlayerAct=whenPlayerAct;
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
    public PHPController getPc(){
        return PC;
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

    //
    public  interface  whenPlayerAct{
        void changeHP(int value);

        void changeAmmo(int value);
    }
}