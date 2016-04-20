package com.example.test.zeropermissionsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.zeropermissionsapp.receivers.AlarmReceiver;
import com.example.test.zeropermissionsapp.receivers.ScreenSleepReceiver;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences preferences;
    Button buttonDownloadStart;
    Button buttonIRStart;
    Button buttonAutoStart;
    Button buttonPreventClose;
    Button buttonSetAlarm;
    Button buttonAnnoyUser;
    Button buttonWasteData;
    TextView informationText;
    public static final String AUTO_BOOT = "autoBoot";
    public static final String PREVENT_CLOSE = "preventClose";
    public static final String WASTE_DATA = "wasteData";
    public static final String INFORMATION = "information";
    public static final String ANNOY_USER = "annoyUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.toolbar_title));
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
        buttonSetAlarm= (Button) findViewById(R.id.buttonSetAlarm);
        buttonAnnoyUser= (Button) findViewById(R.id.buttonAnnoyUser);
        informationText = (TextView) findViewById(R.id.informationText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String intentText = extras.getString(INFORMATION);
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

        if (!preferences.getBoolean(ANNOY_USER, false)) {
            buttonAnnoyUser.setBackgroundColor(Color.RED);
        } else {
            buttonAnnoyUser.setBackgroundColor(Color.GREEN);
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
                informationText.setText(getResources().getString(R.string.infrared_info));
            }
        });

        buttonAutoStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(AUTO_BOOT, false)) {
                    preferences.edit().putBoolean(AUTO_BOOT, true).apply();
                    informationText.setText(getResources().getString(R.string.auto_start_on));
                    buttonAutoStart.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(AUTO_BOOT, false).apply();
                    informationText.setText(getResources().getString(R.string.auto_start_off));
                    buttonAutoStart.setBackgroundColor(Color.RED);
                }
            }
        });

        buttonPreventClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(PREVENT_CLOSE, false)) {
                    preferences.edit().putBoolean(PREVENT_CLOSE, true).apply();
                    informationText.setText(getResources().getString(R.string.prevent_close_on));
                    buttonPreventClose.setBackgroundColor(Color.GREEN);
                } else {
                    preferences.edit().putBoolean(PREVENT_CLOSE, false).apply();
                    informationText.setText(getResources().getString(R.string.prevent_close_off));
                    buttonPreventClose.setBackgroundColor(Color.RED);
                }
            }
        });

        buttonWasteData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(WASTE_DATA, false)) {
                    preferences.edit().putBoolean(WASTE_DATA, true).apply();
                    buttonWasteData.setBackgroundColor(Color.GREEN);
                    informationText.setText(getResources().getString(R.string.waste_data_on));
                } else {
                    preferences.edit().putBoolean(WASTE_DATA, false).apply();
                    buttonWasteData.setBackgroundColor(Color.RED);
                    informationText.setText(getResources().getString(R.string.waste_data_off));
                }
            }
        });

        buttonAnnoyUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!preferences.getBoolean(ANNOY_USER, false)) {
                    preferences.edit().putBoolean(ANNOY_USER, true).apply();
                    buttonAnnoyUser.setBackgroundColor(Color.GREEN);
                    informationText.setText(getResources().getString(R.string.annoy_user_on));
                } else {
                    preferences.edit().putBoolean(ANNOY_USER, false).apply();
                    buttonAnnoyUser.setBackgroundColor(Color.RED);
                    informationText.setText(getResources().getString(R.string.annoy_user_off));
                }
            }
        });


        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent AlarmIntent = new Intent(arg0.getContext(), AlarmReceiver.class);
                PendingIntent Sender = PendingIntent.getBroadcast(arg0.getContext(), 0, AlarmIntent, 0);
                AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
                AlmMgr.set(AlarmManager.RTC, System.currentTimeMillis() +
                        (5 * 1000), Sender);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (preferences.getBoolean(PREVENT_CLOSE, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(INFORMATION, getResources().getString(R.string.backbutton_prevented));
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
            intent.putExtra(INFORMATION, getResources().getString(R.string.onpause_prevented));
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
            intent.putExtra(INFORMATION, getResources().getString(R.string.ondestroy_prevented));
            this.startActivity(intent);
        } else {
            super.onDestroy();
        }
    }
}
