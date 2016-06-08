package com.squirrel.justrread.controllers;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.activities.Navigator;

/**
 * Created by squirrel on 5/22/16.
 */
public class DrawerController {

    private DrawerLayout mDrawerLayout;


    //Drawer CTAs
    private Button mLogin;
    private Button mEditSubreddits;


    public DrawerController(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;

    }

    private void onLoginClick(Context context){
        Navigator.navigateToLogin(context);
    }

    public void initDrawerActions(final Context context){
        mLogin = (Button) mDrawerLayout.findViewById(R.id.drawer_btn_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(context);
            }
        });

        mEditSubreddits = (Button) mDrawerLayout.findViewById(R.id.drawer_edit_subreddits_button);
        mEditSubreddits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.checkUserLoggedIn()){
                    Navigator.navigateToSubredditsSettings(context);
                } else {
                    BaseActivity.showLoginAlert(context);
                }

            }
        });
    }


}
