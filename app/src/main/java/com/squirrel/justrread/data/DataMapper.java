package com.squirrel.justrread.data;

import android.content.ContentValues;
import android.database.Cursor;

import net.dean.jraw.models.Submission;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by squirrel on 5/15/16.
 */
public class DataMapper {
    private static final String LOG_TAG = DataMapper.class.getSimpleName();

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

    public static Post mapCursorToPost(Cursor cursor){
        return new Post(
                cursor.getString(COL_POST_ID),
                cursor.getString(COL_NAME),
                cursor.getInt(COL_UP_VOTES),
                cursor.getInt(COL_DOWN_VOTES),
                cursor.getInt(COL_LIKES),
                cursor.getString(COL_DATE_CREATED),
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
}
