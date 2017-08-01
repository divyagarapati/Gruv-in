package com.tcst.android.gruvin.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.tcst.android.gruvin.adapter.SearchListAdapter;
import com.tcst.android.gruvin.adapter.SpinnerListAdapter;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static java.util.Calendar.AM;
import static java.util.Calendar.PM;

public class NewSchedule extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        LocationListener, View.OnClickListener {
    private static final String TAG = "NewSchedule";
    private static final int RESULT_AUDIO_SELECTION = 1010;
    TextView btnDatePicker, btnTimePicker;
    private EditText edtDescription;
    private int mYear, mMonth, mDay, mHour, mMinute, mSecond;
    private Button btnPlayList, btnAddEvent;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation, dlocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    SearchView searchView;
    Button add;
    TextView txtAreaName, txtAudioNames;
    private EditText edtEventTitle, details;
    int s_month = 0;
    int s_year = 0, s_date = 0;
    int s_hr = 0, s_min = 0;
    public static Address address;
    SearchListAdapter adapter;
    SpinnerListAdapter spinnerAdapter;
    Cursor mCursor, sCursor;
    String input, Lat, Long;
    private ProgressDialog pDialog;
    private String eventDat, eventTim, eventTitle, strAudioNames, eventDes;
    private JSONObject jod;
    String[] dateexpart, timeexpart;
    public SearchView spinner;
    //List<String> spinnercounlist;
    private GruvPreferences gPrefs;
    String des;
    String spinnerval;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newschedule_activity);
        gPrefs = new GruvPreferences(this);
        TextView txtheader = (TextView) findViewById(R.id.txt_back);
        verifyStoragePermissions(this);
        txtheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
              /*  Intent intent = new Intent(NewSchedule.this, LoginOrganizer.class);
                intent.putExtra("UserId", gPrefs.getUserId());
                startActivity(intent);*/
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        adapter = new SearchListAdapter(this, mCursor, 0);
        spinnerAdapter = new SpinnerListAdapter(this, sCursor, 0);
        searchView = (SearchView) findViewById(R.id.searchview);
        spinner = (SearchView) findViewById(R.id.country_spinner);
        spinner.setSuggestionsAdapter(spinnerAdapter);
        spinner.setIconified(false);
        searchView.setSuggestionsAdapter(adapter);
        searchView.setIconified(false);
        spinnerval = spinner.getQuery().toString();
        spinner.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocations();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                details.setText(cursor.getString(cursor.getColumnIndex("address")));
                Log.d("xxx", "yyyyy:" + cursor.getString(cursor.getColumnIndex("address")));
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        details = (EditText) findViewById(R.id.details);
        btnDatePicker = (TextView) findViewById(R.id.txt_date);
        btnTimePicker = (TextView) findViewById(R.id.txt_time);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        edtEventTitle = (EditText) findViewById(R.id.edt_addeventtitle);
        txtAudioNames = (TextView) findViewById(R.id.txtaudioname);
        edtDescription = (EditText) findViewById(R.id.edt_des);

        btnPlayList = (Button) findViewById(R.id.btn_playlist);
        btnAddEvent = (Button) findViewById(R.id.btn_addevent);
        searchView = (SearchView) findViewById(R.id.searchview);
        add = (Button) findViewById(R.id.btn_add);
        if (isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Please Add your's Event Location", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_LONG).show();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        add.setOnClickListener(this);
        btnPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewSchedule.this, AudioListActivity.class);
                gPrefs.getUserId();
                startActivityForResult(intent, RESULT_AUDIO_SELECTION);
            }
        });
        // latitude and longitude
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitForm()) {
                    setAlarm();
                } else {
                    Toast.makeText(NewSchedule.this, "Please fill Event Details", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @SuppressLint("WrongConstant")
    private String getMonthAbbr(int year, int monthOfYear, int date) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DATE, date);
        Log.d(TAG, "getMonthAbbrtime:" + c.getTime());
        return new SimpleDateFormat("yyyy.MMM.dd").format(c.getTime());
    }

    @SuppressLint("WrongConstant")
    private String getTime(int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        Log.d(TAG, "getTime:" + c.getTime());
        return new SimpleDateFormat("hh:mm aa", Locale.US).format(c.getTime());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        if (v == btnDatePicker && validateDate()) {
            getDtTme();

            // Get Current Date
            //final Calendar c = Calendar.getInstance();
            mYear = /*c.get(Calendar.YEAR);*/Integer.parseInt(dateexpart[0]);
            mMonth =/* c.get(Calendar.MONTH);*/Integer.parseInt(dateexpart[1]) - 1;
            mDay = /*c.get(Calendar.DAY_OF_MONTH);*/Integer.parseInt(dateexpart[2]);
            Log.d(TAG, "onClick:" + dateexpart[0] + dateexpart[1] + dateexpart[2]);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            s_year = year;
                            s_month = monthOfYear;
                            s_date = dayOfMonth;
                            btnDatePicker.setText(getMonthAbbr(year, monthOfYear, dayOfMonth));
                        }
                    }, mYear, mMonth, mDay);
            Log.d(TAG, "onClickdtpicker:" + mYear + mMonth + mDay);
            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
        if (v == btnTimePicker && validateDate()) {
            getDtTme();
            //showPicker();
            // Get Current Time
            mHour = /*c.get(Calendar.HOUR_OF_DAY);*/Integer.parseInt(timeexpart[0]);
            mMinute =/* c.get(Calendar.MINUTE);*/Integer.parseInt(timeexpart[1]);
            mSecond = Integer.parseInt(timeexpart[2]);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            s_hr = hourOfDay;
                            s_min = minute;
                            //btnTimePicker.setText(getTime(hourOfDay, minute));
                            btnTimePicker.setText(getTime(hourOfDay, minute));

                            Log.d(TAG, "onTimeSet:" + btnTimePicker.getText().toString());

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }

       /* } else {
            Toast.makeText(this, "Please Select Your Location", Toast.LENGTH_SHORT).show();
        }*/
        if (v == add) {
            input = searchView.getQuery().toString();
            if (!input.isEmpty()) {

                searchplaces();
            }

        } else {
            Toast.makeText(this, "You will get Automatic TimeZone", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean submitForm() {
        if (!validateEventTitle()) {
            return false;
        }
        if (!validateDate()) {
            return false;
        }

        if (!validateTime()) {
            return false;
        }
        if (!validateAdress()) {
            return false;
        }
        if (!validateAudionames()) {
            return false;
        }
        if (!validateDes()) {
            return false;
        }
        if (!validateSpinner()) {
            return false;
        }
        //  Toast.makeText(getApplicationContext(), "Thank You!Verify Your Details", Toast.LENGTH_SHORT).show();
        return true;
    }
    //Validation

    private boolean validateEventTitle() {
        if (edtEventTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Plz Enter Event Name", Toast.LENGTH_SHORT).show();
            requestFocus(edtEventTitle);
            return false;
        }

        return true;
    }

    private boolean validateDate() {
        if (btnDatePicker.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Your Current Location", Toast.LENGTH_SHORT).show();
            requestFocus(btnDatePicker);
            return false;
        }

        return true;
    }

    private boolean validateTime() {
        if (btnTimePicker.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select your Time", Toast.LENGTH_SHORT).show();
            requestFocus(btnTimePicker);
            return false;
        }

        return true;
    }

    private boolean validateAdress() {
        if (details.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Search your Event Location", Toast.LENGTH_SHORT).show();
            requestFocus(details);
            return false;
        }
        return true;
    }

    private boolean validateSpinner() {
        if (spinner.getQuery().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select your Current Location", Toast.LENGTH_SHORT).show();
            requestFocus(details);
            return false;
        }
        return true;
    }

    private boolean validateAudionames() {
        if ("Audio Names".equals(strAudioNames)) {
            Toast.makeText(getApplicationContext(), "Select atleast One Song", Toast.LENGTH_SHORT).show();
            requestFocus(txtAudioNames);
            return false;
        } else if ("{gruvSongs:[]}".equals(strAudioNames)) {
            Toast.makeText(getApplicationContext(), "Plz Choose your PlayList", Toast.LENGTH_SHORT).show();
            requestFocus(txtAudioNames);
            return false;
        }
        return true;
    }

    private boolean validateDes() {
        if (edtDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Plz Give some Description about Event..", Toast.LENGTH_SHORT).show();
            requestFocus(edtDescription);
            return false;
        }

        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        txtAreaName.setText(marker.getTitle());
        Log.d(TAG, "Marker clik:" + marker.getTitle());

        return false;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @SuppressLint("WrongConstant")
    public void setAlarm() {
        eventTitle = edtEventTitle.getText().toString();
        eventDat = btnDatePicker.getText().toString();
        eventTim = btnTimePicker.getText().toString();
        eventDes = edtDescription.getText().toString();
        jod = new JSONObject();
        try {
            jod.put("gruvDescription", eventDes);
            jod.put("placeid", des);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setAlarm:" + jod);
        addEvents();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(false);
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
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //textAudio display
        if (data != null) {
            Bundle b = data.getExtras();
            String straudioname = b.getString("k1");
            txtAudioNames.setText(Uri.decode(straudioname));
            strAudioNames = txtAudioNames.getText().toString();
        }
        Log.d(TAG, "onCreateAudioNames:" + strAudioNames);

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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
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
                        // mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    private void searchplaces() {
        // Tag used to cancel the request
        String tag_string_req = "req_search";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL + AppConfig.URL_SEARCHPLACES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "search Response: " + response.toString());
                MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "address"});
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error == 200) {
                        JSONArray feedArray = jObj.getJSONArray("Place Details");
                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            // items.add(feedObj.getString("Address"));
                            MatrixCursor.RowBuilder rb = matrixCursor.newRow();
                            rb.add(i);
                            rb.add(feedObj.getString("Address"));
                            Log.d(TAG, "adress" + feedObj);
                            // add spacer cells
                            Lat = feedObj.getString("lat");
                            Long = feedObj.getString("long");
                            System.out.println("Latitude" + Lat);
                            System.out.println("Longitude" + Long);

                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                adapter.swapCursor(matrixCursor);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("input", input);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addEvents() {
        // Tag used to cancel the request
        pDialog.setMessage(" Creating event please wait...");
        showDialog();
        String tag_string_req = "req-addevent";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL + AppConfig.URL_SAVEEVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "AddEvent Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    if (error == 200) {
                        String errorMsg = jObj.getString("Description");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        hideDialog();
                        Intent intent = new Intent(NewSchedule.this,
                                EventListActivity.class);
                        //intent.putExtra("UserId",UserId);
                        gPrefs.getUserId();
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("Description");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "AddEvent Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Network Issue!!!", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventName", eventTitle);
                params.put("location", Lat + "," + Long);
                params.put("eventDate", eventDat + " " + eventTim);
                Log.d("xxxxxxxxxx", "yyyyyyyyyyy:" + strAudioNames);
                params.put("playlist", strAudioNames);
                params.put("eventDetails", jod.toString());
                params.put("userId", gPrefs.getUserId());
                Log.d(TAG, "getParams:" + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void searchLocations() {
        // Tag used to cancel the request
        spinnerval = spinner.getQuery().toString();
        String tag_string_req = "req_search";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL + AppConfig.URL_LOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "searchLocation Response: " + response.toString());
                //MatrixCursor matrixCursor = new MatrixCursor(new String[]{"id", "countryid"});
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error == 200) {
                        //MatrixCursor.RowBuilder rbu = matrixCursor.newRow();
                        // rbu.add(jObj.getString("Description"));
                        des = jObj.getString("Description");
                        Log.d(TAG, "adress" + jObj + des);

                        // add spacer cells
                        getDtTme();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("Description");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                //spinnerAdapter.swapCursor(matrixCursor);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", spinnerval);
                Log.d(TAG, "getParams:" + params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getDtTme() {

        String tag_string_req = "req-timedate";
        String placeid = AppConfig.URL_GETTIMEDATE.replace("tcst", des);
        Log.d(TAG, "getDtTme:" + placeid);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                placeid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "AddDATTIM Response: " + response.toString());

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
                            Log.d(TAG, "onResponseiso:" + dateIso);
                            String dateTm = dateIso.substring(0, 19);
                            String[] exactparts = dateTm.split("T");
                            String strce = exactparts[0] + " " + exactparts[1];
                            String replcd = strce.replace("-", ".");
                            Log.d(TAG, "onResponseexact:" + exactparts[0] + exactparts[1]);
                            Log.d(TAG, "onResponse:" + dateTm + "," + strce + ", final: " + replcd);
                            String[] timedate = replcd.split("\\ ");
                            dateexpart = timedate[0].split("\\.");
                            Log.d(TAG, "onResponsedatepart:" + dateexpart[0] + dateexpart[1] + dateexpart[2]);
                            timeexpart = timedate[1].split("\\:");
                            Log.d(TAG, "onResponsetimpart:" + timeexpart[0] + timeexpart[1] + timeexpart[2]);
                            btnDatePicker.setOnClickListener(NewSchedule.this);
                            btnDatePicker.setText(getMonthAbbr(Integer.parseInt(dateexpart[0]), Integer.parseInt(dateexpart[1]) - 1,
                                    Integer.parseInt(dateexpart[2])));
                            btnTimePicker.setText(getTime(Integer.parseInt(timeexpart[0]), Integer.parseInt(timeexpart[1])));
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No value", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tool, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {
            //finish();s
            Intent intent = new Intent(NewSchedule.this, EventListActivity.class);
            //intent.putExtra("UserId",UserId);
            gPrefs.getUserId();
            startActivity(intent);
        }
       /* else if (id == R.id.btn_logout) {
            gPrefs.setLoginState(false);
            Intent in=new Intent(NewSchedule.this,MainActivity.class);
            startActivity(in);
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
