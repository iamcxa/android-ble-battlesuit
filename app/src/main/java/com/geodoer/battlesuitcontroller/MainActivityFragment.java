package com.geodoer.battlesuitcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.db.circularcounter.CircularCounter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment
        extends Fragment
        implements View.OnClickListener{

    private CircularCounter meterHp;
    private CircularCounter meterAmmo;

    private String[] colorsAmmo;
    private String[] colorsHp;

    private Handler handler;

    private Runnable r;

    private Button btnMore;
    private Button btnLess;
    private Button btnAuto;

    private boolean isAutoRun;

    public MainActivityFragment() {
        handler = new Handler();
        r = new Runnable(){
            int currV = 0;
            boolean go = true;
            public void run(){
                if(currV == 60 && go)
                    go = false;
                else if(currV == -60 && !go)
                    go = true;

                if(go)
                    currV++;
                else
                    currV--;

                meterAmmo.setValues(currV, currV*2, currV*3);
                meterHp.setValues(currV, currV*2, currV*3);
                handler.postDelayed(this, 50);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isAutoRun=false;

        btnMore=(Button)view.findViewById(R.id.btnMore);
        btnLess=(Button)view.findViewById(R.id.btnLess);
        btnAuto=(Button)view.findViewById(R.id.btnAuto);

        btnMore.setOnClickListener(this);
        btnLess.setOnClickListener(this);
        btnAuto.setOnClickListener(this);

        colorsAmmo = getResources().getStringArray(R.array.colors_AMMO);
        colorsHp = getResources().getStringArray(R.array.colors_HP);

        meterHp = (CircularCounter) view.findViewById(R.id.meter);
        meterAmmo = (CircularCounter) view.findViewById(R.id.meter_ammo);

        meterHp.setFirstWidth(getResources().getDimension(R.dimen.first))
                .setFirstColor(Color.parseColor(colorsHp[0]))

                .setSecondWidth(getResources().getDimension(R.dimen.second))
                .setSecondColor(Color.parseColor(colorsHp[1]))

                .setThirdWidth(getResources().getDimension(R.dimen.third))
                .setThirdColor(Color.parseColor(colorsHp[2]))

                .setBackgroundColor(-14606047);

        meterAmmo.setFirstWidth(getResources().getDimension(R.dimen.first))
                .setFirstColor(Color.parseColor(colorsAmmo[0]))

                .setSecondWidth(getResources().getDimension(R.dimen.second))
                .setSecondColor(Color.parseColor(colorsAmmo[1]))

                .setThirdWidth(getResources().getDimension(R.dimen.third))
                .setThirdColor(Color.parseColor(colorsAmmo[2]))

                .setBackgroundColor(-14606047);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLess:
                reduceMeter(meterAmmo);
                reduceMeter(meterHp);
                break;

            case R.id.btnMore:
                addMeter(meterAmmo);
                addMeter(meterHp);
                break;

            case R.id.btnAuto:
                if(!isAutoRun){
                    isAutoRun=true;
                    handler.postDelayed(r,50);
                }else{
                    isAutoRun=false;
                    handler.removeCallbacks(r);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isAutoRun=false;
        handler.removeCallbacks(r);
    }

    private void reduceMeter(CircularCounter meter){
        meter.setValues(meter.getValue1()-1,
                meter.getValue2()-2,
                meter.getValue3()-3);
    }

    private void addMeter(CircularCounter meter){
        meter.setValues(meter.getValue1()+1,
                meter.getValue2()+2,
                meter.getValue3()+3);
    }
}
