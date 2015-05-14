package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


public class PlaySnakeFragment extends Fragment {

    public Button upButton;
    public Button downButton;
    public Button leftButton;
    public Button rightButton;
    public Button startButton;
    public Button resetButton;
    private View thisView;
    private TextView statusText;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView =inflater.inflate(R.layout.fragment_playsnake, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        startButton = (Button) thisView.findViewById(R.id.button6);
        resetButton = (Button) thisView.findViewById(R.id.button5);
        downButton = (Button) thisView.findViewById(R.id.button4);
        rightButton = (Button) thisView.findViewById(R.id.button3);
        leftButton = (Button) thisView.findViewById(R.id.button2);
        upButton = (Button) thisView.findViewById(R.id.button);
        statusText = (TextView) thisView.findViewById(R.id.settingStatus);
        loadStrings();
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                loadStrings();
            }
        };
        MainActivity.sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=6";
                activity.sendData(whatToSend);
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=1";
                activity.sendData(whatToSend);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=2";
                activity.sendData(whatToSend);
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=3";
                activity.sendData(whatToSend);
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=4";
                activity.sendData(whatToSend);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "snake?id=5";
                activity.sendData(whatToSend);
            }
        });
        return thisView;
    }

    void loadStrings() {
        if(MainActivity.wearInstalled) {
            if (MainActivity.pebbleInstalled) {
                boolean connected = PebbleKit.isWatchConnected(getActivity());
                if (connected) {
                    statusText.setText("Wear: Enabled | Pebble: Connected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
                } else {
                    statusText.setText("Wear: Enabled | Pebble: Disconnected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
                }
            }
            else {
                statusText.setText("Wear: Enabled | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            }
        }
        else if(MainActivity.pebbleInstalled) {
            boolean connected = PebbleKit.isWatchConnected(getActivity());
            if (connected) {
                statusText.setText("Pebble: Connected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            } else {
                statusText.setText("Pebble: Disconnected | IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
            }
        }
        else {
            statusText.setText("IP: " + MainActivity.sharedPrefs.getString("prefIP", "127.0.0.1"));
        }
    }
}