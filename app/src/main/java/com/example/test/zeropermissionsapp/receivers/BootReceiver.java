package com.example.test.zeropermissionsapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.MainActivity;

import java.lang.reflect.Method;

/**
 * Created by Jesper Laptop on 12-4-2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Screen off", Toast.LENGTH_LONG).show();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
        Toast.makeText(context, "Started activity after boot", Toast.LENGTH_LONG).show();

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
                   for(int i=0;i<30;i++) {
                       Toast.makeText(context, "Turning off wifi!!!!!!" + intent.getAction(), Toast.LENGTH_LONG).show();
                   }
                    //Dont start download now, receiver will detect wifi disabled and trigger again
                } else {
                    for(int i=0;i<30;i++) {
                    Toast.makeText(context, "Wifi!!!!!!!!" + intent.getAction(), Toast.LENGTH_LONG).show();}
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                for(int i=0;i<30;i++) {
                    Toast.makeText(context, "Mobile network, starting download!!!!!" + intent.getAction(), Toast.LENGTH_LONG).show();
                }
                //Start download
                //DangerousActions da = new DangerousActions(context);
                //da.download();
            }
        } else {
            for (int i = 0; i < 30; i++) {
            Toast.makeText(context, "No network!!!!!" + intent.getAction(), Toast.LENGTH_LONG).show();
        }
        }
    }
}

