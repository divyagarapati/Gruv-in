package com.tcst.android.gruvin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tcst.android.gruvin.R;

/**
 * Created by TCST09 on 29-12-2016.
 */

public class SearchListAdapter extends CursorAdapter{
    public SearchListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v=  LayoutInflater.from(context).inflate(R.layout.sample, parent, false);
        TextView txtAddress= (TextView) v.findViewById(R.id.txt);
        txtAddress.setText(cursor.getString(cursor.getColumnIndex("address")));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
