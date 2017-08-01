package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tcst.android.gruvin.DBHelper.Mail;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;

import java.util.Random;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    EditText name,lastname,pwd,email,confirmPassword,phonenumber;
    String username,password,type,data;
    Button signup,login;
    private int otp;
    Context context = this;
    private GruvPreferences gPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        setTitle("SignUp");
        gPrefs=new GruvPreferences(this);
        name = (EditText)findViewById(R.id.usr_name);
        lastname = (EditText)findViewById(R.id.last_name);
        pwd = (EditText)findViewById(R.id.usr_pwd);
        email = (EditText)findViewById(R.id.user_id);
        phonenumber = (EditText)findViewById(R.id.phn_no);
        type="host";
        signup = (Button)findViewById(R.id.signup_btn);
        login = (Button)findViewById(R.id.btnLinkToLoginScreen);
        confirmPassword=(EditText)findViewById(R.id.user_confirmpwd);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FName = name.getText().toString().trim();
                Email = email.getText().toString().trim();
                Pwd = pwd.getText().toString().trim();
                LName = lastname.getText().toString().trim();
                PhnNum = phonenumber.getText().toString().trim();*/
                username = name.getText().toString().trim();
                password = pwd.getText().toString().trim();
                data = email.getText().toString().trim();
                Random r = new Random( System.currentTimeMillis() );
                otp= 10000 + r.nextInt(20000);
                if(submitForm()) {
                    if (pwd.getText().toString().equals(confirmPassword.getText().toString())) {//Integer.parseInt(otp.getText().toString())==Integer.parseInt(otpvalue.toString())) {

                        try {
                            new Connection().execute();
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                        Intent intent = new Intent(context, Verify_Activity.class);
                        intent.putExtra("firstname",username);
                        intent.putExtra("email",data);
                        intent.putExtra("password",password);
                       /* intent.putExtra("lastname",LName);
                        intent.putExtra("phone",PhnNum);*/
                        intent.putExtra("otp",String.valueOf(otp));
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Thank You!Verify Your Details", Toast.LENGTH_SHORT).show();
                        //resetPassword(email,Password.getText().toString());


                    } else {
//                        System.out.println("otp in else: "+Integer.parseInt(newpwd.toString()));
                        Toast.makeText(getApplicationContext(), "Password doesnot match", Toast.LENGTH_SHORT).show();
//                        inputConfirmPassword.setError(getString(R.string.err_msg_match));
                        requestFocus(confirmPassword);

                    }




                }

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this,LoginUser.class);
                startActivity(i);
            }
        });
    }
    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {

            String frommail="info@tcsofttech.com";

            try {
                Mail mailto =new Mail(frommail,"tcst1234");
                mailto.sendmail("OTP from Gruvin App:"+otp,
                        "Your OTP:"+otp,
                        frommail,
                        data);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }

            return null;
        }

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
        String Email = email.getText().toString().trim();

        if (Email.isEmpty() || !isValidEmail(Email)) {
//            inputEmail.setError(getString(R.string.err_msg_email));
            Toast.makeText(getApplicationContext(), "Enter valid Email Address", Toast.LENGTH_SHORT).show();
            requestFocus(email);
            return false;
        } /*else {
            Toast.setErrorEnabled(false);
        }
*/
        return true;
    }

    private boolean validatePassword() {
        if (pwd.getText().toString().trim().isEmpty()) {
//            inputPassword.setError(getString(R.string.err_msg_password));
            Toast.makeText(getApplicationContext(), "Enter the password", Toast.LENGTH_SHORT).show();
            requestFocus(pwd);
            return false;
        }else if (pwd.getText().toString().trim().length()<5) {
//            inputPassword.setError(getString(R.string.err_msg_vaild_password));
            Toast.makeText(getApplicationContext(), "Atleast 6 characters in password", Toast.LENGTH_SHORT).show();
            requestFocus(pwd);
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
