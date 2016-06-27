package com.squirrel.justrread.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squirrel.justrread.R;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 4/25/16.
 */
public abstract class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Post> mPostList;
    protected View mEmptyView;
    protected Cursor mCursor;
    protected Context mContext;
    private final String LOG_TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    protected int mSelectedPosition = 0;
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;
    protected boolean showLoader;
    protected boolean mTwoPane;

    public FeedRecyclerViewAdapter(List<Post> postList, Context context, View emptyView, boolean isTwoPane) {
        mPostList = postList;
        mContext = context;
        mEmptyView = emptyView;
        mTwoPane = isTwoPane;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_list_progress, parent, false);
            return new LoaderPostsListHolder(view);


        } else if (viewType == VIEW_TYPE_ACTIVITY){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_list_item, parent, false);
            FeedItemViewHolder feedViewHolder = new FeedItemViewHolder(view);
            return feedViewHolder;
        }
        throw new IllegalArgumentException("Invalid ViewType: " + viewType);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Loader ViewHolder
        if (holder instanceof LoaderPostsListHolder) {
            LoaderPostsListHolder loaderViewHolder = (LoaderPostsListHolder)holder;
            if (showLoader) {
                Log.d(LOG_TAG, "Show loader");
                loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.mProgressBar.setVisibility(View.GONE);
            }

            return;
        }

        mCursor.moveToPosition(position);
        if(mCursor !=null && position < getItemCount() && mCursor.getCount() != 0){
            //        Post postItem = mPostList.get(position);
            Post postItem = DataMapper.mapCursorToPost(mCursor);
            if(mTwoPane){
                holder.itemView.setSelected(mSelectedPosition == position);
            }
            bindPostsFeedViewHolder(holder, position);

//        //set all the data to the UI elements
//
//        if(postItem.getThumbnail() == null){
//            holder.thumbnailImageView.setVisibility(View.GONE);
//        }else{
//            holder.thumbnailImageView.setVisibility(View.VISIBLE);
//            Glide.with(mContext)
//                    .load(postItem.getThumbnail())
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_duck_white_36dp)
//                    .error(R.drawable.ic_duck_white_36dp)
//                    .into(holder.thumbnailImageView);
//        }
//
//        holder.titleTextView.setText(postItem.getTitle());
//        holder.sourceTextView.setText(postItem.getDomain());
//        holder.postDateTextView.setText(String.valueOf(postItem.getCreated()));
//        holder.commentsBtn.setText(postItem.getNumComments()+"");
//        holder.numberOfVotesTextView.setText(postItem.getUpVotes()+ "");

            //TODO add callbacks for all the buttons

        }
    }

//    @Override
//    public int getItemCount() {
//        return (null != mPostList ? mPostList.size() : 0);
//    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        // +1 for loader
        return mCursor.getCount()+1;
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

//    public void swapPostsData(List<Post> posts){
//        Log.d(LOG_TAG, "New posts received");
//        mPostList = posts;
//        notifyDataSetChanged();
//    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

//    public void selectView(RecyclerView.ViewHolder viewHolder) {
//        if ( viewHolder instanceof FeedItemViewHolder ) {
//            FeedItemViewHolder vfh = (FeedItemViewHolder)viewHolder;
//            vfh.onClick(vfh.itemView);
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position == getItemCount() - 1) {
            Log.d(LOG_TAG, "This is the loading item");
            return VIEW_TYPE_LOADING;
        }

        return VIEW_TYPE_ACTIVITY;
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    public abstract void bindPostsFeedViewHolder(RecyclerView.ViewHolder holder, int position);

}
