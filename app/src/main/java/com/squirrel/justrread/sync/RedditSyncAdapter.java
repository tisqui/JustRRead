package com.squirrel.justrread.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.data.RedditContract;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.OEmbed;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by squirrel on 5/8/16.
 */
public class RedditSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = this.getClass().getSimpleName();
    // Interval at which to sync with the reddit.
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private final AccountManager mAccountManager;
    private Context mContext;
    private Authentification mAuthentification;

    private SubredditPaginator mSubredditPaginator;

    public static String PAGE = "page";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POSTS_STATUS_OK, POSTS_STATUS_SERVER_DOWN, POSTS_STATUS_SERVER_INVALID,  POSTS_STATUS_UNKNOWN, POSTS_STATUS_INVALID})
    public @interface PostsStatus {}

    public static final int POSTS_STATUS_OK = 0;
    public static final int POSTS_STATUS_SERVER_DOWN = 1;
    public static final int POSTS_STATUS_SERVER_INVALID = 2;
    public static final int POSTS_STATUS_UNKNOWN = 3;
    public static final int POSTS_STATUS_INVALID = 4;


    public RedditSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAuthentification = new Authentification(context);
        mAccountManager = AccountManager.get(context);
        mContext = context;
    }

    public void setSubredditPaginator(SubredditPaginator subredditPaginator) {
        mSubredditPaginator = subredditPaginator;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO add the sync code

        Log.d(LOG_TAG, "onPerformSync called");

        AuthenticationState state = AuthenticationManager.get().checkAuthState();

        if (state.equals(AuthenticationState.READY)) {
            //Get the posts data
            Log.d(LOG_TAG, "Getting paginator:");

            if (mSubredditPaginator == null) {
                Log.d("LOG_TAG", "Paginator null");
                mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
                mSubredditPaginator.setLimit(50);
            }
            if (mSubredditPaginator.hasNext()) {
                Listing<Submission> firstPage = mSubredditPaginator.next();
                //delete the previous data, so we do not build the endless story
//                    List<Post> items = Post.listAll(Post.class);
//                    Post.deleteAll(Post.class);

                Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());

                for (Submission s : firstPage) {
                    // Save the post to DB
                    Log.d(LOG_TAG, s.toString());
                    ContentValues postValues = new ContentValues();
                    postValues.put(RedditContract.PostEntry.COLUMN_ID, s.getId());
                    postValues.put(RedditContract.PostEntry.COLUMN_NAME, s.getFullName());
                    postValues.put(RedditContract.PostEntry.COLUMN_UP_VOTES, s.getScore());
                    postValues.put(RedditContract.PostEntry.COLUMN_DOWN_VOTES, s.getScore());
                    postValues.put(RedditContract.PostEntry.COLUMN_LIKES, s.getScore());
                    postValues.put(RedditContract.PostEntry.COLUMN_DATE_CREATED, Utils.persistDate(s.getCreated()));
                    postValues.put(RedditContract.PostEntry.COLUMN_AUTHOR, s.getAuthor());
                    postValues.put(RedditContract.PostEntry.COLUMN_DOMAIN, s.getDomain());
                    postValues.put(RedditContract.PostEntry.COLUMN_SELF_POST, s.isSelfPost() ? 1 : 0);
                    postValues.put(RedditContract.PostEntry.COLUMN_NUM_COMMENTS, s.getCommentCount());
                    postValues.put(RedditContract.PostEntry.COLUMN_NSFW, s.isNsfw() ? 1 : 0);
                    postValues.put(RedditContract.PostEntry.COLUMN_SUBREDDIT, s.getSubredditName());
                    postValues.put(RedditContract.PostEntry.COLUMN_SUBREDDIT_ID, s.getSubredditId());
                    postValues.put(RedditContract.PostEntry.COLUMN_SELFHTML, s.getSelftext());
                    postValues.put(RedditContract.PostEntry.COLUMN_THUMBNAIL, s.getThumbnail());
                    postValues.put(RedditContract.PostEntry.COLUMN_TITLE, s.getTitle());
                    postValues.put(RedditContract.PostEntry.COLUMN_URL, s.getUrl());

                    OEmbed oEmbed = s.getOEmbedMedia();

                    postValues.put(RedditContract.PostEntry.COLUMN_MEDIA_TYPE, oEmbed.getMediaType().toString());
                    postValues.put(RedditContract.PostEntry.COLUMN_MEDIA_LINK, oEmbed.getUrl());
                    postValues.put(RedditContract.PostEntry.COLUMN_MEDIA_THUMBNAIL, oEmbed.getThumbnail().getUrl().toString());

                    contentValuesList.add(postValues);
                }

                int inserted = 0;
                // add to database
                if (contentValuesList.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                    // TODO delete old data so we don't build up an endless history
                }
                Log.d(LOG_TAG, "Sync Complete. " + contentValuesList.size() + " Inserted");
                setPostsStatus(getContext(), POSTS_STATUS_OK);
            }
        } else {
            //do nothing for now, but need to try authentificate
        }

    }

    private String dateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        RedditSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    static private void setPostsStatus(Context c, @PostsStatus int locationStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_posts_status_key), locationStatus);
        spe.commit();
    }

}
