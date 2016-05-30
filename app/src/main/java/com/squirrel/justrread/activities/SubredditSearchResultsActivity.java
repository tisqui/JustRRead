package com.squirrel.justrread.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.squirrel.justrread.R;
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

    private SearchResultsListAdapter mSearchResultsListAdapter;
    private boolean mIsNsfw = false;

    List<String> listOfResults;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_search_results);
        activateToolbarWithHomeEnabled();
        ButterKnife.bind(this);

        listOfResults = new ArrayList<String>();

        mSearchResultsListAdapter = new SearchResultsListAdapter(this, listOfResults);
        mSearchResultsListView.setAdapter(mSearchResultsListAdapter);

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
            //use the query to search your data somehow
            new AsyncTask<Void, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(Void... params) {
                    return RedditAPI.searchForSubreddit(query, mIsNsfw);
                }

                @Override
                protected void onPostExecute(List<String> result) {
                    super.onPostExecute(result);
                    listOfResults = result;
                    if(listOfResults != null){
                        mSearchResultsListAdapter.addAll(listOfResults);
                        mEmptyView.setVisibility(View.GONE);
                    }else {
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }.execute();
        }
        mEmptyView.setVisibility(View.VISIBLE);
    }
}
