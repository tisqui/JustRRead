package com.squirrel.justrread.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 4/25/16.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedItemViewHolder> {
    private List<Post> mPostList;
    private Context mContext;
    private final String LOG_TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    private int mSelectedPosition = 0;

    private final String tempImageUrl = "http://i.imgur.com/EvrnaBB.jpg";

    public FeedRecyclerViewAdapter(List<Post> postList, Context context) {
        mPostList = postList;
        mContext = context;
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_list_item, parent, false);
        FeedItemViewHolder feedViewHolder = new FeedItemViewHolder(view);
        return feedViewHolder;
    }

    @Override
    public void onBindViewHolder(FeedItemViewHolder holder, int position) {
        Post postItem = mPostList.get(position);
        holder.itemView.setSelected(mSelectedPosition == position);

        //set all the data to the UI elements
        Glide.with(mContext)
                .load(tempImageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(holder.thumbnailImageView);

        holder.titleTextView.setText(postItem.getTitle());
        holder.sourceTextView.setText(postItem.getDomain());
        holder.postDateTextView.setText(String.valueOf(postItem.getCreated()));
        holder.commentsBtn.setText(postItem.getNumComments() + " comments");
        holder.numberOfVotesTextView.setText(postItem.getUpVotes()+ "");

        //TODO add callbacks for all the buttons

    }

    @Override
    public int getItemCount() {
        return (null != mPostList ? mPostList.size() : 0);
    }

    public void updateIPostsInList(List<Post> newPosts){
        mPostList = newPosts;
        notifyDataSetChanged();
    }

    public void addPostsToList(List<Post> morePosts){
        mPostList.addAll(morePosts);
        int curSize = getItemCount();
        notifyItemRangeInserted(curSize, mPostList.size() - 1);
    }

    public Post getPost(int position){
        if(mPostList != null){
            return mPostList.get(position);
        }
        return null;
    }

    public void onClick(View view, int position) {
        int old_position = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(old_position);
        notifyItemChanged(mSelectedPosition);
    }
}
