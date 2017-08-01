package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Prasanthi on 21-01-2017.
 */

public class CountDownEvent extends AppCompatActivity {
    private TextView txtLoc, txtTime, txtTitle, loc_value;
    CountDownTimer countDownTimer;
    int hours = 0;
    int Mmin = 0;
    long difference = 0;
    //String dateTime;
    String replcd;
    private GruvPreferences gPrefs;
    public static String EventDateTime;
    public static String EventLocation;
    public static String EventName;
    public static String strplacid;
    File file;
    int j;
    public static String ret;
    public static String[] playlisturl;

    private static final String TAG = CountDownEvent.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_downevent);
        gPrefs = new GruvPreferences(this);
        txtLoc = (TextView) findViewById(R.id.loc_edit);
        txtTime = (TextView) findViewById(R.id.time_edit);
        txtTitle = (TextView) findViewById(R.id.gruv_edit);
        loc_value = (TextView) findViewById(R.id.loc_value);
        checkExternalMedia();
        sendNotify();

        Button Close = (Button) findViewById(R.id.btn_close);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent in = new Intent(CountDownEvent.this, UserHome.class);
                startActivity(in);
            }
        });

    }

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        Log.d(TAG, "\n\nExternal Media: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }

    public void countDown(String datetime) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MMM.dd HH:mm:ss");
            Date Eventdate = simpleDateFormat.parse(datetime);
            Log.d(TAG, "countDownee:" + Eventdate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date currentDate = sdf.parse(replcd);
            Log.d(TAG, "countDowncrn:" + currentDate);
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
                // long days = TimeUnit.MILLISECONDS.toDays(l);
                // l -= TimeUnit.DAYS.toMillis(days);
                // System.out.println("days"+days);
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(l),
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                loc_value.setText(hms);
            }

            @Override
            public void onFinish() {
                loc_value.setText("Finished");
            }
        }.start();
    }

    private void writeToSDFile() {
        File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d(TAG, "\nExternal file system root: " + root);
        if (!root.exists()) {
            root.mkdirs();
        }
        file = new File(root, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.write("");
            pw.close();
            writeToFile(playlisturl[j], CountDownEvent.this);
            f.close();
            readFromFile(CountDownEvent.this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "\n\nFile written to " + file);
    }

    private String readFromFile(Context context) {

        try {
            InputStream inputStream = context.openFileInput("myData.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
               // AudioPlayer.strarr[j] = ret;

                Log.d(TAG, "readFromFile:" + ret.toString());


            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
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

    private void sendNotify() {
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
                        JSONArray feedArray = jObj.getJSONArray("Event Details");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            EventDateTime = feedObj.getString("EventDate");
                            EventLocation = feedObj.getString("EventLocation");
                            EventName = feedObj.getString("EventName");
                            String jsonObject = feedObj.getString("EventDetails");
                            JSONObject jplcobj = new JSONObject(jsonObject);
                            strplacid = jplcobj.getString("placeid");
                            Log.d(TAG, "onResponseplcd:" + strplacid);
                            String jsonObject1 = feedObj.getString("PlayList");
                            System.out.println("in playlst: " + jsonObject1);
                            JSONObject jArray = new JSONObject(jsonObject1);
                            JSONArray sngsArray = new JSONArray(jArray.getString("gruvSongs"));

                            String[] Playlistname = new String[sngsArray.length()];
                            playlisturl = new String[sngsArray.length()];
                            for (int j = 0; j < sngsArray.length(); j++) {
                                JSONObject playObj = (JSONObject) sngsArray.get(j);
                                Playlistname[j] = playObj.getString("songName");
                                playlisturl[j] = playObj.getString("location");
                                Log.d(TAG, "onResponseuri:" + playlisturl[j]);
                                Log.d(TAG, "onResponsename:" + Playlistname[j]);
                                writeToSDFile();

                            }
                        }

                        getDtTme();

                    } else {
                        String des = jObj.getString("Description");
                        loc_value.setText("No Joined Events..");
                        loc_value.setTextSize(22f);
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
                Toast.makeText(getApplicationContext(),
                        "Network Issue", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", "" + gPrefs.getUserId());
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getDtTme() {

        String tag_string_req = "req-timedate";

        String placeid = AppConfig.URL_GETTIMEDATE.replace("tcst", strplacid);
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
                            String dateTm = dateIso.substring(0, 19);
                            String[] exactparts = dateTm.split("T");
                            String strce = exactparts[0] + " " + exactparts[1];
                            replcd = strce.replace("-", ".");
                            Log.d(TAG, "onResponseexact:" + exactparts[0] + exactparts[1]);
                            Log.d(TAG, "onResponse:" + dateTm + exactparts);
                            countDown(EventDateTime);
                        }
                        txtLoc.setText(EventLocation);
                        txtTime.setText(EventDateTime);
                        txtTitle.setText(EventName);

                    } else {

                        Toast.makeText(getApplicationContext(),
                                "no value", Toast.LENGTH_LONG).show();
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
