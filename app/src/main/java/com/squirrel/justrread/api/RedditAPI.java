package com.squirrel.justrread.api;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.FluentIterable;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.data.DataMapper;
import com.squirrel.justrread.data.RedditContract;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.TraversalMethod;
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

//    public void getPostsSorted(SubredditPaginator paginator, Context context, Sorting sort) {
//        if (checkAuthentificationReady()) {
//            if (paginator != null) {
//                paginator.setSorting(sort);
//                if (paginator.hasNext()) {
//                    Listing<Submission> firstPage = paginator.next();
//                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(firstPage.size());
//                    for (Submission s : firstPage) {
//                        contentValuesList.add(DataMapper.mapSubmissionToContentValues(s));
//                    }
//                    if (contentValuesList.size() > 0) {
//                        ContentValues[] cvArray = new ContentValues[contentValuesList.size()];
//                        contentValuesList.toArray(cvArray);
//                        //delete all previous data
//                        context.getContentResolver().delete(RedditContract.PostEntry.CONTENT_URI, null, null);
//                        context.getContentResolver().bulkInsert(RedditContract.PostEntry.CONTENT_URI, cvArray);
//                    }
//                } else {
//                    Log.d(LOG_TAG, "No more pages available");
//                }
//            }
//        } else {
//            Log.d(LOG_TAG, "getFrontPost: Not Authentificated");
//        }
//    }

    //get the subreddit feed
    public void getSubredditPostsSorted(SubredditPaginator paginator, Context context, String subredditId, Sorting sort) {
        if (checkAuthentificationReady()) {
            if (paginator != null) {
                if (sort != null) {
                    paginator.setSorting(sort);
                }
                if (subredditId != null) {
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
                if(userSubredditsPaginator != null && userSubredditsPaginator.hasNext()){
                    Listing<Subreddit> subscriptions = userSubredditsPaginator.next();
                    Vector<ContentValues> contentValuesList = new Vector<ContentValues>(subscriptions.size());
                    for (Subreddit s : subscriptions) {
                        contentValuesList.add(DataMapper.mapSubredditToContentValues(s));
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
