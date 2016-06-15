package com.squirrel.justrread.activities;

import android.os.Bundle;

import com.squirrel.justrread.R;
import com.squirrel.justrread.fragments.SettingsFragment;

/**
 * Created by squirrel on 6/14/16.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        activateToolbarWithHomeEnabled();
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, new SettingsFragment()).commit();
    }
}
