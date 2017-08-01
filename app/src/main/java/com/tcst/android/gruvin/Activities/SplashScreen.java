package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;


/**
 * Created by Prasanthi on 20-01-2017.
 */
public class SplashScreen extends AppCompatActivity {
    private GruvPreferences gPrefs;
    private Context mContext;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mContext=this;
        gPrefs=new GruvPreferences(mContext);
        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(1*2000);
                    if(gPrefs.getLoginState()){
                        mIntent=new Intent(mContext,NewSchedule.class);
                    }else if (gPrefs.getLoginState()){
                        mIntent=new Intent(mContext,UserHome.class);
                    }
                    else
                    {
                        mIntent=new Intent(mContext,MainActivity.class);

                    }

                    startActivity(mIntent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();


    }

}
