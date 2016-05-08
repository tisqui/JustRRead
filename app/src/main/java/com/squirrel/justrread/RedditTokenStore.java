package com.squirrel.justrread;

import android.content.Context;

import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.auth.TokenStore;

/**
 * Created by squirrel on 5/7/16.
 * Update inspired by https://github.com/thatJavaNerd/JRAW-Android/blob/master/library/src/main/java/net/dean/jraw/android/AndroidTokenStore.java
 */
public class RedditTokenStore implements TokenStore {
    private final Context mContext;
    private final String SHARED_PREFS_NAME = "token";

    public RedditTokenStore(Context context) {
        mContext = context;
    }

    @Override
    public boolean isStored(String key) {
        return mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).contains(key);
    }

    @Override
    public String readToken(String key) throws NoSuchTokenException {
        String token = mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).getString(key, null);
        if (token == null)
            throw new NoSuchTokenException("Token for key '" + key + "' does not exist");
        return token;
    }

    @Override
    public void writeToken(String key, String token) {
        mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(key, token)
                .apply();
    }

}
