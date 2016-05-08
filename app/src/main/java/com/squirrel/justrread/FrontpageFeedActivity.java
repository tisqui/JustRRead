package com.squirrel.justrread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.activities.LoginActivity;
import com.squirrel.justrread.activities.Navigator;
import com.squirrel.justrread.fragments.FeedFragment;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthException;


public class FrontpageFeedActivity extends BaseActivity implements FeedFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = FrontpageFeedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserAgent myUserAgent = UserAgent.of("","","","");

        RedditClient reddit = new RedditClient(myUserAgent);
        reddit.setLoggingMode(LoggingMode.ALWAYS);
        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(new RedditTokenStore(), reddit));



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
                Toast.makeText(FrontpageFeedActivity.this, "Log in first", Toast.LENGTH_SHORT).show();
                break;
            case NEED_REFRESH:
                refreshAccessTokenAsync();
                break;
        }
    }

    private void refreshAccessTokenAsync() {
        new AsyncTask<Credentials, Void, Void>() {
            @Override
            protected Void doInBackground(Credentials... params) {
                try {
                    AuthenticationManager.get().refreshAccessToken(LoginActivity.CREDENTIALS);
                } catch (NoSuchTokenException | OAuthException e) {
                    Log.e(LOG_TAG, "Could not refresh access token", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d(LOG_TAG, "Reauthenticated");
            }
        }.execute();
    }
}
