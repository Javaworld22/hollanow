package com.doxa360.android.betacaller.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Apple on 23/06/15.
 */
public class HollaNowSharedPref {
    SharedPreferences mSharedPreferences;

    public HollaNowSharedPref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public int getContactCount(){
        return mSharedPreferences.getInt("contactCount", 0);
    }

    public void setContactCount(int count){
        mSharedPreferences
                .edit()
                .putInt("contactCount", count)
                .apply();
    }

    public int getCallLogCount(){
        return mSharedPreferences.getInt("callLogCount", 0);
    }

    public void setCallLogCount(int count){
        mSharedPreferences
                .edit()
                .putInt("callLogCount", count)
                .apply();
    }

    public double getLattitude(){
        return mSharedPreferences.getFloat("lattitude", (float) 0);
    }

    public void setLattitude(double lattitude){
        mSharedPreferences
                .edit()
                .putFloat("lattitude", (float) lattitude)
                .apply();
    }

    public double getLongtitude(){
        return mSharedPreferences.getFloat("longtitude", (float) 0);
    }

    public void setLongtitude(double longtitude){
        mSharedPreferences
                .edit()
                .putFloat("longtitude", (float) longtitude)
                .apply();
    }

    public void setTutorial(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("tutorial", value)
                .apply();
    }

    public boolean isTutorial() {
        return mSharedPreferences.getBoolean("tutorial", false);
    }

    public String getSentiment(){
        return mSharedPreferences.getString("sentiment", "Update your driving status" );
    }

    public void setSentiment(String sentiment){
        mSharedPreferences
                .edit()
                .putString("sentiment", sentiment)
                .apply();
    }


    public boolean isShared() {
        return mSharedPreferences.getBoolean("pro_shared", false);
    }

    public void setShared() {
        mSharedPreferences
                .edit()
                .putBoolean("pro_shared", true)
                .apply();
    }
}
