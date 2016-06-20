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

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.BaseActivity;
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


    public DrawerController(DrawerLayout drawerLayout, Context context) {
        mDrawerLayout = drawerLayout;
        mContext = context;
    }

    private void setLoginButton(){
        mLogin.setText("Login");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.navigateToLogin(mContext);
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
                        Authentification.logout();
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
//        if(!Utils.checkUserLoggedIn()){
//            Log.d("DrawerController", "User not logged in ******************************");
//           setLoginButton();
//        } else {
//            Log.d("DrawerController", "User logged in ******************************");
//            setLogoutButton();
//        }

        mSettings = (Button) mDrawerLayout.findViewById(R.id.drawer_btn_settings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.navigateToSettings(mContext);
            }
        });

        mEditSubreddits = (Button) mDrawerLayout.findViewById(R.id.drawer_edit_subreddits_button);
        mEditSubreddits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.checkUserLoggedIn()){
                    Navigator.navigateToSubredditsSettings(mContext);
                } else {
                    BaseActivity.showLoginAlert(mContext);
                }
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
                    feedFragment.refreshNewSubreddit("all");
                    feedFragment.setIsSubreddit(false);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
            mDrawerFrontpageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedFragment.setIsSubreddit(false);
                    feedFragment.onResume();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
            mDrawerRandomItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedFragment.refreshNewSubreddit("random");
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
        }
    }

    public void setUserName(){
        mHelloUserText = (TextView) mDrawerLayout.findViewById(R.id.drawer_hello_user_text);
        if(Utils.checkUserLoggedIn()){
            String username = AuthenticationManager.get().getRedditClient().me().getFullName();
            mHelloUserText.setText("Hello," + username);
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
