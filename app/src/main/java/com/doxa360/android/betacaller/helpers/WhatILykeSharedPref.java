package com.doxa360.android.betacaller.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Apple on 23/06/15.
 */
public class WhatILykeSharedPref {
    SharedPreferences mSharedPreferences;

    public WhatILykeSharedPref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public double getLattitude(){
        return mSharedPreferences.getFloat("lattitude", (float) 6.5488244);
    }

    public void setLattitude(double lattitude){
        mSharedPreferences
                .edit()
                .putFloat("lattitude", (float) lattitude)
                .apply();
    }

    public double getLongtitude(){
        return mSharedPreferences.getFloat("longtitude", (float) 3.1166124);
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



}
