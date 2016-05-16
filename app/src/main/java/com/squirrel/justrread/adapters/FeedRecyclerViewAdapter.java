package com.squirrel.justrread.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 4/25/16.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedItemViewHolder> {
    private List<Post> mPostList;
    private View mEmptyView;
    private Cursor mCursor;
    private Context mContext;
    private final String LOG_TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    private int mSelectedPosition = 0;

    public FeedRecyclerViewAdapter(List<Post> postList, Context context, View emptyView) {
        mPostList = postList;
        mContext = context;
        mEmptyView = emptyView;
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_list_item, parent, false);
        FeedItemViewHolder feedViewHolder = new FeedItemViewHolder(view);
        return feedViewHolder;
    }

    @Override
    public void onBindViewHolder(FeedItemViewHolder holder, int position) {

        mCursor.moveToPosition(position);
//        Post postItem = mPostList.get(position);
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

        //TODO add callbacks for all the buttons

    }

//    @Override
//    public int getItemCount() {
//        return (null != mPostList ? mPostList.size() : 0);
//    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public Cursor getCursor() {
        return mCursor;
    }

//    public void addPostsToList(List<Post> morePosts){
//        mPostList.addAll(morePosts);
//        int curSize = getItemCount();
//        notifyItemRangeInserted(curSize, mPostList.size() - 1);
//    }
//
//    public Post getPost(int position){
//        if(mPostList != null){
//            return mPostList.get(position);
//        }
//        return null;
//    }

    public void onClick(View view, int position) {
        int old_position = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(old_position);
        notifyItemChanged(mSelectedPosition);

        mCursor.moveToPosition(position);

    }

    public void swapPostsData(List<Post> posts){
        Log.d(LOG_TAG, "New posts received");
        mPostList = posts;
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof FeedItemViewHolder ) {
            FeedItemViewHolder vfh = (FeedItemViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }


}
