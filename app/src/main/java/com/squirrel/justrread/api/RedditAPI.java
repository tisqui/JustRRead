package com.squirrel.justrread.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.FluentIterable;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.sync.RedditSyncAdapter;

import net.dean.jraw.ApiException;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.TraversalMethod;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.UserSubredditsPaginator;

import java.util.List;
import java.util.Vector;

/**
 * Created by squirrel on 5/2/16.
 */
public class RedditAPI {
    public static final String LOG_TAG = RedditAPI.class.getSimpleName();
    public static boolean showNSFW = false;

    public interface APICallback<T> {
        void onSuccess(T result);

        void onFailure(Throwable t);
    }

    public RedditAPI() {
    }

    public static boolean checkAuthentificationReady() {
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        return state.equals(AuthenticationState.READY);
    }

    //get the subreddit feed
    public void getSubredditPostsSorted(SubredditPaginator paginator, Context context, String subredditId, Sorting sort) {
        if (checkAuthentificationReady()) {
            if (paginator != null) {
                if (sort != null) {
                    paginator.setSorting(sort);
                }
                if (subredditId != null) {
//                    paginator.reset();
                    paginator.setSubreddit(subredditId);
                }
                if (paginator.hasNext()) {
                    Listing<Submission> firstPage = paginator.next();
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
                    for (Submission s : firstPage) {
                        contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
                    }
                    if (contentValuesList.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(cvArray);
                        //delete all previous data
                        context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
                        context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                    }
                } else {
                    Log.d(LOG_TAG, "No more pages available");
                }
            }
        } else {
            Log.d(LOG_TAG, "getFrontPost: Not Authentificated");
        }
    }

    public void getPostsFront(SubredditPaginator paginator, Context context) {
        if (checkAuthentificationReady()) {
            if (paginator != null) {
                if (paginator.hasNext()) {
                    Listing<Submission> firstPage = paginator.next();
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
                    for (Submission s : firstPage) {
                        contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
                    }
                    if (contentValuesList.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(cvArray);
                        context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                        RedditSyncAdapter.syncImmediately(context);
                    }
                } else {
                    Log.d(LOG_TAG, "No more pages available");
                }
            } else {
                Log.d(LOG_TAG, "getFrontPost: Not Authentificated");
            }
        }
    }

    public List<CommentNode> getTopNodeAllComments(String postId) {
        if (checkAuthentificationReady()) {
            FluentIterable<CommentNode> nodes = AuthenticationManager.get().getRedditClient().
                    getSubmission(postId).getComments().walkTree(TraversalMethod.PRE_ORDER);

            List<CommentNode> resultList = nodes.toList();

            return resultList;
        }
        return null;
    }

    public static String getSubredditAbout(String subredditId){
        if (checkAuthentificationReady()) {
            Subreddit subreddit = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
            return subreddit.getSidebar();
        } else {
            Log.d(LOG_TAG, "getSubredditAbout: Not Authentificated");
            return null;
        }
    }

    public static List<String> searchForSubreddit(String searchStr, boolean includeNsfw){
        if(checkAuthentificationReady()){
            return AuthenticationManager.get().getRedditClient().searchSubreddits(searchStr, includeNsfw);
        }else {
            Log.d(LOG_TAG, "getSubredditAbout: Not Authentificated");
            return null;
        }
    }

    public static void getUserSubscriptions(Context context){
        if(checkAuthentificationReady()){
            if(Utils.checkUserLoggedIn()){
                UserSubredditsPaginator userSubredditsPaginator =
                        new UserSubredditsPaginator(AuthenticationManager.get().getRedditClient(), "subscriber");
                if(userSubredditsPaginator != null){
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>();
                    while(userSubredditsPaginator.hasNext()){
                        Listing<Subreddit> subscriptions = userSubredditsPaginator.next();
                        for (Subreddit s : subscriptions) {
                            contentValuesList.add(DataMapper.mapSubredditToContentValues(s));
                        }
                    }
                    if (contentValuesList.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(cvArray);
                        context.getContentResolver().delete(RedditContract.SubscriptionEntry.CONTENT_URI, null, null);
                        context.getContentResolver().bulkInsert(RedditContract.SubscriptionEntry.CONTENT_URI, cvArray);
                    }
                }
            } else {
                Log.d(LOG_TAG, "getUserSubscriptions: User not logged in");
            }
        }
    }

    public static boolean subscribeSubreddit(String subredditId, Context context){
        Subreddit subredditToSubscribe = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
        if(subredditToSubscribe != null){
            //save the subreddit locally
            context.getContentResolver().insert(RedditContract.SubscriptionEntry.CONTENT_URI,
                    DataMapper.mapSubredditToContentValues(subredditToSubscribe));
            try{
                AccountManager accountManager = new AccountManager(AuthenticationManager.get().getRedditClient());
                accountManager.subscribe(subredditToSubscribe);
            }catch (NetworkException e){
                Log.d(LOG_TAG, "Network exception, subscription not succesful: " + e);
            }

            return true;
        }else {
            Log.d(LOG_TAG, "Can't find subreddit to Subscribe");
            return false;
        }
    }

    public static boolean unsubscribeSubreddit(String subredditId, Context context){
        //delete the subscription from local
        int deleted = context.getContentResolver().delete(RedditContract.SubscriptionEntry.CONTENT_URI,
                RedditContract.SubscriptionColumns.COLUMN_ID + "='" + subredditId + "'", null);
        if(deleted > 0){
            Log.d(LOG_TAG, "Unsubscribed succesfully");
            //unsubscribe on the server
            try{
                Subreddit subredditToUnsubscribe = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
                AccountManager accountManager = new AccountManager(AuthenticationManager.get().getRedditClient());
                accountManager.unsubscribe(subredditToUnsubscribe);
                return true;
            }catch (NetworkException e){
                Log.d(LOG_TAG, "Network exception, unsubscription not succesful: " + e);
                return false;
            } catch(IllegalArgumentException e){
                Log.d(LOG_TAG, "Subreddit does not exists: " + e);
                return false;
            }
        } else {
            Log.d(LOG_TAG, "No subscription found in DB to unsibscribe");
            return false;
        }
    }

    public static void vote(final Post post, final VoteDirection voteDirection, final Context context){
        try{
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    AccountManager accountManager = new AccountManager(AuthenticationManager.get().getRedditClient());
                    try {
                        Submission s = AuthenticationManager.get().getRedditClient().getSubmission(post.getPostId());
                        accountManager.vote(s, voteDirection);
                        return true;
                    } catch (ApiException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean res) {
                    super.onPostExecute(res);
                    if(res){
                        //update the DB item - get the new submission
                        Submission newSubmission = AuthenticationManager.get().getRedditClient().getSubmission(post.getPostId());
                        int updated = context.getContentResolver().update(RedditContract.PostEntry.CONTENT_URI,
                                DataMapper.mapSubmissionToContentValues(newSubmission),
                                RedditContract.PostColumns.COLUMN_ID+"='" + post.getPostId() + "'",
                                null);
                        if(updated <1){
                            Log.d(LOG_TAG, "No posts found to update");
                        } else{
                            Log.d(LOG_TAG, "Updated post: " + newSubmission.toString());
                        }
                    }
                }
            }.execute();
        }catch (NetworkException e){
            Log.d(LOG_TAG, "Network exception, unsubscription not succesful: " + e);
        }
    }

    public static boolean checkIfSubscribed(String subredditName, Context context){
        String[] projection = new String[] {RedditContract.SubscriptionColumns.COLUMN_DISPLAY_NAME};
        String selection = RedditContract.SubscriptionColumns.COLUMN_DISPLAY_NAME + " = ?";
        String[] selectionArguments = { subredditName };
        Cursor cursor = context.getContentResolver().query(RedditContract.SubscriptionEntry.CONTENT_URI, projection,
                selection, selectionArguments, null);
        if((cursor != null) && (cursor.getCount() > 0)){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public class GetPosts extends AsyncTask<String, Void, Void> {
        SubredditPaginator mSubredditPaginator;
        String page;
        int pageSize;
        Context context;

        public GetPosts(SubredditPaginator subredditPaginator, int pSize, Context c) {
            mSubredditPaginator = subredditPaginator;
            this.pageSize = pSize;
            this.context = c;
        }

        @Override
        protected Void doInBackground(String... params) {

            if (mSubredditPaginator == null) {
                //getting the first page
                mSubredditPaginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
            }
            mSubredditPaginator.setLimit(pageSize);
            if (mSubredditPaginator.hasNext()) {
                Listing<Submission> firstPage = mSubredditPaginator.next();
                Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
                for (Submission s : firstPage) {
                    contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
                }
                if (contentValuesList.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(cvArray);
                    context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                }
            } else {
                Log.d(LOG_TAG, "No more pages available");
            }
            return null;
        }
    }

}
