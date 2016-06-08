package com.squirrel.justrread.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.squirrel.justrread.Authentification;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;

/**
 * Created by squirrel on 4/24/16.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private Navigator navigator;
    private Authentification mAuthentification;
    private static String AUTH_STATE = "auth_state";

    public static final String FRONTPAGE_FEED_KEY = "FRONTPAGE_FEED";
    public static final String POST_DETAIL_KEY = "POST_DETAIL";
    public static final String SUBREDDIT_ID_KEY = "SUBREDDIT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = new Navigator();

        initialize(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.application_toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
//                int statusBarHeight = getStatusBarHeight();
//                mToolbar.setPadding(0, statusBarHeight, 0, 0);
//                mToolbar.setMinimumHeight(getNavBarSize() + statusBarHeight);
            }
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        getToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;
    }

    /**
     * Initial settings of the activity
     */
    private void initialize(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            //no saved instance, get the data from the calling intent and add fragments
            mAuthentification = new Authentification(this);
            checkAuthentification();
        } else {
            //got the saved instance, get the items from savedInstanceState.get..(Id);
            if(mAuthentification == null){
                mAuthentification = new Authentification(this);
            }
            checkAuthentification();
        }
    }
    /**
     * Add the fragment to this activity
     * @param containerId the container id to where to add the fragment
     * @param fragment fragment to add
     */
    protected void addFragment(int containerId, Fragment fragment) {
//        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
//        fragmentTransaction.add(containerId, fragment);
//        fragmentTransaction.commit();
    }

    private void checkAuthentification(){
        AuthenticationState state = AuthenticationManager.get().checkAuthState();

        if(Utils.isNetworkAvailable(getApplicationContext())){
            switch (state) {
                case READY:
                    break;
                case NONE:
                    Log.d(LOG_TAG, "Authentification without login");
                    mAuthentification.authentificateWithoutLoginAsync();
                    break;
                case NEED_REFRESH:
                    Log.d(LOG_TAG, "Refreshing access token");
                    mAuthentification.refreshAccessTokenAsync();
                    break;
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No internet connection. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the auth state
        savedInstanceState.putString(AUTH_STATE, AuthenticationManager.get().checkAuthState().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavBarSize(){
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        return actionBarHeight;
    }

    public static void showLoginAlert(final Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Login");
        dialog.setMessage("You need to login to proceed. Do you want to login?");
        dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Navigator.navigateToLogin(context);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
