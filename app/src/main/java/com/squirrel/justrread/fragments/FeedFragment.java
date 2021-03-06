package com.squirrel.justrread.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.AboutSubredditActivity;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.adapters.PostClickListener;
import com.squirrel.justrread.adapters.PostsFeedAdapter;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.listeners.EndlessRecyclerViewScrollListener;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.ArrayList;


public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static String LOG_TAG = FeedFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    //    private FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;
    private PostsFeedAdapter mPostsFeedAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private LinearLayoutManager mLinearLayoutManager;
    private static final int POSTS_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    public static final String Y_OFFSET_KEY = "y_offset";
    public static final int TWO_PANE_UNDEFINED = 2;

    private int mCurrentPage;
    private SubredditPaginator mSubredditPaginator;
    private RedditAPI mRedditAPI;

    private boolean mIsSubreddit;
    private String mSubredditId;
    private String mDefaultPageTitle;

    private boolean mTwoPane;

    private boolean mCanUpdate = true;
    private int mFeedPageSize = 50;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri postsUri = RedditContract.PostEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                postsUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPostsFeedAdapter.swapCursor(data);
        updateEmptyView();
        if (data.getCount() != 0) {
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (mPosition == RecyclerView.NO_POSITION) {
                            mPosition = 0;
                            mPostsFeedAdapter.onClick(null, mPosition);
                        }
                        // If we don't need to restart the loader, and there's a desired position to restore
                        // to, do so now.

                        if (mPosition < mPostsFeedAdapter.getCursor().getCount()) {
                            RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(mPosition);
                                //only for tablets open the detailed fragment
                                mPostsFeedAdapter.onClick(null, mPosition);
                                if(mTwoPane) {
                                    ((Callback) getActivity()).onItemSelected(DataMapper.mapCursorToPost(mPostsFeedAdapter.getCursor()));
                                }
                        }
                        mRecyclerView.smoothScrollToPosition(mPosition);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPostsFeedAdapter.swapCursor(null);
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Post post);
    }


    private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructor
    }


    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POSTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRedditAPI = new RedditAPI();
        setHasOptionsMenu(true);

        mDefaultPageTitle = getContext().getString(R.string.drawer_page_title_all);

        // If there's instance state, get the last selected position
//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            if(!mTwoPane){
//                mPosition = savedInstanceState.getInt(SELECTED_KEY);
//                if(mRecyclerView != null){
//                    mRecyclerView.smoothScrollToPosition(mPosition);
//                }
//            }
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        int offset = mRecyclerView.computeVerticalScrollOffset();
        outState.putInt(Y_OFFSET_KEY, offset);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentPage = 1;
        setIsSubreddit(Utils.getiSSubredditFromSharePrefs(getContext()));

        if(isSubreddit()){
            mSubredditId = Utils.getSubredditId(getContext());
        }

        setPageTitle();

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTwoPane = arguments.getBoolean(getContext().getString(R.string.feed_two_pane_key));
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        View emptyView = rootView.findViewById(R.id.posts_list_emply_message);

        mPostsFeedAdapter = new PostsFeedAdapter(new ArrayList<Post>(), getActivity(), emptyView, mTwoPane);
        mRecyclerView.setAdapter(mPostsFeedAdapter);

        mRecyclerView.addOnItemTouchListener(new PostClickListener(getActivity().getApplicationContext(),
                mRecyclerView, new PostClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPostsFeedAdapter.onClick(view, position);
                Post post = DataMapper.mapCursorToPost(mPostsFeedAdapter.getCursor());
                mPosition = position;
                ((Callback) getActivity()).onItemSelected(post);
//                Navigator.navigateToWebview(getContext(), post.getUrl());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO do tmth on long click
            }
        }));

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Utils.isNetworkAvailable(getContext())) {
                     Log.d(LOG_TAG, "LOADING MORE");
                        //show the loading
                        mPostsFeedAdapter.showLoading(true);
                        mPostsFeedAdapter.notifyDataSetChanged();

                        if (mSubredditPaginator != null) {
                            new AsyncTask<Void, Void, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    if (!mIsSubreddit) {
                                       return mRedditAPI.getPostsFront(mSubredditPaginator, getContext(), false);
                                    } else {
                                       return mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                                    }
                                }

                                @Override
                                protected void onPostExecute(Boolean ifDownloadedNew) {
                                    super.onPostExecute(ifDownloadedNew);
                                    mPostsFeedAdapter.showLoading(false);
                                    if(ifDownloadedNew) {
                                        mPostsFeedAdapter.notifyDataSetChanged();
                                        mCurrentPage++;
                                        // mPosition += mPageSize;
                                    }else{
                                        Toast.makeText(getContext(), R.string.feed_no_posts_more_txt, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }.execute();
                        }
                }else{
                    Toast.makeText(getContext(), R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // If there's instance state, get the last selected position
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(SELECTED_KEY)){
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            if(!mTwoPane && savedInstanceState.containsKey(Y_OFFSET_KEY)){
                final int yOffsset = savedInstanceState.getInt(Y_OFFSET_KEY);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollBy(0, yOffsset);
//                        mRecyclerView.scrollTo(0, yOffsset);
                    }
                });
            }
        }

        //set the refresh
        mSwipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_swipe_container);
        mSwipeContainer.setOnRefreshListener(this);

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return rootView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + context.getString(R.string.feed_must_implement_msg));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //register shared prefs listener
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);

        getInitialFrontpage();

    }

    /**
     * Get the data for the frontpage feed
     */
    public void getInitialFrontpage(){
        if (Utils.isNetworkAvailable(getContext())) {
            mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
            mSubredditPaginator.setLimit(mFeedPageSize);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (!mIsSubreddit) {
                            try {
                                mRedditAPI.getPostsFront(mSubredditPaginator, getContext(), true);
                            }catch (NetworkException e){
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                            }catch (NetworkException e){
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
//                        Toast.makeText(getContext(), "Now browsing frontpage " + mSubredditPaginator.getSubreddit(), Toast.LENGTH_SHORT).show();
                    }
                }.execute();
        } else {
            Toast.makeText(getContext(), R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /*
       Updates the empty list view with contextually relevant information that the user can
       use to determine why they aren't seeing data.
    */
    private void updateEmptyView() {
        if (mPostsFeedAdapter.getItemCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.posts_list_emply_message);
            mRecyclerView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            if (null != tv) {
                // if cursor is empty, why?
                int message = R.string.empty_posts_list;
                tv.setText(message);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_posts_status_key))) {
            updateEmptyView();
        }
        if (key.equals(getString(R.string.front_filter_key))) {
            Toast.makeText(getContext(), R.string.feed_getting_new_posts, Toast.LENGTH_SHORT).show();
            refreshNewSorting();
        }
    }

    @Override
    public void onRefresh() {
        if (Utils.isNetworkAvailable(getContext())) {
            Log.d(LOG_TAG, "Refresh of the list started");
            if (mCanUpdate) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        mCanUpdate = false;
                        mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
                        if (!mIsSubreddit) {
                            mRedditAPI.getPostsFront(mSubredditPaginator, getContext(), false);
                        } else {
                            mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mCanUpdate = true;
                        cleanAllSettingsOnrefresh();
                        mSwipeContainer.setRefreshing(false);
                    }
                }.execute();
            } else {
                Log.d(LOG_TAG, "Previous Update is still in progress");
                mSwipeContainer.setRefreshing(false);
            }
        }else {
            Toast.makeText(getContext(), R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
            mSwipeContainer.setRefreshing(false);
        }
    }

    /**
     * Get the lists of posts sorted by the new sorting parameter value
     */
    public void refreshNewSorting() {
        if (Utils.isNetworkAvailable(getContext())) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
                    Sorting sort = Utils.getMainFeedSortFromSharedPrefs(getContext());
                    if (sort != null) {
                        if (!mIsSubreddit) {
                            mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), null, sort);
                        } else {
                            mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, sort);
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    cleanAllSettingsOnrefresh();
                    setPageTitle();
                }
            }.execute();
        } else {
            Toast.makeText(getContext(), R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isSubreddit() {
        return mIsSubreddit;
    }

    public void setIsSubreddit(boolean isSubreddit) {
        mIsSubreddit = isSubreddit;
        Utils.saveiSSubredditToSharedPrefs(getContext(), mIsSubreddit);
    }

    public String getSubredditId() {
        return mSubredditId;
    }

    /**
     * Load the posts for the new subreddit
     * @param subredditId the id of the subreddit to load
     */
    public void refreshNewSubreddit(final String subredditId) {
        if (Utils.isNetworkAvailable(getContext())) {
            setIsSubreddit(true);
            mSubredditId = subredditId;
            Utils.saveSubredditIdToSharedPrefs(getContext(), mSubredditId);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
                    mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    cleanAllSettingsOnrefresh();
                    Toast.makeText(getContext(), "Now browsing " + mSubredditPaginator.getSubreddit(), Toast.LENGTH_SHORT).show();
                    mSubredditId = mSubredditPaginator.getSubreddit();
                    setPageTitle();
                }
            }.execute();
        } else {
            Toast.makeText(getContext(), R.string.no_iternet_connection_subreddit, Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanAllSettingsOnrefresh() {
        mPosition = 0;
        mCurrentPage = 1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(mIsSubreddit){
            menu.findItem(R.id.action_subreddit_about).setVisible(true);
        } else {
            menu.findItem(R.id.action_subreddit_about).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_subreddit_about:
                if(mIsSubreddit){
                    Intent intent = new Intent(getContext(), AboutSubredditActivity.class);
                    intent.putExtra(BaseActivity.SUBREDDIT_ID_KEY, mSubredditId);
                    startActivity(intent);
                }
                return false;
            default:
                break;
        }
        return false;
    }

    /**
     * Set the activity page title. Format /subreddit_name | filter_value
     */
    public void setPageTitle(){
        if(isSubreddit()){
            getActivity().setTitle("/" + mSubredditId + " | " + Utils.getMainFeedSortFromSharedPrefs(getContext()));
        } else {
            getActivity().setTitle(mDefaultPageTitle + " | " + Utils.getMainFeedSortFromSharedPrefs(getContext()));
        }
    }

    public void setDefaultPageTitle(String defaultPageTitle) {
        mDefaultPageTitle = defaultPageTitle;
    }
}
