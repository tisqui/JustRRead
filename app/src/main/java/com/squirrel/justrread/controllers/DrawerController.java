package com.squirrel.justrread.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.Navigator;
import com.squirrel.justrread.fragments.FeedFragment;

import net.dean.jraw.auth.AuthenticationManager;

/**
 * Created by squirrel on 5/22/16.
 */
public class DrawerController {

    private DrawerLayout mDrawerLayout;
    private Context mContext;

    //Drawer CTAs
    private Button mLogin;
    private Button mSettings;
    private Button mEditSubreddits;
    private RelativeLayout mDrawerAllItem;
    private RelativeLayout mDrawerFrontpageItem;
    private RelativeLayout mDrawerRandomItem;
    private TextView mHelloUserText;

    private Tracker mTracker;


    public DrawerController(DrawerLayout drawerLayout, Context context, Tracker tracker) {
        mDrawerLayout = drawerLayout;
        mContext = context;
        mTracker = tracker;

    }

    private void setLoginButton(){
        mLogin.setText("Login");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.navigateToLogin(v.getContext());
            }
        });
    }

    private void setLogoutButton(){
        mLogin.setText("Logout");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Authentification.logout(mContext);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        setLoginButton();
                    }
                }.execute();
            }
        });
    }

    public void initDrawerActions(){
        mLogin = (Button) mDrawerLayout.findViewById(R.id.drawer_btn_login);

        mSettings = (Button) mDrawerLayout.findViewById(R.id.drawer_btn_settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.navigateToSettings(v.getContext());
            }
        });

        mDrawerAllItem = (RelativeLayout) mDrawerLayout.findViewById(R.id.drawer_all_item);
        mDrawerFrontpageItem = (RelativeLayout) mDrawerLayout.findViewById(R.id.drawer_frontpage_item);
        mDrawerRandomItem = (RelativeLayout) mDrawerLayout.findViewById(R.id.drawer_random_item);
    }

    public void setCotentActions(final FeedFragment feedFragment){
        if(feedFragment != null){
            mDrawerAllItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedFragment.refreshNewSubreddit(mContext.getString(R.string.all_key));
                    feedFragment.setPageTitle(mContext.getString(R.string.drawer_page_title_all));
                    feedFragment.setIsSubreddit(false);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                    //set the GA event
                    if(mTracker != null){
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(mContext.getString(R.string.ga_subreddits_category))
                                .setAction(mContext.getString(R.string.ga_all_action))
                                .setLabel(mContext.getString(R.string.ga_subreddit_label))
                                .build());
                    }
                }
            });
            mDrawerFrontpageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedFragment.setIsSubreddit(false);
                    feedFragment.getInitialFrontpage();
                    feedFragment.setPageTitle(mContext.getString(R.string.drawer_page_title_frontpage));
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                    //set the GA event
                    if(mTracker != null){
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(mContext.getString(R.string.ga_subreddits_category))
                                .setAction(mContext.getString(R.string.ga_frontpage_action))
                                .setLabel(mContext.getString(R.string.ga_subreddit_label))
                                .build());
                    }
                }
            });
            mDrawerRandomItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedFragment.refreshNewSubreddit(mContext.getString(R.string.random_subreddit_kay));
                    feedFragment.setPageTitle(mContext.getString(R.string.drawer_page_title_random));
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                    //set the GA event
                    if(mTracker != null){
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(mContext.getString(R.string.ga_subreddits_category))
                                .setAction(mContext.getString(R.string.ga_random_action))
                                .setLabel(mContext.getString(R.string.ga_subreddit_label))
                                .build());
                    }
                }
            });
        }
    }

    public void setUserName(){
        mHelloUserText = (TextView) mDrawerLayout.findViewById(R.id.drawer_hello_user_text);
        if(Utils.checkUserLoggedIn()){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return AuthenticationManager.get().getRedditClient().me().getFullName();
                }

                @Override
                protected void onPostExecute(String name) {
                    super.onPostExecute(name);
                    mHelloUserText.setText("Hello, " + name + " !");
                }
            }.execute();
        }else{
            mHelloUserText.setText(R.string.hello_sername_default_text);
        }
    }

    public void setTheme(){
        if (PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(mContext.getString(R.string.prefs_nightmode_key), false)) {
            mDrawerLayout.findViewById(R.id.drawer_linear_layout_container)
                    .setBackgroundColor(mContext.getResources().getColor(R.color.drawerBackgroundDark));

        } else {
            mDrawerLayout.findViewById(R.id.drawer_linear_layout_container)
                    .setBackgroundColor(mContext.getResources().getColor(R.color.drawerBackground));
        }
    }


}
