package com.doxa360.android.betacaller.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Apple on 12/04/16.
 */
public class Contact {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String TAG = Contact.class.getSimpleName();
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


    public Contact() {
    }

    public Contact(String displayName, String phoneNumber) {
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
    }

    public Contact(String id, String displayName, String phoneNumber) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
    }

    public Contact(String id, String displayName, String phoneNumber, String emailAddress, Bitmap thumbnail) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mThumbnail = thumbnail;
    }

    public Contact(String id, String displayName, String phoneNumber, String emailAddress, String thumbnailUrl, Bitmap thumbnail) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mEmailAddress = emailAddress;
        mThumbnailUrl = thumbnailUrl;
        mThumbnail = thumbnail;
    }

    public Contact(String id, String displayName, String phoneNumber, String emailAddress, String thumbnailUrl, String accountType) {
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
}
