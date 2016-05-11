package com.squirrel.justrread.sync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 5/10/16.
 */
public class PostsLoader extends AsyncTaskLoader<List<Post>> {

    List<Post> mPosts;


    public PostsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Post> loadInBackground() {
//        return SugarRecord.getCursor(Post.class, null, null, null, null, null);
        return Post.listAll(Post.class);
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
