package com.example.blecontroller_demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.geodoer.bluetoothcontroler.controller.GeoBleController;


public class BleMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout LL = new LinearLayout(this);

        Button bb = new Button(this);
        bb.setText("start a dialog");
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                start_bleactivity();
            }
        });

        LL.addView(bb);

        setContentView(LL);
    }

    @Override
    protected void onDestroy()
    {
        GeoBleController.destroyService(this);

        super.onDestroy();
    }

    private void start_bleactivity()
    {
        GeoBleController.startBleActivity(this);
    }
}
