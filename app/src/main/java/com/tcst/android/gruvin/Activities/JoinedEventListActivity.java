package com.tcst.android.gruvin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tcst.android.gruvin.Data.JoinedListItem;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.EventsJoinedAdapter;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prasanthi on 01-02-2017.
 */

public class JoinedEventListActivity extends AppCompatActivity {
    private static final String TAG = "JoinedEventListActivity";
    private ListView joinedEventsList;
    private ProgressDialog pDialog;
    private GruvPreferences gPrefs;
    private EventsJoinedAdapter eventsJoinedAdapter;
    ArrayList<JoinedListItem> joined_items = new ArrayList<JoinedListItem>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinedevent_list);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        gPrefs = new GruvPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        joinedEventsList = (ListView) findViewById(android.R.id.list);
        gPrefs.getUserId();
        joinedEvents();
    }
    private void joinedEvents() {
        String tag_string_req = "joinedEvents";
        pDialog.setMessage("Loading...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_JOINEDEVENTS, new Response.Listener<String>() {

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
                            String joinedLocation=feedObj.getString("EventLocation");
                            String joinedEventName = feedObj.getString("EventName");
                            String eventDateTime = feedObj.getString("EventDate");
                            String[] partdate=eventDateTime.split("\\ ");
                            joined_items.add(new JoinedListItem(joinedEventName, partdate[0],partdate[1]));
                            Log.d(TAG, "onResponse:");
                        }
                        hideDialog();
                        eventsJoinedAdapter = new EventsJoinedAdapter(JoinedEventListActivity.this,joined_items);
                        joinedEventsList.setAdapter(eventsJoinedAdapter);

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
                Log.e(TAG, "Login Error: " + error.getMessage());
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
                Log.d(TAG, "getParams:"+params);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_joineddone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.joinedDone) {
            finish();
            Intent intent = new Intent(JoinedEventListActivity.this,Schedule.class);
            //intent.putExtra("UserId",UserId);
            gPrefs.getUserId();
            startActivityForResult(intent,0);
        }
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
