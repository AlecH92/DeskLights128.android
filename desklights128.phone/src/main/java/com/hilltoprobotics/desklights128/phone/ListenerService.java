package com.hilltoprobotics.desklights128.phone;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {
    public static SharedPreferences sharedPrefs;
    String IPAddress = "";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("ListenerService", "Got message");
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        int theColorRed = Color.red(Integer.parseInt(message));
        int theColorBlue = Color.blue(Integer.parseInt(message));
        int theColorGreen = Color.green(Integer.parseInt(message));
        Log.v("red", String.valueOf(theColorRed));
        Log.v("green", String.valueOf(theColorGreen));
        Log.v("blue", String.valueOf(theColorBlue));
        sendData("color?r="+theColorRed+"&g="+theColorGreen+"&b="+theColorBlue);
        Toast.makeText(this, "Wear Sent " + message, Toast.LENGTH_LONG).show();
        Log.d("DL128.phone","Wear Sent: " + message);
    }

    void sendData(String theData) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        IPAddress = sharedPrefs.getString("prefIP", "127.0.1.1");
        String url = "http://" + IPAddress + "/" + theData;
        final ThreadedRequest tReq = new ThreadedRequest(url);
        tReq.start(new Runnable() {
            public void run() {
            }
        });
    }
}