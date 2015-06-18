package com.geodoer.phpcontroller.controller;

import android.content.Context;
import android.util.Log;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;

/**
 *
 *  Created by MurasakiYoru on 2015/6/18.
 *
 */
public class PHPController
{


    private static final String status = "status" ;
    private static final String data = "data" ;
    private static final String error = "ErrorMessage";
    private static final String TAG = "ParseController";
    private static final String DEFAULT_PLAYER_NAME = "empty";

    private Context context;

    private static long gameId;

    private static int ObjectId ;
    //private static int player_count;
    private static int setHP;
    private static int setAMMO;

    private static int player_num;
    private static String player_name;
    private static int player_hp;
    private static int player_ammo;



    private static PHPaddressBuilder PB;

    public PHPController(Context c)
    {
        this.context = c;
        PB = new PHPaddressBuilder(c);

        gameId = 0;
        ObjectId = 0;
        setHP = 0;
        setAMMO = 0;

        player_num = 0;
        player_name = DEFAULT_PLAYER_NAME;
        player_hp = 0;
        player_ammo = 0;

    }
    public void setGame(final int players_count, final int setHp, final int setAmmo,setGameCallback sGC)
    {
        long gId = sGC.getgId();

        PB.setTag(PHPaddressBuilder.AddGame)
          .addParameter(PHPcolumn.game.gameId,gId)
          .addParameter(PHPcolumn.game.gPcount,players_count)
          .addParameter(PHPcolumn.game.onlining,1)
          .addParameter(PHPcolumn.game.setHp, setHp)
          .addParameter(PHPcolumn.game.setAmmo, setAmmo)
          .addParameter(PHPcolumn.game.startTime, 0);

        if(players_count >0)
        for(int i = 1 ; i <= players_count ; i++)
        {
            PB.addParameter(PHPcolumn.player_stattus.Name(i),DEFAULT_PLAYER_NAME)
              .addParameter(PHPcolumn.player_stattus.Hp(i),setHp)
              .addParameter(PHPcolumn.player_stattus.Ammo(i),setAmmo);
        }

        Log.wtf(TAG,PB.build());

        Ion.with(context)
                .load(PB.build())
                .asJsonObject()
                .setCallback(sGC);

    }
    public void connectGame(connectGameCallback cGC)
    {
        if(cGC.getgId() == 0)
        {
            Log.wtf(TAG, "connectGame id cannot be 0");
            return;
        }
        String uri =
            PB.setTag(PHPaddressBuilder.GetGame)
              .addParameter("row",PHPcolumn.game.gameId)
              .addParameter("val",cGC.getgId())
              .build();

        Ion.with(context)
           .load(uri)
           .asJsonObject()
           .setCallback(cGC);
    }

    public void joinGame(joinGameCallback jGC)
    {
        if(ObjectId==0  )
        {
            Log.wtf(TAG, "joinGame failure with no ObjectID, Please connectGame before join");
            return;
        }
        jGC.setContext(context);

        String uri = PB.setTag(PHPaddressBuilder.GetGame)
                .addParameter("row",PHPcolumn.game.objectId_json)
                .addParameter("val", ObjectId)
                .build();

        Ion.with(context)
           .load(uri)
           .asJsonObject()
           .setCallback(jGC);
    }


    /**
     * ------ get game info method ----
     */
    public void getOnlineGames(getOnlineGamesCallback gGC)
    {
        String uri =
            PB.setTag(PHPaddressBuilder.GetGame)
              .addParameter("row",PHPcolumn.game.onlining)
              .addParameter("val",1)
              .build();

        Ion.with(context)
                .load(uri)
                .asJsonObject()
                .setCallback(gGC);
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
    public int getObjectId()
    {
        return ObjectId;
    }

    public int getPlayer_num() { return player_num; }
    public String getPlayer_name() { return player_name; }
    public int getPlayer_hp() { return player_hp; }
    public int getPlayer_ammo() { return player_ammo; }

    /**
     *
     *     callbacks
     *
     */
    public static abstract class setGameCallback implements FutureCallback<JsonObject>
    {
        private long gId;
        public setGameCallback(long ID)
        {
            this.gId = ID;
        }

        @Override
        public void onCompleted(Exception e, JsonObject result)
        {
            if(e==null)
            {
                if(result.get(status).getAsInt() == 1)
                {
                    gameId = gId;
                    run(true);
                }
                else
                {
                    run(false);
                    Log.wtf(TAG, "setGameCallback exception:"+result.get(error).getAsString());
                }
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
    public static abstract class connectGameCallback implements FutureCallback<JsonObject>
    {
        private long gId;
        public connectGameCallback(long ID)
        {
            this.gId = ID;
        }
        @Override
        public void onCompleted(Exception e, JsonObject result)
        {
            if(e==null)
            {
                if(result.get(status).getAsInt() == 1)
                {
                    JsonArray JA = result.getAsJsonArray(data);
                    if(JA.size() == 0)
                    {
                        run(false);
                        Log.wtf(TAG,"connectGameCallback exception: No game match this ID");
                    }
                    else if (JA.size() > 1)
                    {
                        run(false);
                        Log.wtf(TAG,"connectGameCallback exception: Multi game(more than 1) match this ID");
                    }
                    JsonObject JO = JA.get(0).getAsJsonObject();
                    if(JO.get(PHPcolumn.game.onlining).getAsInt() != 1)
                    {
                        run(false);
                        Log.wtf(TAG,"connectGameCallback exception: this game with this ID Not Online");
                    }
                    else
                    {
                        gameId = gId;
                        ObjectId = JO.get(PHPcolumn.game.objectId_json).getAsInt();
                        setHP = JO.get(PHPcolumn.game.setHp).getAsInt();
                        setAMMO = JO.get(PHPcolumn.game.setAmmo).getAsInt();
                        //player_count = JO.get(PHPcolumn.game.gPcount).getAsInt();
                        run(true);
                    }
                }
                else
                {
                    run(false);
                    Log.wtf(TAG, "connectGameCallback exception:"+result.get(error).getAsString());
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

    public static abstract class joinGameCallback implements FutureCallback<JsonObject>
    {
        private int Num;
        private String Name;
        private int hp;
        private int ammo;
        private Context context;
        public joinGameCallback(int num , String name)
        {
            this.Num = num;
            this.Name = name;
        }
        @Override
        public void onCompleted(Exception e, JsonObject result)
        {
            if(e==null)
            {
                if(result.get(status).getAsInt() == 1)
                {
                    JsonObject JO = result.get(data).getAsJsonArray().get(0).getAsJsonObject();
                    if(JO.get(PHPcolumn.game.onlining).getAsInt() !=1)
                    {
                        run(false);
                        Log.wtf(TAG,"joinGameCallback exception: game offline");
                    }
                    else if(!JO.get(PHPcolumn.player_stattus.Name(Num)).getAsString().equals(DEFAULT_PLAYER_NAME))
                    {
                        run(false);
                        Log.wtf(TAG,"joinGameCallback exception: this Player_Num has been used");
                    }
                    else
                    {
                        hp = JO.get(PHPcolumn.player_stattus.Hp(Num)).getAsInt();
                        ammo = JO.get(PHPcolumn.player_stattus.Ammo(Num)).getAsInt();

                        String uri = PB.setTag(PHPaddressBuilder.UpdateGame)
                                .addParameter("id",ObjectId)
                                .addParameter("row",PHPcolumn.player_stattus.Name(Num))
                                .addParameter("val",Name)
                                .build();
                        Ion.with(context)
                           .load(uri)
                           .asJsonObject()
                           .setCallback(new FutureCallback<JsonObject>()
                           {
                               @Override
                               public void onCompleted(Exception e, JsonObject result)
                               {
                                   if(e==null)
                                   {
                                       if(result.get(status).getAsInt() == 1)
                                       {
                                           player_num= getNum();
                                           player_name = getName();
                                           player_hp = getHp();
                                           player_ammo = getAmmo();
                                           run(true);
                                       }
                                       else
                                       {
                                           run(false);
                                           Log.wtf(TAG, "joinGameCallback exception:"+result.get(error).getAsString());
                                       }
                                   }
                                   else
                                   {
                                       run(false);
                                       Log.wtf(TAG,"joinGameCallback exception:"+e.toString());
                                   }

                               }
                           });
                    }
                }
                else
                {
                    run(false);
                    Log.wtf(TAG, "joinGameCallback exception:"+result.get(error).getAsString());
                }
            }
            else
            {
                run(false);
                Log.wtf(TAG,"joinGameCallback exception:"+e.toString());
            }
        }
        public void setContext(Context c) { this.context = c; }
        public String getName() { return Name; }
        public int getNum() { return Num; }
        public int getAmmo() { return ammo; }
        public int getHp() { return hp; }
        public abstract void run(boolean result);
    }

    public static abstract class getOnlineGamesCallback implements  FutureCallback<JsonObject>
    {
        @Override
        public void onCompleted(Exception e, JsonObject result)
        {
            if (e == null)
            {
                if(result.get(status).getAsInt() == 1)
                {
                    ArrayList<Long> AL = new ArrayList<>();
                    JsonArray JA = result.getAsJsonArray(data);
                    for(int i = 0 ; i< JA.size() ; i++)
                        AL.add(JA.get(i).getAsJsonObject().get(PHPcolumn.game.gameId).getAsLong());
                    run(true, AL);
                }
                else
                {
                    run(false, null);
                    Log.wtf(TAG, "connectGameCallback exception:"+result.get(error).getAsString());
                }
            }
            else
            {
                run(false, null);
                Log.wtf(TAG,"connectGameCallback exception:"+e.toString());
            }
        }
        public abstract void run(boolean result,ArrayList<Long> list);
    }


}
