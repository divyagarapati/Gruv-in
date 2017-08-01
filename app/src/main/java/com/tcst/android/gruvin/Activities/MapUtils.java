package com.tcst.android.gruvin.Activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class MapUtils {

        public static final String TAG = "MapUtils";

        public static double distance(double lat1, double lon1, double lat2,
                                      double lon2) {
            Location selected_location = new Location("locationA");
            selected_location.setLatitude(lat1);
            selected_location.setLongitude(lon1);
            Location near_locations = new Location("locationB");
            near_locations.setLatitude(lat2);
            near_locations.setLongitude(lon2);
            double distance = selected_location.distanceTo(near_locations);
            return distance / 1000;
        }

        public static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);

        }

        public static double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }

        public static boolean isAppAvailable(Context context, String appName) {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }

        public static boolean isNetworkAvailable(Context mContext) {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.e("Network Testing", "Available");
                return true;
            }
            Log.e("Network Testing", "Not Available");
            return false;
        }


    }
