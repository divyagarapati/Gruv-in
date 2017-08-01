package com.tcst.android.gruvin.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by TCST06 on 12-Jan-17.
 */

public class GeofenceActivity extends Activity
{
   // GoogleApiClient googleApiClient;

    public static final String GeofenceId = "GEO_ID";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate geofanceactivity", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);

       // startGeofenceMonitoring();
    }


}
