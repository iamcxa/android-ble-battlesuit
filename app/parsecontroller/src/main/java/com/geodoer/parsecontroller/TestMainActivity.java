package com.geodoer.parsecontroller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.geodoer.parsecontroller.controller.GameIdmaker;
import com.geodoer.parsecontroller.controller.ParseController;

import java.util.ArrayList;
import java.util.List;


/**
 *     This activity is Test and perform how to use
 * this battle suit parse api.
 *
 * created by Hem
 */

public class TestMainActivity extends ActionBarActivity
{
    private ParseController PC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final String TAG = "ParseController";

        LinearLayout LL = new LinearLayout(this);
        LL.setOrientation(LinearLayout.VERTICAL);


        ScrollView SV = new ScrollView(this);



        Button setGame            = new Button(this);
        Button getOnlineGames     = new Button(this);
        Button getGameInformation = new Button(this);
        Button connectGame        = new Button(this);
        Button performGameInfo    = new Button(this);
        Button performPlayerInfo  = new Button(this);
        Button joinGame           = new Button(this);
        Button updateHP           = new Button(this);
        Button updateAMMO         = new Button(this);
        Button updateAMMO_t       = new Button(this);
        Button getWhoShoot        = new Button(this);


        setGame.setText("setGame");
        getOnlineGames.setText("getOnlineGames");
        connectGame.setText("connectGame");
        performGameInfo.setText("performInfo");
        performPlayerInfo.setText("performPlayerInfo");
        joinGame.setText("joinGame");
        updateHP.setText("updateHP -1");
        updateAMMO.setText("updateAMMO -1");
        updateAMMO_t.setText("updateAMMO_t to 1");
        getGameInformation.setText("getGameInformation");
        getWhoShoot.setText("getWhoShoot");

        LL.addView(setGame);
        LL.addView(getOnlineGames);
        LL.addView(getGameInformation);
        LL.addView(connectGame);
        LL.addView(joinGame);
        LL.addView(performGameInfo);
        LL.addView(performPlayerInfo);
        LL.addView(updateHP);
        LL.addView(updateAMMO);
        LL.addView(updateAMMO_t);
        LL.addView(getWhoShoot);

        SV.addView(LL);


        Log.wtf(TAG,"--------------APP START---------------");
        /**
         *   Initialize
         *   need Context
         */

        PC =new ParseController(this);

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
                PC.setGame(2,30,30,new ParseController.setGameCallback( GameIdmaker.newId() )
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
                PC.getOnlineGames(new ParseController.getOnlineGamesCallback() {
                    @Override
                    public void run(boolean result, ArrayList<Long> list) {
                        if (result) {
                            Log.wtf(TAG, "getOnlineGames success");

                            if (list == null) Log.wtf(TAG, "getOnline list is null");
                            else {
                                Log.wtf(TAG, "getOnline list size :" + list.size());
                                for (long i : list) {
                                    Log.wtf(TAG, "Online Games ID : " + i);
                                }
                            }
                            //---------------------------------------------
                        } else
                            Log.wtf(TAG, "getOnlineGames fail");
                    }

                });
            }
        });
        getGameInformation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *     getGameInformation( getGameInformationCallback )
                 *     result = success or not
                 *       false with parse exception
                 *     Player_count = number of players in this game
                 *
                 *     Name_list, Hp_list, Ammo_list
                 *       = player's status
                 *
                 *     when name is not "empty"
                 *       that is there is a player online
                 *
                 */
                PC.getGameInformation(new ParseController.getGameInformationCallback()
                {
                    @Override
                    public void run(boolean result, int Player_count,
                                    ArrayList<String> Name_list,
                                    ArrayList<Integer> Hp_list,
                                    ArrayList<Integer> Ammo_list)
                    {

                        if (result)
                        {
                            Log.wtf(TAG, "getGamesInformation success");

                            if (Player_count == 0) Log.wtf(TAG, "this game no player");
                            else
                            {
                                Log.wtf(TAG, "player count :" + Player_count);
                                for (int i=0 ;i< Player_count ;i++)
                                {
                                    Log.wtf(TAG, "PlayerNumber:"+(i+1)+
                                                 " ,Name:"+Name_list.get(i) +
                                                 " ,Hp:"+Hp_list.get(i) +
                                                 " ,Ammo:" + Ammo_list.get(i) );
                                }
                            }
                        } else

                            Log.wtf(TAG, "getGamesInformation fail");
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

                PC.connectGame( new ParseController.connectGameCallback(PC.getGameId())
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

                PC.joinGame(new ParseController.joinGameCallback(1,"Test Name")
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

        updateHP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *   updateInfo(  updateInfoCallback ( PC.Player.HP , -1 )
                 *   result = success or not
                 *        false with parse exception
                 *
                 */
                PC.Player.updateInfo(new ParseController.updateInfoCallback( ParseController.PlayerStatus.HP, -1 )
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "updateHP -1 success");
                        else Log.wtf(TAG, "updateHP -1 fail");
                    }
                });

            }
        });
        updateAMMO.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *   updateInfo(  updateInfoCallback ( PC.Player.AMMO , -1 )
                 *   result = success or not
                 *        false with parse exception
                 *
                 */
                PC.Player.updateInfo(new ParseController.updateInfoCallback( ParseController.PlayerStatus.AMMO, -1 )
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "update Ammo -1 success");
                        else Log.wtf(TAG, "update Ammo -1 fail");
                    }
                });

            }
        });
        updateAMMO_t.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *   updateInfo(  updateInfoCallback ( PlayerStatus.AMMO_t , 1 )
                 *   this is set AMMO_t to 1
                 *
                 *   result = success or not
                 *        false with parse exception
                 *
                 */
                PC.Player.updateInfo(new ParseController.updateInfoCallback(ParseController.PlayerStatus.AMMO_t, 1)
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf(TAG, "update Ammo_t 1 success");
                        else Log.wtf(TAG, "update Ammo_t 1 fail");
                    }
                });
            }
        });
        getWhoShoot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *     getWhoShooting( getWhoShootCallback )
                 *     result = success or not
                 *       false with parse exception
                 *     list = List of Who was Shooting
                 *       possible with size = 0 //nobody
                 *
                 */
                PC.getWhoShooting(new ParseController.getWhoShootCallback()
                {
                    @Override
                    public void run(boolean result, ArrayList<Integer> list)
                    {
                        if (result)
                        {
                            Log.wtf(TAG, "getWhoShooting success");
                        }
                        else
                        {
                            Log.wtf(TAG, "getWhoShooting fail");
                        }
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
                Log.wtf(TAG, "Player status = " + PC.Player.getStatus());
                Log.wtf(TAG, "Player Num    = " + PC.Player.getNum());
                Log.wtf(TAG, "Player Name   = " + PC.Player.getName());
                Log.wtf(TAG, "Player HP     = " + PC.Player.getHp());
                Log.wtf(TAG, "Player AMMO   = " + PC.Player.getAmmo());
                Log.wtf(TAG, "Player AMMO_t = " + PC.Player.getAmmo_t());
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
