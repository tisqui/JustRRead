package com.squirrel.justrread.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.api.RedditAPI;

import java.util.List;

/**
 * Created by squirrel on 5/30/16.
 */
public class SearchResultsListAdapter extends ArrayAdapter<String> {

    private List<String> mSubredditsList;

    public SearchResultsListAdapter(Context context, List<String> sub) {
        super(context, 0, sub);
        mSubredditsList = sub;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String sub = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subreddit_search_results_list_item, parent, false);
        }
        // Lookup view for data population
        TextView subredditName = (TextView) convertView.findViewById(R.id.search_res_subreddit_title);
        // Populate the data into the template view using the data object
        subredditName.setText("/" + sub);
        // Return the completed view to render on screen

        Button subredditSubscribe = (Button) convertView.findViewById(R.id.search_res_subscribe);

        //check the subscription
        if(RedditAPI.checkIfSubscribed(sub, getContext())){
            //user is subscribed to this subreddit
            setUnsubscribeButton(sub, subredditSubscribe);
        } else {
            //not subscribed
            setSubscribeButton(sub, subredditSubscribe);
        }
        return convertView;
    }

    private void setSubscribeButton(final String subredditId, final Button subButton){
        subButton.setText("Subscribe");
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkUserLoggedIn()) {
                    Toast.makeText(getContext(), "Subscribing", Toast.LENGTH_SHORT).show();
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            return RedditAPI.subscribeSubreddit(subredditId, getContext());
                        }

                        @Override
                        protected void onPostExecute(Boolean res) {
                            super.onPostExecute(res);
                            if(res){
                                setUnsubscribeButton(subredditId, subButton);
                                Toast.makeText(getContext(), "You subscribed to /" + subredditId,
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), "Subscription error: " + subredditId,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                } else {
                    BaseActivity.showLoginAlert(getContext());
                }
            }
        });

    }

    private void setUnsubscribeButton(final String subredditId, final Button subButton){
        subButton.setText("Unsubscribe");
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkUserLoggedIn()) {
                    Toast.makeText(getContext(), "Unsubscribing", Toast.LENGTH_SHORT).show();
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            return RedditAPI.unsubscribeSubreddit(subredditId, getContext());
                        }

                        @Override
                        protected void onPostExecute(Boolean res) {
                            super.onPostExecute(res);
                            if(res){
                                setSubscribeButton(subredditId, subButton);
                                Toast.makeText(getContext(), "You unsubscribed from /" + subredditId,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Unsubscription error: " + subredditId,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                } else {
                    BaseActivity.showLoginAlert(getContext());
                }
            }
        });
    }

    public String getSubredditByPosition(int position){
        if(mSubredditsList != null){
            return mSubredditsList.get(position);
        }
        return null;
    }

}
