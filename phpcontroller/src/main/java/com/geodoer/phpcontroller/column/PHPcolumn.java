package com.geodoer.phpcontroller.column;

/**
 * Created by MurasakiYoru on 2015/6/18.
 */
public class PHPcolumn
{
    public static class game
    {
        public static final String objectId = "id";
        public static final String objectId_json = "_id";

        public static final String gameId = "gId";

        public static final String gPcount ="gPcount";
        public static final String setHp = "sHp";
        public static final String setAmmo = "sAmmo";
        public static final String startTime = "sTime";


        public static final String onlining = "onlining";
    }
    public static class player_stattus
    {
        public static String Name  (int num) { return "p"+ num + "Name"  ; }
        public static String Hp    (int num) { return "p"+ num + "Hp"    ; }
        public static String Ammo  (int num) { return "p"+ num + "Ammo"  ; }
    }

}
