package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    Button organizer, user;
    private GruvPreferences gPrefs;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        gPrefs = new GruvPreferences(this);
        organizer = (Button) findViewById(R.id.organiser);
        user = (Button) findViewById(R.id.users);

        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent;
                if (gPrefs.getLoginState()) {
                    mIntent = new Intent(MainActivity.this, NewSchedule.class);
                } else {
                    mIntent = new Intent(MainActivity.this, LoginOrganizer.class);
                }
                startActivity(mIntent);
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in;
                if (gPrefs.getLoginUserState()) {
                    in = new Intent(MainActivity.this, CountDownEvent.class);
                    Log.d(TAG, "yyyyyyyyyyyyy");
                } else {
                    Log.d(TAG, "zzzzzzzzzz");
                    in = new Intent(MainActivity.this, LoginUser.class);
                }
                startActivity(in);
            }
        });


    }



}
