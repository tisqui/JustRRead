package com.squirrel.justrread.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.squirrel.justrread.R;

/**
 * Created by squirrel on 6/14/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_nightmode_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_nsfw_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference instanceof SwitchPreference) {
            boolean state = Boolean.parseBoolean(value.toString());
            ((SwitchPreference) preference).setChecked(state);
        }
        return true;
    }
}
