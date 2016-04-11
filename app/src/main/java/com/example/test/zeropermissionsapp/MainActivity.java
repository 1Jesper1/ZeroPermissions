package com.example.test.zeropermissionsapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.test.zeropermissionsapp.receivers.ScreenSleepReceiver;

public class MainActivity extends AppCompatActivity {
    Button buttonStart;
    Button buttonPower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenSleepReceiver();
        registerReceiver(mReceiver, filter);
        // Locate the button in activity_main.xml
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonPower = (Button) findViewById(R.id.buttonPower);

        // Capture button clicks
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DangerousActions da = new DangerousActions(MainActivity.this);
                da.download();
            }
        });

        // Capture button clicks
        buttonPower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DangerousActions da = new DangerousActions(MainActivity.this);
                da.sendIR();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
