package com.squirrel.justrread.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squirrel.justrread.R;

import java.util.ArrayList;

/**
 * Created by squirrel on 5/30/16.
 */
public class SearchResultsListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mSubredditsList;

    public SearchResultsListAdapter(Context context, ArrayList<String> sub) {
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
        subredditName.setText(sub);
        // Return the completed view to render on screen

        return convertView;
    }

    public String getSubredditByPosition(int position){
        if(mSubredditsList != null){
            return mSubredditsList.get(position);
        }
        return null;
    }

}
