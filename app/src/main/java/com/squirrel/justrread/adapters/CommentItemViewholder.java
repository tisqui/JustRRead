package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squirrel.justrread.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/26/16.
 */
public class CommentItemViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @Bind(R.id.comment_author)
    TextView mCommentAuthorTextView;

    @Bind(R.id.comment_date)
    TextView mCommentDateTextView;

    @Bind(R.id.comment_rating)
    TextView mCommentRatingTextView;

    @Bind(R.id.comment_text)
    TextView mCommentTextView;

    @Bind(R.id.comment_container)
    RelativeLayout mCommentContainer;

    public CommentItemViewholder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onClick(View v) {

    }
}
