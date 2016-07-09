package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
    private Authentification mAuthentification;

    public static final String FRONTPAGE_FEED_KEY = "FRONTPAGE_FEED";
    public static final String POST_DETAIL_KEY = "POST_DETAIL";
    public static final String SUBREDDIT_ID_KEY = "SUBREDDIT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.prefs_nightmode_key), false)) {
            setTheme(R.style.AppTheme_Dark);
        }

        initialize(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.application_toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
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
     * Check user authentification status. If the user is not authentificated -
     * authetificate without login to be able to use API. If token needs refresh -
     * refresh token.
     */
    public void checkAuthentification(){
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
                Toast.makeText(getApplicationContext(), R.string.no_iternet_connection_text, Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Find height of the status bar
     * @return the height of the status bar
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Find the height of the navigation bar
     * @return the height of the navigation bar
     */
    public int getNavBarSize(){
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return getResources().getDimensionPixelSize(tv.resourceId);
    }

    /**
     * Show dialog which proposes user to login with 2 buttons "Cancel"
     * and "Login". "Login" goes tot he Login activity.
     * @param context
     */
    public static void showLoginAlert(final Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Login");
        dialog.setMessage(context.getString(R.string.dialog_login_message));
        dialog.setPositiveButton(context.getString(R.string.dialog_login_btn_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Navigator.navigateToLogin(context);
            }
        });

        dialog.setNegativeButton(context.getString(R.string.dialog_cancel_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
