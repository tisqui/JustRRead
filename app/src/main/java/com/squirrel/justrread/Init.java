package com.squirrel.justrread;

import android.app.Application;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.UserAgent;

/**
 * Created by squirrel on 5/25/16.
 */
public class Init extends Application {

    private RedditTokenStore mRedditTokenStore;
    private static final String APP_ID = BuildConfig.APP_ID;
    private static final String RED_URL = BuildConfig.REDIRECT_URL;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeAuth();
    }

    public RedditTokenStore getRedditTokenStore() {
        return mRedditTokenStore;
    }
    public void initializeAuth(){
        RedditClient reddit = new RedditClient(UserAgent.of("installed app", BuildConfig.APP_UNIQUE_ID, "v0.1", BuildConfig.USER_NAME));
        reddit.setLoggingMode(LoggingMode.NEVER);
        mRedditTokenStore = new RedditTokenStore(this);
        //initialize Authentification manager
        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(mRedditTokenStore, reddit));
    }
}
