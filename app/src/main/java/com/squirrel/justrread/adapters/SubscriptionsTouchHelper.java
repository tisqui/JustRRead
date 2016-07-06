package com.squirrel.justrread.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.squirrel.justrread.R;
import com.squirrel.justrread.api.RedditAPI;

import net.dean.jraw.http.NetworkException;

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
        dialog.setTitle(mContext.getString(R.string.subscriptions_dialog_title));
        dialog.setMessage(mContext.getString(R.string.subscriptions_dialog_messae));
        dialog.setPositiveButton(mContext.getString(R.string.subscriptions_dialog_unsubscribe), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        int item = viewHolder.getAdapterPosition()-1;
                        String id = mSubscriptionsRecyclerViewAdapter.
                                getSubscriptionIdByPosition(item);
                        boolean res;
                        try {
                            res = RedditAPI.unsubscribeSubreddit(id, mContext);
                        } catch (NetworkException e){
                            e.printStackTrace();
                            res = false;
                        }
                        return res;
                    }

                    @Override
                    protected void onPostExecute(Boolean res) {
                        super.onPostExecute(res);
                        if(res){
                            mSubscriptionsRecyclerViewAdapter.remove(viewHolder.getAdapterPosition());
                        }
                        else {
                            Toast.makeText(mContext, R.string.subscriptions_unsubscribe_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        dialog.setNegativeButton(mContext.getString(R.string.unsubscribe_dialog_cancel_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
//        mSubscriptionsRecyclerViewAdapter.remove(viewHolder.getAdapterPosition());
    }
}
