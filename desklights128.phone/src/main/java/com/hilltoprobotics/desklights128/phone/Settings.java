package com.hilltoprobotics.desklights128.phone;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class Settings extends PreferenceActivity {

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("aBonjourIP");
            pref.setSummary(MainActivity.bonjourIP);
    }
}