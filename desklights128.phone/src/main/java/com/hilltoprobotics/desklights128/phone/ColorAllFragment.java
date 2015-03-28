package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


public class ColorAllFragment extends Fragment {

    public Button updateButton;
    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView =inflater.inflate(R.layout.fragment_colorall, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        updateButton = (Button) thisView.findViewById(R.id.updateButton);
//pebble status text
        TextView statusText = (TextView) thisView.findViewById(R.id.settingStatus);
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
        //end pebble status text

        updateButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String whatToSend = "color?h=" + Integer.toHexString(activity.theColor).toUpperCase().substring(2);
                    activity.sendData(whatToSend);
                }
            });
            return thisView;
        }

}