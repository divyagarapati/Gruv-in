package com.tcst.android.gruvin.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tcst.android.gruvin.Activities.AudioPlayer;
import com.tcst.android.gruvin.Data.AudioItem;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class AudioListAdapter extends ArrayAdapter<AudioItem> {
    private static final String TAG = "AudioListAdapter";
    private Context context;
    public static MediaPlayer mp3;
    private boolean[] cbState;
    private ArrayList<AudioItem> items;
    private ArrayList<AudioItem> selItemList;
    public static String[] txtTitle;
    public static String[] play;
    private static String[] txtDuration;
    public static String[] songId;
    private int i = 0, j = 0, k = 0, l = 0;
    private GruvPreferences gPrefs;
    private String audioPath;
    private AudioItem rowItem = null;
    private ProgressDialog progressDialog;

    public AudioListAdapter(Context context, ArrayList<AudioItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        selItemList = items;
        txtTitle = new String[items.size()];
        cbState = new boolean[items.size()];
        txtDuration = new String[items.size()];
        play = new String[items.size()];
        songId = new String[items.size()];

    }

    /*private view holder class*/
    private static class ViewHolder {
        TextView txtTitle, txtDuration;
        Button play, pause;
        CheckBox state;
        String songId;
    }

    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        rowItem = this.getItem(position);
        gPrefs = new GruvPreferences(context);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.audiolist_adapter, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.textSeparator);
            holder.play = (Button) convertView.findViewById(R.id.play);
            holder.pause = (Button) convertView.findViewById(R.id.pause);
            holder.txtDuration = (TextView) convertView.findViewById(R.id.txtduration);
            holder.state = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.state.setChecked(selItemList.get(position).isSelected());
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mp3 != null) {
                        mp3.release();
                    }
                    mp3 = new MediaPlayer();
                    mp3.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    new auiofile().execute();

                    mp3.start();
                    holder.play.setVisibility(View.GONE);
                    holder.pause.setVisibility(View.VISIBLE);
                }
            });
            holder.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mp3.pause();
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                }
            });
            if (rowItem != null) {
                holder.txtTitle.setText(rowItem.getAudioname());
                holder.txtDuration.setText(rowItem.getAudioDuration());
                holder.state.setChecked(rowItem.isSelected());
                holder.songId = rowItem.getSongid();
                holder.state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        if (cb.isChecked()) {
                            cbState[position] = true;
                            AudioItem ims = items.get(position);
                            Log.d(TAG, "onClicks:"+position);
                            ims.setSelected(true);
                            txtTitle[i++] = selItemList.get(position).getAudioname();
                            play[j++] = selItemList.get(position).getAudioUrl();
                            txtDuration[k++] = selItemList.get(position).getAudioDuration();
                            songId[l++] = selItemList.get(position).getSongid();

                        } else {
                            cbState[position] = false;
                            AudioItem im = items.get(position);
                            im.setSelected(false);
                            txtTitle[--i] = null;
                            play[--j] = null;
                            txtDuration[--k] = null;
                            songId[--l] = null;
                        }
                    }

                });
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;

    }

    private class auiofile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setIcon(R.drawable.play);
            progressDialog.setTitle("Connecting to Server");
            progressDialog.setMessage("Please Wait Getting your playlist from Server....");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            audioPath = rowItem.audioUrl;
            try {
                mp3.setDataSource(audioPath);
                mp3.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }


}
