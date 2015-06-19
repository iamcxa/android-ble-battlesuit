package com.geodoer.parsecontroller.controller;

/**
 * Created by MurasakiYoru on 2015/6/9.
 */
public class GameIdmaker
{
    private static long nowId;

    public static long newId()
    {
        nowId=System.currentTimeMillis();
        return nowId;
    }

    public static long getNowId(){
        return nowId;
    }
}
