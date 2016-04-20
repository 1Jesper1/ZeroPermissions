package com.example.test.zeropermissionsapp.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.example.test.zeropermissionsapp.R;

import com.example.test.zeropermissionsapp.MainActivity;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Jesper Laptop on 20-4-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Intent in = new Intent(context, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent Sender = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("Annoying User")
                    .setContentText("Annoyed yet?")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setSound(Uri.parse("android.resource://com.example.test.zeropermissionsapp/raw/lesbian_gta4"))
                    .setVibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500})
                    .setWhen(System.currentTimeMillis());
            Notification notification = builder.build();
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume =  audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
            audioManager.setRingerMode(audioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume , 0);
            manager.notify(1, notification);
        }
    }
}
