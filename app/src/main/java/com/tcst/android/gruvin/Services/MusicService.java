package com.tcst.android.gruvin.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tcst.android.gruvin.Activities.CountDownActivity;


public class MusicService extends Service {
    public static MediaPlayer player;
    int songCount=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

       // mp=MediaPlayer.create(this, R.raw.sleep);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
       // mp.start();
        //Toast.makeText(this, "onStartMusic", Toast.LENGTH_SHORT).show();
        Log.d("songOnStart","play");
                try {
                    player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setAudioStreamType(Integer.parseInt(CountDownActivity.playlisturl[songCount++]));
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
        }

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
