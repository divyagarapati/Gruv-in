package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TCST05 on 29-12-2016.
 */

public class UserHome extends AppCompatActivity {
    Button find, rsvp, host;
    public static int UserId;
    public static GoogleApiClient googleApiClient = null;
    private static final String TAG = UserHome.class.getSimpleName();
    private GruvPreferences gPrefs;
    public static int value = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_join_screen);
        gPrefs = new GruvPreferences(this);
        find = (Button) findViewById(R.id.find);
        rsvp = (Button) findViewById(R.id.rsvp);
        host = (Button) findViewById(R.id.host);
        //btnLogout=(Button)findViewById(R.id.user_logout);
        gPrefs.getUserId();
        //UserId = getIntent().getExtras().getInt("UserId");
        //Log.d("userId: ", "" + UserId);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this, Schedule.class);
                gPrefs.getUserId();
                startActivity(i);

            }
        });
        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this, RsvpCode.class);
                startActivity(i);
            }
        });
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this, NewSchedule.class);
                i.putExtra("UserId", UserId);
                startActivity(i);
            }
        });
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("GOOGLEAPICLIENT:", "connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("GOOGLEAPICLIENT:", "connection suspended");

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("GOOGLEAPICLIENT:", "connection failed");

                    }
                })
                .build();

    }

    ///To ensure that Google Play Services are avilable
    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d("GOOGLEPLAYSERVICES:", "GooglePlayServices are not Avaliable  so showing Dialog");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d("GOOGLEPLAYSERVICES:", "GoogleApiClientServices are  Avaliable no need to show Dialog");

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }




}
