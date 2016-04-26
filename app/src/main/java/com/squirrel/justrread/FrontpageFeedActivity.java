package com.squirrel.justrread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.fragments.FeedFragment;

public class FrontpageFeedActivity extends BaseActivity implements FeedFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage_feed);
        FeedFragment feedFragment = ((FeedFragment) getSupportFragmentManager()
                .findFragmentById(R.id.feed_fragment));
        initialize(savedInstanceState);
        getToolbar();
    }

    /**
     * Initial settings of the activity
     */
    private void initialize(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            //no saved instance, get the data from the calling intent and add fragments

        } else {
            //got the saved instance, get the items from savedInstanceState.get..(Id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frontpage_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, FrontpageFeedActivity.class);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO smth
    }
}
