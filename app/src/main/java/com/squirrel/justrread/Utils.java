package com.squirrel.justrread;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.squirrel.justrread.sync.RedditSyncAdapter;

/**
 * Created by squirrel on 5/15/16.
 */
public class Utils {
    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the posts status integer type
     */
    @SuppressWarnings("ResourceType")
    static public @RedditSyncAdapter.PostsStatus
    int getPostsStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_posts_status_key), RedditSyncAdapter.POSTS_STATUS_UNKNOWN);
    }

    /**
     * Resets the posts status.
     * @param c Context used to get the SharedPreferences
     */
    static public void resetPostsStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_posts_status_key), RedditSyncAdapter.POSTS_STATUS_UNKNOWN);
        spe.apply();
    }
}
