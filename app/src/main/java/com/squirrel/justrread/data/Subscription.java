package com.squirrel.justrread.data;

import java.io.Serializable;

/**
 * Created by squirrel on 5/30/16.
 */
public class Subscription implements Serializable {
    private String mSubredditId;
    private String mSubredditDisplayName;
    private boolean mIsNsfw;

    public String getSubredditId() {
        return mSubredditId;
    }

    public void setSubredditId(String subredditId) {
        mSubredditId = subredditId;
    }

    public String getSubredditDisplayName() {
        return mSubredditDisplayName;
    }

    public void setSubredditDisplayName(String subredditDisplayName) {
        mSubredditDisplayName = subredditDisplayName;
    }

    public boolean isNsfw() {
        return mIsNsfw;
    }

    public void setIsNsfw(boolean isNsfw) {
        mIsNsfw = isNsfw;
    }

    @Override
    public String toString() {
        return "Subreddit{" +
                "mSubredditId='" + mSubredditId + '\'' +
                ", mSubredditDisplayName='" + mSubredditDisplayName + '\'' +
                ", mIsNsfw='" + mIsNsfw + '\'' +
                '}';
    }
}
