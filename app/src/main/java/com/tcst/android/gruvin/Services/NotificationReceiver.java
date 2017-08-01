package com.tcst.android.gruvin.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tcst.android.gruvin.Activities.CountDownActivity;
import com.tcst.android.gruvin.Activities.UserHome;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TCST06 on 25-Nov-16.
 */

public class NotificationReceiver extends BroadcastReceiver {
    LocationRequest locationRequest;
    Context context;
    public static int id;
    public static int songPlayCondition = 0;
    public String errormsg;
    private GruvPreferences gPrefs;
    public static final String GeofenceId = "GEO_ID";
    public static String TAG = "NotificationReceiver";

    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {

        id = intent.getExtras().getInt("id");
        System.out.println(" id :" + id);
        this.context = context;
        gPrefs = new GruvPreferences(context);

        getDtTme();

    }

    public void startLocationMonitoring() {
        try {
            locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(UserHome.googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }
            });
        } catch (Exception e) {
            Log.d("startLocationMonitoring", "" + e);
        }
    }

    public void startGeofenceMonitoring() {
        startLocationMonitoring();
        Geofence geofence = new Geofence.Builder()
                .setRequestId(GeofenceId)
                .setCircularRegion(CountDownActivity.lat[id], CountDownActivity.lng[id], 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();
        Intent i = new Intent(context, GeofenceService.class);
        PendingIntent pi = PendingIntent.getService(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!UserHome.googleApiClient.isConnected()) {
            Log.d("startGeofenceMonitoring", "Google Api Client Not Connected");
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.GeofencingApi.addGeofences(UserHome.googleApiClient, geofencingRequest, pi)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("Geofence....", "Geofence added Successfully");
                            } else {
                                Log.d("Geofence....", "Geofence not added ");
                            }
                        }
                    });
        }
    }

    private boolean checktimings(String time, String endtime) {
        boolean ret = false;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date startDate = simpleDateFormat.parse(time);
            Date endDate = simpleDateFormat.parse(endtime);
            long difference = endDate.getTime() - startDate.getTime();
            Log.d(TAG, "checktimings:" + time + "," + endtime);
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int Mmin = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            int sSec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * Mmin)) / (1000);
            Log.i("log_tag", "Hours: " + hours + ", Mins: " + Mmin + ",seconds:" + sSec);
            if (hours == 1 && Mmin > 0) {
                ret = false;
            } else if (hours == 0)
                ret = false;
            else if (hours == 1 && Mmin == 0)
                ret = true;
        } catch (Exception e) {
            Log.i("log_tag exception", "" + e);
            Toast.makeText(context, "Sorry..Selected playlist are not ready to play. You are not at zone", Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    public void stopGeofenceMonitoring() {
        ArrayList<String> geofenceIDs = new ArrayList<String>();
        geofenceIDs.add("GEO_ID");
        LocationServices.GeofencingApi.removeGeofences(UserHome.googleApiClient, geofenceIDs);
        Log.d("stopGeofenceMonitoring:", "stopped");
    }

    String[] eventParts, eventTimeParts;

    private void getDtTme() {
        String tag_string_req = "req-timedate";
        String placeid = AppConfig.URL_GETTIMEDATE.replace("tcst", gPrefs.getSpinner());
        Log.d(TAG, "getDtTme:" + placeid);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                placeid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "AddDATTIM Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("version");

                    // Check for error node in json
                    if (error == 2) {

                        JSONArray feedArray = jObj.getJSONArray("locations");
                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);

                            JSONObject object = feedObj.getJSONObject("time");
                            String dateIso = object.getString("iso");
                            String dateTm = dateIso.substring(0, 18);

                            String[] exactparts = dateTm.split("T");
                            String strce = exactparts[0] + " " + exactparts[1];
                            String replcd = strce.replace("-", ".");
                            Log.d(TAG, "onResponseexact:" + exactparts[0] + "" + exactparts[1]);
                            Log.d(TAG, "onResponse:" + dateTm + "final:" + replcd);
                            eventParts = CountDownActivity.eventdateTime[id].split("\\ ");
                            SimpleDateFormat simpleD = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                            Date rplcfinal = simpleD.parse(replcd);
                            SimpleDateFormat simplDat = new SimpleDateFormat("yyyy.MMM.dd HH:mm:ss");
                            Log.d(TAG, "onResponserplc:" + rplcfinal);
                            String rplc = simplDat.format(rplcfinal);
                            Log.d(TAG, "onResponserplcfina:" + rplc);
                            String[] currentParts = rplc.split("\\ ");
                            eventTimeParts = eventParts[1].split("\\:");
                            Log.d(TAG, "eventTimeParts[1]:" + eventParts[1] + eventTimeParts[0] + ":" + eventTimeParts[1]);
                            String[] currentTimeParts = currentParts[1].split("\\:");
                            Log.d(TAG, "currentTimeParts[1]:" + currentTimeParts[0] + ":" + currentTimeParts[1]);
                            Log.d(TAG, "onResponseparts:" + eventParts[0] + currentParts[0]);
                            if (eventParts[0].equals(currentParts[0])) {
                                Log.d("Date Matched", "entered");
                                Log.d(TAG, "onResponsetimematchs:" + eventTimeParts[0] + ":" + eventTimeParts[1] + "," + currentTimeParts[0] + ":" + currentTimeParts[1]);
                                if ((eventTimeParts[0] + ":" + eventTimeParts[1]).equals(currentTimeParts[0] + ":" + currentTimeParts[1])) {
                                    songPlayCondition = 1;
                                    Log.d("Current D&T", "" + songPlayCondition);
                                    Log.d("Current Date and Time", "entered");
                                    startGeofenceMonitoring();
                                    Log.d(TAG, "onResponsecure:" + currentParts[1] + "," + eventParts[1]);
                                } else if (checktimings((currentParts[1]), (eventParts[1]))) {
                                    Log.d("1hr before Notification", "entered");
                                }

                            }

                        }

                    } else {
                        // Error in login. Get the error message
                        String dateTim = jObj.getString("time");
                        Toast.makeText(context, dateTim, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException | ParseException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());

            }
        });
        strReq.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}
