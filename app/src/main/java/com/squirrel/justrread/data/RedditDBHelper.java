package com.squirrel.justrread.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.squirrel.justrread.data.RedditContract.PostEntry;

/**
 * Created by squirrel on 5/12/16.
 */
public class RedditDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = RedditDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "reddit_reader.db";
    private Context mContext;

    public interface Tables {
        String POST = "post";
    }

    public RedditDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table for posts
        final String SQL_CREATE_POST_TABLE = "CREATE TABLE " + Tables.POST + " (" +
                PostEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                PostEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PostEntry.COLUMN_UP_VOTES + " INTEGER, " +
                PostEntry.COLUMN_DOWN_VOTES + " INTEGER, " +
                PostEntry.COLUMN_LIKES + " INTEGER, " +
                PostEntry.COLUMN_DATE_CREATED + " INTEGER, " +
                PostEntry.COLUMN_AUTHOR + " TEXT, " +
                PostEntry.COLUMN_DOMAIN + " TEXT, " +
                PostEntry.COLUMN_SELF_POST + " INTEGER, " +
                PostEntry.COLUMN_NUM_COMMENTS + " INTEGER, " +
                PostEntry.COLUMN_NSFW + " INTEGER, " +
                PostEntry.COLUMN_SUBREDDIT + " TEXT, " +
                PostEntry.COLUMN_SUBREDDIT_ID + " TEXT, " +
                PostEntry.COLUMN_SELFHTML + " TEXT, " +
                PostEntry.COLUMN_THUMBNAIL + " TEXT, " +
                PostEntry.COLUMN_TITLE + " TEXT, " +
                PostEntry.COLUMN_URL + " TEXT, " +
                PostEntry.COLUMN_MEDIA_TYPE + " TEXT, " +
                PostEntry.COLUMN_MEDIA_LINK + " TEXT, " +
                PostEntry.COLUMN_MEDIA_THUMBNAIL + " TEXT" +
                " );";

        db.execSQL(SQL_CREATE_POST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion; //version the user is running now
        if(version == 1){
            //do not delete existing data, add fields - take care about the new version
            version  = 2;
        }
        if(version != DATABASE_VERSION){
            //worst case scenario
            db.execSQL("DROP TABLE IF EXISTS " + Tables.POST);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}
