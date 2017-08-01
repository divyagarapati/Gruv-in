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
 * Created by Prasanthi on 18-07-2017.
 */

public class SpinnerListAdapter extends CursorAdapter {


    public SpinnerListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v=  LayoutInflater.from(context).inflate(R.layout.spinner_value, parent, false);
        TextView txtAddress= (TextView) v.findViewById(R.id.txt_value);
        txtAddress.setText(cursor.getString(cursor.getColumnIndex("countryid")));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
