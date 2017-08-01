package com.tcst.android.gruvin.Activities;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tcst.android.gruvin.Provider.GruvinProvider;
import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.PastEventAdapter;

public class PastEvents extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = PastEvents.class.getSimpleName();

    private static final int LOAD_EVENTADD_DETAILS = 10;
    private Context mContext;
    private ListView eventlist;
    private PastEventAdapter pastEventAdapter;
    private Cursor mEventAddCursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.past_events, container,false);
        mContext=getActivity();
    /* btnCreateNewSchedule = (Button) findViewById(R.id.btnnewschedule);*/
        eventlist=(ListView)view.findViewById(android.R.id.list);
        pastEventAdapter = new PastEventAdapter(mContext,mEventAddCursor);
        eventlist.setAdapter(pastEventAdapter);
        getLoaderManager().initLoader(LOAD_EVENTADD_DETAILS, null,this);
        Log.d(TAG, "onCreate:"+mEventAddCursor);
        return view;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(getActivity(),SelectImagesFromGallery.class);
        startActivity(intent);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader:" + id);
        CursorLoader cursorLoader = null;
        switch (id) {
            case LOAD_EVENTADD_DETAILS:
                cursorLoader = new CursorLoader(mContext, GruvinProvider.URI_EVENTLIST_DETAILS_ALL, null, null, null, null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "xxxxxxxxxxxx loaderId  :" + loader.getId());
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
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
