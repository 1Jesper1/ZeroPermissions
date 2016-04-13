package com.example.test.zeropermissionsapp;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.receivers.ScreenSleepReceiver;

public class MainActivity extends AppCompatActivity {
    Button buttonStart;
    Button buttonPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dangerous application");
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Toast.makeText(this, extras.getString("toast"), Toast.LENGTH_LONG).show();
        }
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
    public void onBackPressed() {
        Toast.makeText(this, "Back press stay", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("toast", "Hallo");
        this.startActivity(intent);
    }
}
