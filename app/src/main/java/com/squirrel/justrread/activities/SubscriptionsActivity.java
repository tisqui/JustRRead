package com.squirrel.justrread.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squirrel.justrread.R;
import com.squirrel.justrread.adapters.SubscriptionsRecyclerViewAdapter;
import com.squirrel.justrread.adapters.SubscriptionsTouchHelper;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.data.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubscriptionsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = SubscriptionsActivity.class.getSimpleName();
    private RecyclerView mSubscriptionsRecyclerView;
    private SubscriptionsRecyclerViewAdapter mSubscriptionsRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private static final int SUBSCRIPTIONS_LOADER = 1;

    @Bind(R.id.subscriptions_empty_text)
    TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open, R.anim.activity_close);
        setContentView(R.layout.activity_subscriptions);
        ButterKnife.bind(this);
        activateToolbarWithHomeEnabled();

        mSubscriptionsRecyclerView = (RecyclerView) findViewById(R.id.subscriptions_recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubscriptionsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSubscriptionsRecyclerViewAdapter = new SubscriptionsRecyclerViewAdapter(mEmptyText, new ArrayList<Subscription>());
        mSubscriptionsRecyclerView.setAdapter(mSubscriptionsRecyclerViewAdapter);

        ItemTouchHelper.Callback callback = new SubscriptionsTouchHelper(mSubscriptionsRecyclerViewAdapter, this);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mSubscriptionsRecyclerView);

        getSupportLoaderManager().initLoader(SUBSCRIPTIONS_LOADER, null, this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subscriptions, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.subscriptions_action_search).getActionView();

        ComponentName cn = new ComponentName(this, SubredditSearchResultsActivity.class);

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(cn));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
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
        List<Subscription> savedSubscriptions = new ArrayList<Subscription>();
        while(data.moveToNext()){
            savedSubscriptions.add(DataMapper.mapCursorToSubscription(data));
        }
        mSubscriptionsRecyclerViewAdapter.swapSubscriptionsList(savedSubscriptions);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSubscriptionsRecyclerViewAdapter.swapSubscriptionsList(null);
    }

}
