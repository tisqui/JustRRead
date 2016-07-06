package com.squirrel.justrread.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squirrel.justrread.BuildConfig;
import com.squirrel.justrread.Init;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.adapters.SearchResultsListAdapter;
import com.squirrel.justrread.api.RedditAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/29/16.
 */
public class SubredditSearchResultsActivity extends BaseActivity {

    @Bind(R.id.subreddit_search_results_list)
    ListView mSearchResultsListView;

    @Bind(R.id.search_empty_results)
    TextView mEmptyView;

    @Bind(R.id.search_progress_bar)
    ProgressBar mSearchProgress;

    private SearchResultsListAdapter mSearchResultsListAdapter;

    List<String> listOfResults;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open, R.anim.activity_close);
        setContentView(R.layout.activity_subreddit_search_results);
        activateToolbarWithHomeEnabled();
        ButterKnife.bind(this);

        mTracker = ((Init)getApplication()).getDefaultTracker();

        listOfResults = new ArrayList<String>();

        mSearchResultsListAdapter = new SearchResultsListAdapter(this, listOfResults);
        mSearchResultsListView.setAdapter(mSearchResultsListAdapter);
        mSearchProgress.setVisibility(View.GONE);

        AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(BuildConfig.TEST_DEVICE_ID)
                .build();
        if(mAdView != null){
            mAdView.loadAd(adRequest);
        }
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchProgress.setVisibility(View.VISIBLE);

            //add GA event
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.ga_search_category))
                    .setAction(getString(R.string.ga_search_action))
                    .setLabel(query)
                    .build());

            if(Utils.isNetworkAvailable(this)) {
                new AsyncTask<Void, Void, List<String>>() {
                    @Override
                    protected List<String> doInBackground(Void... params) {
                        return RedditAPI.searchForSubreddit(query, RedditAPI.showNSFW);
                    }

                    @Override
                    protected void onPostExecute(List<String> result) {
                        super.onPostExecute(result);
                        mSearchProgress.setVisibility(View.GONE);
                        listOfResults = result;
                        if (listOfResults != null) {
                            mSearchResultsListAdapter.addAll(listOfResults);
                            mEmptyView.setVisibility(View.GONE);
                        } else {
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                }.execute();
            }else {
                mEmptyView.setText(R.string.no_internet_connection_search_results);
            }
        }
        mEmptyView.setVisibility(View.VISIBLE);
    }
}
