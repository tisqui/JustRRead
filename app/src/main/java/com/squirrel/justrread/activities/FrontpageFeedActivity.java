package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.fragments.FeedFragment;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;


public class FrontpageFeedActivity extends BaseActivity implements FeedFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = FrontpageFeedActivity.class.getSimpleName();
    RedditClient reddit;
    Authentification mAuthentification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuthentification = new Authentification(this);

//        UserAgent myUserAgent = UserAgent.of();
//
//        reddit = new RedditClient(myUserAgent);
//        reddit.setLoggingMode(LoggingMode.ALWAYS);
//        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(new RedditTokenStore(), reddit));

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
        if (id == R.id.action_login) {
            Navigator.navigateToLogin(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        Log.d(LOG_TAG, "AuthenticationState for onResume(): " + state);

        switch (state) {
            case READY:
                break;
            case NONE:
                Toast.makeText(FrontpageFeedActivity.this, "Authentification without login", Toast.LENGTH_SHORT).show();
                mAuthentification.authentificateWithoutLoginAsync();
//                authentificateWithoutLoginAsync();
                break;
            case NEED_REFRESH:
                mAuthentification.refreshAccessTokenAsync();
//                refreshAccessTokenAsync();
                break;
        }
    }

}
