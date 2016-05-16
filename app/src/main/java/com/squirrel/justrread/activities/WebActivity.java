package com.squirrel.justrread.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squirrel.justrread.R;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by squirrel on 5/16/16.
 */
public class WebActivity extends BaseActivity {
    static final String LOG_TAG = WebActivity.class.getSimpleName();
    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    private String mUrl;
    public static final String EXTRA_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        activateToolbarWithHomeEnabled();
        mWebView = (WebView) findViewById(R.id.webview_container);
        mUrl = getIntent().getExtras().getString(EXTRA_URL, "");
        getSupportActionBar().setSubtitle(getDomainName(mUrl));

        mWebChromeClient = new WebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                mWebView.reload();
                return true;
            case R.id.share:
                //TODO share intent
                return true;
        }
        return false;
    }

    public static String getDomainName(String url) {
        URI uri;
        try {
            uri = new URI(url);

            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }
}
