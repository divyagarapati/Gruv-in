package com.tcst.android.gruvin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tcst.android.gruvin.Activities.Invite;
import com.tcst.android.gruvin.Data.EventListItem;
import com.tcst.android.gruvin.Preferences.GruvPreferences;
import com.tcst.android.gruvin.R;

import java.util.ArrayList;


public class EventOrganizerAdapter extends ArrayAdapter<EventListItem> {
    private static final String TAG ="EventOrganizerAdapter" ;
    private Context mContext;
    private ArrayList<EventListItem> eventListItems;
    private GruvPreferences gPrefs;


    public EventOrganizerAdapter(Context mContext,ArrayList<EventListItem> eventListItems) {
        super(mContext,0, eventListItems);
        this.mContext = mContext;
        this.eventListItems=eventListItems;
    }
    /*private view holder class*/
    private static class ViewHolder {
        TextView txtTitle,txtDate,txtTime;
        Button invite,invitedbutton;

    }
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final EventListItem rowItem= this.getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.content_eventlist_adapter, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txteventtitle);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_eventdate);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txt_eventtime);
            holder.invite=(Button) convertView.findViewById(R.id.btn_invite);
            holder.invitedbutton=(Button)convertView.findViewById(R.id.btn_invited);

            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.txtTitle.setText(rowItem.getEventtitle());
        holder.txtDate.setText(rowItem.getEventdate());
        holder.txtTime.setText(rowItem.getEventtime());
        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.invite.setVisibility(View.GONE);
                gPrefs=new GruvPreferences(mContext);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Gruv Invites");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"Welcome to GRUV-IN, You've invited by "+gPrefs.getFirstName()+" "+"Event Date & Time:"+
                        rowItem.getEventdate()+" "+rowItem.getEventtime()+"Event Name:"+rowItem.getEventtitle()+" "+"Your invite code:"+ Invite.InviteCode);
                try {
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no client installed.", Toast.LENGTH_SHORT).show();

                }
                holder.invitedbutton.setVisibility(View.VISIBLE);
            }
        });

        return convertView;

    }
}
