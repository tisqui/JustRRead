package com.squirrel.justrread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squirrel.justrread.activities.LoginActivity;
import com.squirrel.justrread.data.RedditContract;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.auth.NoSuchTokenException;
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

    public void refreshAccessTokenAsync() {
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

//    private void tryAuthentificateAppWithExistingToken() {
//        new AsyncTask<String, Void, Void>() {
//            @Override
//            protected Void doInBackground(String... params) {
//                try {
//
//                    String token = mRedditTokenStore.readToken("");
//
//                    if (token == null) {
//                        //not logged in mode, authentificate without user
//                        final Credentials fcreds = Credentials.userlessApp(BuildConfig.APP_ID, UUID.randomUUID());
//                        OAuthData authData = AuthenticationManager.get().getRedditClient().getOAuthHelper().easyAuth(fcreds);
//                        AuthenticationManager.get().getRedditClient().authenticate(authData);
//
//                    } else {
//                        //some token exists, app was logged in
//                        Credentials credentials = Credentials.installedApp(BuildConfig.APP_ID, BuildConfig.REDIRECT_URL);
//                        OAuthData finalData = AuthenticationManager.get().getRedditClient().getOAuthHelper().refreshToken(credentials);
//                        AuthenticationManager.get().getRedditClient().authenticate(finalData);
//                    }
//
//                    return null;
//                } catch (NetworkException | OAuthException e) {
//                    Log.e(LOG_TAG, "Could not log in", e);
//                    return null;
//                } catch (NoSuchTokenException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        }.execute();
//    }


//    //FOR TEST PURPOSES ONLY
//    public void getFrontPageContent() {
//        new AsyncTask<String, Void, Void>() {
//            @Override
//            protected Void doInBackground(String... params) {
//                // Set up a Paginator for the front page
//                SubredditPaginator paginator = new SubredditPaginator(AuthenticationManager.get().getRedditClient());
//                paginator.setLimit(50);
//                if (paginator != null) {
//                    // Request the first page
//                    if (paginator.hasNext()) {
//                        Listing<Submission> firstPage = paginator.next();
//                        for (Submission s : firstPage) {
//                            // Print some basic stats about the posts
//                            Log.d(LOG_TAG, "Subreddit name: " + s.getSubredditName() + " score: " + s.getScore() + "Title" + s.getTitle());
//                            System.out.printf("[/r/%s - %s karma] %s\n", s.getSubredditName(), s.getScore(), s.getTitle());
//                        }
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//            }
//        }.execute();
//
//    }

    public static void logout(Context context) {
        //need to revoke access token first
        if(Utils.checkUserLoggedIn()){
            //clean all user subscriptions from DB
            context.getContentResolver().delete(RedditContract.SubscriptionEntry.CONTENT_URI, null, null);
            AuthenticationManager.get().getRedditClient().getOAuthHelper().revokeAccessToken(LoginActivity.CREDENTIALS);
            AuthenticationManager.get().getRedditClient().deauthenticate();

        } else {
            Log.d(LOG_TAG, "Can't logout, because OAUthStatus is: " +
                    AuthenticationManager.get().getRedditClient().getOAuthHelper().getAuthStatus());
        }
    }
}
