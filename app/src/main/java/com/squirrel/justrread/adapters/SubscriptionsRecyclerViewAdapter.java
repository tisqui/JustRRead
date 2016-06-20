package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squirrel.justrread.R;
import com.squirrel.justrread.data.Subscription;

import java.util.Collections;
import java.util.List;

/**
 * Created by squirrel on 6/7/16.
 */
public class SubscriptionsRecyclerViewAdapter extends RecyclerView.Adapter<SubscriptionItemViewHolder> {
    private final String LOG_TAG = SubscriptionsRecyclerViewAdapter.class.getSimpleName();
    private View mEmptyView;
    private List<Subscription> mSubscriptionsList;

    public SubscriptionsRecyclerViewAdapter(View emptyView, List<Subscription> subscriptionsList) {
        mEmptyView = emptyView;
        mSubscriptionsList = subscriptionsList;
    }

    @Override
    public SubscriptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_recycler_vie_item, parent, false);
        return new SubscriptionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubscriptionItemViewHolder holder, int position) {
        if(!mSubscriptionsList.isEmpty()){
            Subscription s = mSubscriptionsList.get(position);
            if(s!=null){
                if(s.getSubredditDisplayName()!=null){
                    holder.mSubscriptionIdTextView.setText(s.getSubredditDisplayName());
                }
                else {
                    holder.mSubscriptionIdTextView.setText("No name available");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSubscriptionsList.size();
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(mSubscriptionsList, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public void swapSubscriptionsList(List<Subscription> newList){
        mSubscriptionsList = newList;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void remove(int position) {
        mSubscriptionsList.remove(position);
        notifyItemRemoved(position);
    }
}
