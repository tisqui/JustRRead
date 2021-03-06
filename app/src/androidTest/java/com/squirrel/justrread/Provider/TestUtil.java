package com.squirrel.justrread.Provider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.squirrel.justrread.data.Post;
import com.squirrel.justrread.data.RedditContract;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by squirrel on 5/12/16.
 */
public class TestUtil extends AndroidTestCase {

    public static final String TEST_POST_ID = "123";
    public static final String TEST_SUBSCRIPTION_ID = "qwerty";


    static ContentValues createPostTableValues(String postId) {
        ContentValues postValues = new ContentValues();
        postValues.put(RedditContract.PostEntry.COLUMN_ID, postId);
        postValues.put(RedditContract.PostEntry.COLUMN_NAME, "Name");
        postValues.put(RedditContract.PostEntry.COLUMN_UP_VOTES, 100);
        postValues.put(RedditContract.PostEntry.COLUMN_DOWN_VOTES, 200);
        postValues.put(RedditContract.PostEntry.COLUMN_LIKES, 100);
        postValues.put(RedditContract.PostEntry.COLUMN_DATE_CREATED, "Mon, 12 May, 2016");
        postValues.put(RedditContract.PostEntry.COLUMN_AUTHOR, "Author");
        postValues.put(RedditContract.PostEntry.COLUMN_DOMAIN, "Domain");
        postValues.put(RedditContract.PostEntry.COLUMN_SELF_POST, 1);
        postValues.put(RedditContract.PostEntry.COLUMN_NUM_COMMENTS, 100);
        postValues.put(RedditContract.PostEntry.COLUMN_NSFW, 1);
        postValues.put(RedditContract.PostEntry.COLUMN_SUBREDDIT, "Subreddit");
        postValues.put(RedditContract.PostEntry.COLUMN_SUBREDDIT_ID, "Subreddit id");
        postValues.put(RedditContract.PostEntry.COLUMN_SELFHTML, "HTML");
        postValues.put(RedditContract.PostEntry.COLUMN_THUMBNAIL, "link to thumbnail");
        postValues.put(RedditContract.PostEntry.COLUMN_TITLE, "Title");
        postValues.put(RedditContract.PostEntry.COLUMN_URL, "URL");
        postValues.put(RedditContract.PostEntry.COLUMN_MEDIA_TYPE, "1");
        postValues.put(RedditContract.PostEntry.COLUMN_MEDIA_LINK, "URL");
        postValues.put(RedditContract.PostEntry.COLUMN_THUMBNAIL, "URL");

        return postValues;
    }

    static ContentValues createSubTableValues(String subId) {
        ContentValues subValues = new ContentValues();
        subValues.put(RedditContract.SubscriptionEntry.COLUMN_ID, subId);
        subValues.put(RedditContract.SubscriptionEntry.COLUMN_DISPLAY_NAME, "name");
        subValues.put(RedditContract.SubscriptionEntry.COLUMN_NSFW, 1);
        return subValues;
    }

    static Post createPost(String id){
        return new Post(id, "Name", 100, 200, 100, new Date(System.currentTimeMillis()), "author", "domain", true, 100, true, "subreddit",
                "subreddit id", "html", "link", "title", "url", "url", "url", "url");
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            String valueFromCursor = valueCursor.getString(idx);

            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueFromCursor);
        }
    }

    /*
        Class taken from the Udacity course lectures for testing: The functions we provide inside of
        TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }
}
