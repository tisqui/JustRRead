package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.squirrel.justrread.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/24/16.
 */
public class LoaderPostsListHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    public LoaderPostsListHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
