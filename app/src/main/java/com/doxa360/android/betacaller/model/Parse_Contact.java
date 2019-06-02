package com.doxa360.android.betacaller.model;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Apple on 12/04/16.
 */
public class Parse_Contact implements Serializable{

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String NAME = "NAME";
    public static final String USERNAME = "USERNAME";
    public static final String PHOTO = "PHOTO";
    private static final String TAG = Parse_Contact.class.getSimpleName();
    String mAccountType;
    String mId;
    String mDisplayName;
    String mPhoneNumber;
    String mEmailAddress;
    String mThumbnailUrl;
     Bitmap mThumbnail;
    private String mVersion;
    private double mLatitude;
    private double mLongitude;
    private transient File mFile;
    private String mLastSeen;
    private String mCreatedDate;
    private String mOccupation;
    SerializableUser mUser;
    //User mUsers;


    public Parse_Contact() {
    }

    public Parse_Contact(String displayName, String phoneNumber) {
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
    }

    public Parse_Contact(String id, String displayName, String phoneNumber) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
    }

    public Parse_Contact(String id, String displayName, String phoneNumber, String emailAddress, Bitmap thumbnail) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mThumbnail = thumbnail;
    }

    public Parse_Contact(String id, String displayName, String phoneNumber, String emailAddress, String thumbnailUrl, Bitmap thumbnail) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mThumbnailUrl = thumbnailUrl;
        mThumbnail = thumbnail;
    }

    public Parse_Contact(String id, String displayName, String phoneNumber, String emailAddress, String thumbnailUrl, String accountType) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mThumbnailUrl = thumbnailUrl;
        mAccountType = accountType;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        mThumbnail = thumbnail;
    }

    public void setOccupation(String occupation){
        mOccupation = occupation;
    }

    public String getOccupation(){
        return  mOccupation;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public void setAccountType(String accountType) {
        mAccountType = accountType;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setLastSeen(String lastSeen){
        String time = null;
        try {
            Log.e(TAG, "Year of  "+ "2018-02-24 14:36:30");
             SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            // cal.add(Calendar.DAY_OF_YEAR,-7);
            Date date = ((Date)format.parse(lastSeen));
            //Log.e(TAG, "Date is given as "+date);
            //Log.e(TAG, "Date is given as "+time);
            long msgTimeMills = date.getTime();
            cal.setTimeInMillis(msgTimeMills);
            Calendar now = Calendar.getInstance();
            final String strTimeFormate = "h:mm aa";
            final String strDateFormate = "dd/MM/yyyy  h:mm aa";
            if(now.get(Calendar.DATE) == cal.get(Calendar.DATE) && (now.get(Calendar.MONTH) == cal.get(Calendar.MONTH))
                    && (now.get(Calendar.YEAR) == cal.get(Calendar.YEAR))){
                mLastSeen = "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal);
                Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
            }else if((now.get(Calendar.DATE) - cal.get(Calendar.DATE)) == 1
                    && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                    && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))){
                Log.e(TAG,  "Yestarday at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "Yestarday at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "Yestarday at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                Log.e(TAG,  "Yestarday at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                mLastSeen = "Yestarday at "+ android.text.format.DateFormat.format(strTimeFormate, cal);
            }else{
                mLastSeen = " "+android.text.format.DateFormat.format(strDateFormate, cal);
                Log.e(TAG,   " "+android.text.format.DateFormat.format(strDateFormate, cal));
            }
        }catch (ParseException ex){
            //Log.e(TAG, "Exception year olf study "+time);
            Log.e(TAG, "Exception year olf study "+ex.getMessage());
            ex.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }
    public boolean newUsers(String dateCreated){
        boolean isNewUser = false;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            Date date = ((Date) format.parse(dateCreated));
            cal.setTime(date);
            cal.add(Calendar.DATE, 7);
            if(cal.getTime().compareTo(now.getTime()) < 0){
                // its more than 7 days
                isNewUser = true;
            }
        }catch(ParseException e){
            isNewUser = false;
        }
        return isNewUser;
    }

    public String getLastSeen(){
        Log.e(TAG, "Date is given as "+mLastSeen);
        return mLastSeen;
    }

    public void setCreatedDate(String createdDate){
        mCreatedDate = createdDate;
    }

    public String getCreatedDate(){
        return mCreatedDate;
    }

    public void setSerialUser(SerializableUser user){
        mUser = user;
    }
    //public void setUser(User user){
    //    mUsers = user;
   // }
   // public User getUsers(){
    //    return mUsers;
   // }

    public SerializableUser getSerialUser(){
        return mUser;
    }
    public void setLatitude(float latitude) {
        mLatitude = (double) latitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }



    public void setLongitude(float longitude) {
        mLongitude = (double) longitude;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", mId);
            if (mDisplayName!=null) jsonObject.put("displayName", mDisplayName);
            if (mPhoneNumber!=null) jsonObject.put("phoneNumber", mPhoneNumber);
            if (mEmailAddress!=null) jsonObject.put("emailAddress", mEmailAddress);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject;
    }

    public void setFile(File file){
        mFile = file;
    }
    public File getFile(){
        return mFile;
    }
}
