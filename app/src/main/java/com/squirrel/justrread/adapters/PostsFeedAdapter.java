package com.squirrel.justrread.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.activities.Navigator;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;

import net.dean.jraw.models.VoteDirection;

import java.util.List;

/**
 * Created by squirrel on 5/24/16.
 */
public class PostsFeedAdapter extends FeedRecyclerViewAdapter {

    public PostsFeedAdapter(List<Post> postList, Context context, View emptyView, boolean isTwoPane) {
        super(postList, context, emptyView, isTwoPane);
    }

    @Override
    public void bindPostsFeedViewHolder(RecyclerView.ViewHolder vholder, int position) {

        if(vholder instanceof FeedItemViewHolder){
            FeedItemViewHolder holder = (FeedItemViewHolder) vholder;
            final Post postItem = DataMapper.mapCursorToPost(mCursor);
            if(mTwoPane) {
                holder.itemView.setSelected(mSelectedPosition == position);
            }

            //set the selector, depending on the theme
            if (PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getBoolean(mContext.getString(R.string.prefs_nightmode_key), false)) {
                holder.itemView.setBackgroundResource(R.drawable.feed_item_selector_dark);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.feed_item_selector);
            }

            //set all the data to the UI elements

            if(postItem.getThumbnail() == null){
                holder.thumbnailImageView.setVisibility(View.GONE);
            }else{
                holder.thumbnailImageView.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(postItem.getThumbnail())
                        .centerCrop()
                        .placeholder(R.drawable.ic_duck_white_36dp)
                        .error(R.drawable.ic_duck_white_36dp)
                        .into(holder.thumbnailImageView);
            }

            holder.titleTextView.setText(postItem.getTitle());
            holder.sourceTextView.setText(postItem.getDomain());
            holder.postDateTextView.setText(Utils.getPostedTimeAgo(postItem.getCreated(), mContext));
            holder.commentsBtn.setText(postItem.getNumComments()+"");
            holder.numberOfVotesTextView.setText(postItem.getUpVotes() + "");

            holder.thumbnailImageView.setClickable(true);
            //if there is a link for the post - make the image clickable
            if(postItem.getUrl() != null && !postItem.getUrl().isEmpty()){
                holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigator.navigateToWebview(mContext, postItem.getUrl());

                    }
                });
            }

            final Post currentPost = getPost(position);
            holder.upBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utils.checkUserLoggedIn()){
                        if(currentPost!=null){
                            RedditAPI.vote(currentPost, VoteDirection.UPVOTE, mContext);}
                    }else{
                        BaseActivity.showLoginAlert(mContext);
                    }
                }
            });

            holder.downBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utils.checkUserLoggedIn()){
                        if (currentPost!=null) {
                            RedditAPI.vote(currentPost, VoteDirection.DOWNVOTE, mContext);
                        }
                    }else{
                        BaseActivity.showLoginAlert(mContext);
                    }
                }
            });
        }
    }
}
