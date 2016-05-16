package com.squirrel.justrread.data;

import android.content.ContentValues;

import net.dean.jraw.models.Submission;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by squirrel on 5/15/16.
 */
public class DataMapper {
    private static final String LOG_TAG = DataMapper.class.getSimpleName();

    public static ContentValues mapSubmissionToContentValues(Submission s){
        ContentValues postValues = new ContentValues();
        postValues.put(RedditContract.PostEntry.COLUMN_ID, s.getId());
        postValues.put(RedditContract.PostEntry.COLUMN_NAME, s.getFullName());
        postValues.put(RedditContract.PostEntry.COLUMN_UP_VOTES, s.getScore());
        postValues.put(RedditContract.PostEntry.COLUMN_DOWN_VOTES, s.getScore());
        postValues.put(RedditContract.PostEntry.COLUMN_LIKES, s.getScore());
        postValues.put(RedditContract.PostEntry.COLUMN_DATE_CREATED, dateToString(s.getCreated()));
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

        return postValues;
    }

    private static String dateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
