package com.squirrel.justrread.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squirrel.justrread.R;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;

import java.util.List;

/**
 * Created by squirrel on 5/26/16.
 */
public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentItemViewholder> {
    private final String LOG_TAG = CommentsRecyclerViewAdapter.class.getSimpleName();
    private View mEmptyView;

    private List<CommentNode> mCommentNodesList;


    public CommentsRecyclerViewAdapter(List<CommentNode> list, View emptyView) {
        mCommentNodesList = list;
        mEmptyView = emptyView;
    }

    @Override
    public CommentItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_list_item, parent, false);
        return new CommentItemViewholder(view);
    }

    @Override
    public void onBindViewHolder(CommentItemViewholder holder, int position) {
        if (!mCommentNodesList.isEmpty()) {
            Comment comment = mCommentNodesList.get(position).getComment();

            if(comment!=null){
                //set all UI elements
                if (comment.getAuthor() != null) {
                    holder.mCommentAuthorTextView.setText(comment.getAuthor());
                } else {
                    holder.mCommentAuthorTextView.setText("no author");
                }

                if (comment.getEditDate() != null) {
                    holder.mCommentDateTextView.setText(comment.getCreated().toString());
                } else {
                    holder.mCommentDateTextView.setText("no date");
                }
                holder.mCommentRatingTextView.setText(comment.getScore() + "");
                if (comment.getAuthor() != null) {
                    holder.mCommentTextView.setText(Html.fromHtml(comment.getBody()));
                } else {
                    holder.mCommentTextView.setVisibility(View.GONE);
                }
                holder.mCommentContainer.setPadding(mCommentNodesList.get(position).getDepth()*16, 0, 0, 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCommentNodesList==null ? 0:mCommentNodesList.size();
    }



    public void swapTopNode(List<CommentNode> nodeList) {
        mCommentNodesList = nodeList;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }


}
