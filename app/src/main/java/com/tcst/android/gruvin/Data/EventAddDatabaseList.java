package com.tcst.android.gruvin.Data;


public class EventAddDatabaseList {

    public static final String TABLE_NAME="eventlisttable";
    public static final String ID="_id";
    public static final String EVENT_NAME="eventname";
    public static final String EVENT_LOCATION="address";
    public static final String EVENT_DATE="event_date";
    public static final String EVENT_TIME="event_time";

    public static String[] PROJECTION_ALL ={ID,EVENT_NAME,EVENT_LOCATION,EVENT_DATE,EVENT_TIME};

}
