package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BonjourTablesFragment extends Fragment {

    private static String TAG = "dl128";
    private View thisView;
    private TextView statusText;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private ListView theList;
    public static SharedPreferences sharedPrefs;
    public static final ArrayList<String> list = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_bonjour, container, false);

        theList = (ListView) thisView.findViewById(R.id.listView);
        statusText = (TextView) thisView.findViewById(R.id.settingStatus);
        loadStrings();
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                loadStrings();
            }
        };
        MainActivity.sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, list);
        theList.setAdapter(adapter);

        theList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sharedPrefs.edit().putString("prefIP", list.get(position)).apply();
            }
        });
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
    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}