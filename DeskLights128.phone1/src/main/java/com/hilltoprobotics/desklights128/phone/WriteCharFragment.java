package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


public class WriteCharFragment extends Fragment {

    public Button updateButton;
    public TextView delayTime;
    private View thisView;
    private TextView statusText;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_writechar, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        updateButton = (Button) thisView.findViewById(R.id.updateButton);
        delayTime = (EditText) thisView.findViewById(R.id.delayTime);
        statusText = (TextView) thisView.findViewById(R.id.settingStatus);
        loadStrings();
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                loadStrings();
            }
        };
        MainActivity.sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);

        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "write?l=" + delayTime.getText().length() + "&c=" + delayTime.getText();
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