package com.geodoer.phpcontroller.controller;

import android.content.Context;

import com.geodoer.phpcontroller.R;

import java.util.Objects;

/**
 *
 *  Created by MurasakiYoru on 2015/6/18.
 */
public class PHPaddressBuilder
{
    public static final String AddGame = "Add";
    public static final String GetGame = "Get";
    public static final String UpdateGame = "Update";
    public static final String Shootout = "shootout";

    private static String PHPadd;
    private static String PHPget;
    private static String PHPupdate;
    private static String PHPshootout;

    //private Context context;

    //private StringBuilder SB;
    private StringBuffer SB;
    private String prefix;

    public PHPaddressBuilder(Context c)
    {
        PHPadd = c.getResources().getString(R.string.add);
        PHPget = c.getResources().getString(R.string.get);
        PHPupdate = c.getResources().getString(R.string.update);
        PHPshootout = c.getResources().getString(R.string.shootout);

        //SB = new StringBuilder();
        SB = new StringBuffer();
    }
    private void clear()
    {
        SB.setLength(0);
        prefix = "?";
    }
    public PHPaddressBuilder setTag(String tag)
    {
        clear();
        switch (tag)
        {
            case AddGame:
                SB.append(PHPadd);
                break;

            case GetGame:
                SB.append(PHPget);
                break;

            case UpdateGame:
                SB.append(PHPupdate);
                break;

            case Shootout:
                SB.append(PHPshootout);
                break;

            default:
        }

        return this;
    }
    public PHPaddressBuilder addParameter(String row,String val)
    {
        SB.append(prefix);
        prefix = "&";
        SB.append(row);
        SB.append("=");
        SB.append(val);
        return this;
    }
    public PHPaddressBuilder addParameter(String row,int val)
    {
        return addParameter(row,val+"");
    }
    public PHPaddressBuilder addParameter(String row,long val)
    {
        return addParameter(row,val+"");
    }
    public String build()
    {
        return SB.toString();
    }
}
