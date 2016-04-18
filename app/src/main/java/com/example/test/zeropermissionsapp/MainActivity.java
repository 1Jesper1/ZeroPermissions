package com.example.test.zeropermissionsapp;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.receivers.ScreenSleepReceiver;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences preferences;
    Button buttonDownloadStart;
    Button buttonIRStart;
    Button buttonAutoStart;
    Button buttonPreventClose;
    TextView informationText;
    public static final String AUTO_BOOT = "autoBoot";
    public static final String PREVENT_CLOSE = "preventClose";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dangerous application");
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenSleepReceiver();
        registerReceiver(mReceiver, filter);

        buttonDownloadStart = (Button) findViewById(R.id.buttonDownloadStart);
        buttonIRStart = (Button) findViewById(R.id.buttonIRStart);
        buttonAutoStart = (Button) findViewById(R.id.buttonAutoStart);
        buttonPreventClose = (Button) findViewById(R.id.buttonPreventClose);
        informationText = (TextView) findViewById(R.id.informationText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String intentText = extras.getString("information");
            informationText.setText(intentText);
        }

        if (!preferences.getBoolean(PREVENT_CLOSE, false)) {
            buttonPreventClose.setBackgroundColor(Color.RED);
        } else {
            buttonPreventClose.setBackgroundColor(Color.GREEN);
        }

        if (!preferences.getBoolean(AUTO_BOOT, false)) {
            buttonAutoStart.setBackgroundColor(Color.RED);
        } else {
            buttonAutoStart.setBackgroundColor(Color.GREEN);
        }

        buttonDownloadStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DangerousActions da = new DangerousActions(MainActivity.this);
                da.download();
            }
        });

        buttonIRStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                DangerousActions da = new DangerousActions(MainActivity.this);
                da.sendIR();
                informationText.setText("Sending infrared signals");
            }
        });

        buttonAutoStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(AUTO_BOOT, false)) {
                    preferences.edit().putBoolean(AUTO_BOOT, true).apply();
                    buttonAutoStart.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(AUTO_BOOT, false).apply();
                    buttonAutoStart.setBackgroundColor(Color.RED);
                }
            }
        });

        buttonPreventClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(PREVENT_CLOSE, false)) {
                    preferences.edit().putBoolean(PREVENT_CLOSE, true).apply();
                    buttonPreventClose.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(PREVENT_CLOSE, false).apply();
                    buttonPreventClose.setBackgroundColor(Color.RED);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (preferences.getBoolean(PREVENT_CLOSE, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("information", "Back button prevented");
            this.startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onPause() {
        if (preferences.getBoolean(PREVENT_CLOSE, false)) {
            super.onPause();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("information", "App close prevented");
            this.startActivity(intent);
        } else {
            super.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (preferences.getBoolean(PREVENT_CLOSE, false)) {
            super.onDestroy();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("information", "App destroy prevented");
            this.startActivity(intent);
        } else {
            super.onDestroy();
        }
    }
}
