package com.squirrel.justrread.activities;

import android.content.Context;
import android.content.Intent;

/**
 * Created by squirrel on 4/24/16.
 * Class for navigation through the application
 */
public class Navigator {
    public Navigator() {
    }

    public static void navigateToFrontpageFeed(Context context) {
        if (context != null) {
            Intent intentToLaunch = FrontpageFeedActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    public static void navigateToLogin(Context context){
        if(context!= null){
            Intent intentToLaunch = new Intent(context, LoginActivity.class);
            context.startActivity(intentToLaunch);
        }
    }

}
