package com.squirrel.justrread.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.fragments.FeedFragment;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;


public class FrontpageFeedActivity extends BaseActivity implements FeedFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = FrontpageFeedActivity.class.getSimpleName();
    Authentification mAuthentification;
    private static String AUTH_STATE = "auth_state";
    private static String[] mSubredditsList = {"/WTF", "/aww", "/funny"};

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerSubredditsList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(savedInstanceState);

//        UserAgent myUserAgent = UserAgent.of();
//
//        reddit = new RedditClient(myUserAgent);
//        reddit.setLoggingMode(LoggingMode.ALWAYS);
//        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(new RedditTokenStore(), reddit));


        setContentView(R.layout.activity_frontpage_feed);
        FeedFragment feedFragment = ((FeedFragment) getSupportFragmentManager()
                .findFragmentById(R.id.feed_fragment));
        getToolbar();

        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.left_drawer_linear_layout);
        mDrawerSubredditsList = (ListView) findViewById(R.id.drawer_subreddits_listview);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerSubredditsList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mSubredditsList));
        mDrawerSubredditsList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                getToolbar(),
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ){
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_reddit, this.getTheme());
//        mDrawerToggle.setHomeAsUpIndicator(drawable);
//        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    mDrawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

       mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reddit);

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinearLayout);
        menu.findItem(R.id.action_login).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Initial settings of the activity
     */
    private void initialize(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            //no saved instance, get the data from the calling intent and add fragments
            mAuthentification = new Authentification(this);
            checkAuthentification();
        } else {
            //got the saved instance, get the items from savedInstanceState.get..(Id);
            checkAuthentification();
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
    }

    private void checkAuthentification(){
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        Log.d(LOG_TAG, "AuthenticationState for onResume(): " + state);

        if(Utils.isNetworkAvailable(getApplicationContext())){
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
        else{
            Toast.makeText(getApplicationContext(), "No internet connection. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the auth state
        savedInstanceState.putString(AUTH_STATE, AuthenticationManager.get().checkAuthState().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

        /** Swaps fragments in the main content view */
        private void selectItem(int position) {
            // Create a new fragment and specify the planet to show based on position
//            Fragment fragment = new Fragment();
//            Bundle args = new Bundle();
            //put arguments
//        args.putString(SubredditFragment.SubredditId, position);
//        fragment.setArguments(args);

//        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, fragment)
//                .commit();

            // Highlight the selected item, update the title, and close the drawer
//            mDrawerSubredditsList.setItemChecked(position, true);
//            setTitle(mSubredditsList[position]);
            Toast.makeText(this, "Item " + position + "clicked", Toast.LENGTH_SHORT).show();
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }

        @Override
        public void setTitle(CharSequence title) {
            mTitle = title;
            getSupportActionBar().setTitle(mTitle);
        }

}
