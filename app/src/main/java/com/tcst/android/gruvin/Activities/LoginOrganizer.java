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
import com.tcst.android.gruvin.DBHelper.SQLiteHandler;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginOrganizer extends AppCompatActivity {
    Button login, signup, forgot, forgotuser;
    EditText userid, Password;
    String userName, password, type;
    public SQLiteHandler db;
    private ProgressDialog pDialog;
    private static final String TAG = SignUp.class.getSimpleName();
    private GruvPreferences gPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("Login");
        gPrefs = new GruvPreferences(this);
        userid = (EditText) findViewById(R.id.user_id);
        Password = (EditText) findViewById(R.id.user_pwd);
        login = (Button) findViewById(R.id.login_btn);
        signup = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        forgot = (Button) findViewById(R.id.btn_forgot);
        forgotuser = (Button) findViewById(R.id.btn_forgot_user);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                userName = userid.getText().toString();
                gPrefs.setFirstName(userName);
                password = Password.getText().toString();
                type = "host";
                if (submitForm()) {
                    if (!userName.isEmpty() && !password.isEmpty()) {
                        // login user
                        checkLogin();

                        userid.setText("");
                        Password.setText("");

                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext(),
                                "Please enter the credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i = new Intent(LoginOrganizer.this,SignUp.class);
                startActivity(i);*/
                Intent i = new Intent(LoginOrganizer.this, PremiumAccountClass.class);
                startActivity(i);

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginOrganizer.this, ForgetPassword.class);
                startActivity(i);
            }
        });
        forgotuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginOrganizer.this, ForgetUserName.class);
                startActivity(i);
            }
        });
    }

    /**
     * function to verify login details in mysql db
     */
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
                        // Now store the user in SQLite
                        String UserId = String.valueOf(jObj.getInt("UserId"));
                        HashMap<String, String> user = db.getUserDetails();
                        // String Userid = user.get("UserId");
                        String Userid = gPrefs.setUserId(UserId);
                        System.out.println("userid :" + Userid);
                        // Launch main activity
                        if (Userid == null) {
                            db.addUser(UserId, userName, password, type);
                        } else if (!UserId.equals(Userid)) {
                            db.addUser(UserId, userName, password, type);
                        }

                        Intent intent = new Intent(LoginOrganizer.this, NewSchedule.class);
                        //intent.putExtra("UserId",UserId);

                        startActivity(intent);
                        gPrefs.setLoginState(true);
                        finish();
                    } else  {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("Description");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
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
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        if (userid.getText().toString().trim().isEmpty()) {
//            inputFirstName.setError(getString(R.string.err_msg_first_name));
            Toast.makeText(getApplicationContext(), "Enter your first name", Toast.LENGTH_SHORT).show();
            requestFocus(userid);
            return false;
        } /*else {
            inputFirstName.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validatePassword() {
        if (Password.getText().toString().trim().isEmpty()) {
//            inputPassword.setError(getString(R.string.err_msg_password));
            Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
            requestFocus(Password);
            return false;
        } else if (Password.getText().toString().trim().length() < 5) {
//            inputPassword.setError(getString(R.string.err_msg_vaild_password));
            Toast.makeText(getApplicationContext(), "Atleast 6 characters in password", Toast.LENGTH_SHORT).show();
            requestFocus(Password);
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
}
