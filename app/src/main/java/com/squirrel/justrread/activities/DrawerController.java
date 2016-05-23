package com.squirrel.justrread.activities;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

import com.squirrel.justrread.R;

/**
 * Created by squirrel on 5/22/16.
 */
public class DrawerController {

    //Drawer CTAs
    private Button mLogin;

    public DrawerController() {}

    private void onLoginClick(Context context){
        Navigator.navigateToLogin(context);
    }

    public void initDrawerActions(final Context context, DrawerLayout drawerLayout){
        mLogin = (Button) drawerLayout.findViewById(R.id.drawer_btn_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(context);
            }
        });
    }
}
