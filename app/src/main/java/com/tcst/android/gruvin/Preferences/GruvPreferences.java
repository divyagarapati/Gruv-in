package com.tcst.android.gruvin.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tcst.android.gruvin.R;
import com.tcst.android.gruvin.adapter.AudioListAdapter;


/**
 * Created by divya on 3/29/2016.
 */
public class GruvPreferences {
    private Context mContext;
    private SharedPreferences sharedPrefs;

    public GruvPreferences(Context mContext) {
        this.mContext = mContext;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void setLoginState(Boolean status) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(
                mContext.getResources().getString(R.string.sPrefs_login),
                status);
        editor.apply();
    }

    public boolean getLoginState() {
        return sharedPrefs.getBoolean(mContext.getResources().getString(R.string.sPrefs_login), false);
    }
    public void setLoginUserState(Boolean status) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(
                mContext.getResources().getString(R.string.sPrefs_userlogin),
                status);
        editor.apply();
    }

    public boolean getLoginUserState() {
        return sharedPrefs.getBoolean(mContext.getResources().getString(R.string.sPrefs_userlogin), false);
    }
    public void setFirstName(String name) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(
                mContext.getResources().getString(R.string.sPrefs_name),
                name);
        editor.apply();
    }

    public String getFirstName() {
        return sharedPrefs.getString(mContext.getResources().getString(R.string.sPrefs_name), null);
    }

    public String setUserId(String userIdvalue) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getResources().getString(R.string.sPrefs_event),
                userIdvalue);
        editor.apply();
        return userIdvalue;
    }

    public String getUserId() {
        return sharedPrefs.getString(mContext.getResources().getString(R.string.sPrefs_event), null);
    }
    public String setSpinner(String spnrvalue) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getResources().getString(R.string.sPrefs_spinner),
                spnrvalue);
        editor.apply();
        return spnrvalue;
    }

    public String getSpinner() {
        return sharedPrefs.getString(mContext.getResources().getString(R.string.sPrefs_spinner), null);
    }



}
