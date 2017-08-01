package com.tcst.android.gruvin.Activities;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tcst.android.gruvin.Data.EventAddDatabaseList;
import com.tcst.android.gruvin.Provider.GruvinProvider;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.PastEventAdapter;

public class UserEventListActivity extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = UserEventListActivity.class.getSimpleName();

    private static final int LOAD_EVENTADD_DETAILS = 10;
    private static final int LOAD_EVENTADD_BY_NAME_DETAILS = 20;
    private Context mContext;
    private ListView eventlist;
    private String eventtitle, eventDate, eventTime;
    private PastEventAdapter pastEventAdapter;
    private Cursor mEventAddCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.userevent_list, container,false);
        mContext=getActivity();
        eventlist = (ListView)view.findViewById(android.R.id.list);
        pastEventAdapter = new PastEventAdapter(mContext, mEventAddCursor);
        eventlist.setAdapter(pastEventAdapter);
        getLoaderManager().initLoader(LOAD_EVENTADD_DETAILS, null, this);
        Log.d(TAG, "onCreate:" + mEventAddCursor);
        return view;
}

   @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick:"+position);
        Cursor cursor = (Cursor)l.getItemAtPosition(position);
        eventtitle = cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_NAME));
        eventDate = cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_DATE));
        eventTime = cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_TIME));
        if (eventtitle != null && eventDate != null && eventTime != null) {
            Bundle bundle = new Bundle();
            Intent in = new Intent(mContext, Schedule.class);
            bundle.putString("eventtitle", cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_NAME)));
            bundle.putString("eventlocation", cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_LOCATION)));
            bundle.putString("eventdate", cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_DATE)));
            bundle.putString("eventtime", cursor.getString(cursor.getColumnIndex(EventAddDatabaseList.EVENT_TIME)));
            in.putExtras(bundle);
            startActivity(in);

        }
    }
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader:" + id);
        CursorLoader cursorLoader = null;
        switch (id) {
            case LOAD_EVENTADD_DETAILS:
                cursorLoader = new CursorLoader(mContext, GruvinProvider.URI_EVENTLIST_DETAILS_ALL, null, null, null, null);
                break;
            case LOAD_EVENTADD_BY_NAME_DETAILS:
                cursorLoader = new CursorLoader(mContext, GruvinProvider.URI_EVENTLIST_DETAILS_ALL, null, null, null, null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "loaderId  :" + loader.getId());
        switch (loader.getId()) {
            case LOAD_EVENTADD_DETAILS:
                if(cursor!=null) {
                    Log.i(TAG, "cursor count:" + cursor.getCount());
                    pastEventAdapter.swapCursor(cursor);
                                    }
                break;
        }


    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

}
