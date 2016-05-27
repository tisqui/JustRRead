package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squirrel.justrread.R;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 4/25/16.
 */
public class FeedItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @Bind(R.id.fragment_feed_list_item_up_btn) Button upBtn;
    @Bind(R.id.fragment_feed_list_item_votes_num) TextView numberOfVotesTextView;
    @Bind(R.id.fragment_feed_list_item_down_btn) Button downBtn;
    @Bind(R.id.fragment_feed_list_item_thumbnail) ImageView thumbnailImageView;
    @Bind(R.id.fragment_feed_list_item_title) TextView titleTextView;
    @Bind(R.id.fragment_feed_list_item_source) TextView sourceTextView;
    @Bind(R.id.fragment_feed_list_item_time) TextView postDateTextView;
    @Bind(R.id.fragment_feed_list_item_comments_btn) TextView commentsBtn;
//    @Bind(R.id.fragment_feed_list_item_share_btn) Button shareBtn;

    public FeedItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onClick(View v) {

    }
}
