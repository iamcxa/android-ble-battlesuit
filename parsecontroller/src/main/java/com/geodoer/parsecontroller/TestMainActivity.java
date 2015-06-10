package com.geodoer.parsecontroller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.geodoer.parsecontroller.controller.GameIdmaker;
import com.geodoer.parsecontroller.controller.ParseController;

import java.util.ArrayList;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test_main);
        LinearLayout LL = new LinearLayout(this);
        LL.setOrientation(LinearLayout.VERTICAL);

        Button setGame = new Button(this);
        Button getOnliningGames = new Button(this);
        Button connectGame = new Button(this);
        Button performGameInfo = new Button(this);
        Button performPlayerInfo = new Button(this);
        Button joinGame = new Button(this);
        Button updateHP = new Button(this);
        Button updateAMMO = new Button(this);


        setGame.setText("setGame");
        getOnliningGames.setText("getOnliningGames");
        connectGame.setText("connectGame");
        performGameInfo.setText("performInfo");
        performPlayerInfo.setText("performPlayerInfo");
        joinGame.setText("joinGame");
        updateHP.setText("updateHP -1");
        updateAMMO.setText("updateAMMO -1");

        LL.addView(setGame);
        LL.addView(getOnliningGames);
        LL.addView(connectGame);
        LL.addView(joinGame);
        LL.addView(performGameInfo);
        LL.addView(performPlayerInfo);
        LL.addView(updateHP);
        LL.addView(updateAMMO);

        Log.wtf("PARSE","--------------APP START---------------");
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
                        if(result)Log.wtf("PARSE", "set Game success");
                        else Log.wtf("PARSE","set Game fail");
                    }
                });
            }
        });

        getOnliningGames.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                 *
                 *     getOnliningGames( getOnliningGamesCallback )
                 *     result = success or not
                 *       false with parse exception
                 *     list = List of Onlining Games
                 *       possible with size = 0
                 *
                 */
                PC.getOnliningGames(new ParseController.getOnliningGamesCallback()
                {
                    @Override
                    public void run(boolean result, ArrayList<Long> list)
                    {
                        if(result)
                        {
                            Log.wtf("PARSE", "get Onlining Games success");

                            //-------get ID from list--------------------
                            if(list ==null) Log.wtf("PARSE", "getOnlining list is null");
                            else
                            {
                                Log.wtf("PARSE", "getOnlining list size :" + list.size());
                                for (long i : list)
                                {
                                    Log.wtf("PARSE", "Onlining Games ID : " + i);
                                }
                            }
                            //---------------------------------------------
                        }
                        else
                            Log.wtf("PARSE", "get Onlining Games fail");
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
                        if(result)Log.wtf("PARSE", "connect success");
                        else Log.wtf("PARSE", "connect fail");

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

                        if(result)Log.wtf("PARSE", "join success");
                        else Log.wtf("PARSE", "join fail");
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
                 *   updateInfo(  updateInfoCallback ( PC.Player.Hp , -1 )
                 *   result = success or not
                 *        false with parse exception
                 *
                 */
                PC.Player.updateInfo(new ParseController.updateInfoCallback( PC.Player.Hp, -1 )
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf("PARSE", "update HP -1 success");
                        else Log.wtf("PARSE", "update HP -1 fail");
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
                 *   updateInfo(  updateInfoCallback ( PC.Player.Ammo , -1 )
                 *   result = success or not
                 *        false with parse exception
                 *
                 */
                PC.Player.updateInfo(new ParseController.updateInfoCallback( PC.Player.Ammo, -1 )
                {
                    @Override
                    public void run(boolean result)
                    {
                        if(result)Log.wtf("PARSE", "update Ammo -1 success");
                        else Log.wtf("PARSE", "update Ammo -1 fail");
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

                Log.wtf("PARSE", "Game ObjectId = " + PC.getObjectId());
                Log.wtf("PARSE", "Game ID       = " + PC.getGameId());
                Log.wtf("PARSE", "Game setHP    = " + PC.getSetHP());
                Log.wtf("PARSE", "Game setAmmo  = " + PC.getSetAMMO());
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
                Log.wtf("PARSE", "Player status = " + PC.Player.getStatus());
                Log.wtf("PARSE", "Player Num    = " + PC.Player.getNum());
                Log.wtf("PARSE", "Player Name   = " + PC.Player.getName());
                Log.wtf("PARSE", "Player HP     = " + PC.Player.getHP());
                Log.wtf("PARSE", "Player AMMO   = " + PC.Player.getAMMO());
            }
        });



        setContentView(LL);


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
