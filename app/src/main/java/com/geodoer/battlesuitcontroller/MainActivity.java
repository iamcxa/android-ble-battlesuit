package com.geodoer.battlesuitcontroller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.geodoer.battlesuitcontroller.util.SystemUiHider;
import com.geodoer.battlesuitcontroller.view.BattleFragment;
import com.geodoer.battlesuitcontroller.view.BleFragment;
import com.geodoer.battlesuitcontroller.view.HostFragment;
import com.geodoer.battlesuitcontroller.view.JoinFragment;
import com.geodoer.battlesuitcontroller.view.MainFragment;
import com.geodoer.battlesuitcontroller.view.SettingsActivity;
import com.geodoer.bluetoothcontroler.service.GeoBleService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity
        extends
        AppCompatActivity
        implements
        MainFragment.OnFragmentInteractionListener,
        BattleFragment.OnFragmentInteractionListener,
        HostFragment.OnFragmentInteractionListener,
        JoinFragment.OnFragmentInteractionListener,
        BleFragment.OnFragmentInteractionListener{

    private DrawerLayout mDrawerLayout;
    private SystemUiHider mSystemUiHider;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) setupDrawerContent(navigationView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        switchFragment(MainFragment.newInstance("", ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intentSetting= new Intent();
                intentSetting.setClass(this, SettingsActivity.class);
                this.startActivity(intentSetting);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                //mTitle = getString("");
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                break;
            case 3:
                //mTitle = getString(R.string.title_section3);
                break;
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        // update the main content by replacing
                        Fragment newFragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.navm_home:
                                newFragment = MainFragment.newInstance("", "");
                                break;
                            case R.id.navm_battle:
                                //newFragment = BattleFragment.newInstance("", "");
                                break;
                            case R.id.navm_bluetooth:

                                break;
                            case R.id.navm_status:

                                break;
                        }
                        switchFragment(newFragment);
                        Toast.makeText(getApplication(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // registerReceiver(mBroadcast, new IntentFilter(MY_MESSAGE));
        final Intent intent = new Intent(GeoBleService.mAction_stopself);
        sendBroadcast(intent);
    }

    public void switchFragment(Fragment newFragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment).commit();
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
