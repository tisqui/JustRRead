package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squirrel.justrread.R;
import com.squirrel.justrread.fragments.DetailPostFragment;

/**
 * Created by squirrel on 5/25/16.
 */
public class DetailedPostActivity extends BaseActivity implements DetailPostFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = DetailedPostActivity.class.getSimpleName();
    private CharSequence mTitle;


    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO add the on interaction handling
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);
        activateToolbarWithHomeEnabled();
        mTitle = "";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_detail_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_detail_share) {
//            return true;
//        }
//        if (id == R.id.action_detail_search) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, FrontpageFeedActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
}
