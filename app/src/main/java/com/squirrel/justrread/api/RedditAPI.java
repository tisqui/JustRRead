package com.squirrel.justrread.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.FluentIterable;
import com.squirrel.justrread.Authentification;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by squirrel on 5/2/16.
 */
public class RedditAPI {
    public static final String LOG_TAG = RedditAPI.class.getSimpleName();
    public static boolean showNSFW = false;

    public RedditAPI() {
    }

    public static boolean checkAuthentificationReady() {
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        switch (state) {
            case READY:
                //no need to perform actions
                return true;
            case NONE:
                //user was not logged in, app was not authentificated - try to auth without login
                Authentification.authentificateWithoutLoginAsync();
                break;
            case NEED_REFRESH:
                //authentificated, but token should be refreshed
                Authentification.refreshAccessTokenAsync();
                break;
        }
        return false;
    }

    /**
     * Gets the page of the posts lists for certain subreddit.
     * @param paginator the SubredditPaginator to use for posts
     * @param context current context
     * @param subredditId id of the subreddit for which to get posts
     * @param sort sorting of the posts - Top, Controversial, New, Hot
     * @return true if got new posts, false - if not
     * @throws NetworkException
     */
    public boolean getSubredditPostsSorted(SubredditPaginator paginator, Context context,
                                           String subredditId, Sorting sort) throws NetworkException{
        boolean res = false;
        if (checkAuthentificationReady()) {
            if (paginator != null) {
                if (subredditId != null) {
                    //check of this is not the first page of the same subreddit for this paginator
                    if(!subredditId.equals(paginator.getSubreddit())) {
                        //need to reset paginator before setting new subreddit
                        //delete all data, we are getting new subreddit
                        context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
                        paginator.reset();
                        paginator.setSubreddit(subredditId);
                    }
                }
                if (sort != null) {
                    paginator.setSorting(sort);
                    context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
                }
                if (paginator.hasNext()) {
                    Listing<Submission> firstPage = paginator.next();
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
                    for (Submission s : firstPage) {
                        contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
                    }
                    if (contentValuesList.size() > 0) {
                        res = true;
                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(cvArray);
                        context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                    }
                } else {
                    Log.d(LOG_TAG, "No more posts available");
                }
            }
        } else {
            Log.d(LOG_TAG, "getFrontPost: Not Authentificated");
        }
        return true;
    }

    /**
     * Get the posts lists for the frontpage
     * @param paginator the SubredditPaginator to use for posts
     * @param context current context
     * @param doClear if delete the previous posts from DB
     * @return true if got new posts, false - if not
     * @throws NetworkException
     */
    public boolean getPostsFront(SubredditPaginator paginator, Context context, boolean doClear) throws NetworkException{
        boolean res = false;
        if (checkAuthentificationReady()) {
            if (paginator != null) {
                if (paginator.hasNext()) {
                    Listing<Submission> firstPage = paginator.next();
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
                    for (Submission s : firstPage) {
                        contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
                    }
                    if (contentValuesList.size() > 0) {
                        res = true;
                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(cvArray);
                        if(doClear){
                            context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
                        }
                        context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
                    }
                } else {
                    Log.d(LOG_TAG, "No more pages available");
                }
            } else {
                Log.d(LOG_TAG, "getFrontPost: Not Authentificated");
            }
        }
        return res;
    }

    /**
     * Get the list of comments for the post by postId
     * @param postId the id of the post to get all comments for
     * @return the list of all comment nodes
     * @throws NetworkException
     */
    public List<CommentNode> getTopNodeAllComments(String postId) throws NetworkException{
        if (checkAuthentificationReady()) {
            FluentIterable<CommentNode> nodes = AuthenticationManager.get().getRedditClient().
                    getSubmission(postId).getComments().walkTree(TraversalMethod.PRE_ORDER);

            return nodes.toList();
        }
        return new ArrayList<CommentNode>();
    }

    /**
     * Get the About information of the subreddip
     * @param subredditId the id of the subreddit
     * @return the about text formatted as markdown
     * @throws NetworkException
     */
    public static String getSubredditAbout(String subredditId) throws NetworkException{
        if (checkAuthentificationReady()) {
            Subreddit subreddit = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
            return subreddit.getSidebar();
        } else {
            Log.d(LOG_TAG, "getSubredditAbout: Not Authentificated");
            return null;
        }
    }

    /**
     * Get the lists of subreddits for the search request
     * @param searchStr the input search key
     * @param includeNsfw if include the NSFW content
     * @return the list of the subreddits found
     * @throws NetworkException
     */
    public static List<String> searchForSubreddit(String searchStr, boolean includeNsfw) throws NetworkException{
        if(checkAuthentificationReady()){
            return AuthenticationManager.get().getRedditClient().searchSubreddits(searchStr, includeNsfw);
        }else {
            Log.d(LOG_TAG, "getSubredditAbout: Not Authentificated");
            return null;
        }
    }

    /**
     * Get the list of all subscriptions of the user
     * @param context the current context
     * @throws NetworkException
     */
    public static void getUserSubscriptions(Context context) throws NetworkException{
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

    /**
     * Subscribe to the subreddit
     * @param subredditId the id of the subreddit to subscribe
     * @param context the current context
     * @return true if the subscription was succesful
     * @throws NetworkException
     */
    public static boolean subscribeSubreddit(String subredditId, Context context) throws NetworkException{
        if(checkAuthentificationReady()) {
            Subreddit subredditToSubscribe = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
            if (subredditToSubscribe != null) {
                //save the subreddit locally
                context.getContentResolver().insert(RedditContract.SubscriptionEntry.CONTENT_URI,
                        DataMapper.mapSubredditToContentValues(subredditToSubscribe));
                try {
                    AccountManager accountManager = new AccountManager(AuthenticationManager.get().getRedditClient());
                    accountManager.subscribe(subredditToSubscribe);
                } catch (NetworkException e) {
                    Log.d(LOG_TAG, "Network exception, subscription not succesful: " + e);
                }

                return true;
            } else {
                Log.d(LOG_TAG, "Can't find subreddit to Subscribe");
                return false;
            }
        }else{
            Log.d(LOG_TAG, "Can't subscribe, authentification not valid");
            Authentification.refreshAuthAfterSleep(context);
            subscribeSubreddit(subredditId, context);
            return false;
        }
    }

    /**
     * Unsubscribe from subreddit
     * @param subredditId the id of the subreddit to unsubscribe
     * @param context the current context
     * @return true if the unsubscription was succesful
     * @throws NetworkException
     */
    public static boolean unsubscribeSubreddit(String subredditId, Context context) throws NetworkException{
        if(checkAuthentificationReady()) {
                //unsubscribe on the server
                try {
                    Subreddit subredditToUnsubscribe = AuthenticationManager.get().getRedditClient().getSubreddit(subredditId);
                    AccountManager accountManager = new AccountManager(AuthenticationManager.get().getRedditClient());
                    accountManager.unsubscribe(subredditToUnsubscribe);
                    //delete the subscription from local
                    int deleted = context.getContentResolver().delete(RedditContract.SubscriptionEntry.CONTENT_URI,
                            RedditContract.SubscriptionColumns.COLUMN_DISPLAY_NAME + "='" + subredditId + "'", null);
                    RedditSyncAdapter.syncImmediately(context);
                    return true;
                } catch (NetworkException e) {
                    Log.d(LOG_TAG, "Network exception, unsubscription not succesful: " + e);
                    return false;
                } catch (IllegalArgumentException e) {
                    Log.d(LOG_TAG, "Subreddit does not exists: " + e);
                    return false;
                }

        }else {
            Log.d(LOG_TAG, "Can't unsubscribe, authentification not valid");
            Authentification.refreshAuthAfterSleep(context);
            unsubscribeSubreddit(subredditId, context);
            return false;
        }
    }

    /**
     * Vote for the post
     * @param post the id of the post to vote for
     * @param voteDirection the direction of the vote - up or down
     * @param context the current context
     */
    public static void vote(final Post post, final VoteDirection voteDirection, final Context context){
        if(checkAuthentificationReady()) {
            try {
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
                        if (res) {
                            //update the DB item - get the new submission
                            Submission newSubmission = AuthenticationManager.get().getRedditClient().getSubmission(post.getPostId());
                            int updated = context.getContentResolver().update(RedditContract.PostEntry.CONTENT_URI,
                                    DataMapper.mapSubmissionToContentValues(newSubmission),
                                    RedditContract.PostColumns.COLUMN_ID + "='" + post.getPostId() + "'",
                                    null);
                            if (updated < 1) {
                                Log.d(LOG_TAG, "No posts found to update");
                            } else {
                                Log.d(LOG_TAG, "Updated post: " + newSubmission.toString());
                                Toast.makeText(context, "Think you for voting for the post!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }.execute();
            } catch (NetworkException e) {
                Log.d(LOG_TAG, "Network exception, unsubscription not succesful: " + e);
            }
        }else {
            Log.d(LOG_TAG, "Authentification needs refresh");
            Authentification.refreshAuthAfterSleep(context);
            vote(post, voteDirection, context);
        }
    }

    /**
     * Check if the user is subscribed to the subreddit by subredditName
     * @param subredditName
     * @param context
     * @return true if subscribed
     */
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
            assert cursor != null;
            cursor.close();
            return false;
        }
    }

}
