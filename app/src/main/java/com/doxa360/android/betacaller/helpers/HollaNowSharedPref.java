package com.doxa360.android.betacaller.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.ArraySet;

import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.bimps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Apple on 23/06/15.
 */
public class HollaNowSharedPref {
    SharedPreferences mSharedPreferences;
    private String TAG = this.getClass().getSimpleName();

    public HollaNowSharedPref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public List<Integer> getPre_Likes(){
        String list =  mSharedPreferences.getString("like", "");
        List<String> mLikes =  new GsonBuilder().create().fromJson(list, List.class);
        List<Integer> newLikes = new ArrayList<Integer>();
        if(mLikes != null)
         for(int i= 0;i<mLikes.size();i++){
            int intLike = Integer.parseInt(mLikes.get(i));
            newLikes.add(intLike);
        }
       return newLikes;
    }

    public void setPre_Likes(String count){
        mSharedPreferences
                .edit()
                .putString("like", count)
                .apply();
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

    public void setGalleryCount(Set<String> count){
        mSharedPreferences
                .edit()
                .putStringSet("gallerycount", count)
                //.putInt("gallerycount", count)
                .apply();
    }

    public Set<String> getGalleryCount(){
       // return mSharedPreferences.getInt("gallerycount", 0);
        return mSharedPreferences.getStringSet("gallerycount", new HashSet<String>());
    }

    /// For storing HollaContact last updated details  //////////////////////////
    public void setHollaContact(List<Parse_Contact> count){
        String user = new GsonBuilder().create().toJson(count, List.class);
        mSharedPreferences
                .edit()
                .putString("hollacontact", user)
                .apply();
    }

    /////// For retrieving HollaContact last updated details /////////////////////

    public List<Parse_Contact> getHollaContact(){
        String user =  mSharedPreferences.getString("hollacontact", "");
        Type listType = new TypeToken<List<Parse_Contact>>(){}.getType();
        List <Parse_Contact>list =  new GsonBuilder().create().fromJson(user, listType);
        return list;
    }

    public void setLastCallnote(CallNote call){
        String callNote = new GsonBuilder().create().toJson(call, CallNote.class);
        mSharedPreferences
                .edit()
                .putString("last_callnote", callNote)
                .apply();
    }

    public CallNote getLastCallnote(){
        String stringCallNote = mSharedPreferences.getString("last_callnote", "");
        Type callNoteType = new TypeToken<CallNote>(){}.getType();
        CallNote callnote = new GsonBuilder().create().fromJson(stringCallNote, callNoteType);
        return callnote;
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

    public void setView(int view){
        mSharedPreferences
                .edit()
                .putInt("view", view)
                .apply();
    }

    public int getView(){
        return mSharedPreferences.getInt("view", 4);
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



    public void setUsername(String username){
        mSharedPreferences
                .edit()
                .putString("username",  username)
                .apply();
    }

    public void setPhone(String phone){
        mSharedPreferences
                .edit()
                .putString("userphone",  phone)
                .apply();
    }

    public void setPhoneEmoji(String phone){
        mSharedPreferences
                .edit()
                .putString("phoneemoji",  phone)
                .apply();
    }

    public String getPhoneEmoji(){
        return mSharedPreferences.getString("phoneemoji", " ");
    }

    public String getPhone(){
        return mSharedPreferences.getString("userphone", " ");
    }

    public String getUsername(){
        return mSharedPreferences.getString("username", " ");
    }

    public void setList( Set<String> allContacts){
        mSharedPreferences
                .edit()
                .putStringSet("SetContact",  allContacts)
                .apply();
    }

    public Set<String> getSetContact(){
        return mSharedPreferences.getStringSet("SetContact", new HashSet<String>());
    }


    public int getNotificationCounter(){
        return mSharedPreferences.getInt("notificationCount", 0);
    }

    public void setNotificationCounter(int count){
        mSharedPreferences
                .edit()
                .putInt("notificationCount", count)
                .apply();
    }

    public void setNotification_flag(int value) {
        mSharedPreferences
                .edit()
                .putInt("flag_notification1", value)
                .apply();
    }

    public int getNotification_flag() {
       // try {
            Log.e("shared pref", mSharedPreferences.getInt("flag_notification1", 0) + " ");
       // }catch (ClassCastException e){setNotification_flag(0);}
        return mSharedPreferences.getInt("flag_notification1", 0);
    }

    public void setAnotherDevice_flag(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("anotherdevice", value)
                .apply();
    }

    public boolean getAnotherDevice_flag() {
        Log.e("shared pref", mSharedPreferences.getBoolean("Another device", false)+"");
        return mSharedPreferences.getBoolean("anotherdevice", false);
    }

    public void setRefresh_flag(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("refreshflag", value)
                .apply();
    }

    public boolean getrefresh_flag() {
        Log.e("shared pref", mSharedPreferences.getBoolean("Another device", false)+"");
        return mSharedPreferences.getBoolean("refreshflag", false);
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

    public void setPageTransit(boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean("page", value)
                .apply();
    }

    public boolean isTransit() {
        Log.e("HollaNowSharedPref", mSharedPreferences.getBoolean("tutorial", false)+"");
        return mSharedPreferences.getBoolean("page", false);
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

    public void setFlagContacts(String sentiment){
        mSharedPreferences
                .edit()
                .putString("flagcontacts", sentiment)
                .apply();
    }

    public void setNewUser(String contact_name){
        mSharedPreferences
                .edit()
                .putString("contact_name", contact_name)
                .apply();
    }

    public String getNewUser(){
        return mSharedPreferences.getString("contact_name", "" );
    }


    public String getFlagContacts(){
        return mSharedPreferences.getString("flagcontacts", "No Update" );
    }

    public void setFlagPhoneAuth(boolean phone){
        mSharedPreferences
                .edit()
                .putBoolean("phoneAuth", phone)
                .apply();
    }

    public boolean getFlagPhoneAuth() {
        Log.e("GetFlagPhoneAuth", mSharedPreferences.getBoolean("phoneAuth", false)+"");
        return mSharedPreferences.getBoolean("phoneAuth", false);
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

    public void setNotificationUser(String jsonUser) {
        mSharedPreferences.edit()
                .putString("jsonNotification", jsonUser)
//                .commit();
                .apply();
    }

    public Post_Model getNotificationUser() {
        String user =  mSharedPreferences.getString("jsonNotification", "");
        return new GsonBuilder().create().fromJson(user, Post_Model.class);
    }

    public void setListUsernames(String jsonUser) {
        mSharedPreferences.edit()
                .putString("username_notification", jsonUser)
//                .commit();
                .apply();
    }

    public List<String> getListUsernames() {
        String list =  mSharedPreferences.getString("username_notification", "");
        return new GsonBuilder().create().fromJson(list, List.class);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////  Test for knowing the last update on Holla Contact
    public void setListHollaContact(String jsonUser) {
        mSharedPreferences.edit()
                .putString("username_hollacontact", jsonUser)
//                .commit();
                .apply();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //////////  This is to receive the last update of Holla Contact //////////////////////////
    public List<String> getListHollaContact() {
        String list =  mSharedPreferences.getString("username_hollacontact", "");
        return new GsonBuilder().create().fromJson(list, List.class);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    ///  This is for knowing the last update of notification  ////////////////////////////////
    public void setListUsernamesToTest(String jsonUser) {
        mSharedPreferences.edit()
                .putString("username_notification_test", jsonUser)
//                .commit();
                .apply();
    }


 ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////   This is for getting the last update /////////////////////////////////////////
    public List<String> getListUsernamesToTest() {
        String list =  mSharedPreferences.getString("username_notification_test", "");
        return new GsonBuilder().create().fromJson(list, List.class);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////   This makes sure that new User check is performed only twice   /////////////

    public void setCount_Number_of_times_checked_user(int count){
        mSharedPreferences
                .edit()
                .putInt("timesCount", count)
                .apply();
    }

    public int getCount_Number_of_times_checked_user(){
        return mSharedPreferences.getInt("timesCount", 0);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////   Save the date for every new date generated  ///////////////////

    public void setDate_for_every_check(int count){
        mSharedPreferences
                .edit()
                .putInt("datecheck", count)
                .apply();
    }

    public int getDate_for_every_check(){
        return mSharedPreferences.getInt("datecheck", 0);
    }

    public List<ShoutOutModel> getListShout() {
        Type listType = new TypeToken<ArrayList<ShoutOutModel>>(){}.getType();
        String list =  mSharedPreferences.getString("bimp", "");
        return new GsonBuilder().create().fromJson(list, listType);

    }

    public void setListShout(String jsonUser) {
        mSharedPreferences.edit()
                .putString("bimp", jsonUser)
//                .commit();
                .apply();
    }


    public List<CallNote> getListCallNote() {
        Type listType = new TypeToken<ArrayList<CallNote>>(){}.getType();
        String list =  mSharedPreferences.getString("callnote", "");
        return new GsonBuilder().create().fromJson(list, listType);

    }

    public void setListCallNote(String jsonUser) {
        mSharedPreferences.edit()
                .putString("callnote", jsonUser)
//                .commit();
                .apply();
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

    public void setCountry(String country) {
        mSharedPreferences.edit()
                .putString("country", country)
                .apply();
    }

    public String getCountry() {
        return mSharedPreferences.getString("country", "");
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

    public void setlocation(Location location){
        Gson gson = new Gson();
        String json = gson.toJson(location);
        mSharedPreferences.edit()
                .putString("LOCATION", json)
                .commit();
    }

    public String getJsonLocation(){
        return mSharedPreferences.getString("LOCATION", " ");
    }

    public void setCreditCount(int count){
        mSharedPreferences
                .edit()
                .putInt("creditCount", count)
                .apply();
    }

    public int getCreditCount(){
        return mSharedPreferences.getInt("creditCount", 0);
    }

    public void setDateOfUpload(String date) {
        mSharedPreferences.edit()
                .putString("uploaddate", date)
                .apply();
    }

    public String getDateOfUpload() {
        return mSharedPreferences.getString("uploaddate", "");
    }

    public void setPhotoPath(String path) {
        mSharedPreferences.edit()
                .putString("pathphoto", path)
                .apply();
    }

    public String getPhotoPath() {
        return mSharedPreferences.getString("pathphoto", "");
    }

    // Check for how many times overlay appears
    public void setOverlayAppearance(int count){
        mSharedPreferences
                .edit()
                .putInt("overlay", count)
                .apply();
    }

    public int getOverlayAppearance(){
        return mSharedPreferences.getInt("overlay", 0);
    }


}
