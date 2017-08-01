package com.tcst.android.gruvin.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tcst.android.gruvin.R;

/**
 * Created by Prasanthi on 04-05-2017.
 */

public class PremiumAccountClass extends AppCompatActivity {
    Button btnOk;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium_page);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        TextView txt = (TextView) findViewById(R.id.txt_infomail);
        btnOk = (Button) findViewById(R.id.btn_ok);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/plain");
                email.putExtra(Intent.EXTRA_EMAIL,new String[]{"info@gruv-in.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Host Account Inquiry");
                startActivity(email);
            }
        });
        /*SpannableString redSpannable= new SpannableString(getResources().getString(R.string.mail));
        redSpannable.setSpan(new ForegroundColorSpan(getColor(R.color.colorAccent)), 0, redSpannable.length(), 0);
        builder.append(redSpannable);
        txt.setText(builder, TextView.BufferType.SPANNABLE);*/
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(PremiumAccountClass.this, MainActivity.class);
                startActivity(in);

            }
        });
    }
}
