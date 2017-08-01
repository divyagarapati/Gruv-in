package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;


public class Invite extends AppCompatActivity {

    public static String InviteCode,EventLocation,EventDate,EventName,line;
    TextView code,Location,name,time,inviteCode;

    private static final String TAG = Invite.class.getSimpleName();
    private GruvPreferences gPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);
        gPrefs=new GruvPreferences(this);
        EventName=getIntent().getExtras().getString("EventName");
        InviteCode=getIntent().getExtras().getString("InviteCode");
        EventLocation=getIntent().getExtras().getString("EventLocation");
        EventDate=getIntent().getExtras().getString("EventTime");
        code=(TextView) findViewById(R.id.codeDisplay_txt);
        Location=(TextView) findViewById(R.id.loc_edt);
        name=(TextView) findViewById(R.id.gruv_edt);
        time=(TextView) findViewById(R.id.time_edt);
        inviteCode=(TextView)findViewById(R.id.invite_txt);
        line=System.getProperty("line.separator");
        Location.setText(EventLocation);
        name.setText(EventName);
        time.setText(EventDate);
        code.setText(InviteCode);
        inviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gruv Invites");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Welcome to GRUV-IN, You've invited by "+gPrefs.getFirstName()+line+"Event Place:"+
                        EventLocation+" "+"Event Name:"+EventName+" "+"Time of the Rave:"+ EventDate+" "+"Your invited code:"+InviteCode);
                try {
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Invite.this, "There are no client installed.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}
