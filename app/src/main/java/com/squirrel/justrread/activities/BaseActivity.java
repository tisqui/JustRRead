package com.squirrel.justrread.activities;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.squirrel.justrread.R;

/**
 * Created by squirrel on 4/24/16.
 */
public class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    Navigator navigator;

    public static final String FRONTPAGE_FEED_KEY = "FRONTPAGE_FEED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator();
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.application_toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        getToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }

    /**
     * Add the fragment to this activity
     * @param containerId the container id to where to add the fragment
     * @param fragment fragment to add
     */
    protected void addFragment(int containerId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(containerId, fragment);
        fragmentTransaction.commit();
    }
}
