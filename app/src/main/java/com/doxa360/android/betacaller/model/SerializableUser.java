package com.doxa360.android.betacaller.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 3/25/2018.
 */

public class SerializableUser implements Serializable {



    private String mEmail;

    private String mPassword;

    private String mUsername;

    private String mName;

    private String mPhone;

    private String mOccupation;

    private String mIndustry;

    private String mAbout;

    private String mAddress;

    private float mLat;

    private float mLng;

    private String mLastSeen;

    private boolean mCorporate;

    private boolean mSearchVisible;

    private String mContactsBackup;

    private String mProfilePhoto;

    private String mDeviceId;

    private float mDistance;

    private String mToken;

    private String mProfilePhoto2;
    private String mPhotoText;

    private String mTextColor;

    private int noCount;

    private String mCountry;

    private String mUpdatedDate;

    private String mCreatedDate;

    private boolean mIndicateSaved;

    private List<Parse_Contact> mUserArrayList;

    private String mPath;

    public SerializableUser(){

    }


    public void setEmail(String email){
        mEmail = email;
    }
    public String getEmail(){
        return mEmail;
    }
    public void setUserName(String username){
        mUsername = username;
    }
    public String getUsername(){
        return mUsername;
    }
    public void setPassword(String password){
        mPassword = password;
    }
    public String getPassword(){
        return mPassword;
    }
    public void setName(String name){
        mName = name;
    }
    public String getName(){
        return mName;
    }
    public void setPhone(String phone){
        mPhone = phone;
    }
    public String getPhone(){
        return mPhone;
    }
    public void setOccupation(String occupation){
        mOccupation = occupation;
    }
    public String getOccupation(){
        return mOccupation;
    }
    public void setIndustry(String industry){
        mIndustry = industry;
    }
    public String getIndustry(){
        return mIndustry;
    }
    public void setAbout(String about){
        mAbout = about;
    }
    public String getAbout(){
        return mAbout;
    }
    public void setAddress(String address){
        mAddress = address;
    }
    public String getmAddress(){
        return mAddress;
    }
    public void setDateCreated(String createDate){
        mCreatedDate = createDate;
    }
    public String getDateCreated(){
        return mCreatedDate;
    }
    public void setUpdate(String update){
        mUpdatedDate = update;
    }
    public String getUpdate(){
        return mUpdatedDate;
    }
    public void setCountry(String country){
        mCountry = country;
    }
    public String getCountry(){
        return mCountry;
    }
    public void setProfilePhoto(String profilePhoto){
        mProfilePhoto = profilePhoto;
    }
    public String getProfilePhoto(){
        return mProfilePhoto;
    }

    public void setIndicateSaved(boolean indicateSaved){
        mIndicateSaved = indicateSaved;
    }

    public boolean getIndicateSaved(){
        return mIndicateSaved;
    }

    public void setmUserArrayList(List<Parse_Contact> userArrayList){
        mUserArrayList = userArrayList;
    }

    public List<Parse_Contact> getmUserArrayList(){
        return mUserArrayList;
    }

    public void setPath(String path){
        mPath = path;
    }

    public String getPath(){
        return mPath;
    }
}
