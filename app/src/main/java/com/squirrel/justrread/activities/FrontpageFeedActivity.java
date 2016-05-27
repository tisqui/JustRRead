package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squirrel.justrread.Init;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.controllers.DrawerController;
import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.fragments.DetailPostFragment;
import com.squirrel.justrread.fragments.FeedFragment;


public class FrontpageFeedActivity extends BaseActivity implements FeedFragment.OnFragmentInteractionListener, FeedFragment.Callback, DetailPostFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = FrontpageFeedActivity.class.getSimpleName();
    private static String[] mSubredditsList = {"/pics", "/gifs", "/videos"};
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private DrawerLayout mDrawerLayout;
    private DrawerController mDrawerController;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerSubredditsList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    private boolean mTwoPane;
    private boolean isFirstLaunch = true; //the flag to define if the app was launched for the first time

    public static final int FRONT_FILTER_HOT = 0;
    public static final int FRONT_FILTER_NEW = 1;
    public static final int FRONT_FILTER_TOP = 2;
    public static final int FRONT_FILTER_CONTROVERSIAL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Init myInit = (Init) getApplicationContext();
        myInit.initializeAuth();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage_feed);
        getToolbar();

        if (findViewById(R.id.two_pane_fragment_post_detail) != null) {
            // The application is in two pane mode
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

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
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reddit);

        //set all the Drawer actions
        mDrawerController = new DrawerController(mDrawerLayout);
        mDrawerController.initDrawerActions(this);

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinearLayout);
        menu.findItem(R.id.action_hot).setVisible(!drawerOpen);
        menu.findItem(R.id.action_top).setVisible(!drawerOpen);
        menu.findItem(R.id.action_new).setVisible(!drawerOpen);
        menu.findItem(R.id.action_controversial).setVisible(!drawerOpen);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
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
        if (id == R.id.action_hot) {
            Utils.saveMainFeedSortToSharedPrefs(this, FRONT_FILTER_HOT);
            return true;
        }
        if (id == R.id.action_top) {
            Utils.saveMainFeedSortToSharedPrefs(this, FRONT_FILTER_TOP);
            return true;
        }
        if (id == R.id.action_new) {
            Utils.saveMainFeedSortToSharedPrefs(this, FRONT_FILTER_NEW);
            return true;
        }
        if (id == R.id.action_controversial) {
            Utils.saveMainFeedSortToSharedPrefs(this, FRONT_FILTER_CONTROVERSIAL);
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


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Update the feed fragment content when the new subreddit is selected
     */
    private void selectItem(int position) {

        // Call the method to refresh the feed for subreddit now
        FeedFragment feedFragment = ((FeedFragment) getSupportFragmentManager()
                .findFragmentById(R.id.feed_fragment));
        if(feedFragment != null){
            feedFragment.refreshNewSubreddit(convertPositionToId(position));
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerSubredditsList.setItemChecked(position, true);
        setTitle(mSubredditsList[position]);

        Toast.makeText(this, "Item " + position + "clicked", Toast.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private String convertPositionToId(int pos){
        switch(pos){
            case 0:
                return "pics";
            case 1:
                return "gifs";
            case 2:
                return "videos";
            default: return "t5_2qh1e";
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When the new post is clicked in the feed list - update the detailed fragment for the tablet view.
     * @param post - the post object passed to the detailed view
     */
    @Override
    public void onItemSelected(Post post) {

        if (isFirstLaunch) {
            isFirstLaunch = false;
            if (mTwoPane) {
                setDetailsFragmentForTablet(post);
            }
        } else {
            if (mTwoPane) {
                setDetailsFragmentForTablet(post);
            } else {
                Intent intent = new Intent(this, DetailedPostActivity.class);
                intent.putExtra(BaseActivity.POST_DETAIL_KEY, post);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onWebOpen(String url) {
        //TODO add the logic for opened web page
    }

    /**
     * Set up the post which will be passed to tge detailed fragment
     * @param post
     */
    private void setDetailsFragmentForTablet(Post post) {
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.POST_DETAIL_KEY, post);

        //pass the movie object to the fragment
        DetailPostFragment detailsFragment = new DetailPostFragment();
        detailsFragment.setArguments(args);

        //start fragment transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.two_pane_fragment_post_detail, detailsFragment, DETAILFRAGMENT_TAG)
                .commit();
    }

}
