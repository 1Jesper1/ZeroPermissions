package com.example.test.zeropermissionsapp;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Jesper Laptop on 29-3-2016.
 */
public class DangerousActions {
    private Context context;
    ConsumerIrManager mCIR;

    public void download() {

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(context);
        downloadTask.execute("http://speedtest.reliableservers.com/10MBtest.bin");
    }

    public void sendIR() {
        final IRCode irCode1;
        final IRCode irCode2;
        final IRCode irCode3;
        final IRCode irCode4;
        boolean isSupported = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                // Android 4.4W and up
                isSupported = true;
            } else {
                // Android 4.4
                String[] parts = Build.VERSION.RELEASE.split("\\.");
                if (parts.length >= 3 && Integer.parseInt(parts[2]) >= 3) {
                    // Android 4.4.3 to 4.4W
                    isSupported = true;
                }
            }
        } else {
            isSupported = false;
        }

        mCIR = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!mCIR.hasIrEmitter()) {
                //Notify if none Ir Emitter
                return;
            }
        }
        String powerSamsung1 = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e";
        String powerSamsung2 = "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";
        String powerPhilips1 = "0000 0073 0000 000C 0020 0020 0040 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0040 0020 0020 0040 0020 0020 0CC8";

        Helpers helper = new Helpers();
        if (isSupported) {
            irCode1 = helper.convertToDuration(powerSamsung1);
            irCode2 = helper.convertToDuration(powerSamsung2);
            irCode3 = helper.convertToDuration(powerPhilips1);
        } else {
            irCode1 = helper.convertToCount(powerSamsung1);
            irCode2 = helper.convertToCount(powerSamsung2);
            irCode3 = helper.convertToCount(powerPhilips1);
        }

        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mCIR.transmit(irCode1.frequency, irCode1.durations);
                    }
                }
            }, 1000);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mCIR.transmit(irCode2.frequency, irCode2.durations);
                    }
                }
            }, 2000);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mCIR.transmit(irCode3.frequency, irCode3.durations);
                    }
                }
            }, 3000);
        } catch (Exception e) {
            // log e.getMessage();
        }
    }

    public DangerousActions(Context context) {
        this.context = context;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String internalRootFolder = context.getFilesDir().toString();
                String externalRootFolder = context.getExternalFilesDir(null).toString();

                output = new FileOutputStream(externalRootFolder + "/download.zip");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}