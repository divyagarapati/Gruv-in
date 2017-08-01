package com.tcst.android.gruvin.Activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


public class Schedule extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    private static final String TAG = "Schedule";
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation, dlocation;
    Marker mCurrLocationMarker, previousMarker;
    LocationRequest mLocationRequest;
    String[] EventAddress;
    String[] Eventname;
    String[] Game;
    String[] EventDateTime;
    String[] InviteCode;
    private ProgressDialog pDialog;
    public static String InviteCode_send, EventName, EventLocation, EventDate;
    TextView txtEventName, txtEventLocation, txtEventTime, txt_gamename, txtJoined;
    public double slatt, dlatt;
    public double slng, dlng;
    public int radius = 100;
    MarkerOptions markerOptions;
    Button join;
    LocationManager locationManager;
    private GruvPreferences gPrefs;
    public static String[] parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_location);
        gPrefs = new GruvPreferences(this);
        TextView txtheader = (TextView) findViewById(R.id.txt_back);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        txtheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Schedule.this, UserHome.class);
                //intent.putExtra("UserId",UserId);
                gPrefs.getUserId();
                startActivity(intent);

            }
        });
        txtJoined = (TextView) findViewById(R.id.txt_joined);
        txtJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Schedule.this, JoinedEventListActivity.class);
                startActivity(in);
            }
        });

        txtEventName = (TextView) findViewById(R.id.eventName);
        txtEventLocation = (TextView) findViewById(R.id.txt_eventloac);
        txtEventTime = (TextView) findViewById(R.id.time_text);
        txt_gamename = (TextView) findViewById(R.id.txt_gamename);
        join = (Button) findViewById(R.id.join);
        gPrefs.getUserId();
       /* UserId=getIntent().getExtras().getInt("UserId");
        Log.d("userId: ",""+UserId);*/
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinEvent();

            }
        });
        if (NewSchedule.isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Please join the event", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady:");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS not found");  // GPS not found
            builder.setMessage("You have to turn on location permission on your device?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
            return;
            //Toast.makeText(this, "Please enable your GPS", Toast.LENGTH_SHORT).show();
        }
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                if (previousMarker != null) {
                    previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.currentmarker));
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon));
                previousMarker = marker;
                for (int i = 0; i < Eventname.length; i++) {
                    if (title.equals(Eventname[i])) {
                        txtEventLocation.setText("Location : " + EventAddress[i]);
                        txtEventTime.setText("Date : " + EventDateTime[i]);
                        txt_gamename.setText("Game #" + Game[i]);
                        txtEventName.setText("Gruv : " + Eventname[i]);
                        InviteCode_send = InviteCode[i];
                        // break;
                    }

                }

                return true;
            }
        });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        dlocation = location;
        slng = location.getLongitude();
        slatt = location.getLatitude();
        System.out.println("latlng : " + slatt + "\t" + slng);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        getEvents();
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        String test = "" + latLng.latitude + latLng.longitude;
        //  Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
    }

    private void drawMarker(LatLng point) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    private void AddMarker() {

        if (EventAddress != null) {
            //Toast.makeText(this, "Inside if", Toast.LENGTH_SHORT).show();
            Log.d("when result not zero ", "" + EventAddress.length);
            for (int i = 0; i < EventAddress.length; i++) {
                String[] parts = EventAddress[i].split("\\,");
                dlatt = Double.parseDouble(parts[0]);
                dlng = Double.parseDouble(parts[1]);
                Log.d("when result not zero", "" + dlatt + dlng);
                LatLng LL = new LatLng(dlatt, dlng);
                if (i + 1 == EventAddress.length) {
                    markerOptions = new MarkerOptions().position(LL).title(Eventname[i]);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    previousMarker = mCurrLocationMarker;

                } else {
                    markerOptions = new MarkerOptions().position(LL).title(Eventname[i]);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.currentmarker));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                }
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LL));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                txtEventLocation.setText("Location : " + EventAddress[i]);
                txtEventTime.setText("Date : " + EventDateTime[i]);
                txt_gamename.setText("Game #" + Game[i]);
                txtEventName.setText("Gruv : " + Eventname[i]);
                InviteCode_send = InviteCode[i];
            }
        } else {
            // Toast.makeText(this, "in else", Toast.LENGTH_SHORT).show();
            Log.d("when result zero", "0");
            LatLng latLng = new LatLng(slatt, slng);
            markerOptions = new MarkerOptions().position(latLng).title("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.currentmarker));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }

    }

    private void getEvents() {
        // Tag used to cancel the request
        String tag_string_req = "req_getevents";
        pDialog.setMessage("Loading...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_searchEventDetails, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "events Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error == 200) {
                        JSONArray feedArray = jObj.getJSONArray("Event Details");
                        EventAddress = new String[feedArray.length()];
                        Eventname = new String[feedArray.length()];
                        Game = new String[feedArray.length()];
                        EventDateTime = new String[feedArray.length()];
                        InviteCode = new String[feedArray.length()];

                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);

                            EventAddress[i] = feedObj.getString("EventLocation");
                            Eventname[i] = feedObj.getString("EventName");
                            Game[i] = feedObj.getString("EventID");
                            EventDateTime[i] = feedObj.getString("EventDate");
                            Log.d(TAG, "onResponseevent:" + EventDateTime[i]);
                            InviteCode[i] = feedObj.getString("InviteCode");
                            System.out.println("address from api: " + EventAddress[i]);
                            System.out.println("Address" + EventAddress);
                        }
                        hideDialog();
                        AddMarker();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("Description");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "schedule Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Network Issue!!!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", "" + slatt);
                params.put("lon", "" + slng);
                params.put("radius", "" + radius);
                params.put("userId", "" + gPrefs.getUserId());
                Log.d(TAG, "params: " + slatt + "\t" + slng + "\t" + radius);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void writeToSDFile() {
        File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d(TAG, "\nExternal file system root: " + root);
        //File dir = new File(root.getAbsolutePath() + "/download");
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(root, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);

            //writeToFile(playlisturl, CountDownActivity.this);
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "\n\nFile written to " + file);
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("myData.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void joinEvent() {
        String tag_string_req = "Join_events";
        pDialog.setMessage("Loading...");
        showDialog();
        StringRequest strReq2 = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_JOINTEVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Join events Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error == 200) {
                        EventLocation = jObj.getString("EventLocation");
                        EventName = jObj.getString("EventName");
                        EventDate = jObj.getString("EventDate");
                        String jsonObject = jObj.getString("EventDetails");
                        JSONObject jplcobj = new JSONObject(jsonObject);
                        String placid = jplcobj.getString("placeid");
                        Log.d(TAG, "onResponseplcd:" + placid);
                        gPrefs.setSpinner(placid);
                        Log.d(TAG, "onResponse:" + EventDate);
                        hideDialog();
                        parts = EventDate.split("\\ ");
                        finish();
                        Intent i = new Intent(Schedule.this, CountDownActivity.class);
                        i.putExtra("EventName", EventName);
                        i.putExtra("EventLocation", EventLocation);
                        i.putExtra("InviteCode", InviteCode_send);
                        i.putExtra("EventTime", parts[0] + parts[1]);
                        i.putExtra("datetime", EventDate);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),
                                "success", Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("Description");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Invite Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Network Issue", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", "" + gPrefs.getUserId());
                params.put("inviteCode", "" + InviteCode_send);
                Log.d(TAG, "params: " + gPrefs.getUserId() + "\t" + InviteCode_send);
                return params;
            }
        };
        strReq2.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq2, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_joined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_joined) {
            finish();
            Intent intent = new Intent(Schedule.this, JoinedEventListActivity.class);
            //intent.putExtra("UserId",UserId);
            gPrefs.getUserId();
            startActivityForResult(intent, 0);
        }
        /*else if (id == R.id.miProfile) {
            Intent launchNewIntent = new Intent(NewSchedule.this, LoginOrganizer.class);
            startActivityForResult(launchNewIntent, 0);
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}


