package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * Created by TCST09 on 26-12-2016.
 */
public class Verify_Activity extends AppCompatActivity {
    private static final String TAG = Verify_Activity.class.getSimpleName();
    private EditText otp;
    Context context = this;
    public String Email, Pwd, PhnNum, ConPwd, otpvalue,userName,password,type,email,data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("firstname");
            password = extras.getString("password");
            type="host";
            email = extras.getString("email");
            data="{emailid:"+email+"}";

            otpvalue = extras.getString("otp");
            System.out.println("otp value:  " + otpvalue);
            System.out.println("phone" + PhnNum + "password" + Pwd + "yes  " + Email);
            //The key argument here must match that used in the other activity
        }
        otp = (EditText) findViewById(R.id.otp);
        Button btnVerify = (Button) findViewById(R.id.ok_btn);
        // Progress dialog

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitForm()) {
                    if (otpvalue.equals(otp.getText().toString())) {//Integer.parseInt(otp.getText().toString())==Integer.parseInt(otpvalue.toString())) {
                        // System.out.println("heyyyyyyyyy:  "+alias+firstname+lastname+email+password+phoneno);
                        registerUser();
                        //uploadImage();

                    } else {
                        System.out.println("otp in else: " + Integer.parseInt(otpvalue.toString()));
                        Toast.makeText(getApplicationContext(), "Please Enter Valid OTP", Toast.LENGTH_SHORT).show();
//                        inputotp.setError(getString(R.string.err_msg_valid_otp));
                        requestFocus(otp);

                    }
                }
            }
        });
    }
    private boolean submitForm() {

        if (!validateotp()) {
            return false;
        }
        //Toast.makeText(getApplicationContext(), "Thank You!Successfully Registered", Toast.LENGTH_SHORT).show();
        return true;
    }
    private void registerUser() {

        // Tag used to cancel the request
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Signup Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    if (error==200) {
                        String errorMsg = jObj.getString("Description");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Verify_Activity.this,
                                LoginOrganizer.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("Description");
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                System.out.println("respond::::"+error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("password", password);
                params.put("type", type);
                params.put("email", email);
                params.put("data", data);
                return params;
            }

        };

        // Adding request to request queue
       AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private boolean validateotp() {
        System.out.println("otp in validate method: "+Integer.parseInt(otpvalue.toString()));
        if (otp.getText().toString().trim().isEmpty()) {
//            inputotp.setError(getString(R.string.err_msg_otp));
            Toast.makeText(getApplicationContext(), "Please Enter OTP", Toast.LENGTH_SHORT).show();
            requestFocus(otp);
            return false;
        }
        /* else {
            inputotp.setErrorEnabled(false);
        }*/

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}