package com.tcst.android.gruvin.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tcst.android.gruvin.Data.JoinedListItem;
import com.tcst.android.gruvin.R;

import java.util.ArrayList;

/**
 * Created by Prasanthi on 01-02-2017.
 */

public class EventsJoinedAdapter extends ArrayAdapter<JoinedListItem> {
    private static final String TAG ="EventsJoinedAdapter" ;
    Context mContext;
    ArrayList<JoinedListItem> joinedListItems;


    public EventsJoinedAdapter(Context mContext,ArrayList<JoinedListItem> joinedListItems) {
        super(mContext,0, joinedListItems);
        this.mContext = mContext;
        this.joinedListItems=joinedListItems;
    }
    private static class ViewHolder {
        TextView txtTitle,txtDate,txtTime;

    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final JoinedListItem rowItem = this.getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.content_joinedeventlist_adapter,null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtjoinedevent);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_joineventdate);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txt_joineventtime);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtTitle.setText(rowItem.getJoineventtitle());
        holder.txtDate.setText(rowItem.getJoineventdate());
        holder.txtTime.setText(rowItem.getJoineventtime());
        return convertView;
    }
}
