package com.geodoer.phpcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.geodoer.phpcontroller.column.PHPcolumn;
import com.geodoer.phpcontroller.controller.GameIdmaker;
import com.geodoer.phpcontroller.controller.PHPController;
import com.geodoer.phpcontroller.utils.StatusChangeListener;

import java.util.List;
import java.util.Map;


public class TestMainActivity extends AppCompatActivity
{
    private PHPController PC;

    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final String TAG = "PHPController";

        LinearLayout LL = new LinearLayout(this);
        LL.setOrientation(LinearLayout.VERTICAL);
        ScrollView SV = new ScrollView(this);

        Button setGame            = new Button(this);
        Button getOnlineGames     = new Button(this);
        //Button getGameInformation = new Button(this);
        Button connectGame        = new Button(this);
        Button performGameInfo    = new Button(this);
        Button performPlayerInfo  = new Button(this);
        Button joinGame           = new Button(this);
        //Button updateHP           = new Button(this);
        //Button updateAMMO         = new Button(this);
        //Button updateAMMO_t       = new Button(this);
        //Button getWhoShoot        = new Button(this);
        Button startservice       = new Button(this);
        Button stopservice        = new Button(this);

        Button sendCC = new Button(this);
        Button sendBBCC = new Button(this);

        setGame.setText("setGame");
        getOnlineGames.setText("getOnlineGames");
        connectGame.setText("connectGame");
        performGameInfo.setText("performInfo");
        performPlayerInfo.setText("performPlayerInfo");
        joinGame.setText("joinGame");
        //updateHP.setText("updateHP -1");
        //updateAMMO.setText("updateAMMO -1");
        //updateAMMO_t.setText("updateAMMO_t to 1");
        //getGameInformation.setText("getGameInformation");
        //getWhoShoot.setText("getWhoShoot");
        startservice.setText("startservice");
        stopservice.setText("stopservice");


        sendBBCC.setText("sendBBCC");
        sendCC.setText("sendCC");

        LL.addView(setGame);
        LL.addView(getOnlineGames);
        //LL.addView(getGameInformation);
        LL.addView(connectGame);
        LL.addView(joinGame);
        LL.addView(performGameInfo);
        LL.addView(performPlayerInfo);
        //LL.addView(updateHP);
        //LL.addView(updateAMMO);
        //LL.addView(updateAMMO_t);
        //LL.addView(getWhoShoot);
        LL.addView(startservice);
        LL.addView(stopservice);

        LL.addView(sendCC);
        LL.addView(sendBBCC);

        SV.addView(LL);

        Log.wtf(TAG, "--------------APP START---------------");
        /**
         *   Initialize
         *   need Context
         */
        PC =new PHPController(this);


        setGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *    setGame( number of players , HP , AMMO , setGameCallback(new gameID) )
                 *    result = success or not
                 *      false with parse exception
                 *
                 *    with this api , GameID will auto set in API
                 *
                 */
                PC.setGame(2,30,30,new PHPController.setGameCallback( GameIdmaker.newId() )
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "setGame success");
                        else Log.wtf(TAG,"setGame fail");
                    }
                });
            }
        });

        connectGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *      connectGame( connectGameCallback( targetId ) )
                 *      result = success or not
                 *        false with no games
                 *                   more games
                 *                   not Online
                 *                   parse exception
                 *
                 */

                PC.connectGame( new PHPController.connectGameCallback(PC.getGameId())
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "connectGame success");
                        else Log.wtf(TAG, "connectGame fail");
                    }
                });

            }
        });

        joinGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *      joinGame ( joinGameCallback( playerNum , Name ) )
                 *      result = success or not
                 *        false with parse exception
                 *
                 */

                PC.joinGame(new PHPController.joinGameCallback(1,"Test Name")
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "joinGame success");
                        else Log.wtf(TAG, "joinGame fail");
                    }
                });

            }
        });

        getOnlineGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *
                 *     getOnlineGames( getOnliningGamesCallback )
                 *     result = success or not
                 *       false with parse exception
                 *     list = List of Onlining Games
                 *       possible with size = 0
                 *
                 */
                PC.getOnlineGames(new PHPController.getOnlineGamesCallback()
                {
                    @Override
                    public void run(boolean result, List<Map<String, Long>> list)
                    {
                        if (result)
                        {
                            Log.wtf(TAG, "getOnlineGames success");

                            if (list == null) Log.wtf(TAG, "getOnline list is null");
                            else
                            {
                                Log.wtf(TAG, "getOnline list size :" + list.size());
                                for (Map i : list) {
                                    Log.wtf("pc",
                                            "Onlining Games ID : " + i.get(PHPcolumn.game.gameId) +
                                                    "and its people count : " + i.get(PHPcolumn.game.gPcount));
                                }
                            }
                            //---------------------------------------------
                        }
                        else
                            Log.wtf(TAG, "getOnlineGames fail");
                    }

                });
            }
        });

        performGameInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *
                 *   Getting Game Info method
                 *
                 */

                Log.wtf(TAG, "Game ObjectId = " + PC.getObjectId());
                Log.wtf(TAG, "Game ID       = " + PC.getGameId());
                Log.wtf(TAG, "Game setHP    = " + PC.getSetHP());
                Log.wtf(TAG, "Game setAmmo  = " + PC.getSetAMMO());
            }
        });

        performPlayerInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *   Getting player Info method
                 */
                Log.wtf(TAG, "Player Num    = " + PC.getPlayer_num());
                Log.wtf(TAG, "Player Name   = " + PC.getPlayer_name());
                Log.wtf(TAG, "Player HP     = " + PC.getPlayer_hp());
                Log.wtf(TAG, "Player AMMO   = " + PC.getPlayer_ammo());
            }
        });


        /**
         *
         * add a status changed listener to api
         *
         */
        StatusChangeListener SCL = new StatusChangeListener()
        {
            @Override
            public void onHPChanged(int value)
            {
                Log.wtf(TAG,"HP have been changed to "+value);
            }

            @Override
            public void onAMMOChanged(int value)
            {
                Log.wtf(TAG,"AMMO have been changed to "+value);
            }
        };
        PC.addSCListener(SCL);
        //PC.clearSCListener();

        startservice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 * start service
                 * must after join
                 */
                PC.startService();
            }
        });

        stopservice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 * stop service
                 *
                 */
                PC.stopService();
            }
        });


        //---------test block-------------------
        //here is only simulate the BLE signal as CC / BB+CC
        sendCC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                intent.putExtra(EXTRA_DATA,"CC");
                sendBroadcast(intent);
            }
        });

        sendBBCC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Intent intent1 = new Intent(ACTION_DATA_AVAILABLE);
                intent1.putExtra(EXTRA_DATA,"CC");
                sendBroadcast(intent1);

                final Intent intent2 = new Intent(ACTION_DATA_AVAILABLE);
                intent2.putExtra(EXTRA_DATA,"BB");
                sendBroadcast(intent2);
            }
        });


        setContentView(SV);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
