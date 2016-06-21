package com.squirrel.justrread.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.squirrel.justrread.api.RedditAPI;

/**
 * Created by squirrel on 6/7/16.
 */
public class SubscriptionsTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SubscriptionsRecyclerViewAdapter mSubscriptionsRecyclerViewAdapter;
    private Context mContext;

    public SubscriptionsTouchHelper(SubscriptionsRecyclerViewAdapter subscriptionsRecyclerViewAdapter, Context context) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mSubscriptionsRecyclerViewAdapter = subscriptionsRecyclerViewAdapter;
        mContext = context;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mSubscriptionsRecyclerViewAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        //delete the item when swipe
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Unsubscribe");
        dialog.setMessage("Do you want to unsubscribe from subreddit?");
        dialog.setPositiveButton("Unsubscribe", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        int item = viewHolder.getAdapterPosition()-1;
                        String id = mSubscriptionsRecyclerViewAdapter.
                                getSubscriptionIdByPosition(item);
                        return  RedditAPI.unsubscribeSubreddit(id, mContext);
                    }

                    @Override
                    protected void onPostExecute(Boolean res) {
                        super.onPostExecute(res);
                        if(res){
                            mSubscriptionsRecyclerViewAdapter.remove(viewHolder.getAdapterPosition());
                        }
                        else {
                            Toast.makeText(mContext, "Unsubscription error. Try once more.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
//        mSubscriptionsRecyclerViewAdapter.remove(viewHolder.getAdapterPosition());
    }
}
