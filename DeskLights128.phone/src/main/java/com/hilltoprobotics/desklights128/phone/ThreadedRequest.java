package com.hilltoprobotics.desklights128.phone;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ThreadedRequest
{
    private URL url;
    private Handler mHandler;
    private Runnable pRunnable;

    public ThreadedRequest(String newUrl)
    {
        try {
            url = new URL(newUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mHandler = new Handler();
    }

    public void start(Runnable newRun)
    {
        pRunnable = newRun;
        processRequest.start();
    }

    private Thread processRequest = new Thread()
    {
        public void run()
        {
            //Do you request here...
            Log.v("t", "Web Send: " + url);
            try {
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(hc.getInputStream());
                in.read();
                hc.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pRunnable == null || mHandler == null) return;
            mHandler.post(pRunnable);
        }
    };
}