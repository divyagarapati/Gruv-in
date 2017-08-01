package com.tcst.android.gruvin.Activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.Services.NotificationReceiver;

import java.util.concurrent.TimeUnit;


public class AudioPlayer extends AppCompatActivity {
    private static final String TAG = "AudioPlayer";
    private MediaPlayer mp3;
    private ImageView songImage;
    private TextView showTime, txtSongName;
    private Handler mHandler;
    int songCount = 0, songName = 0;
    public NotificationReceiver notificationReceiver;
    private GruvPreferences gPrefs;
    private int new_finalTime, new_startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioplayer);
        gPrefs = new GruvPreferences(this);
        txtSongName = (TextView) findViewById(R.id.txtsongname);
        songImage = (ImageView) findViewById(R.id.song_image);
        showTime = (TextView) findViewById(R.id.txtcurenttime);
        notificationReceiver = new NotificationReceiver();
        mHandler = new Handler();
        new MySongPlay().execute();

    }

    MediaPlayer.OnPreparedListener mLis = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp3.start();
        }
    };
    //static String strarr[] = new String[]{CountDownEvent.ret};

    public void startNextFile() {
        // Log.d(TAG, "startNextFilearr:" + strarr.toString());
        if (CountDownActivity.playlisturl.length/*strarr.length*/ > 0) {
            if (songCount < CountDownActivity.playlisturl.length /*strarr.length*/) {
                try {
                    mp3 = new MediaPlayer();
                    String name = CountDownActivity.Playlistname[songName++];
                    Log.d(TAG, "startNextFile:" + name);
                    mp3.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp3.setDataSource(CountDownActivity.playlisturl[songCount++]);
                    // mp3.setDataSource(strarr[songCount++]);
                    txtSongName.setText(name);
                    mp3.prepareAsync();
                    mp3.setOnPreparedListener(mLis);
                    mHandler.removeCallbacks(UpdateSongTime);
                    mHandler.postDelayed(UpdateSongTime, 0);
                } catch (Exception e) {
                    Log.d("startNextFile()", "" + e);
                }
            } else {
                AudioPlayer.this.finish();
                notificationReceiver.stopGeofenceMonitoring();
                Intent iuserHome = new Intent(AudioPlayer.this, UserHome.class);
                gPrefs.getUserId();
                startActivity(iuserHome);
                mp3.stop();
                mp3.release();
            }
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            try {
                if (mp3 != null && mp3.isPlaying()) {
                    new_startTime = mp3.getCurrentPosition();
                    new_finalTime = mp3.getDuration();
                    showTime.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) new_startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) new_startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) new_startTime)))
                    );
                    notificationReceiver.stopGeofenceMonitoring();
                    /*seekBar.setMax(new_finalTime);
                    seekBar.setProgress(new_startTime);*/
                    mHandler.postDelayed(this, 0);
                } else {
                    //Toast.makeText(AudioPlayer.this, "something went wrong...", Toast.LENGTH_SHORT).show();
                    mHandler.removeCallbacks(UpdateSongTime);
                    mHandler.postDelayed(UpdateSongTime, 0);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }
    };

    private class MySongPlay extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mp3 == null) {
                startNextFile();
            } else if (isCancelled())
                System.exit(0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
