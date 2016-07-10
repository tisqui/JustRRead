package com.squirrel.justrread;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

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
    public Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeAuth();
        getDefaultTracker();
    }

    public RedditTokenStore getRedditTokenStore() {
        return mRedditTokenStore;
    }

    /**
     * Intialize the authentification client from JRAW
     */
    public void initializeAuth(){
        RedditClient reddit = new RedditClient(UserAgent.of("installed app", "com.squirrel.justrread", "v0.1", BuildConfig.USER_NAME));
        reddit.setLoggingMode(LoggingMode.NEVER);
        mRedditTokenStore = new RedditTokenStore(this);
        //initialize Authentification manager
        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(mRedditTokenStore, reddit));
    }

    /**
     * Get the google analytics Tracker. Ensuring that there is only one instance.
     * @return the current tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.track_app);
            analytics.enableAutoActivityReports(this);
            mTracker.enableAutoActivityTracking(true);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
        return mTracker;
    }
}
