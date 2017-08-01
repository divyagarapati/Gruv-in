package com.tcst.android.gruvin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.tcst.android.gruvin.Data.EventListItem;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.EventOrganizerAdapter;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventListActivity extends AppCompatActivity {
    private static final String TAG = EventListActivity.class.getSimpleName();
    private ListView eventlist;
    private EventOrganizerAdapter eventOrganizerAdapter;
    ArrayList<EventListItem> event_items = new ArrayList<EventListItem>();
    private GruvPreferences gPrefs;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizerevent_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        gPrefs = new GruvPreferences(this);
        eventlist = (ListView) findViewById(android.R.id.list);
        gPrefs.getUserId();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        addEventDetails();
    }

    private void addEventDetails() {
        String tag_string_req = "req-addeventdetails";
        pDialog.setMessage("Loading...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL + AppConfig.URL_GETALLEVENTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, "AddEventDetails Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    if (error == 200) {

                        JSONArray feedArray = jObj.getJSONArray("Event Details");
                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            String eventLocation = feedObj.getString("EventLocation");
                            String eventName = feedObj.getString("EventName");
                            String eventDescription = feedObj.getString("Description");
                            String eventDetails = feedObj.getString("EventDetails");
                            String jsonObject = feedObj.getString("EventDetails");
                            JSONObject jplcobj = new JSONObject(jsonObject);
                            String placid = jplcobj.getString("placeid");
                            Log.d(TAG, "onResponseplcd:" + placid);
                            Log.d(TAG, "onResponseeventdetails:" + eventDetails);
                            String eventId = feedObj.getString("userId");
                            String eventPlaylist = feedObj.getString("PlayList");
                            String eventInvitecode = feedObj.getString("InviteCode");
                            String eventEventId = feedObj.getString("EventID");
                            String eventDateTime = feedObj.getString("EventDate");
                            String[] partdate = eventDateTime.split("\\ ");
                            event_items.add(new EventListItem(eventName, partdate[0], partdate[1], null, null, null));
                            Log.d(TAG, "onResponse:");
                        }
                        hideDialog();
                        eventOrganizerAdapter = new EventOrganizerAdapter(EventListActivity.this, event_items);
                        eventlist.setAdapter(eventOrganizerAdapter);

                        // Check for error node in json
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
                Log.e(TAG, "AddEventDetails Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Network Issue!!!", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miDone) {
            finish();
            gPrefs.getUserId();
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
