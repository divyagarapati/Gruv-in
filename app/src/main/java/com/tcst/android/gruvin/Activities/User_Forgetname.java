package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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


public class User_Forgetname extends AppCompatActivity {
    private static final String TAG = ForgetUserName.class.getSimpleName();
    Button login;
    private EditText Email;
    private int otp;
    Context context = this;
    public String email,type;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Email = (EditText) findViewById(R.id.email_id);

        Button btnVerify = (Button) findViewById(R.id.Submit_btn);
        // Progress dialog
        btnVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (submitForm()) {
                    email = Email.getText().toString().trim();
                    type = "user";
                    forgetPassword();
                }
            }
        });
    }
    public void forgetPassword() {

        // Tag used to cancel the request
        String tag_string_req = "req_forgetusername";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_FGTUSR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Forgetusername Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    // Check for error node in json
                    if (error==200) {
                        // Launch main activity
                        Intent intent = new Intent(User_Forgetname.this, LoginUser.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Forgotname Error: " + error.getMessage());
                System.out.println("respond::::"+error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("type",type);
                return params;
            }
        };

        // Adding request to request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean submitForm() {

        if (!validateEmail()) {
            return false;
        }


        return true;
    }

    private boolean validateEmail() {
        String email = Email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
//            inputEmail.setError(getString(R.string.err_msg_email));
            Toast.makeText(getApplicationContext(), "Enter valid Email Address", Toast.LENGTH_SHORT).show();
            requestFocus(Email);
            return false;
        } /*else {
            Toast.setErrorEnabled(false);
        }
*/
        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }




}