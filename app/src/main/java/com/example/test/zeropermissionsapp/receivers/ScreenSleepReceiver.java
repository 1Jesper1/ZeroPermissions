package com.example.test.zeropermissionsapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.DangerousActions;

import java.lang.reflect.Method;

/**
 * Created by Jesper Laptop on 11-4-2016.
 */
public class ScreenSleepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Toast.makeText(context, "Screen off", Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

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
                        //wifiManager.setWifiEnabled(false);
                        Toast.makeText(context, "Turning off wifi", Toast.LENGTH_SHORT).show();
                        //Dont start download now, receiver will detect wifi disabled and trigger again
                    } else {
                        Toast.makeText(context, "Wifi", Toast.LENGTH_SHORT).show();
                    }
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Toast.makeText(context, "Mobile network", Toast.LENGTH_SHORT).show();
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
