package com.example.kourtney.pokedex;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Kourtney on 11/26/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

