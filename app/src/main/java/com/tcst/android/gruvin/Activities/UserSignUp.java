package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.tcst.android.gruvin.DBHelper.GruvinDatabaseHelper;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserSignUp extends AppCompatActivity {
    private static final String TAG = "UserSignUp";
    EditText name,password1,email1,confirmPassword,lastname,phonenumber;
    Button signup,login;
    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    GruvinDatabaseHelper gruvinDatabaseHelper;
    String userName,password,data,email,type;
    private GruvPreferences gPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usersign_up_activity);
        setTitle("UserSignUp");
        gruvinDatabaseHelper = new GruvinDatabaseHelper(this);
        gPrefs=new GruvPreferences(this);
        gruvinDatabaseHelper = gruvinDatabaseHelper.open();
        name = (EditText)findViewById(R.id.usr_user_name);
        lastname = (EditText)findViewById(R.id.usr_last_name);
        password1 = (EditText)findViewById(R.id.usr_user_pwd);
        email1 = (EditText)findViewById(R.id.user_user_id);
        phonenumber = (EditText)findViewById(R.id.user_phn_no);
        signup = (Button)findViewById(R.id.user_signup_btn);
        login = (Button)findViewById(R.id.btnLinkToUserLoginScreen);
        confirmPassword=(EditText)findViewById(R.id.user_user_confirmpwd);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 userName = name.getText().toString();
                password = password1.getText().toString();
                email = email1.getText().toString();
                type="user";
                data="{emailid:"+email+"}";
                String strConfirmPassword=confirmPassword.getText().toString();
                if(submitForm()) {
                    if (userName.equals("") || password.equals("")
                            || email.equals("") || strConfirmPassword.equals("")) {

                        Toast.makeText(getApplicationContext(), "Field Vaccant",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!password.equals(strConfirmPassword)) {
                        Toast.makeText(getApplicationContext(),
                                "Password does not match", Toast.LENGTH_LONG)
                                .show();
                    }
                    if (!email.matches(emailPattern)) {
                        email1.setError("Invalid Email address");
                    } else {
                        registerUser();
                        //gruvinDatabaseHelper.insertUserEntry(userName, strPassword);
                        // Toast.makeText(getApplicationContext(),
                        //"Account Successfully Created ", Toast.LENGTH_LONG)
                        // .show();
                        //Log.d(TAG,"Created Successfully"+userName+strPassword+strConfirmPassword+strEmail);
                        //finish();
                        //Intent i = new Intent(UserSignUp.this,LoginUser.class);
                        //startActivity(i);

                    }
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserSignUp.this,LoginUser.class);
                startActivity(i);
            }
        });
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

                        Intent intent = new Intent(UserSignUp.this,
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gruvinDatabaseHelper.close();
    }
    private boolean submitForm() {
        if (!validateFirstName()) {
            return false;
        }

        if (!validateLastName()) {
            return false;
        }

        if (!validateEmail()) {
            return false;
        }
        if (!validatePhone()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        if (!validateConfirmPassword()) {
            return false;
        }
        //  Toast.makeText(getApplicationContext(), "Thank You!Verify Your Details", Toast.LENGTH_SHORT).show();
        return true;
    }


    private boolean validateFirstName() {
        if (name.getText().toString().trim().isEmpty()) {
//            inputFirstName.setError(getString(R.string.err_msg_first_name));
            Toast.makeText(getApplicationContext(), "Enter your first name", Toast.LENGTH_SHORT).show();
            requestFocus(name);
            return false;
        } /*else {
            inputFirstName.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validateLastName() {
        if (lastname.getText().toString().trim().isEmpty()) {
//            inputLastName.setError(getString(R.string.err_msg_last_name));
            Toast.makeText(getApplicationContext(), "Enter your last name", Toast.LENGTH_SHORT).show();
            requestFocus(lastname);
            return false;
        } /*else {
            inputLastName.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validatePhone() {
        if (phonenumber.getText().toString().trim().isEmpty()) {
//            inputPhone.setError(getString(R.string.err_msg_phone));
            Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_SHORT).show();
            requestFocus(phonenumber);
            return false;
        }else if (phonenumber.getText().toString().trim().length()<10) {
//            inputPhone.setError(getString(R.string.err_msg_vaild_phone));
            Toast.makeText(getApplicationContext(), "Enter valid Mobile Number ", Toast.LENGTH_SHORT).show();
            requestFocus(phonenumber);
            return false;
        }else if (phonenumber.getText().toString().trim().length()>10) {
//            inputPhone.setError(getString(R.string.err_msg_vaild_phone));
            Toast.makeText(getApplicationContext(), "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            requestFocus(phonenumber);
            return false;
        }/* else {
            inputPhone.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validateEmail() {
        String Email = email1.getText().toString().trim();

        if (Email.isEmpty() || !isValidEmail(Email)) {
//            inputEmail.setError(getString(R.string.err_msg_email));
            Toast.makeText(getApplicationContext(), "Enter valid Email Address", Toast.LENGTH_SHORT).show();
            requestFocus(email1);
            return false;
        } /*else {
            Toast.setErrorEnabled(false);
        }
*/
        return true;
    }

    private boolean validatePassword() {
        if (password1.getText().toString().trim().isEmpty()) {
//            inputPassword.setError(getString(R.string.err_msg_password));
            Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
            requestFocus(password1);
            return false;
        }else if (password1.getText().toString().trim().length()<5) {
//            inputPassword.setError(getString(R.string.err_msg_vaild_password));
            Toast.makeText(getApplicationContext(), "Atleast 6 characters in password", Toast.LENGTH_SHORT).show();
            requestFocus(password1);
            return false;
        } /*else {
           inputPassword.setErrorEnabled(false);
     }*/

        return true;
    }
    private boolean validateConfirmPassword() {
        if (confirmPassword.getText().toString().trim().isEmpty()) {
           /* inputConfirmPassword.setError(getString(R.string.err_msg_password));*/
            Toast.makeText(getApplicationContext(), "Enter the Confirm password", Toast.LENGTH_SHORT).show();
            requestFocus(confirmPassword);
            return false;
        }else if (confirmPassword.getText().toString().trim().length()<5) {
//            inputConfirmPassword.setError(getString(R.string.err_msg_vaild_password));
            Toast.makeText(getApplicationContext(), "Atleast 6 characters in Confirmpassword", Toast.LENGTH_SHORT).show();
            requestFocus(confirmPassword);
            return false;
        } /*else {
            inputConfirmPassword.setErrorEnabled(false);
        }*/

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
