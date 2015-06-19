package com.geodoer.parsecontroller.column;

/**
 * Created by MurasakiYoru on 2015/6/8.
 */
public class ParseColumn
{
    public static class game
    {
        //
        //public static final String objectId = "objectId";

        //game setting
        public static final String gameId = "gId";
        public static final String gPcount ="gPcount";
        public static final String onlining = "onlining";

        public static final String setHp = "sHp";
        public static final String setAmmo = "sAmmo";
        public static final String startTime = "sTime";
    }
    public static class player_stattus
    {
        public static String Name  (int num) { return "p"+ num + "Name"  ; }
        public static String Hp    (int num) { return "p"+ num + "Hp"    ; }
        public static String Ammo  (int num) { return "p"+ num + "Ammo"  ; }
        public static String Ammo_t(int num) { return "p"+ num + "Ammo_t"; }
    }
}
