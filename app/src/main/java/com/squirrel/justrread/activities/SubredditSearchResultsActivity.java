package com.squirrel.justrread.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.squirrel.justrread.R;
import com.squirrel.justrread.adapters.SearchResultsListAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/29/16.
 */
public class SubredditSearchResultsActivity extends BaseActivity {

    @Bind(R.id.subreddit_search_results_list)
    ListView mSearchResultsListView;
    private SearchResultsListAdapter mSearchResultsListAdapter;

    ArrayList<String> listOfResults;


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
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            listOfResults.add("One");
            listOfResults.add("Two");
            mSearchResultsListAdapter.addAll(listOfResults);
        }
    }
}
