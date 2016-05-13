package com.squirrel.justrread.Provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.data.RedditDBHelper;

import java.util.HashSet;

/**
 * Created by squirrel on 5/12/16.
 */
public class TestDB extends AndroidTestCase {
    void deleteTheDatabase() {
        mContext.deleteDatabase(RedditDBHelper.DATABASE_NAME);
    }
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreatingDB()throws Throwable{

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(RedditDBHelper.Tables.POST);

        mContext.deleteDatabase(RedditDBHelper.DATABASE_NAME);

        //create the DB
        SQLiteDatabase db = new RedditDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // check if the tables were created
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: DB creation error", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without all 3 tables that expected",
                tableNameHashSet.isEmpty());

        // checking columns
        c = db.rawQuery("PRAGMA table_info(" + RedditDBHelper.Tables.POST + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> postsColumnHashSet = new HashSet<String>();
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_ID);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_NAME);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_UP_VOTES);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_DOWN_VOTES);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_LIKES);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_DATE_CREATED);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_AUTHOR);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_DOMAIN);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_SELF_POST);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_NUM_COMMENTS);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_NSFW);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_SUBREDDIT);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_SUBREDDIT_ID);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_SELFHTML);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_THUMBNAIL);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_TITLE);
        postsColumnHashSet.add(RedditContract.PostColumns.COLUMN_URL);


        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            postsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movies entry columns",
                postsColumnHashSet.isEmpty());
        db.close();
    }

    public void testPostsTable(){
        //get writable DB
        RedditDBHelper postsDB = new RedditDBHelper(mContext);
        SQLiteDatabase db = postsDB.getWritableDatabase();

        ContentValues movieValues = TestUtil.createPostTableValues(TestUtil.TEST_POST_ID);

        long movie_id;
        movie_id = db.insert(RedditDBHelper.Tables.POST, null, movieValues);

        assertTrue(movie_id != -1);
        Cursor cursor = db.query(RedditDBHelper.Tables.POST,
                null, null, null, null, null, null);
        assertTrue("No records returned, but should to", cursor.moveToFirst());

        TestUtil.validateCurrentRecord("Quesry is not validated", cursor, movieValues);
        cursor.close();
    }

    public void insertPost(){
        RedditDBHelper redditDB = new RedditDBHelper(mContext);
        SQLiteDatabase db = redditDB.getWritableDatabase();
        ContentValues testValues = TestUtil.createPostTableValues(TestUtil.TEST_POST_ID);

        long rowId;
        rowId = db.insert(RedditDBHelper.Tables.POST, null, testValues);
        assertTrue(rowId != -1);

        Cursor cursor = db.query(RedditDBHelper.Tables.POST,null, null, null, null, null, null);
        assertTrue("Error: No Records returned from movies query", cursor.moveToFirst());
        TestUtil.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);
        assertFalse("Error: More than one record returned from movies query",
                cursor.moveToNext());
        cursor.close();
        db.close();

    }
}
