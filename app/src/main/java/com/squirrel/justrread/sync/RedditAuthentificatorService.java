package com.squirrel.justrread.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by squirrel on 5/8/16.
 */
public class RedditAuthentificatorService extends Service {
    // Instance field that stores the authenticator object
    private RedditAuthentificator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new RedditAuthentificator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
