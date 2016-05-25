package com.squirrel.justrread.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 5/24/16.
 */
public class PostsFeedAdapter extends FeedRecyclerViewAdapter {

    public PostsFeedAdapter(List<Post> postList, Context context, View emptyView) {
        super(postList, context, emptyView);
    }

    @Override
    public void bindPostsFeedViewHolder(RecyclerView.ViewHolder vholder, int position) {

        if(vholder instanceof FeedItemViewHolder){
            FeedItemViewHolder holder = (FeedItemViewHolder) vholder;
            Post postItem = DataMapper.mapCursorToPost(mCursor);
            holder.itemView.setSelected(mSelectedPosition == position);

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
            holder.postDateTextView.setText(String.valueOf(postItem.getCreated()));
            holder.commentsBtn.setText(postItem.getNumComments()+"");
            holder.numberOfVotesTextView.setText(postItem.getUpVotes()+ "");
        }
    }
}
