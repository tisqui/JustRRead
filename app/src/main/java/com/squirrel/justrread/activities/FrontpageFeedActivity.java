package com.squirrel.justrread.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.Init;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.controllers.DrawerController;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.data.Subscription;
import com.squirrel.justrread.fragments.DetailPostFragment;
import com.squirrel.justrread.fragments.FeedFragment;
import com.squirrel.justrread.sync.RedditSyncAdapter;

import java.util.ArrayList;
import java.util.Arrays;


public class FrontpageFeedActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FeedFragment.OnFragmentInteractionListener,
        FeedFragment.Callback,
        DetailPostFragment.OnFragmentInteractionListener {

    static final String LOG_TAG = FrontpageFeedActivity.class.getSimpleName();
    private static String[] mSubredditsList = {"/pics", "/gifs", "/videos"};
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private DrawerLayout mDrawerLayout;
    private DrawerController mDrawerController;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerSubredditsList;
    private ArrayAdapter<String> mDrawerSubredditsListAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    private TextView mEmptyListView;
    private ProgressBar mLoadingListbar;

    private boolean mTwoPane;
    private boolean isFirstLaunch = true; //the flag to define if the app was launched for the first time

    public static final int FRONT_FILTER_HOT = 0;
    public static final int FRONT_FILTER_NEW = 1;
    public static final int FRONT_FILTER_TOP = 2;
    public static final int FRONT_FILTER_CONTROVERSIAL = 3;

    private static final int SUBSCRIPTIONS_LOADER = 1;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    private Button mLogin;

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

        mDrawerSubredditsListAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, new ArrayList<String>(Arrays.asList(mSubredditsList)));
        mDrawerSubredditsList.setAdapter(mDrawerSubredditsListAdapter);
        mDrawerSubredditsList.setOnItemClickListener(new DrawerItemClickListener());
        mEmptyListView = (TextView) findViewById(R.id.drawer_empty_subscriptions_listview);
        mLoadingListbar = (ProgressBar) findViewById(R.id.drawersubscriptions_listview_progress);
        mEmptyListView.setVisibility(View.GONE);
        mLoadingListbar.setVisibility(View.VISIBLE);

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

        mLogin = (Button) mDrawerLayout.findViewById(R.id.drawer_btn_login);
        setLoginButton();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reddit);

        //set all the Drawer actions
        mDrawerController = new DrawerController(mDrawerLayout, this);
        mDrawerController.initDrawerActions();
        mDrawerController.setUserName();
        mDrawerController.setCotentActions(((FeedFragment) getSupportFragmentManager()
                .findFragmentById(R.id.feed_fragment)));
        mDrawerController.setTheme();

        mCallbacks = this;
        getSupportLoaderManager().initLoader(SUBSCRIPTIONS_LOADER, null, mCallbacks);

    }

    public void reloadActivity(){
        this.recreate();
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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        ComponentName cn = new ComponentName(this, SubredditSearchResultsActivity.class);

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(cn));

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
        if(id == R.id.logout){
            Authentification.logout();
        }
        if (id == R.id.action_subreddit_about) {
            FeedFragment feedFragment = ((FeedFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.feed_fragment));
            if (feedFragment.isSubreddit()) {
                Intent intent = new Intent(this, AboutSubredditActivity.class);
                intent.putExtra(BaseActivity.SUBREDDIT_ID_KEY, feedFragment.getSubredditId());
                startActivity(intent);
            }
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

        //load the subscriptions data, if user logged in
        if (Utils.checkUserLoggedIn()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    RedditAPI.getUserSubscriptions(getApplicationContext());
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        } else {
            mEmptyListView.setVisibility(View.VISIBLE);
            mEmptyListView.setText("No subscriptions available for the not logged in user.");
            mLoadingListbar.setVisibility(View.GONE);
            mDrawerSubredditsList.setVisibility(View.GONE);
        }
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
        if (feedFragment != null && mDrawerSubredditsList.getCount() >0) {
            feedFragment.refreshNewSubreddit(mDrawerSubredditsListAdapter.getItem(position));
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerSubredditsList.setItemChecked(position, true);
        setTitle(mDrawerSubredditsListAdapter.getItem(position));

        Toast.makeText(this, "Item " + position + "clicked", Toast.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When the new post is clicked in the feed list - update the detailed fragment for the tablet view.
     *
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
     *
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri subUri = RedditContract.SubscriptionEntry.CONTENT_URI;

        return new CursorLoader(this,
                subUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingListbar.setVisibility(View.GONE);
        mDrawerSubredditsListAdapter.clear();
        int size = data.getCount();
        if (Utils.checkUserLoggedIn()) {
            if (size > 1) {
                mEmptyListView.setVisibility(View.GONE);
                mDrawerSubredditsList.setVisibility(View.VISIBLE);
                for (int i = 0; i < size; i++) {
                    data.moveToPosition(i);
                    Subscription s = DataMapper.mapCursorToSubscription(data);
                    mDrawerSubredditsListAdapter.insert(s.getSubredditDisplayName(), i);
                }
            } else {
                mEmptyListView.setText(R.string.drawer_empty_subscriptions_list);
                mEmptyListView.setVisibility(View.VISIBLE);
                mDrawerSubredditsList.setVisibility(View.GONE);
            }
        } else {
            mEmptyListView.setText(R.string.drawer_empty_subscriptions_list_for_logged_off);
            mEmptyListView.setVisibility(View.VISIBLE);
            mDrawerSubredditsList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDrawerSubredditsListAdapter.clear();
    }

    private void updateEmptyView() {
        if (mDrawerSubredditsList.getCount() == 0) {
            TextView tv = (TextView) findViewById(R.id.drawer_empty_subscriptions_listview);
            if (null != tv) {
                // if cursor is empty, why?
                int message = R.string.empty_posts_list;
                @RedditSyncAdapter.PostsStatus int location = Utils.getSubscriptionsStatus(this);
                switch (location) {
                    case RedditSyncAdapter.SUB_STATUS_SERVER_DOWN:
                        message = R.string.list_posts_server_is_down;
                        break;
                    case RedditSyncAdapter.SUB_STATUS_SERVER_INVALID:
                        message = R.string.list_posts_server_is_invalid;
                        break;
                    case RedditSyncAdapter.SUB_STATUS_INVALID:
                        message = R.string.list_posts_posts_invalid;
                        break;
                    default:
                        if (!Utils.isNetworkAvailable(this)) {
                            message = R.string.no_network_available;
                        }
                }
                tv.setText(message);
            }
        }
    }

    private void setLoginButton(){
        if(!Utils.checkUserLoggedIn()){
            Log.d("DrawerController", "User not logged in ******************************");
            mLogin.setText("Login");
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                }
            });
        } else {
            Log.d("DrawerController", "User logged in ******************************");
            mLogin.setText("Logout");
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Authentification.logout();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            reloadActivity();
                        }
                    }.execute();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                TextView helloUserText = (TextView) mDrawerLayout.findViewById(R.id.drawer_hello_user_text);
                String result = data.getStringExtra("result");
                helloUserText.setText("Hello, " + result + " !");
                setLoginButton();
                Toast.makeText(this, "Logged in as: " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
