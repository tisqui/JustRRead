package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by squirrel on 6/7/16.
 */
public class SubscriptionsTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SubscriptionsRecyclerViewAdapter mSubscriptionsRecyclerViewAdapter;

    public SubscriptionsTouchHelper(SubscriptionsRecyclerViewAdapter subscriptionsRecyclerViewAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mSubscriptionsRecyclerViewAdapter = subscriptionsRecyclerViewAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mSubscriptionsRecyclerViewAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        mSubscriptionsRecyclerViewAdapter.remove(viewHolder.getAdapterPosition());
    }
}
