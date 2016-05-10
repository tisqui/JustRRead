package com.squirrel.justrread.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by squirrel on 5/8/16.
 * Dummy provider. All DB interaction will be handled by SugarORM
 */
public class RedditProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "com.squirrel.justrread";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POSTS = "posts";

    static final int POSTS = 100;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Cursor retCursor;
//        switch (sUriMatcher.match(uri)) {
//            case POSTS:
//                retCursor = null;
////                retCursor = Post.find(Post.class, selection, selectionArgs, null, null, null);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return retCursor;
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            case POSTS:
//                //TODO find out if I need this
//                return null;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(CONTENT_AUTHORITY, PATH_POSTS,POSTS);
        return matcher;
    }
}
