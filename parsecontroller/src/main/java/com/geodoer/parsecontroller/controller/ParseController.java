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

public class ParseController
{
    private static final String TAG = "ParseController";

    private final String table_name;

    private static long gameId;
    private static String ObjectId ;
    private static int player_count;
    private static int setHP;
    private static int setAMMO;

    private static final String DEFAULT_PLAYER_NAME = "empty";
    public static PlayerStatus Player ;

    public ParseController(Context c)
    {
        this.table_name = c.getResources().getString(R.string.parse_table);
        Parse.initialize(c,
                c.getResources().getString(R.string.parse_account_key),
                c.getResources().getString(R.string.parse_app_key) );

        initialize_all();
    }
    private void initialize_all()
    {
        gameId = 0;
        ObjectId = "";
        setHP = 0;
        setAMMO = 0;
        Player = new PlayerStatus();
    }


    /**
     *         Host   method
     */
    public void setGame(final int players, final int setHp, final int setAmmo,setGameCallback sGC)
    {
        long gId = sGC.getgId();
        ParseObject thisGame = new ParseObject(table_name);
        thisGame.put(ParseColumn.game.gameId , gId);
        thisGame.put(ParseColumn.game.startTime , 0);
        thisGame.put(ParseColumn.game.gPcount, players);
        thisGame.put(ParseColumn.game.setHp, setHp);
        thisGame.put(ParseColumn.game.setAmmo , setAmmo);
        thisGame.put(ParseColumn.game.onlining, true);
        if(players >0)
        for(int i = 1 ; i <= players; i++)
        {
            thisGame.put(ParseColumn.player_stattus.Name(i),DEFAULT_PLAYER_NAME);
            thisGame.put(ParseColumn.player_stattus.Hp(i),setHp);
            thisGame.put(ParseColumn.player_stattus.Ammo(i),setAmmo);
            thisGame.put(ParseColumn.player_stattus.Ammo_t(i),0);
        }

        thisGame.saveInBackground(sGC);
    }


    public void connectGame(connectGameCallback cGC)
    {
        if(cGC.getgId() == 0)
        {
            Log.wtf(TAG,"connectGame id cannot be 0");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.whereEqualTo(ParseColumn.game.gameId,cGC.getgId());
        query.findInBackground(cGC);
    }

    public void joinGame(joinGameCallback jGC)
    {
        if(ObjectId.equals("") || ObjectId.isEmpty() )
        {
            Log.wtf(TAG, "joinGame failure with no ObjectID, Please connectGame before join");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.getInBackground(ObjectId, new joinGameGetCallback(jGC) );
    }


//    public void closeGame()
//    {
//
//    }
//
//    public void disconnectGame()
//    {
//
//    }


    /**
     * ------ get game info method ----
     */
    public void getOnlineGames(getOnlineGamesCallback gGC)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.whereEqualTo(ParseColumn.game.onlining,true);
        query.orderByDescending("createAt");
        query.findInBackground(gGC);
    }
    public void getGameInformation (getGameInformationCallback gGC)
    {
        if(ObjectId.equals("") || ObjectId.isEmpty() )
        {
            Log.wtf(TAG, "getGameInformation failure with no ObjectID, Please connectGame before join");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.getInBackground(ObjectId,gGC);
    }
    public void getWhoShooting(getWhoShootCallback gWC)
    {
        if(ObjectId.equals("") || ObjectId.isEmpty() )
        {
            Log.wtf(TAG, "getWhoShooting failure with no ObjectID, Please connectGame before get");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table_name);
        query.getInBackground(ObjectId,gWC);
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
        public static final String AMMO = "Player_Ammo";
        public static final String HP = "Player_Hp";
        public static final String AMMO_t = "Player_Ammo_t";
        private boolean status = false;
        private String Name;
        private int Num = 0;
        private int Hp = 0;
        private int Ammo = 0;
        private int Ammo_t = 0;

        public PlayerStatus()
        {
            this(false,0,"",0,0);
        }
        public PlayerStatus(boolean status,int num,String name,int hp ,int ammo)
        {
            setPlayerStatus(status, num, name, hp, ammo);
        }
        public void setPlayerStatus(boolean status,int num,String name,int hp ,int ammo)
        {
            this.status = status;
            this.Num = num;
            this.Name = name;
            this.Hp = hp;
            this.Ammo = ammo;
            this.Ammo_t = 0;
        }

        public void updateInfo(updateInfoCallback uGC)
        {
            if(!status)
            {
                Log.wtf(TAG,"Player not stand by, Please joinGame before");
                return;
            }
            ParseQuery<ParseObject> query = new ParseQuery<>(table_name);
            query.getInBackground(ObjectId,new updateInfoGetCallback(uGC));
        }
        public void changeHP(int i)
        {
            this.Hp += i;
        }
        public void changeAMMO(int i)
        {
            this.Ammo += i;
        }
        public void changeAMMO_t(int i) { this.Ammo_t = i;}

        public boolean getStatus()
        {
            return status;
        }
        public int getNum()
        {
            return Num;
        }
        public int getHp()
        {
            return Hp;
        }
        public int getAmmo()
        {
            return Ammo;
        }
        public int getAmmo_t()
        {
            return Ammo_t;
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
                run(true);
            }
            else
            {
                run(false);
                Log.wtf(TAG,"setGameCallback exception:"+e.toString());
            }
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

        @Override
        public void done(List<ParseObject> parseObjects, ParseException e)
        {
            if(e==null)
            {
                if(parseObjects.size()== 0)
                {
                    run(false);
                    Log.wtf(TAG,"connectGameCallback exception: No game match this ID");
                }
                else if(parseObjects.size()> 1)
                {
                    run(false);
                    Log.wtf(TAG,"connectGameCallback exception: Multi game(more than 1) match this ID");
                }

                ParseObject po = parseObjects.get(0);
                if(!po.getBoolean(ParseColumn.game.onlining))
                {
                    run(false);
                    Log.wtf(TAG,"connectGameCallback exception: this game with this ID Not Online");
                }
                else
                {
                    //connect  success
                    gameId = gId;
                    ObjectId = po.getObjectId();
                    setHP = po.getInt(ParseColumn.game.setHp);
                    setAMMO = po.getInt(ParseColumn.game.setAmmo);
                    player_count = po.getInt(ParseColumn.game.gPcount);

                    run(true);
                }
            }
            else
            {
                run(false);
                Log.wtf(TAG,"connectGameCallback exception:"+e.toString());
            }
        }
        public abstract void run(boolean result);
        public long getgId()
        {
            return gId;
        }
    }
    public static abstract class getOnlineGamesCallback implements FindCallback<ParseObject>
    {
        @Override
        public void done(List<ParseObject> parseObjects, ParseException e)
        {
            if (e == null)
            {
                ArrayList<Long> AL = new ArrayList<>();
                for (ParseObject i : parseObjects) AL.add(i.getLong(ParseColumn.game.gameId));
                run(true, AL);
            }
            else
            {
                run(false, null);
                Log.wtf(TAG,"connectGameCallback exception:"+e.toString());
            }
        }
        public abstract void run(boolean result,ArrayList<Long> list);
    }
    public static abstract class getGameInformationCallback implements GetCallback<ParseObject>
    {
        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if (e == null)
            {
                ArrayList<String> Name = new ArrayList<>();
                ArrayList<Integer> Hp = new ArrayList<>();
                ArrayList<Integer> Ammo = new ArrayList<>();
                for(int i = 1; i <= player_count ; i++)
                {
                    Name.add(parseObject.getString(ParseColumn.player_stattus.Name(i)));
                    Hp.add(parseObject  .getInt(ParseColumn.player_stattus.Hp(i)));
                    Ammo.add(parseObject.getInt(ParseColumn.player_stattus.Ammo(i)));
                }
                run(true,player_count,Name,Hp,Ammo);
            }
            else
            {

                run(false,0,null,null,null);
                Log.wtf(TAG,"getGameInformationCallback exception:"+e.toString());
            }

        }
        public abstract void run(boolean result,int Player_count,
                                 ArrayList<String> Name_list,
                                 ArrayList<Integer> Hp_list,
                                 ArrayList<Integer> Ammo_list );
    }
    public static abstract class getWhoShootCallback implements  GetCallback<ParseObject>
    {
        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if (e==null)
            {
                int num = parseObject.getInt(ParseColumn.game.gPcount);
                ArrayList<Integer> list = new ArrayList<>();
                if(num>0)
                {
                    for (int i = 1; i <= num; i++)
                    {
                        if (parseObject.getInt(ParseColumn.player_stattus.Ammo_t(i)) == 1)
                            list.add(i);
                    }
                    run(true, list);
                }
                else
                {
                    Log.wtf(TAG,"getWhoShootingCallback exception: player count error?");
                    run(false,null);
                }
            }
            else
            {
                run(false,null);
                Log.wtf(TAG,"getWhoShootCallback exception:"+e.toString());
            }

        }
        public abstract void run(boolean result,ArrayList<Integer> list);
    }


    private static class joinGameGetCallback implements GetCallback<ParseObject>
    {
        private int Num;
        private String Name;
        private int Hp;
        private int Ammo;
        private joinGameCallback jGC;
        public joinGameGetCallback(joinGameCallback GC)
        {
            this.Num = GC.getNum();
            this.Name = GC.getName();
            this.Hp = GC.getHp();
            this.Ammo = GC.getAmmo();
            this.jGC = GC;
        }
        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if(e==null)
            {
                if(!parseObject.getBoolean(ParseColumn.game.onlining))
                {
                    jGC.run(false);
                    Log.wtf(TAG,"joinGameCallback exception: game offline");
                    return;
                }
                if( !parseObject.getString(ParseColumn.player_stattus.Name(Num)).equals(DEFAULT_PLAYER_NAME) )
                {
                    jGC.run(false);
                    Log.wtf(TAG,"joinGameCallback exception: this Player_Num has been used");
                    return;
                }
                parseObject.put(ParseColumn.player_stattus.Name(Num),Name);
                parseObject.put(ParseColumn.player_stattus.Hp(Num), Hp);
                parseObject.put(ParseColumn.player_stattus.Ammo(Num), Ammo);
                parseObject.saveInBackground(jGC);
            }
            else
            {
                jGC.run(false);
                Log.wtf(TAG,"joinGameCallback exception:"+e.toString());
            }
        }

    }
    public static abstract class joinGameCallback implements SaveCallback
    {
        private int Num;
        private String Name;
        private int Hp;
        private int Ammo;

        public joinGameCallback(int num, String name)
        {
            this.Num = num;
            this.Name = name;
            this.Hp = setHP;
            this.Ammo = setAMMO;
        }

        @Override
        public void done(ParseException e)
        {
            if(e==null)
            {
                Player.setPlayerStatus(true, Num, Name, Hp, Ammo);
                run(true);
            }
            else
            {
                run(false);
                Log.wtf(TAG,"joinGameCallback exception:"+e.toString());
            }
        }
        public abstract void run(boolean result);

        public int getNum() {
            return Num;
        }
        public String getName() {
            return Name;
        }
        public int getHp() {
            return Hp;
        }
        public int getAmmo() {
            return Ammo;
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
            int Num = Player.getNum();

            switch ( uGC.getItem() )
            {
                case PlayerStatus.AMMO:

                    this.item = ParseColumn.player_stattus.Ammo(Num);
                    change = Player.getAmmo() + uGC.getChange();
                    break;

                case PlayerStatus.HP:

                    this.item = ParseColumn.player_stattus.Hp(Num);
                    change = Player.getHp() + uGC.getChange();
                    break;

                case PlayerStatus.AMMO_t:

                    this.item = ParseColumn.player_stattus.Ammo_t(Num);
                    change = uGC.getChange();

                    if(change == Player.getAmmo_t())
                    {
                        Log.wtf(TAG,"updateInfoGetCallback exception: AMMO_t have the same status");
                        uGC.run(false);
                        return;
                    }
                    break;

                default:

                    this.item = null;
            }

        }

        @Override
        public void done(ParseObject parseObject, ParseException e)
        {
            if(e==null)
            {
                if( item == null || item.isEmpty() )
                {
                    uGC.run(false);
                    Log.wtf(TAG,"updateInfoGetCallback exception: item name error(neither HP ,AMMO nor AMMO_t ?)");
                    return;
                }
                if(!parseObject.getBoolean(ParseColumn.game.onlining))
                {
                    uGC.run(false);
                    Log.wtf(TAG,"updateInfoGetCallback exception: game offline");
                    return;
                }

                parseObject.put(item,change);
                parseObject.saveInBackground(uGC);
            }
            else
            {
                uGC.run(false);
                Log.wtf(TAG,"updateInfoGetCallback exception:" +e.toString());
            }
        }

    }
    public static abstract class updateInfoCallback implements SaveCallback
    {
        private String item;
        private int change;

        public updateInfoCallback(String item,int change)
        {
            this.item = item;
            this.change = change;
        }


        @Override
        public void done(ParseException e)
        {
            if(e==null)
            {
                switch(item)
                {
                    case PlayerStatus.AMMO:

                        Player.changeAMMO(change);
                        run(true);
                        break;

                    case PlayerStatus.HP:

                        Player.changeHP(change);
                        run(true);
                        break;

                    case PlayerStatus.AMMO_t:

                        Player.changeAMMO_t(change);
                        run(true);
                        break;

                    default:

                        run(false);
                        Log.wtf(TAG,"updateInfoGetCallback exception: item name error(neither HP nor AMMO?)");
                }

            }
            else
            {
                run(false);
                Log.wtf(TAG,"updateInfoGetCallback exception:" +e.toString());
            }
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


