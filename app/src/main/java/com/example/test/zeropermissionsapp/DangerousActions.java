package com.example.test.zeropermissionsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.test.zeropermissionsapp.models.IRCode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jesper Laptop on 29-3-2016.
 */
public class DangerousActions {
    private Context mContext;
    ConsumerIrManager mCIR;

    public DangerousActions(Context context) {
        this.mContext = context;
    }

    public void sendIR() {
        final IRCode irCode1;
        final IRCode irCode2;
        final IRCode irCode3;
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

        mCIR = (ConsumerIrManager) mContext.getSystemService(Context.CONSUMER_IR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!mCIR.hasIrEmitter()) {
                //Notify if no IR Emitter
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

    public void download() {
        final DownloadTask downloadTask = new DownloadTask(mContext);
        downloadTask.execute("http://speedtest.reliableservers.com/10MBtest.bin");
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.mContext = context;
            final DownloadTask downloadTask = this;
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Downloading file");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                    Toast.makeText(mContext, "Download cancelled", Toast.LENGTH_LONG).show();
                }
            });
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

                input = connection.getInputStream();
                String internalRootFolder = mContext.getFilesDir().toString();
                //String externalRootFolder = mContext.getExternalFilesDir(null).toString();

                output = new FileOutputStream(internalRootFolder + "/download.zip");

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, "File downloaded", Toast.LENGTH_LONG).show();
        }
    }
}