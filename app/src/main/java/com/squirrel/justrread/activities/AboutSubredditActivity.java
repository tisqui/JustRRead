package com.squirrel.justrread.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.api.RedditAPI;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by squirrel on 5/27/16.
 */
public class AboutSubredditActivity extends BaseActivity {
    static final String LOG_TAG = AboutSubredditActivity.class.getSimpleName();
    private String mContent;
    private String mSubredditId;

    @Bind(R.id.subreddit_about_text)
    TextView mSubredditAboutTextView;

    @Bind(R.id.subreddit_about_empty)
    TextView mSubredditAboutEmpty;

    @Bind(R.id.subreddit_about_loading_progress)
    ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_subreddit);
        ButterKnife.bind(this);
        activateToolbarWithHomeEnabled();

        mSubredditId = getIntent().getExtras().getString(BaseActivity.SUBREDDIT_ID_KEY, "");
        getSupportActionBar().setSubtitle(mSubredditId);

        mSubredditAboutEmpty.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.GONE);

        getAboutContent();

    }

    private void getAboutContent(){
        if (Utils.isNetworkAvailable(this)) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            if(mSubredditId != null && !mSubredditId.isEmpty()){
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        mContent = RedditAPI.getSubredditAbout(mSubredditId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mLoadingProgress.setVisibility(View.GONE);
                        loadMarkdownToTextView();
                    }
                }.execute();
            } else {
                mSubredditAboutEmpty.setText(getString(R.string.subreddit_about_no_data));
                mSubredditAboutEmpty.setVisibility(View.VISIBLE);
            }
        } else {
            mSubredditAboutEmpty.setText(getString(R.string.subreddit_about_no_connection));
            mSubredditAboutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void loadMarkdownToTextView(){
        String html = Utils.getHtmlFromMarkdown(mContent);
        mSubredditAboutTextView.setText(Html.fromHtml(html));
    }
}
