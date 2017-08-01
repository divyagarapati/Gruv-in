package com.tcst.android.gruvin.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tcst.android.gruvin.Data.EventAddDatabaseList;
import com.tcst.android.gruvin.DBHelper.GruvinDatabaseHelper;


public class GruvinProvider extends ContentProvider {
    public static final String TAG = "DataBaseProvider";

    public static String AUTHORITY = "com.tcst.android.gruvin.Provider";
    public Context mContext;
    public GruvinDatabaseHelper gruvinDatabaseHelper;
    public SQLiteDatabase mDb;

    public static final int CODE_EVENTADDLIST = 10;


    public static final String EVENT_LIST_DETAILS = EventAddDatabaseList.TABLE_NAME;
    public static final String EVENT_LIST_DETAILS_ALL = EVENT_LIST_DETAILS + "/all";


    public static Uri URI_EVENTLIST_DETAILS_ALL = Uri.parse("content://"
            + AUTHORITY + "/" + EVENT_LIST_DETAILS_ALL);

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, EVENT_LIST_DETAILS_ALL, CODE_EVENTADDLIST);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, " oncraete");
        mContext = getContext();

        gruvinDatabaseHelper = new GruvinDatabaseHelper(mContext);
        mDb = gruvinDatabaseHelper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor c = null;
        mDb = gruvinDatabaseHelper.getWritableDatabase();

        Log.d(TAG, "uri" + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTADDLIST:
                qb.setTables(EventAddDatabaseList.TABLE_NAME);
                c = qb.query(mDb, projection, selection, null, null, null, sortOrder);
        }
        if (c != null) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        mDb = gruvinDatabaseHelper.getWritableDatabase();
        Log.d(TAG, " uri  " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTADDLIST:
                long val = mDb.insert(EventAddDatabaseList.TABLE_NAME, null, values);
                Log.d(TAG,"Inserted values:"+val);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
       return 0;
    }
}
