package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squirrel.justrread.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 6/7/16.
 */
public class SubscriptionItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.subscription_id)
    TextView mSubscriptionIdTextView;

    public SubscriptionItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
