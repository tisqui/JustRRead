package com.squirrel.justrread.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by squirrel on 5/12/16.
 */
public class RedditContract {
    public static final String CONTENT_AUTHORITY = "com.squirrel.justrread";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POST = "post";

    public interface PostColumns{
        String COLUMN_ID = "id";
        String COLUMN_NAME ="name";
        String COLUMN_UP_VOTES = "up_votes";
        String COLUMN_DOWN_VOTES = "down_votes";
        String COLUMN_LIKES = "likes";
        String COLUMN_DATE_CREATED="created";
        String COLUMN_AUTHOR="author";
        String COLUMN_DOMAIN="domain";
        String COLUMN_SELF_POST="self_post";
        String COLUMN_NUM_COMMENTS="num_comments";
        String COLUMN_NSFW="nsfw";
        String COLUMN_SUBREDDIT="subreddit";
        String COLUMN_SUBREDDIT_ID="subreddit_id";
        String COLUMN_SELFHTML="self_html";
        String COLUMN_THUMBNAIL="thumbnail";
        String COLUMN_TITLE = "title";
        String COLUMN_URL = "url";
        String COLUMN_MEDIA_TYPE="media_type";
        String COLUMN_MEDIA_LINK="media_link";
        String COLUMN_MEDIA_THUMBNAIL="media_thumbnail";

    }

    /* Inner class that defines the table contents of the posts table */
    public static final class PostEntry implements BaseColumns, PostColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POST;

        public static String getPostId(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildPostUri(String id) {
            return CONTENT_URI.buildUpon().appendEncodedPath(id).build();
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter("");
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

}
