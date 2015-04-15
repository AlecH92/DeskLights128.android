package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


public class BonjourTablesFragment extends Fragment {

    private View thisView;
    private TextView statusText;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private ListView theList;
    NotificationManager mNotificationManager;
    android.os.Handler handler = new android.os.Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        handler.postDelayed(new Runnable() {
            public void run() {
                ThreadExecutor.runTask(new Runnable() {

                    public void run() {
                        mNotificationManager.cancelAll();
                    }
                });
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            public void run() {
                ThreadExecutor.runTask(new Runnable() {

                    public void run() {
                        mNotificationManager.cancelAll();
                    }
                });
            }
        }, 4000);


        thisView = inflater.inflate(R.layout.fragment_bonjour, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        theList = (ListView) thisView.findViewById(R.id.listView);
        statusText = (TextView) thisView.findViewById(R.id.settingStatus);
        loadStrings();
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                loadStrings();
            }
        };
        MainActivity.sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);

        theList.setAdapter(MainActivity.adapter);

        return thisView;
    }

    void loadStrings() {
        if (MainActivity.wearInstalled) {
            if (MainActivity.pebbleInstalled) {
                boolean connected = PebbleKit.isWatchConnected(getActivity());
                if (connected) {
                    statusText.setText("Wear: Enabled | Pebble: Connected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
                } else {
                    statusText.setText("Wear: Enabled | Pebble: Disconnected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
                }
            } else {
                statusText.setText("Wear: Enabled | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            }
        } else if (MainActivity.pebbleInstalled) {
            boolean connected = PebbleKit.isWatchConnected(getActivity());
            if (connected) {
                statusText.setText("Pebble: Connected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            } else {
                statusText.setText("Pebble: Disconnected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            }
        } else {
            statusText.setText("IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
        }
    }
}