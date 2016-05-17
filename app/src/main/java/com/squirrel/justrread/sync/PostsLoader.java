package com.squirrel.justrread.sync;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by squirrel on 5/10/16.
 */
public class PostsLoader extends AsyncTaskLoader<List<Post>> {

    List<Post> mPosts;
    Context mContext;
    ContentObserver mContentObserver;

    static final int COL_POST_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_UP_VOTES = 2;
    static final int COL_DOWN_VOTES = 3;
    static final int COL_LIKES = 4;
    static final int COL_DATE_CREATED = 5;
    static final int COL_AUTHOR = 6;
    static final int COL_DOMAIN = 7;
    static final int COL_SELF = 8;
    static final int COL_NUM_COMMENTS = 9;
    static final int COL_NSFW = 10;
    static final int COL_SUBREDDIT = 11;
    static final int COL_SUBREDDIT_ID = 12;
    static final int COL_SELFHTML = 13;
    static final int COL_THUMBNAIL = 14;
    static final int COL_TITLE = 15;
    static final int COL_URL = 16;


    public PostsLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public List<Post> loadInBackground() {
        List<Post> postsList = new ArrayList<>();
        Cursor postCursor = mContext.getContentResolver().query(RedditContract.PostEntry.CONTENT_URI, null, null, null, null);
        int count = postCursor.getCount();
        for(int i=0; i< count; i++){
            postCursor.moveToPosition(i);
            postsList.add(mapPost(postCursor));
        }
        return postsList;
    }

    private Post mapPost(Cursor cursor){
        return new Post(
                cursor.getString(COL_POST_ID),
                cursor.getString(COL_NAME),
                cursor.getInt(COL_UP_VOTES),
                cursor.getInt(COL_DOWN_VOTES),
                cursor.getInt(COL_LIKES),
                new Date(cursor.getLong(COL_DATE_CREATED)),
                cursor.getString(COL_AUTHOR),
                cursor.getString(COL_DOMAIN),
                cursor.getInt(COL_SELF)==1 ? true:false,
                cursor.getInt(COL_NUM_COMMENTS),
                cursor.getInt(COL_NSFW)==1 ? true:false,
                cursor.getString(COL_SUBREDDIT),
                cursor.getString(COL_SUBREDDIT_ID),
                cursor.getString(COL_SELFHTML),
                cursor.getString(COL_THUMBNAIL),
                cursor.getString(COL_TITLE),
                cursor.getString(COL_URL)
        );
    }

    @Override
    public void deliverResult(List<Post> posts) {
        if (isReset()) {
            if (posts != null) {
                onReleaseResources(posts);
            }
        }
        List<Post> oldPosts = mPosts;
        mPosts = posts;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(posts);
        }

        if (oldPosts != null) {
            onReleaseResources(oldPosts);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mPosts != null) {
            deliverResult(mPosts);
        }

        if(mContentObserver == null){
            //create ContentObserver
        }

        if (takeContentChanged() || mPosts == null) {
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Post> posts) {
        super.onCanceled(posts);
        onReleaseResources(posts);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mPosts != null) {
            onReleaseResources(mPosts);
            mPosts = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Post> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
