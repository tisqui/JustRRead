package com.squirrel.justrread.Provider;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.data.RedditDBHelper;
import com.squirrel.justrread.data.RedditProvider;

/**
 * Created by squirrel on 5/12/16.
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecordsFromContentProvider() {
        mContext.getContentResolver().delete(
                RedditContract.PostEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.PostEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from posts table during delete", 0, cursor.getCount());
        cursor.close();

        mContext.getContentResolver().delete(
                RedditContract.SubscriptionEntry.CONTENT_URI,
                null,
                null
        );

        cursor = mContext.getContentResolver().query(
                RedditContract.SubscriptionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from subscriptions table during delete", 0, cursor.getCount());
        cursor.close();

    }

    public void deleteAllRecords() {

        deleteAllRecordsFromContentProvider();
    }

    // Strart each test to start with a clean slate, run deleteAllRecords
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                RedditProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + RedditContract.CONTENT_AUTHORITY,
                    providerInfo.authority, RedditContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.squirrel.justrread/post/
        String type = mContext.getContentResolver().getType(RedditContract.PostEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.squirrel.justrread/post
        assertEquals("Error: the PostsEntry CONTENT_URI should return PostsEntry.CONTENT_TYPE",
                RedditContract.PostEntry.CONTENT_TYPE, type);
        // content://com.squirrel.justrread/subscription/
        type = mContext.getContentResolver().getType(RedditContract.SubscriptionEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.squirrel.justrread/subsciption
        assertEquals("Error: the SubsriptionEntry CONTENT_URI should return SunscriptionEntry.CONTENT_TYPE",
                RedditContract.SubscriptionEntry.CONTENT_TYPE, type);

    }

    public void testBasicPostsQueries() {
        RedditDBHelper redditDB = new RedditDBHelper(mContext);
        SQLiteDatabase db = redditDB.getWritableDatabase();

        ContentValues testValues = TestUtil.createPostTableValues(TestUtil.TEST_POST_ID);
        long moviesRowId = db.insert(RedditDBHelper.Tables.POST, null, testValues);;

        Cursor postCursor = mContext.getContentResolver().query(
                RedditContract.PostEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtil.validateCursor("testBasicMoviesQueries, movies query", postCursor, testValues);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Post Query did not properly set NotificationUri",
                    postCursor.getNotificationUri(), RedditContract.PostEntry.CONTENT_URI);
        }
    }


    public void testUpdatePosts() {
        ContentValues values = TestUtil.createPostTableValues(TestUtil.TEST_POST_ID);

        Uri postsUri = mContext.getContentResolver().
                insert(RedditContract.PostEntry.CONTENT_URI, values);
        long postRowId = ContentUris.parseId(postsUri);

        assertTrue(postRowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(RedditContract.PostEntry.COLUMN_ID, postRowId);
        updatedValues.put(RedditContract.PostEntry.COLUMN_TITLE, "Super new post");

        Cursor postCursor = mContext.getContentResolver().query(RedditContract.PostEntry.CONTENT_URI, null, null, null, null);

        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        postCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                RedditContract.PostEntry.CONTENT_URI, updatedValues, RedditContract.PostEntry.COLUMN_ID + "= ?",
                new String[] {TestUtil.TEST_POST_ID});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        postCursor.unregisterContentObserver(tco);
        postCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.PostEntry.CONTENT_URI,
                null,   // projection
                RedditContract.PostEntry.COLUMN_ID + " = " + postRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtil.validateCursor("testUpdatePosts.  Error validating posts entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testBasicSubQueries() {
        RedditDBHelper redditDB = new RedditDBHelper(mContext);
        SQLiteDatabase db = redditDB.getWritableDatabase();

        ContentValues testValues = TestUtil.createSubTableValues(TestUtil.TEST_SUBSCRIPTION_ID);
        long subRowId = db.insert(RedditDBHelper.Tables.SUBSCRIPTION, null, testValues);;

        Cursor subCursor = mContext.getContentResolver().query(
                RedditContract.SubscriptionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtil.validateCursor("testBasicSubsQueries, sub query", subCursor, testValues);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Subscription Query did not properly set NotificationUri",
                    subCursor.getNotificationUri(), RedditContract.SubscriptionEntry.CONTENT_URI);
        }
    }


    public void testUpdateSubscriptions() {
        ContentValues values = TestUtil.createSubTableValues(TestUtil.TEST_SUBSCRIPTION_ID);

        Uri subUri = mContext.getContentResolver().
                insert(RedditContract.SubscriptionEntry.CONTENT_URI, values);
        long subRowId = ContentUris.parseId(subUri);

        assertTrue(subRowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(RedditContract.SubscriptionEntry.COLUMN_ID, subRowId);
        updatedValues.put(RedditContract.SubscriptionEntry.COLUMN_DISPLAY_NAME, "Super new subreddit");

        Cursor subCursor = mContext.getContentResolver().query(RedditContract.SubscriptionEntry.CONTENT_URI, null, null, null, null);

        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        subCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                RedditContract.SubscriptionEntry.CONTENT_URI, updatedValues, RedditContract.SubscriptionEntry.COLUMN_ID + "= ?",
                new String[] {TestUtil.TEST_SUBSCRIPTION_ID});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        subCursor.unregisterContentObserver(tco);
        subCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.SubscriptionEntry.CONTENT_URI,
                null,   // projection
                RedditContract.SubscriptionEntry.COLUMN_ID + " = " + subRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtil.validateCursor("testUpdateSubscription.  Error validating subscriptions entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues values = TestUtil.createPostTableValues(TestUtil.TEST_POST_ID);

        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RedditContract.PostEntry.CONTENT_URI, true, tco);
        Uri postsUri = mContext.getContentResolver().insert(RedditContract.PostEntry.CONTENT_URI, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long postsRowId = ContentUris.parseId(postsUri);

        assertTrue(postsRowId != -1);

        //check the data
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.PostEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtil.validateCursor("testInsertReadProvider. Error validating SubscriptionEntry.",
                cursor, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

    }

    public void testInsertReadSubscriptionProvider() {
        ContentValues values = TestUtil.createSubTableValues(TestUtil.TEST_SUBSCRIPTION_ID);

        TestUtil.TestContentObserver tco = TestUtil.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(RedditContract.SubscriptionEntry.CONTENT_URI, true, tco);
        Uri subUri = mContext.getContentResolver().insert(RedditContract.SubscriptionEntry.CONTENT_URI, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long subRowId = ContentUris.parseId(subUri);

        assertTrue(subRowId != -1);

        //check the data
        Cursor cursor = mContext.getContentResolver().query(
                RedditContract.SubscriptionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtil.validateCursor("testInsertReadProvider. Error validating SubscriptionsEntry.",
                cursor, values);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

    }
}
