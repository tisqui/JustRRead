package com.squirrel.justrread.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

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

    public static void navigateToWebview(Context context, String url){
        if(context!=null && url != null){
            Intent intentToLaunch = new Intent(context, WebActivity.class);
            intentToLaunch.putExtra(WebActivity.EXTRA_URL, url);
            intentToLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentToLaunch);
        }
    }

    public static void navigateToSubredditsSettings(Context context){
        if(context!=null){
            Intent intentToLaunch = new Intent(context, SubscriptionsActivity.class);
            context.startActivity(intentToLaunch);
        }
    }

    public static void sharePostUrlFacebook(String url,  Context context){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        Log.d("Sharing", "Sharing to Facebook");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList)
        {
            if ((app.activityInfo.name).startsWith("com.facebook"))
            {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                context.startActivity(shareIntent);
                break;
            }
        }
        Toast.makeText(context, "Can't share to Facebook, because Facebook app is not installed.", Toast.LENGTH_SHORT).show();
    }

    public static void sharePostTwitter(String text, Context context){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        Log.d("Sharing" , "Sharing to Twitter");

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList)
        {
            if (app.activityInfo.name.contains("twitter")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                context.startActivity(shareIntent);
                break;
            }
        }
        Toast.makeText(context, "Can't share to Twitter, because Twitter app is not installed.", Toast.LENGTH_SHORT).show();
    }

    public static void shareWebUrl(Context context, String url){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, "Share post link via:"));
    }

    public static void navigateToSettings(Context context){
        if(context!= null){
            Intent intentToLaunch = new Intent(context, SettingsActivity.class);
            context.startActivity(intentToLaunch);
        }
    }
}
