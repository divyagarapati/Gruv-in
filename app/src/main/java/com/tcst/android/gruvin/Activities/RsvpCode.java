package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TCST05 on 30-12-2016.
 */

public class RsvpCode extends AppCompatActivity {
    EditText code;
    Button btnJoin;
    int UserId;
    String InviteCode_send;
    private static final String TAG = RsvpCode.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_code);
        code = (EditText) findViewById(R.id.edit_code);
        btnJoin =(Button) findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCode_send = code.getText().toString();
                if (!InviteCode_send.isEmpty()) {
                    // login user
                    checkeventbyinvite();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        TextView txtheader=(TextView)findViewById(R.id.txt_back);
        txtheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent=new Intent(RsvpCode.this,UserHome.class);
                intent.putExtra("UserId",UserId);
                startActivity(intent);

            }
        });
    }


    private void checkeventbyinvite() {
        String tag_string_req = "Req_eventbyinvite";
        StringRequest strReq2 = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_EVENTSBYINVITE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Join events Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error==200) {
                        String EventLocation=jObj.getString("EventLocation");
                        String EventName=jObj.getString("EventName");
                        String EventDate=jObj.getString("EventDate");
                        String[] parts=EventDate.split("\\ ");

                        Intent i = new Intent(RsvpCode.this,Invite.class);
                        i.putExtra("EventName",EventName);
                        i.putExtra("EventLocation", EventLocation);
                        i.putExtra("InviteCode",InviteCode_send);
                        i.putExtra("EventTime", parts[1]);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),
                                "success", Toast.LENGTH_LONG).show();
                    }else{
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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Invite Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Network Issue", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("inviteCode",""+InviteCode_send);
                Log.d(TAG,"params: "+UserId+"\t"+InviteCode_send);
                return params;
            }
        };
        strReq2.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq2, tag_string_req);
    }

}
