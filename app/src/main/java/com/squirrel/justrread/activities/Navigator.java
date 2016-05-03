package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.Intent;

import com.squirrel.justrread.FrontpageFeedActivity;

/**
 * Created by squirrel on 4/24/16.
 * Class for navigation through the application
 */
public class Navigator {
    public Navigator() {
    }

    public void navigateToFrontpageFeed(Context context) {
        if (context != null) {
            Intent intentToLaunch = FrontpageFeedActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    public void navigateToLogin(Context context){
        if(context!= null){
            Intent intentToLaunch = new Intent(context, LoginActivity.class);
            context.startActivity(intentToLaunch);
        }
    }

}
