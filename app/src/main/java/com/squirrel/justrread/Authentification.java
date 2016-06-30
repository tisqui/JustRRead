package com.squirrel.justrread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squirrel.justrread.activities.LoginActivity;
import com.squirrel.justrread.data.RedditContract;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

import java.util.UUID;

/**
 * Created by squirrel on 5/8/16.
 */
public class Authentification {
    private static String LOG_TAG = Authentification.class.getSimpleName();

    private RedditTokenStore mRedditTokenStore;

    public Authentification(Context context) {
//        RedditClient reddit = new RedditClient(UserAgent.of("installed app", BuildConfig.APP_UNIQUE_ID, "v0.1", BuildConfig.USER_NAME));
//        reddit.setLoggingMode(LoggingMode.ALWAYS);
//        mRedditTokenStore = new RedditTokenStore(context);
//        //initialize Authentification manager
//        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(mRedditTokenStore, reddit));
    }

    public void checkAuthState() {
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        Log.d(LOG_TAG, "AuthenticationState for onResume(): " + state);

        switch (state) {
            case READY:
                //no need to perform actions
                break;
            case NONE:
                //user was not logged in, app was not authentificated - try to auth without login
                authentificateWithoutLoginAsync();
                break;
            case NEED_REFRESH:
                //authentificated, but token should be refreshed
                refreshAccessTokenAsync();
                break;
        }
    }

    public static void refreshAccessTokenAsync() {
        new AsyncTask<Credentials, Void, Void>() {
            @Override
            protected Void doInBackground(Credentials... params) {
                try {
                    AuthenticationManager.get().refreshAccessToken(LoginActivity.CREDENTIALS);
                } catch (NoSuchTokenException | OAuthException e) {
                    Log.e(LOG_TAG, "Could not refresh access token", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d(LOG_TAG, "Reauthenticated");
            }
        }.execute();
    }

    public void authentificateWithoutLoginAsync() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                final Credentials fcreds = Credentials.userlessApp(BuildConfig.APP_ID, UUID.randomUUID());
                OAuthData authData;
                try {
                    authData = AuthenticationManager.get().getRedditClient().getOAuthHelper().easyAuth(fcreds);
                    AuthenticationManager.get().getRedditClient().authenticate(authData);
                } catch (OAuthException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d(LOG_TAG, "Authentification DONE");
                AuthenticationState state = AuthenticationManager.get().checkAuthState();
                Log.d(LOG_TAG, "AuthenticationState state: " + state);
//                new RedditAPI().getPostsFront(null, 50, mContext);
            }
        }.execute();
    }

    public static void logout(Context context) {
        //need to revoke access token first
        if(Utils.checkUserLoggedIn()){
            //clean all user subscriptions from DB
            context.getContentResolver().delete(RedditContract.SubscriptionEntry.CONTENT_URI, null, null);
            try {
                AuthenticationManager.get().getRedditClient().getOAuthHelper().revokeAccessToken(LoginActivity.CREDENTIALS);
//                AuthenticationManager.get().getRedditClient().deauthenticate();
            }catch (NetworkException e){
                Log.d(LOG_TAG, e.getStackTrace().toString());
            }

        } else {
            Log.d(LOG_TAG, "Can't logout, because OAUthStatus is: " +
                    AuthenticationManager.get().getRedditClient().getOAuthHelper().getAuthStatus());
        }
    }

    public static void refreshAuthAfterSleep(Context context){
        if(Utils.isNetworkAvailable(context)){
            AuthenticationState state = AuthenticationManager.get().checkAuthState();
            switch (state) {
                case NEED_REFRESH:
                    Log.d(LOG_TAG, "Refreshing access token");
                    refreshAccessTokenAsync();
                    break;
            }
        }
        else{
            Toast.makeText(context, R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
        }
    }
}
