package com.tcst.android.gruvin.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.tcst.android.gruvin.Activities.AudioPlayer;
import com.tcst.android.gruvin.Activities.CountDownActivity;
import com.tcst.android.gruvin.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by TCST04 on 07-01-2017.
 */

public class GeofenceService extends IntentService
{
    public GeofenceService()
    {
        super("GeofenceService");

    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        GeofencingEvent event= GeofencingEvent.fromIntent(intent);
        if (event.hasError())
        {

        }
        else
        {
            int transition=event.getGeofenceTransition();
            List<Geofence> geofences=event.getTriggeringGeofences();
            Geofence geofence=geofences.get(0);
            String reqId=geofence.getRequestId();
            if (transition== Geofence.GEOFENCE_TRANSITION_ENTER)
            {
                Log.d("GEogence Enter","===Enter");
               // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification no=new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.mipmap.ic_launcher))
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentTitle("At event Location")
                        .setContentText("You have Scheduled an event at.....")
                        .build();

                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                nm.notify(101,no);

               if (NotificationReceiver.songPlayCondition!=0) {
                   Log.d("songPlayCondn in GRecv",""+NotificationReceiver.songPlayCondition);
                   Intent in=new Intent(this, AudioPlayer.class);
                   in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(in);
                   NotificationReceiver.songPlayCondition=0;
                }
            }

        }
    }
}

