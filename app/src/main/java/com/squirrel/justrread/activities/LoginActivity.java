package com.squirrel.justrread.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.squirrel.justrread.BuildConfig;
import com.squirrel.justrread.R;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/2/16.
 */
public class LoginActivity extends BaseActivity {

    public static final String APP_ID = BuildConfig.APP_ID;
    public static final String URL = BuildConfig.REDIRECT_URL;

    public static final Credentials CREDENTIALS = Credentials.installedApp(APP_ID, URL);
    public static final String LOG_TAG = LoginActivity.class.getSimpleName();
    @Bind(R.id.login_webview) WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activateToolbarWithHomeEnabled();

        // Create our RedditClient
        final OAuthHelper helper = AuthenticationManager.get().getRedditClient().getOAuthHelper();

        // OAuth2 scopes to request. See https://www.reddit.com/dev/api/oauth for a full list
        String[] scopes = {"identity", "read"};

        final URL authorizationUrl = helper.getAuthorizationUrl(CREDENTIALS, true, true, scopes);
//        final WebView webView = ((WebView) findViewById(R.id.login_webview));
        // Load the authorization URL into the browser
        mWebView.loadUrl(authorizationUrl.toExternalForm());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("code=")) {
                    // We've detected the redirect URL
                    onUserChallenge(url, CREDENTIALS);
                } else if (url.contains("error=")) {
                    Toast.makeText(LoginActivity.this, "You must press 'allow' to log in with this account", Toast.LENGTH_SHORT).show();
                    mWebView.loadUrl(authorizationUrl.toExternalForm());
                }
            }
        });
    }

    private void onUserChallenge(final String url, final Credentials creds) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    OAuthData data = AuthenticationManager.get().getRedditClient().getOAuthHelper().onUserChallenge(params[0], creds);
                    AuthenticationManager.get().getRedditClient().authenticate(data);
                    return AuthenticationManager.get().getRedditClient().getAuthenticatedUser();
                } catch (NetworkException | OAuthException e) {
                    Log.e(LOG_TAG, "Could not log in", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i(LOG_TAG, s);
                LoginActivity.this.finish();
            }
        }.execute(url);
    }
}
