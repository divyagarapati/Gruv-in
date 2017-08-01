package com.tcst.android.gruvin.app;

public class AppConfig {
    public static String BASEURL = "http://54.153.72.33:8080/gruvin/api/";
    // Server user login url
    public static String URL_LOGIN = "getUserDetails";
    // Server user register url
    public static String URL_REGISTER = "saveUserDetails";
    // Server user forgot password url
    public static String URL_FGTPWD = "getPasswordByUsername";
    // Server user forgot username url
    public static String URL_FGTUSR = "getUsernameByEmail";
    // Server user forgot save evens url
    public static String URL_SAVEEVENTS = "saveEventDetails";
    // Server user  AllEventDetails url
    public static String URL_GETALLEVENTS = "getAllEventDetails";
    // Server user  SearchPlaces url
    public static String URL_SEARCHPLACES = "getSearchPlaces";
    // Server user  Join Event url
    public static String URL_JOINTEVENT = "joinEvent";
    // Server user  All Joined Events url
    public static String URL_JOINEDEVENTS = "getAllJoinedEventDetails";
    // Server user  searchEventDetails between radius url
    public static String URL_searchEventDetails = "searchEventDetails";
    public static String URL_GETAUDIO = "getPlayList";
    public static String URL_EVENTSBYINVITE = "getEventByInvite";
    public static String URL_LOCATION="getSearchLocations";
    // public static String URL_SERVERTIME = "getServerDate";
    public static String URL_GETTIMEDATE = "http://api.xmltime.com/timeservice?accesskey=C8Qm55pV2G&secretkey=XYjMy5dyikUDC13YbFfq&version=2&placeid=tcst&out=js&prettyprint=1&geo=0&lang=no&time=1&sun=0&timechanges=0&tz=0&verbosetime=0";

}