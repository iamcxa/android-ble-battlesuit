package com.geodoer.phpcontroller.utils;

/**
 * Created by MurasakiYoru on 2015/6/21.
 */
public interface StatusChangeListener
{
    //final static String SCL_HP = "hp";

    //final static String SCL_AMMO = "ammo";

    //void onStatusChanged(String tag,int value);

    void onHPChanged(int value);

    void onAMMOChanged(int value);

}
