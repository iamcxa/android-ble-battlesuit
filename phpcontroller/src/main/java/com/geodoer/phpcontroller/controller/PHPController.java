package com.geodoer.phpcontroller.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

//import com.geodoer.bluetoothcontroler.service.BluetoothLeService;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.utils.StatusChangeListener;
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
public class PHPController extends Service
{
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";


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
     *
     * Listener block
     *
     */
    private static ArrayList<StatusChangeListener> SCL_list = new ArrayList<>();
    public void addSCListener(StatusChangeListener SCL)
    {
        SCL_list.add(SCL);
    }
    public static void clearSCListener()
    {
        SCL_list.clear();
    }
    private static void runSCListener(int tag, int value)
    {
        if(!SCL_list.isEmpty())
            for(StatusChangeListener SCL:SCL_list)
                switch(tag)
                {
                    case 1: //HP
                        SCL.onHPChanged(value);
                        break;
                    case 2: //AMMO
                        SCL.onAMMOChanged(value);
                        break;
                }
    }
    /**
     *
     *  start service
     *
    **/


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

    /**
     *
     * getter setter
     */

    public long getGameId(){ return gameId; }
    public int getSetHP(){ return setHP; }
    public int getSetAMMO(){ return setAMMO; }
    public int getObjectId(){ return ObjectId; }

    public int getPlayer_num() { return player_num; }
    public String getPlayer_name() { return player_name; }
    public int getPlayer_hp() { return player_hp; }
    public int getPlayer_ammo() { return player_ammo; }


    public void setPlayer_hp(int player_HP)
    {
        player_hp = player_HP;
        runSCListener(1,player_HP);
    }

    public void setPlayer_ammo(int player_AMMO)
    {
        player_ammo = player_AMMO;
        runSCListener(2,player_AMMO);
    }

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


    /***
     *
     * Service
     *
     */

    public PHPController()
    {

    }

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public void startService()
    {
        if(ObjectId==0  )
        {
            Log.wtf(TAG, "startService failure with no ObjectID, Please connectGame before startService");
            return;
        }

        Intent intent = new Intent(context,PHPController.class);
        context.startService(intent);
    }
    public void stopService()
    {
        Intent intent = new Intent(context,PHPController.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mHandlerThread= new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        Log.wtf(TAG,"PHP Service onStartCommand");

        registerReceiver(mBattleSuitReceiver, makeGattUpdateIntentFilter());
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mHandlerThread = null;

        Log.wtf(TAG,"PHP Service onDestroy");

        unregisterReceiver(mBattleSuitReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mBattleSuitReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(EXTRA_DATA);
                data = data.substring(0, 2).toUpperCase();
                if(data.equals("CC"))
                {
                    mHandler.post(new postToServer(data));
                }
                else
                {
                    mHandler.postDelayed(new postToServer(data), 100);
                }

                Log.wtf(TAG,"mBattleSuitReceiver onReceive = "+data);
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /***
     *
     *
     **/
    private class postToServer implements Runnable
    {
        private String message;
        private postToServer(String m)
        {
            this.message = m.toUpperCase();
        }
        @Override
        public void run()
        {
            //final int num = message.equals("CC")? 1:2;

            final String url =
            PB.setTag(PHPaddressBuilder.Shootout)
              .addParameter("id",ObjectId)
              //.addParameter("p",player_num)
              .addParameter("p",player_num)
              .addParameter("val",message)
              .build();

            Ion.with(getApplicationContext())
               .load(url)
               .asJsonObject()
               .setCallback(new FutureCallback<JsonObject>() {
                   @Override
                   public void onCompleted(Exception e, JsonObject result)
                   {
                       if (e == null && result.get(status).getAsInt() == 1)
                       {
                           Log.wtf(TAG,"postToServer post receive uri = " + url);

                           JsonObject JO = result.get(data).getAsJsonObject();
                           switch (message)
                           {
                               case "AA":
                               case "BB":
                                   if(!JO.get("miss").getAsBoolean())
                                   {
                                       //Log.wtf(TAG,"playernum = "+num);
                                       setPlayer_hp(JO.get(PHPcolumn.player_stattus.Hp(player_num)).getAsInt());
                                   }
                                   else
                                   {
                                       Log.wtf(TAG,"AABB miss");
                                   }
                                   //Log.wtf(TAG, "postToServer :setHP"+ JO.get(PHPcolumn.player_stattus.Hp(player_num)).getAsInt() ) ;
                                   break;

                               case "CC":

                                   setPlayer_ammo(JO.get(PHPcolumn.player_stattus.Ammo(player_num)).getAsInt());
                                   //Log.wtf(TAG, "postToServer :setAmmo"+ JO.get(PHPcolumn.player_stattus.Ammo(player_num)).getAsInt() ) ;
                                   break;
                           }
                       }
                       else if(e != null)
                       {
                           Log.wtf(TAG, "postToServer exception:" + e.toString());
                       }
                       else
                       {
                           Log.wtf(TAG, "postToServer exception:status != 1");
                       }

                   }
               });

        }
    }
}
