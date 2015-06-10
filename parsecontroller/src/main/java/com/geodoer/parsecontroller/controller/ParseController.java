package com.geodoer.parsecontroller.controller;

import android.content.Context;
import android.util.Log;

import com.geodoer.parsecontroller.R;
import com.geodoer.parsecontroller.column.ParseColumn;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MurasakiYoru on 2015/6/5.
 */
public class ParseController
{
    private Context context;

    private final String account_key ;
    private final String app_key;
    private final String table_name;

    private static long gameId = 0;
    private static String ObjectId ;
    private static int setHP;
    private static int setAMMO;

    public static PlayerStatus Player ;

    public ParseController(Context c)
    {
        this.context = c;
        this.account_key = context.getResources().getString(R.string.parse_account_key);
        this.app_key = context.getResources().getString(R.string.parse_app_key);
        this.table_name = context.getResources().getString(R.string.parse_table);

        Player = new PlayerStatus();

        Parse.initialize(context, account_key, app_key);
    }


    /**
     *         Host   method
     */
    public void setGame(int players, final int setHp, final int setAmmo,setGameCallback sGC)
    {
        //final long gId = GameIdmaker.newId();
        long gId = sGC.getgId();

        ParseObject thisGame;
        thisGame = new ParseObject(table_name);
        thisGame.put(ParseColumn.game.gameId , gId);
        thisGame.put(ParseColumn.game.startTime , (long)0);
        thisGame.put(ParseColumn.game.gPcount,players);
        thisGame.put(ParseColumn.game.setHp,setHp);
        thisGame.put(ParseColumn.game.setAmmo ,setAmmo);
        thisGame.put(ParseColumn.game.onlining, true);

        if(players >0)
        for(int i = 1 ; i <= players; i++)
        {
            thisGame.put(ParseColumn.player_stattus.Name(i),"empty");
            thisGame.put(ParseColumn.player_stattus.Hp(i),setHp);
            thisGame.put(ParseColumn.player_stattus.Ammo(i),setAmmo);
        }
        thisGame.saveInBackground(sGC);
    }
    public void connectGame(connectGameCallback cGC)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.whereEqualTo(ParseColumn.game.gameId,cGC.getgId());
        query.findInBackground(cGC);
    }

    public void joinGame(joinGameCallback jGC)
    {
        if(ObjectId == null || ObjectId.equals(""))
        {
            Log.wtf("PARSE", "join fail with no ObjectID");
            return;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.getInBackground(ObjectId, new joinGameGetCallback(jGC) );

    }



    public void closeGame()
    {

    }

    public void disconnectGame()
    {

    }


    /**
     * ------ get game info method ----
     */
    public void getOnliningGames(getOnliningGamesCallback callBack)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.whereEqualTo(ParseColumn.game.onlining,true);
        query.findInBackground(callBack);
    }

    public long getGameId()
    {
        return gameId;
    }
    public int getSetHP()
    {
        return setHP;
    }
    public int getSetAMMO()
    {
        return setAMMO;
    }
    public String getObjectId()
    {
        return ObjectId;
    }

    /**
     *
     *  player fields
     *
     */
    public class PlayerStatus
    {
        public static final String Ammo = "Ammo";
        public static final String Hp = "Hp";

        private boolean status = false;
        private String Name;
        private int Num = 0;
        private int HP = 0;
        private int AMMO = 0;

        public void setPlayer(boolean status,int num,String name,int hp ,int ammo)
        {
            this.Num = num;
            this.status = status;
            this.Name = name;
            this.HP = hp;
            this.AMMO = ammo;
        }
        public void updateInfo(updateInfoCallback uGC)
        {
            if(!status)
            {
                Log.wtf("PARSE","Player not stand by");
                return;
            }

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(table_name);
            query.getInBackground(ObjectId,new updateInfoGetCallback(uGC));

        }
        public void changeHP(int i)
        {
            HP += i;
        }
        public void changeAMMO(int i)
        {
            AMMO += i;
        }


        public boolean getStatus()
        {
            return status;
        }
        public int getNum()
        {
            return Num;
        }

        public int getHP()
        {
            return HP;
        }

        public int getAMMO()
        {
            return AMMO;
        }
        public String getName()
        {
            return Name;
        }


    }

    /**
     *
     *    Abstract
     *
    **/
    public static abstract class setGameCallback implements SaveCallback
    {
        private long gId;
        public setGameCallback(long ID)
        {
            this.gId = ID;
        }

        @Override
        public void done(ParseException e)
        {
            if(e==null)
            {

                gameId = gId;
                //Log.wtf("PARSE","set Game set Gameid = "+gameId);
                run(true);
            }
            else
                run(false);
        }
        public long getgId()
        {
            return gId;
        }
        public abstract void run(boolean result);
    }
    public static abstract class connectGameCallback implements FindCallback<ParseObject>
    {
        private long gId;
        public connectGameCallback(long ID)
        {
            this.gId = ID;
        }
        public long getgId()
        {
            return gId;
        }
        @Override
        public void done(List<ParseObject> parseObjects, ParseException e)
        {
            if(e==null)
            {
                //----exception with same ID but...

                Log.wtf("PARSE","connect call back get gameid="+getgId());
                //test the same ID exist or no game
                if(parseObjects.size()!= 1)
                {
                    run(false);
                    return;
                }

                ParseObject po = parseObjects.get(0);
                //onlining
                if(po.getBoolean(ParseColumn.game.onlining) == false)
                {
                    run(false);
                    return;
                }
                //
                if(po.getLong(ParseColumn.game.gameId) != gId)
                    run(false);
                else
                {
                    //connect  success
                    gameId = gId;
                    ObjectId = po.getObjectId();
                    setHP = po.getInt(ParseColumn.game.setHp);
                    setAMMO = po.getInt(ParseColumn.game.setAmmo);

                    run(true);
                    return;
                }

            }
            else
            {
                run(false);
                return;
            }
        }

        public abstract void run(boolean result);
    }
    public static abstract class getOnliningGamesCallback implements FindCallback<ParseObject>
    {
        @Override
        public void done(List<ParseObject> parseObjects, ParseException e)
        {
            if(e==null)
            {
                ArrayList<Long> AL = new ArrayList<>();
                for(ParseObject i : parseObjects)
                {
                    AL.add(i.getLong(ParseColumn.game.gameId));
                }

                run(true,AL);
            }
            else
                run(false,null);
        }
        public abstract void run(boolean result,ArrayList<Long> list);
    }


    private static class joinGameGetCallback implements GetCallback<ParseObject>
    {
        private int Num;
        private String Name;
        private int HP;
        private int AMMO;

        private joinGameCallback jGC;
        public joinGameGetCallback(joinGameCallback GC)
        {
            this.Num = GC.getNum();
            this.Name = GC.Name;
            this.AMMO = GC.getAMMO();
            this.HP = GC.getHP();

            this.jGC = GC;
        }

        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if(e==null)
            {
                parseObject.put(ParseColumn.player_stattus.Name(Num),Name);
                parseObject.put(ParseColumn.player_stattus.Hp(Num),HP);
                parseObject.put(ParseColumn.player_stattus.Ammo(Num),AMMO);
                parseObject.saveInBackground(jGC);
            }
        }

    }
    public static abstract class joinGameCallback implements SaveCallback
    {
        private int Num;
        private String Name;
        private int HP;
        private int AMMO;



        public joinGameCallback(int num, String name)
        {
            this.Num = num;
            this.Name = name;
            this.HP = setHP;
            this.AMMO = setAMMO;
        }

        @Override
        public void done(ParseException e)
        {
            if(e==null)
            {
                Player.setPlayer(true,Num,Name,HP,AMMO);
                run(true);
            }
            else
                run(false);
        }
        public abstract void run(boolean result);


        public int getNum() {
            return Num;
        }
        public String getName() {
            return Name;
        }

        public int getHP() {
            return HP;
        }

        public int getAMMO() {
            return AMMO;
        }
    }


    private static class updateInfoGetCallback implements GetCallback<ParseObject>
    {
        private String item;
        private int change;

        private updateInfoCallback uGC;
        public updateInfoGetCallback(updateInfoCallback GC)
        {
            this.uGC = GC;
            //Log.wtf("PARSE" ,"uGC .getItem  = "+uGC.getItem());
            if(uGC.getItem().equals(Player.Ammo))
            {
                this.item = ParseColumn.player_stattus.Ammo(Player.getNum());
                change = Player.getAMMO() + uGC.getChange();
            }
            else if(uGC.getItem().equals(Player.Hp))
            {
                this.item = ParseColumn.player_stattus.Hp(Player.getNum());
                change = Player.getHP() + uGC.getChange();
            }
            //Log.wtf("PARSE","updateInfoGetCallback item = "+item);
            //Log.wtf("PARSE","updateInfoGetCallback change = "+change);
        }

        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if(e==null)
            {
                if(item==null)return;

                parseObject.put(item,change);
                parseObject.saveInBackground(uGC);
            }
        }

    }
    public static abstract class updateInfoCallback implements SaveCallback
    {
        private String item;
        private int change;

        public updateInfoCallback(String it,int ch)
        {
            this.item = it;
            this.change = ch;
        }


        @Override
        public void done(ParseException e)
        {
            if(e==null)
            {
                if (item.equals(Player.Ammo))
                {
                    Player.changeAMMO(change);
                    run(true);
                }
                else if (item.equals(Player.Hp))
                {
                    Player.changeHP(change);
                    run(true);
                }
                else run(false);
            }
            else
                run(false);
        }
        public abstract void run(boolean result);


        public String getItem() {
            return item;
        }
        public int getChange() {
            return change;
        }
    }

}


