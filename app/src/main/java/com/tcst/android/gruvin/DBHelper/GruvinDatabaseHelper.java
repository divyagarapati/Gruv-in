package com.tcst.android.gruvin.DBHelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tcst.android.gruvin.Data.EventAddDatabaseList;

public class GruvinDatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE = "create table " + "LOGIN" + "( "
            + "ID" + " integer primary key autoincrement,"
            + "USERNAME  text,PASSWORD text); ";
    public static final String DATABASE_USERCREATE = "create table " + "USERLOGIN" + "( "
            + "USERID" + " integer primary key autoincrement,"
            + "USERUSERNAME  text,USERPASSWORD text); ";
    private static final String TAG ="GruvinDatabaseHelper" ;
    public static SQLiteDatabase db;

    public GruvinDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        context = context;
    }

    public GruvinDatabaseHelper open() throws SQLException {
        db=this.getWritableDatabase();
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_USERCREATE);
        createEventListTable(db);

        Log.d(TAG,"Music Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + EventAddDatabaseList.TABLE_NAME);
        onCreate(db);

    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String userName, String password) {
        ContentValues newValues = new ContentValues();
        newValues.put("USERNAME", userName);
        newValues.put("PASSWORD", password);
        db.insert("LOGIN", null, newValues);

    }
    public void insertUserEntry(String userUserName, String userPassword) {
        ContentValues newUserValues = new ContentValues();
        newUserValues.put("USERUSERNAME", userUserName);
        newUserValues.put("USERPASSWORD", userPassword);
        db.insert("USERLOGIN", null, newUserValues);

    }

    public int deleteEntry(String UserName) {

        String where = "USERNAME=?";
        int numberOFEntriesDeleted = db.delete("LOGIN", where,
                new String[]{UserName});
        return numberOFEntriesDeleted;
    }

    public String getSinlgeEntry(String userName) {
        Cursor cursor = db.query("LOGIN", null, " USERNAME=?",
                new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        cursor.close();
        return password;
    }
    public String getUserSinlgeEntry(String UserUserName) {
        Cursor cursor1 = db.query("USERLOGIN", null, " USERUSERNAME=?",
                new String[]{UserUserName}, null, null, null);
        if (cursor1.getCount() < 1) {
            cursor1.close();
            return "NOT EXIST";
        }
        cursor1.moveToFirst();
        String Userpassword = cursor1.getString(cursor1.getColumnIndex("USERPASSWORD"));
        cursor1.close();
        Log.d(TAG,"User Password is:"+Userpassword);
        return Userpassword;
    }

    public String updateEntry(String userName, String password) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("USERNAME", userName);
        updatedValues.put("PASSWORD", password);

        String where = "USERNAME = ?";
        db.update("LOGIN", updatedValues, where, new String[]{userName});
        return userName;
    }

    private void createEventListTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + EventAddDatabaseList.TABLE_NAME
                + "("+ EventAddDatabaseList.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EventAddDatabaseList.EVENT_NAME + " VARCHAR DEFAULT (null),"
                + EventAddDatabaseList.EVENT_LOCATION + " VARCHAR DEFAULT (null),"
                + EventAddDatabaseList.EVENT_DATE + " VARCHAR DEFAULT (null),"
                + EventAddDatabaseList.EVENT_TIME + " VARCHAR DEFAULT (null) " + ")");

        Log.d(TAG, " table Event List Tabale is created ");
    }
    public void deleteEventDataBaseList(SQLiteDatabase db)
    {
        db.delete(EventAddDatabaseList.TABLE_NAME, null, null);

    }


}
