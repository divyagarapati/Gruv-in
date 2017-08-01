package com.tcst.android.gruvin.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.tcst.android.gruvin.DBHelper.GruvinDatabaseHelper;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginUser extends AppCompatActivity {
    Button login, signup, forgot, forgotuser;
    EditText userId, userPassword;
    String userName, password, type;
    GruvinDatabaseHelper gruvinDatabaseHelper;
    private ProgressDialog pDialog;
    private static final String TAG = LoginUser.class.getSimpleName();
    private GruvPreferences gPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        gPrefs = new GruvPreferences(this);
        userId = (EditText) findViewById(R.id.user_id);
        userPassword = (EditText) findViewById(R.id.user_pwd);
        login = (Button) findViewById(R.id.login_btn);
        signup = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        forgot = (Button) findViewById(R.id.btn_forgot);
        forgotuser = (Button) findViewById(R.id.btn_forgot_user);
        gruvinDatabaseHelper = new GruvinDatabaseHelper(this);
        gruvinDatabaseHelper = gruvinDatabaseHelper.open();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                userName = userId.getText().toString();
                gPrefs.setFirstName(userName);
                password = userPassword.getText().toString();
                type = "user";
                if (submitForm()) {
                    if (!userName.isEmpty() && !password.isEmpty()) {

                        checkLogin();
                        userId.setText("");
                        userPassword.setText("");

                    } else {
                        Toast.makeText(LoginUser.this,
                                "User Name or Password does not match",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginUser.this, UserSignUp.class);
                startActivity(i);
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginUser.this, User_Forgetpwd.class);
                startActivity(i);
            }
        });
        forgotuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginUser.this, ForgetUserName.class);
                startActivity(i);
            }
        });


    }

    private void checkLogin() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");
                    // Check for error node in json
                    if (error == 200) {
                        String UserId = String.valueOf(jObj.getInt("UserId"));
                        gPrefs.setUserId(UserId);
                        gPrefs.setLoginUserState(true);
                        // Launch main activity
                        // sendNotify();
                        Intent intent = new Intent(LoginUser.this, CountDownEvent.class);
                        //intent.putExtra("UserId",UserId);
                        startActivity(intent);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Network Issue!!! Try Again!!!!!", Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("password", password);
                params.put("type", type);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean submitForm() {
        if (!validateFirstName()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }
        //  Toast.makeText(getApplicationContext(), "Thank You!Verify Your Details", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validateFirstName() {
        if (userId.getText().toString().trim().isEmpty()) {
//            inputFirstName.setError(getString(R.string.err_msg_first_name));
            Toast.makeText(getApplicationContext(), "Enter your first name", Toast.LENGTH_SHORT).show();
            requestFocus(userId);
            return false;
        } /*else {
            inputFirstName.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validatePassword() {
        if (userPassword.getText().toString().trim().isEmpty()) {
//            inputPassword.setError(getString(R.string.err_msg_password));
            Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
            requestFocus(userPassword);
            return false;
        } else if (userPassword.getText().toString().trim().length() < 5) {
//            inputPassword.setError(getString(R.string.err_msg_vaild_password));
            Toast.makeText(getApplicationContext(), "Atleast 6 characters in password", Toast.LENGTH_SHORT).show();
            requestFocus(userPassword);
            return false;
        } /*else {
           inputPassword.setErrorEnabled(false);
     }*/

        return true;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
   /* private void sendNotify() {
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
                            String EventDateTime = feedObj.getString("EventDate");
                            String EventLocation = feedObj.getString("EventLocation");
                            String EventName = feedObj.getString("EventName");
                            String jsonObject = feedObj.getString("EventDetails");
                            JSONObject jplcobj = new JSONObject(jsonObject);
                            String strplacid = jplcobj.getString("placeid");
                            gPrefs.setSpinner(strplacid);
                            Log.d(TAG, "onResponseplcd:" + strplacid);
                        }


                    } else {
                        String des = jObj.getString("Description");
                        // Toast.makeText(UserHome.this,des,Toast.LENGTH_SHORT).show();

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
    }*/


}

