package com.squirrel.justrread;

import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.auth.TokenStore;

/**
 * Created by squirrel on 5/7/16.
 */
public class RedditTokenStore implements TokenStore {
    private String mTempToken;

    @Override
    public boolean isStored(String key) {
        return mTempToken == null;
    }

    @Override
    public String readToken(String key) throws NoSuchTokenException {
        return mTempToken;
    }

    @Override
    public void writeToken(String key, String token) {
        mTempToken = token;
    }
}
