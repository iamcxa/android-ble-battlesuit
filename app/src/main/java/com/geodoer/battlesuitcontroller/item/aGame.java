package com.geodoer.battlesuitcontroller.item;

/**
 * Created by iamcx_000 on 2015/6/18.
 */
//
public class aGame{
    private int playerCount;
    private int setHp;
    private int setAmmo;
    private int gameTime;


    public aGame() {
        this.playerCount = 2;
        this.setHp = 10;
        this.setAmmo = 120;
        this.gameTime = 600;
        this.gameId = 0;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    private long gameId;

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getSetHp() {
        return setHp;
    }

    public void setSetHp(int setHp) {
        this.setHp = setHp;
    }

    public int getSetAmmo() {
        return setAmmo;
    }

    public void setSetAmmo(int setAmmo) {
        this.setAmmo = setAmmo;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }
}