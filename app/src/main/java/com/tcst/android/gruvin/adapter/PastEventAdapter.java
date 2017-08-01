package com.tcst.android.gruvin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tcst.android.gruvin.Data.EventAddDatabaseList;
import com.tcst.android.gruvin.R;


public class PastEventAdapter extends CursorAdapter {
    private static final String TAG ="PastEventAdapter" ;
    private Context mContext;
    private LayoutInflater vi;
    private String date,time;
    public static TextView txtEventName,txtEventDate,txtEventTime;
    public PastEventAdapter(Context mContext, Cursor c) {
        super(mContext, c,0);
        this.mContext=mContext;
        vi=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.content_pastevent_list,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(TAG, "count:" + cursor.getCount());
        txtEventName = (TextView) view.findViewById(R.id.txteventtitle);
        txtEventDate = (TextView) view.findViewById(R.id.txt_eventdate);
        txtEventTime = (TextView) view.findViewById(R.id.txt_eventtime);
        txtEventName.setText(cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_NAME)));

        date = cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_DATE));
        txtEventDate.setText(date);
        Log.d(TAG, "Event Date:" + date);

        time = cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_TIME));
        txtEventTime.setText(time);

    }
}
