package com.squirrel.justrread.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.squirrel.justrread.R;

/**
 * Created by squirrel on 6/14/16.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

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
