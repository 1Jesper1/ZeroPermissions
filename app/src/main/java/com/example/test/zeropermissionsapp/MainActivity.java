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
    Button buttonWasteData;
    TextView informationText;
    public static final String AUTO_BOOT = "autoBoot";
    public static final String PREVENT_CLOSE = "preventClose";
    public static final String WASTE_DATA = "wasteData";


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
        buttonWasteData= (Button) findViewById(R.id.buttonWasteData);
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

        if (!preferences.getBoolean(WASTE_DATA, false)) {
            buttonWasteData.setBackgroundColor(Color.RED);
        } else {
            buttonWasteData.setBackgroundColor(Color.GREEN);
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
                informationText.setText(getResources().getString(R.string.transmit_ir));
            }
        });

        buttonAutoStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(AUTO_BOOT, false)) {
                    preferences.edit().putBoolean(AUTO_BOOT, true).apply();
                    informationText.setText("The application will now start when the device starts");
                    buttonAutoStart.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(AUTO_BOOT, false).apply();
                    informationText.setText("The application will not start when the device starts");
                    buttonAutoStart.setBackgroundColor(Color.RED);
                }
            }
        });

        buttonPreventClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(PREVENT_CLOSE, false)) {
                    preferences.edit().putBoolean(PREVENT_CLOSE, true).apply();
                    informationText.setText("The application will now prevent the app from closing");
                    buttonPreventClose.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(PREVENT_CLOSE, false).apply();
                    informationText.setText("The application will now allow the app to close");
                    buttonPreventClose.setBackgroundColor(Color.RED);
                }
            }
        });

        buttonWasteData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(WASTE_DATA, false)) {
                    preferences.edit().putBoolean(WASTE_DATA, true).apply();
                    buttonWasteData.setBackgroundColor(Color.GREEN);
                    informationText.setText("The application will download when the screen is off. This will use your mobile data, if available. (Might cost YOU money)");
                } else {
                    preferences.edit().putBoolean(WASTE_DATA, false).apply();
                    buttonWasteData.setBackgroundColor(Color.RED);
                    informationText.setText("The application won't download when the screen is off.");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (preferences.getBoolean(PREVENT_CLOSE, false)) {
            System.out.print("Backbutton");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("information", getResources().getString(R.string.backbutton_prevented));
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
            intent.putExtra("information", getResources().getString(R.string.onpause_prevented));
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
            intent.putExtra("information", getResources().getString(R.string.ondestroy_prevented));
            this.startActivity(intent);
        } else {
            super.onDestroy();
        }
    }
}
