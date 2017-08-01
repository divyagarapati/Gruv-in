package com.tcst.android.gruvin.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tcst.android.gruvin.Data.AudioItem;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.AudioListAdapter;
import com.tcst.android.gruvin.app.AppConfig;
import com.tcst.android.gruvin.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Prasanthi on 28-12-2016.
 */
public class AudioListActivity extends Activity{

    private static final String TAG = "AudioListActivity";
    private ListView mainList;
    private ProgressDialog pDialog;
    AudioListAdapter mAudiolistadapter=null;
    private Button btnOk;
    private String UserId;
    ArrayList<AudioItem> right_items=new ArrayList<AudioItem>();
    private GruvPreferences gPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        gPrefs=new GruvPreferences(this);
        btnOk = (Button) findViewById(R.id.btn_ok);
        mainList = (ListView) findViewById(android.R.id.list);
        gPrefs.getUserId();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        selectAudio();

        setListViewHeightBasedOnChildren(mainList);
        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name=right_items.get(position).getAudioname();
                boolean res=right_items.get(position).isSelected();
                Toast.makeText(AudioListActivity.this, name+" selected:"+res, Toast.LENGTH_SHORT).show();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < right_items.size(); i++) {
                    AudioItem audioItem = right_items.get(i);
                    if (audioItem.isSelected())
                        sb.append(audioItem.getAudioname());
                    Log.d(TAG, "onClickAudio:" + audioItem.getAudioname());
                }
                Intent in = new Intent(AudioListActivity.this, NewSchedule.class);

                gPrefs.getUserId();
                try {
                    JSONObject jsonObject=new JSONObject();
                    JSONArray ja=new JSONArray();
                    for (int m = 0; m < mAudiolistadapter.txtTitle.length; m++) {
                        if(mAudiolistadapter.txtTitle[m]!=null&&mAudiolistadapter.play[m]!=null) {
                            JSONObject job = new JSONObject();
                            try {

                                job.put("location", mAudiolistadapter.play[m]);
                                job.put("songName", mAudiolistadapter.txtTitle[m]);
                                job.put("songId",mAudiolistadapter.songId[m]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ja.put(job);
                        }
                        jsonObject.put("gruvSongs",ja);
                        Log.d(TAG, "onClickNamePrior:" + mAudiolistadapter.txtTitle[m]);
                    }
                    in.putExtra("k1",Uri.encode(jsonObject.toString()));
                    setResult(Activity.RESULT_OK,in);

                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
}

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight;
        totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void selectAudio() {
        pDialog.setMessage("Loading...");
        showDialog();
        String tag_string_req = "req-addaudio";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASEURL+AppConfig.URL_GETAUDIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "AddAudio Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("Status");

                    // Check for error node in json
                    if (error==200) {

                        JSONArray feedArray = jObj.getJSONArray("gruvSongs");
                        for (int i = 0; i < feedArray.length(); i++) {

                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            String SongName = feedObj.getString("songName");
                            String Duration = feedObj.getString("duration");
                            String SongUrl = feedObj.getString("location");
                            String SongId = feedObj.getString("songId");

                            right_items.add(new AudioItem(Duration, SongUrl,SongName,SongId, false));

                            Log.d(TAG, "onResponse:"+Duration+SongUrl+SongName+SongId);
                        }
                        hideDialog();
                        mAudiolistadapter = new AudioListAdapter(AudioListActivity.this, right_items);
                        mainList.setAdapter(mAudiolistadapter);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Select atleast one Song", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "AddAudio Error: " + error.getMessage());
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Select atleast one song", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }


        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
@Override
public void onBackPressed() {

    super.onBackPressed();
    if(AudioListAdapter.mp3!=null) {
        AudioListAdapter.mp3.pause();
    }
}
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

