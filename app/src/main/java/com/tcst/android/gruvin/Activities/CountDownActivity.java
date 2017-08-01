package com.tcst.android.gruvin.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.Services.NotificationReceiver;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Prasanthi on 21-01-2017.
 */

public class CountDownActivity extends AppCompatActivity {
    private TextView txtLoc, txtTime, txtTitle, loc_value;
    String EventLocations, EventDates, EventNames, InviteCode;
    CountDownTimer countDownTimer;
    int hours = 0;
    int Mmin = 0;
    long difference = 0;
    public static double[] lat, lng;
    public static String EventDateTime, EventLocation;
    public static String[] eventdateTime;
    public static String[] Playlistname;
    public static String[] playlisturl;
    public static Intent[] intents;
    public static PendingIntent[] pendingIntents;
    public static AlarmManager[] alarmManagers;
    public static Calendar[] cn;
    String countTime;
    String[] timeexact;
    String replcd;
    private GruvPreferences gPrefs;
    public static String placid;
    File file;
    JSONArray sngsArray;
    int j;
    int countval = 0;
    String hms;
    private static final String TAG = CountDownActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_down);
        gPrefs = new GruvPreferences(this);
        Button btnClose = (Button) findViewById(R.id.btn_close);

        txtLoc = (TextView) findViewById(R.id.loc_edit);
        txtTime = (TextView) findViewById(R.id.time_edit);
        txtTitle = (TextView) findViewById(R.id.gruv_edit);
        loc_value = (TextView) findViewById(R.id.loc_value);

        countTime = getIntent().getExtras().getString("datetime");
        EventNames = getIntent().getExtras().getString("EventName");
        EventLocations = getIntent().getExtras().getString("EventLocation");
        InviteCode = getIntent().getExtras().getString("InviteCode");
        EventDates = getIntent().getExtras().getString("datetime");

        sendNotify(gPrefs.getUserId());
        getDtTme();
        txtLoc.setText(EventLocations);
        txtTitle.setText(EventNames);
        txtTime.setText(EventDates);
        /// new UserHome().sendNotify(gPrefs.getUserId(),context);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(CountDownActivity.this, Invite.class);
                i.putExtra("EventName", EventNames);
                i.putExtra("EventLocation", EventLocations);
                i.putExtra("InviteCode", InviteCode);
                i.putExtra("EventTime", EventDates);
                startActivity(i);
            }
        });
        //checkExternalMedia();
    }

    public void countDown(String datetime) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MMM.dd HH:mm:ss");
            Date Eventdate = simpleDateFormat.parse(datetime);
            Log.d(TAG, "countDownnn:" + Eventdate + " event date: " + datetime);
            //Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            //String dateTime = sdf.format(calendar.getTime());
            Date currentDate = sdf.parse(replcd);
            Log.d(TAG, "countDowncu:" + currentDate);
            difference = Eventdate.getTime() - currentDate.getTime();
            System.out.println("differnce time: " + difference);
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            Mmin = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            Log.i("log_tag", "Hours: " + hours + ", Mins: " + Mmin);
        } catch (Exception e) {
            Log.i("log_tag exception", "" + e);
        }

        countDownTimer = new CountDownTimer(difference, 1000) {
            @Override
            public void onTick(long l) {

                hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                loc_value.setText(hms);
                if (hms.equals("00:00:05")) {
                    sendNotify(gPrefs.getUserId());
                }

            }

            @Override
            public void onFinish() {
                // loc_value.setText("00:00:00");
                //Log.d(TAG, "onFinish:" + loc_value);
               /* Toast.makeText(CountDownActivity.this,"Sorry..Selected playlist are not ready to play. You are not at zone",
                        Toast.LENGTH_SHORT).show();*/

            }
        }.start();
    }

    public void sendNotify(final String userid) {
        // Tag used to cancel the request
        String tag_string_req = "sendNotify";
        Log.d("sendNotify()", "entered");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL + AppConfig.URL_JOINEDEVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    // Check for error node in json
                    if (error == 200) {
                        //value=1;
                        JSONArray feedArray = jObj.getJSONArray("Event Details");
                        intents = new Intent[feedArray.length()];
                        pendingIntents = new PendingIntent[feedArray.length()];
                        alarmManagers = new AlarmManager[feedArray.length()];
                        cn = new Calendar[feedArray.length()];
                        lat = new double[feedArray.length()];
                        lng = new double[feedArray.length()];
                        eventdateTime = new String[feedArray.length()];
                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            EventDateTime = feedObj.getString("EventDate");
                            eventdateTime[i] = EventDateTime;
                            EventLocation = feedObj.getString("EventLocation");
                            String[] latlngs = EventLocation.split("\\,");
                            String jsonObject = feedObj.getString("PlayList");
                            System.out.println("in playlst: " + jsonObject);
                            JSONObject jArray = new JSONObject(jsonObject);

                            sngsArray = new JSONArray(jArray.getString("gruvSongs"));

                            Playlistname = new String[sngsArray.length()];
                            playlisturl = new String[sngsArray.length()];
                            for (j = 0; j < sngsArray.length(); j++) {
                                JSONObject playObj = (JSONObject) sngsArray.get(j);
                                Playlistname[j] = playObj.getString("songName");
                                playlisturl[j] = playObj.getString("location");
                                Log.d(TAG, "onResponseuri:" + playlisturl[j]);
                                Log.d(TAG, "onResponsename:" + Playlistname[j]);
                                //writeToSDFile();

                            }
                            String jsonObjectid = feedObj.getString("EventDetails");
                            JSONObject jplcobj = new JSONObject(jsonObjectid);
                            placid = jplcobj.getString("placeid");
                            Log.d(TAG, "onResponseplcd:" + placid);
                            lat[i] = Double.parseDouble(latlngs[0]);
                            lng[i] = Double.parseDouble(latlngs[1]);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MMM.dd HH:mm:ss");
                            try {

                                Date date = formatter.parse(EventDateTime);
                                Log.d(TAG, "Event date time: " + EventDateTime);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.dd.HH.mm.ss");
                                String test = simpleDateFormat.format(date);
                                Log.d(TAG, "Event date time: " + test);
                                String[] parts = test.split("\\.");
                                cn[i] = new GregorianCalendar();
                                Log.d(TAG, "onResponse:" + cn[i]);
                                cn[i].clear();
                                cn[i].set(Calendar.YEAR, Integer.parseInt(parts[0]));
                                cn[i].set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
                                cn[i].set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
                                cn[i].set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[3]));
                                cn[i].set(Calendar.MINUTE, Integer.parseInt(parts[4]));
                                cn[i].set(Calendar.SECOND, Integer.parseInt(parts[5]));
                                cn[i].set(Calendar.MILLISECOND, 0);
                                Log.d(TAG, "timeand date:" + cn[i].getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            intents[i] = new Intent(CountDownActivity.this, NotificationReceiver.class);
                            intents[i].putExtra("id", i);
                            pendingIntents[i] = PendingIntent.getBroadcast(CountDownActivity.this, i, intents[i], 0);
                            alarmManagers[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManagers[i].set(AlarmManager.RTC_WAKEUP, cn[i].getTimeInMillis(), pendingIntents[i]);
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("Description");
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
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(CountDownActivity.this,
                        "Network Issue", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", "" + userid);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

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
                            String dateTm = dateIso.substring(0, 19);
                            String[] exactparts = dateTm.split("T");
                            String strce = exactparts[0] + " " + exactparts[1];
                            replcd = strce.replace("-", ".");
                            Log.d(TAG, "onResponseexact:" + exactparts[0] + exactparts[1]);
                            timeexact = exactparts[1].split("\\:");
                            Log.d(TAG, "onResponsetime:" + timeexact[0] + timeexact[1] + timeexact[2]);
                            Log.d(TAG, "onResponse:" + dateIso + "," + dateTm + "," + strce + ", final: " + replcd);
                            countDown(countTime);

                        }


                    } else {
                        // Error in login. Get the error message
//                        String errorMsg = jObj.getString("error");
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

}
