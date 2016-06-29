package com.squirrel.justrread;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squirrel.justrread.activities.FrontpageFeedActivity;
import com.squirrel.justrread.sync.RedditSyncAdapter;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.paginators.Sorting;

import org.markdownj.MarkdownProcessor;

import java.util.Calendar;
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
    static public @RedditSyncAdapter.SubscriptiosnStatus
    int getSubscriptionsStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_subscriptions_status_key), RedditSyncAdapter.SUB_STATUS_UNKNOWN);
    }

    /**
     * Resets the posts status.
     * @param c Context used to get the SharedPreferences
     */
    static public void resetSubscriptionsStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_subscriptions_status_key), RedditSyncAdapter.SUB_STATUS_UNKNOWN);
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

    public static void saveiSSubredditToSharedPrefs(Context c, boolean isSubreddit){
        saveIntItemToSharedPrefs(c, c.getString(R.string.is_subreddit), isSubreddit ? 1 : 0);
    }

    public static boolean getiSSubredditFromSharePrefs(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        int res = sp.getInt(c.getString(R.string.is_subreddit), 0);
        return res == 0 ? false : true;
    }

    public static void saveSubredditIdToSharedPrefs(Context c, String subredditId){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(c.getString(R.string.subreddit_id), subredditId);
        spe.apply();
    }

    public static String getSubredditId(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getString(c.getString(R.string.subreddit_id), "");
    }


    public static String getHtmlFromMarkdown(String markdown){
        return new MarkdownProcessor().markdown(markdown);
    }

    public static boolean checkUserLoggedIn(){
        try{
           String authUser= AuthenticationManager.get().getRedditClient().getAuthenticatedUser();
            return authUser != null && !authUser.isEmpty();
        }
        catch (IllegalStateException e){
            Log.d("Check log is status", "IllegalStateException " + e);
            return false;
        }
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    //date util from http://stackoverflow.com/questions/13018550/time-since-ago-library-for-android-java
    public static String getPostedTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +  ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_an)+ " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }
}
