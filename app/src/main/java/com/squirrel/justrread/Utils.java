package com.squirrel.justrread;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.squirrel.justrread.activities.FrontpageFeedActivity;
import com.squirrel.justrread.sync.RedditSyncAdapter;

import net.dean.jraw.paginators.Sorting;

import java.util.Date;

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

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static void saveMainFeedSortToSharedPrefs(Context c, int value){
        saveIntItemToSharedPrefs(c, c.getString(R.string.front_filter_key), value);
    }

    public static Sorting getMainFeedSortFromSharedPrefs(Context c){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        int sortInt = sharedPref.getInt(c.getString(R.string.front_filter_key), 0);
        Sorting res;
        switch (sortInt){
            case FrontpageFeedActivity.FRONT_FILTER_HOT:
                res = Sorting.HOT;
                break;
            case FrontpageFeedActivity.FRONT_FILTER_NEW:
                res=Sorting.NEW;
                break;
            case FrontpageFeedActivity.FRONT_FILTER_TOP:
                res=Sorting.TOP;
                break;
            case FrontpageFeedActivity.FRONT_FILTER_CONTROVERSIAL:
                res=Sorting.CONTROVERSIAL;
                break;
            default:
                res= Sorting.HOT;
        }
        return res;
    }

    public static void saveIntItemToSharedPrefs(Context c, String key, int value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(key, value);
        spe.apply();
    }

}
