package com.squirrel.justrread.fragments;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.adapters.PostClickListener;
import com.squirrel.justrread.adapters.PostsFeedAdapter;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.listeners.EndlessRecyclerViewScrollListener;
import com.squirrel.justrread.sync.RedditSyncAdapter;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.ArrayList;


public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener, SwipeRefreshLayout.OnRefreshListener {
    private static String LOG_TAG = FeedFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    //    private FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;
    private PostsFeedAdapter mPostsFeedAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private LinearLayoutManager mLinearLayoutManager;
    private static final int POSTS_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    public static final String SUBREDDIT_KEY = "subreddit_key";

    private int mCurrentPage;
    private SubredditPaginator mSubredditPaginator;
    private RedditAPI mRedditAPI;

    private boolean mIsSubreddit;
    private String mSubredditId;

    private boolean mCanUpdate = true;

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
        if (data.getCount() == 0) {
//            getActivity().supportStartPostponedEnterTransition();
        } else {
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int position = mPosition;
                        if (position == RecyclerView.NO_POSITION) position = 0;
                        // If we don't need to restart the loader, and there's a desired position to restore
                        // to, do so now.

                        if (position < mPostsFeedAdapter.getCursor().getCount()) {
                            mRecyclerView.smoothScrollToPosition(position);
                            RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(position);

                            mPostsFeedAdapter.onClick(null, position);
                            ((Callback) getActivity()).onItemSelected(DataMapper.mapCursorToPost(mPostsFeedAdapter.getCursor()));
                        }

//                        if (null != vh && mAutoSelectView) {
//                            FeedRecyclerViewAdapter.selectView(vh);
//                        }
//                        if ( mHoldForTransition ) {
//                            getActivity().supportStartPostponedEnterTransition();
//                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPostsFeedAdapter.swapCursor(null);
    }


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Post post);

        public void onWebOpen(String url);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "test");
        fragment.setArguments(args);
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentPage = 1;
        mIsSubreddit = false;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        View emptyView = rootView.findViewById(R.id.posts_list_emply_message);

        mPostsFeedAdapter = new PostsFeedAdapter(new ArrayList<Post>(), getActivity().getApplicationContext(), emptyView);
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
                if (mCanUpdate) {
                    Log.d(LOG_TAG, "LOADING MORE");
                    //show the loading
                    mPostsFeedAdapter.showLoading(true);
                    mPostsFeedAdapter.notifyDataSetChanged();

                    if (mSubredditPaginator != null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                mCanUpdate = false;
                                if (!mIsSubreddit) {
                                    mRedditAPI.getPostsFront(mSubredditPaginator, getContext());
                                } else {
                                    mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                mPostsFeedAdapter.showLoading(false);
                                mPostsFeedAdapter.notifyDataSetChanged();
                                mCanUpdate = true;
                                mCurrentPage++;
                                mPosition += 50;
                            }
                        }.execute();
                    }
                } else {
                    Log.d(LOG_TAG, "Previous Update is still in progress, can't load more now.");
                }
            }
        });

        // If there's instance state, get the last selected position
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

        mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
        mSubredditPaginator.setLimit(50);
        if (mCanUpdate) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mCanUpdate = false;
                    if (!mIsSubreddit) {
                        mRedditAPI.getPostsFront(mSubredditPaginator, getContext());
                    } else {
                        mRedditAPI.getSubredditPostsSorted(mSubredditPaginator, getContext(), mSubredditId, null);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mCanUpdate = true;
                }
            }.execute();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*
       Updates the empty list view with contextually relevant information that the user can
       use to determine why they aren't seeing weather.
    */
    private void updateEmptyView() {
        if (mPostsFeedAdapter.getItemCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.posts_list_emply_message);
            if (null != tv) {
                // if cursor is empty, why?
                int message = R.string.empty_posts_list;
                @RedditSyncAdapter.PostsStatus int location = Utils.getPostsStatus(getActivity());
                switch (location) {
                    case RedditSyncAdapter.POSTS_STATUS_SERVER_DOWN:
                        message = R.string.list_posts_server_is_down;
                        break;
                    case RedditSyncAdapter.POSTS_STATUS_SERVER_INVALID:
                        message = R.string.list_posts_server_is_invalid;
                        break;
                    case RedditSyncAdapter.POSTS_STATUS_INVALID:
                        message = R.string.list_posts_posts_invalid;
                        break;
                    default:
                        if (!Utils.isNetworkAvailable(getActivity())) {
                            message = R.string.no_network_available;
                        }
                }
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
            Toast.makeText(getContext(), "Getting the new posts", Toast.LENGTH_SHORT).show();
            refreshNewSorting();
        }
    }

    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "Refresh of the list started");
        if (mCanUpdate) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    mCanUpdate = false;
                    mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
                    if (!mIsSubreddit) {
                        mRedditAPI.getPostsFront(mSubredditPaginator, getContext());
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
        }
    }

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
                }
            }.execute();
        } else {
            Toast.makeText(getContext(), "Internet connection is not available, please try later.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isSubreddit() {
        return mIsSubreddit;
    }

    public void setIsSubreddit(boolean isSubreddit) {
        mIsSubreddit = isSubreddit;
    }

    public void refreshNewSubreddit(final String subredditId) {
        if (Utils.isNetworkAvailable(getContext())) {
            mIsSubreddit = true;
            mSubredditId = subredditId;
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
                }
            }.execute();
        } else {
            Toast.makeText(getContext(), "Internet connection is not available, please try later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanAllSettingsOnrefresh() {
        mPosition = 0;
        mCurrentPage = 1;
    }
}
