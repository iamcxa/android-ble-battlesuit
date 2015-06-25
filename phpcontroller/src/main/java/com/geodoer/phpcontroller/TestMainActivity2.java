package com.geodoer.phpcontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geodoer.bluetoothcontroler.controller.GeoBleController;
import com.geodoer.bluetoothcontroler.service.GeoBleService;
import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.controller.GameIdmaker;
import com.geodoer.phpcontroller.controller.PHPController;
import com.geodoer.phpcontroller.controller.PHPaddressBuilder;
import com.geodoer.phpcontroller.utils.StatusChangeListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by MurasakiYoru on 2015/6/25.
 */
public class TestMainActivity2 extends Activity
{
    private Button button_host,button_join,button_CC,button_BB,button_refresh,button_BLE,button_reset;
    private TextView status_game,status_player1,status_player2,title,text_warning,device_address;

    private static final String WARNING = "WARING";

    private Boolean flag = false;

    private Boolean warning_flag = false;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private static final String status = "status" ;
    private static final String data = "data" ;
    private static final String error = "ErrorMessage";
    private static final String TAG = "ParseController";
    private static final String DEFAULT_PLAYER_NAME = "empty";

    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private PHPController PC;
    private PHPaddressBuilder PB;
    //private GeoBleController GBC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        registerReceiver(mReceiver, makeGattUpdateIntentFilter());

        PC= new PHPController(this);
        PB = new PHPaddressBuilder(this);
        mHandlerThread = new HandlerThread("TEST");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mHandler.post(AUTOREFRESH);
        //GBC = new GeoBleController();

        button_host = (Button)findViewById(R.id.button_host);
        button_join = (Button)findViewById(R.id.button_join);
        button_refresh = (Button)findViewById(R.id.button_refresh);
        button_BB = (Button)findViewById(R.id.button_BB);
        button_CC = (Button)findViewById(R.id.button_CC);
        button_BLE = (Button)findViewById(R.id.button_BLE);
        button_reset = (Button) findViewById(R.id.button_reset);

        button_refresh.setEnabled(false);
        button_BB.setEnabled(false);
        button_CC.setEnabled(false);
        button_reset.setEnabled(false);

        status_game = (TextView)findViewById(R.id.textView_gamestatus);
        status_player1 = (TextView)findViewById(R.id.textView_player1status);
        status_player2 = (TextView)findViewById(R.id.textView_player2status);
        title = (TextView)findViewById(R.id.textView_title);
        text_warning = (TextView)findViewById(R.id.textView_warning);
        device_address = (TextView)findViewById(R.id.textView_deviceaddress);

        text_warning.setVisibility(View.INVISIBLE);

        button_host.setOnClickListener(HOST);
        button_join.setOnClickListener(JOIN);
        button_refresh.setOnClickListener(REFRESHBUTTON);
        button_BB.setOnClickListener(sendBB);
        button_CC.setOnClickListener(sendCC);



        button_BLE.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GeoBleController.startBleActivity(getApplication());
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String uri ;
                //--------------------------------
                uri = PB.setTag(PHPaddressBuilder.UpdateGame)
                        .addParameter("id",PC.getObjectId())
                        .addParameter("row",PHPcolumn.player_stattus.Hp(1))
                        .addParameter("val",30)
                        .build();
                Ion.with(getApplicationContext())
                    .load(uri)
                    .asJsonObject()
                    .setCallback(null);
                //--------------------------------
                uri = PB.setTag(PHPaddressBuilder.UpdateGame)
                        .addParameter("id",PC.getObjectId())
                        .addParameter("row",PHPcolumn.player_stattus.Hp(2))
                        .addParameter("val",30)
                        .build();
                Ion.with(getApplicationContext())
                        .load(uri)
                        .asJsonObject()
                        .setCallback(null);
                //--------------------------------
                uri = PB.setTag(PHPaddressBuilder.UpdateGame)
                        .addParameter("id",PC.getObjectId())
                        .addParameter("row",PHPcolumn.player_stattus.Ammo(1))
                        .addParameter("val",30)
                        .build();
                Ion.with(getApplicationContext())
                        .load(uri)
                        .asJsonObject()
                        .setCallback(null);
                //--------------------------------
                uri = PB.setTag(PHPaddressBuilder.UpdateGame)
                        .addParameter("id",PC.getObjectId())
                        .addParameter("row",PHPcolumn.player_stattus.Ammo(2))
                        .addParameter("val",30)
                        .build();
                Ion.with(getApplicationContext())
                        .load(uri)
                        .asJsonObject()
                        .setCallback(null);


            }
        });



        StatusChangeListener SCL = new StatusChangeListener()
        {
            @Override
            public void onHPChanged(int value)
            {
                REFRESH();
            }

            @Override
            public void onAMMOChanged(int value)
            {
                REFRESH();
            }
        };
        PC.addSCListener(SCL);

    }

    private View.OnClickListener HOST = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            PC.setGame(2,30,30,new PHPController.setGameCallback(GameIdmaker.newId())
            {
                @Override
                public void run(boolean result)
                {
                    if(result)
                    {
                        PC.connectGame(new PHPController.connectGameCallback(PC.getGameId())
                        {
                            @Override
                            public void run(boolean result)
                            {
                                if(result)
                                {
                                    PC.joinGame(new PHPController.joinGameCallback(1,"Hoster")
                                    {
                                        @Override
                                        public void run(boolean result)
                                        {
                                            if(result)
                                            {
                                                REFRESH();
                                                flag = true;
                                                PC.startService();

                                                button_host.setEnabled(false);
                                                button_join.setEnabled(false);

                                                button_refresh.setEnabled(true);
                                                button_BB.setEnabled(true);
                                                button_CC.setEnabled(true);
                                                button_reset.setEnabled(true);
                                                title.setText("HOST");
                                            }
                                            else TOAST("HOST-joingame: error");
                                        }
                                    });
                                }
                                else TOAST("HOST-connectgame: error");

                            }
                        });
                    }
                    else TOAST("HOST-setgame: error");


                }
            });

        }
    };
    private View.OnClickListener JOIN = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            PC.getOnlineGames(new PHPController.getOnlineGamesCallback()
            {
                @Override
                public void run(boolean result, ArrayList<Long> list)
                {
                    if(result && list!=null && list.size()!=0)
                    {
                        PC.connectGame(new PHPController.connectGameCallback(Collections.max(list))
                        {
                            @Override
                            public void run(boolean result)
                            {
                                if(result)
                                {
                                    PC.joinGame(new PHPController.joinGameCallback(2,"Joiner")
                                    {
                                        @Override
                                        public void run(boolean result)
                                        {
                                            if(result)
                                            {
                                                REFRESH();
                                                flag = true;
                                                PC.startService();

                                                button_host.setEnabled(false);
                                                button_join.setEnabled(false);

                                                button_refresh.setEnabled(true);
                                                button_BB.setEnabled(true);
                                                button_CC.setEnabled(true);
                                                button_reset.setEnabled(true);
                                                title.setText("JOIN");
                                            }
                                            else TOAST("JOIN-joingame: error");
                                        }
                                    });
                                }
                                else TOAST("JOIN-connectgame: error");

                            }
                        });



                    }
                    else TOAST("JOIN-getOnlineGames: error");
                }
            });
        }
    };
    private View.OnClickListener REFRESHBUTTON = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            REFRESH();
        }
    };


    private void REFRESH()
    {
        String uri = PB.setTag(PHPaddressBuilder.GetGame)
                .addParameter("row", PHPcolumn.game.gameId)
                .addParameter("val",PC.getGameId())
                .build();
        Ion.with(this)
            .load(uri)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>()
                {
                    @Override
                    public void onCompleted(Exception e, JsonObject result)
                    {
                        if(e==null)
                        {
                            JsonArray JA = result.get(data).getAsJsonArray();
                            JsonObject JO = JA.get(0).getAsJsonObject();

                            String Name;
                            int hp,ammo;

                            String gID = JO.get(PHPcolumn.game.gameId).getAsString();
                            int pcount = 0;
                            if(!JO.get(PHPcolumn.player_stattus.Name(1)).getAsString().equals(DEFAULT_PLAYER_NAME))
                            {
                                pcount++;
                                Name = JO.get(PHPcolumn.player_stattus.Name(1)).getAsString();
                                hp = JO.get(PHPcolumn.player_stattus.Hp(1)).getAsInt();
                                ammo = JO.get(PHPcolumn.player_stattus.Ammo(1)).getAsInt();

                                status_player1.setText("玩家一\n名稱:"+Name+"\n生命:"+hp+"\n子彈:"+ammo);
                            }
                            else status_player1.setText("玩家一\n未加入");

                            if(!JO.get(PHPcolumn.player_stattus.Name(2)).getAsString().equals(DEFAULT_PLAYER_NAME))
                            {
                                pcount++;
                                Name = JO.get(PHPcolumn.player_stattus.Name(2)).getAsString();
                                hp = JO.get(PHPcolumn.player_stattus.Hp(2)).getAsInt();
                                ammo = JO.get(PHPcolumn.player_stattus.Ammo(2)).getAsInt();

                                status_player2.setText("玩家二\n名稱:"+Name+"\n生命:"+hp+"\n子彈:"+ammo);
                            }
                            else status_player2.setText("玩家二\n未加入");


                            status_game.setText("場次編號:" + gID + "\n目前玩家人數:" + pcount);
                        }
                        else TOAST("REFRESH : error");

                    }
                });
    }

    private void TOAST(String text)
    {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener sendBB = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA,"BB");
            sendBroadcast(intent);
        }
    };
    private View.OnClickListener sendCC = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA,"CC");
            sendBroadcast(intent);
        }
    };


    @Override
    protected void onDestroy()
    {
        mHandler.removeCallbacks(AUTOREFRESH);
        PC.stopService();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private Thread AUTOREFRESH = new Thread()
    {
        @Override
        public void run()
        {
            super.run();
            if(flag)REFRESH();

            mHandler.postDelayed(this,1000);

        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            if (ACTION_DATA_AVAILABLE.equals(action))
            {
                String data = intent.getStringExtra(EXTRA_DATA);
                data = data.substring(0, 2).toUpperCase();
                if(data.equals("BB")|| data.equals("AA")) {

                    if (!warning_flag)
                    {
                        warning_flag = true;
                        //text_warning.setVisibility(View.VISIBLE);
                        setWarningVis(true);
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                if (warning_flag)
                                {
                                    getText_warning().setVisibility(View.INVISIBLE);
                                    warning_flag = false;
                                }
                            }
                        }, 1000);

                    }
                }

            }
            else if(action.equals("com.geodoer.geobluetooth_example.BleActivity.servicestate.device_address"))
            {
                String data = intent.getStringExtra(EXTRA_DATA);

                device_address.setText(data.substring(15,17));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction("com.geodoer.geobluetooth_example.BleActivity.servicestate.device_address");
        return intentFilter;
    }

    private void setWarningVis(Boolean y)
    {
        if(y)
            text_warning.setVisibility(View.VISIBLE);
        else
            text_warning.setVisibility(View.INVISIBLE);
    }

    private TextView getText_warning()
    {
        return this.text_warning;
    }

    @Override
    protected void onStart()
    {
        if(GeoBleService.getDeviceAddress().length() > 1)
            device_address.setText("device address:"+GeoBleService.getDeviceAddress().substring(15,17));
        super.onStart();
    }
}
