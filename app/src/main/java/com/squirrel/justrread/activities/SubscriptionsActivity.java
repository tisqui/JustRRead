package com.squirrel.justrread.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.widget.TextView;

import com.squirrel.justrread.R;
import com.squirrel.justrread.adapters.SubscriptionsRecyclerViewAdapter;
import com.squirrel.justrread.adapters.SubscriptionsTouchHelper;
import com.squirrel.justrread.data.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubscriptionsActivity extends BaseActivity {

    private static String LOG_TAG = SubscriptionsActivity.class.getSimpleName();
    private RecyclerView mSubscriptionsRecyclerView;
    private SubscriptionsRecyclerViewAdapter mSubscriptionsRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @Bind(R.id.subscriptions_empty_text)
    TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        ButterKnife.bind(this);
        activateToolbarWithHomeEnabled();

        mSubscriptionsRecyclerView = (RecyclerView) findViewById(R.id.subscriptions_recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSubscriptionsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSubscriptionsRecyclerViewAdapter = new SubscriptionsRecyclerViewAdapter(mEmptyText, getDummyData());
        mSubscriptionsRecyclerView.setAdapter(mSubscriptionsRecyclerViewAdapter);

        ItemTouchHelper.Callback callback = new SubscriptionsTouchHelper(mSubscriptionsRecyclerViewAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mSubscriptionsRecyclerView);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_subscriptions, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_detail_share) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private List<Subscription> getDummyData() {
        List<Subscription> list = new ArrayList<Subscription>();
        Subscription s1 = new Subscription("gifs", "/gifs", true);
        Subscription s2 = new Subscription("wtf", "/WTF", true);
        Subscription s3 = new Subscription("awww", "/awww", true);
        Subscription s4 = new Subscription("videos", "/videos", true);
        list.add(s1);
        list.add(s1);
        list.add(s2);
        list.add(s3);list.add(s4);
        return list;

    }
}
