package com.doxa360.android.betacaller.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.doxa360.android.betacaller.model.User;
import com.google.gson.GsonBuilder;

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

    public float getLattitude(){
        return mSharedPreferences.getFloat("lattitude", (float) 0);
    }

    public void setLattitude(float lattitude){
        mSharedPreferences
                .edit()
                .putFloat("lattitude",  lattitude)
                .apply();
    }

    public float getLongtitude(){
        return mSharedPreferences.getFloat("longtitude", (float) 0);
    }

    public void setLongtitude(float longtitude){
        mSharedPreferences
                .edit()
                .putFloat("longtitude",  longtitude)
                .apply();
    }

    public void setTutorial(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("tutorial", value)
                .apply();
    }

    public boolean isTutorial() {
        Log.e("shared pref", mSharedPreferences.getBoolean("tutorial", false)+"");
        return mSharedPreferences.getBoolean("tutorial", false);
    }

    public void setTutorial2(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("tutorial2", value)
                .apply();
    }

    public boolean isTutorial2() {
        Log.e("shared2", mSharedPreferences.getBoolean("tutorial", false)+"");
        return mSharedPreferences.getBoolean("tutorial2", false);
    }

    public void setTutorial3(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("tutorial3", value)
                .apply();
    }

    public boolean isTutorial3() {
        Log.e("shared3", mSharedPreferences.getBoolean("tutorial", false)+"");
        return mSharedPreferences.getBoolean("tutorial3", false);
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

    public void setCurrentUser(String jsonUser) {
        mSharedPreferences.edit()
                .putString("jsonUser", jsonUser)
//                .commit();
                .apply();
    }

    public User getCurrentUser() {
        String user =  mSharedPreferences.getString("jsonUser", "");
        return new GsonBuilder().create().fromJson(user, User.class);
    }

    @SuppressLint("CommitPrefEdits")
    public boolean clearCurrentUser() {
        mSharedPreferences.edit()
                .remove("jsonUser")
                .commit();
        return true;
    }

    public void setToken(String token) {
        mSharedPreferences.edit()
                .putString("token", token)
                .apply();
    }

    public String getToken() {
        return mSharedPreferences.getString("token", "");
    }

    public void setDeviceId(String deviceId) {
        mSharedPreferences.edit()
                .putString("deviceId", deviceId)
                .apply();
    }

    public String getDeviceId() {
        return mSharedPreferences.getString("deviceId", "");
    }

//    public void setDevice(boolean value) {
//        mSharedPreferences
//                .edit()
//                .putBoolean("tutorial", value)
//                .apply();
//    }
//
//    public boolean isTutorial() {
//        Log.e("shared", mSharedPreferences.getBoolean("tutorial", false)+"");
//        return mSharedPreferences.getBoolean("tutorial", false);
//    }

}
