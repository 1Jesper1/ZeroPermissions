package com.example.test.zeropermissionsapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.MainActivity;

import java.lang.reflect.Method;

/**
 * Created by Jesper Laptop on 12-4-2016.
 */
public class BootReceiver extends BroadcastReceiver {
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Getting preference", Toast.LENGTH_LONG).show();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean(MainActivity.AUTO_BOOT, false)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("information", "App started after boot");
            context.startActivity(activityIntent);

            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    boolean mobileDataEnabled = false;
                    try {
                        Class cmClass = Class.forName(connectivityManager.getClass().getName());
                        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                        method.setAccessible(true);
                        mobileDataEnabled = (Boolean) method.invoke(connectivityManager);
                    } catch (Exception e) {
                        //Error
                    }
                    if (mobileDataEnabled && mMobile.isAvailable()) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(false);
                        Toast.makeText(context, "Turning off wifi", Toast.LENGTH_LONG).show();
                        //Dont start download now, receiver will detect wifi disabled and trigger again
                    } else {
                        Toast.makeText(context, "Wifi", Toast.LENGTH_LONG).show();
                    }
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Toast.makeText(context, "Mobile network, starting download", Toast.LENGTH_LONG).show();
                    //Start download
                    //DangerousActions da = new DangerousActions(context);
                    //da.download();
                }
            } else {
                Toast.makeText(context, "No network", Toast.LENGTH_LONG).show();
            }
        }
    }
}

