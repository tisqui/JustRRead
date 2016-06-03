package com.squirrel.justrread.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by squirrel on 5/8/16.
 * Dummy provider. All DB interaction will be handled by SugarORM
 */
public class RedditProvider extends ContentProvider {
    public static final int POST = 100;
    public static final int SUBSCRIPTION = 200;
    private RedditDBHelper mRedditDBHelper;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mRedditDBHelper = new RedditDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case POST:
                retCursor = mRedditDBHelper.getReadableDatabase().query(
                        RedditDBHelper.Tables.POST,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUBSCRIPTION:
                retCursor = mRedditDBHelper.getReadableDatabase().query(
                        RedditDBHelper.Tables.SUBSCRIPTION,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POST:
                return RedditContract.PostEntry.CONTENT_TYPE;
            case SUBSCRIPTION:
                return RedditContract.SubscriptionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mRedditDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POST: {
                long _id = db.insert(RedditDBHelper.Tables.POST, null, values);
                if ( _id > 0 )
                    returnUri = RedditContract.PostEntry.buildPostUri(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUBSCRIPTION: {
                long _id = db.insert(RedditDBHelper.Tables.SUBSCRIPTION, null, values);
                if ( _id > 0 )
                    returnUri = RedditContract.SubscriptionEntry.buildSubscriptionUri(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mRedditDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POST: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RedditDBHelper.Tables.POST, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case SUBSCRIPTION:
            {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RedditDBHelper.Tables.SUBSCRIPTION, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mRedditDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deleted = 0;
        switch(match){
            case POST:{
                deleted = db.delete(RedditDBHelper.Tables.POST, selection, selectionArgs);
                if(deleted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            }
            case SUBSCRIPTION: {
                deleted = db.delete(RedditDBHelper.Tables.SUBSCRIPTION, selection, selectionArgs);
                if(deleted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mRedditDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updated = 0;

        switch(match){
            case POST:{
                updated= db.update(RedditDBHelper.Tables.POST, values, selection, selectionArgs);
                break;
            }
            case SUBSCRIPTION: {
                updated= db.update(RedditDBHelper.Tables.SUBSCRIPTION, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(updated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    public static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RedditContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, RedditContract.PATH_POST, POST);
        uriMatcher.addURI(authority, RedditContract.PATH_SUBSCRIPTION, SUBSCRIPTION);
        return uriMatcher;
    }
}
