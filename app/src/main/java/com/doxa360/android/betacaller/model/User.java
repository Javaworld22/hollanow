package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by Apple on 28/12/2016.
 */

public class User implements Parcelable {

    @SerializedName("id")
    private int mId;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("name")
    private String mName;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("occupation")
    private String mOccupation;
    @SerializedName("industry")
    private String mIndustry;
    @SerializedName("about")
    private String mAbout;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("lat")
    private float mLat;
    @SerializedName("lng")
    private float mLng;
    @SerializedName("last_seen")//'corporate','search_visible','contacts_backup'
    private String mLastSeen;
    @SerializedName("corporate")
    private boolean mCorporate;
    @SerializedName("search_visible")
    private boolean mSearchVisible;
    @SerializedName("contacts_backup")
    private String mContactsBackup;
    @SerializedName("photo")
    private String mProfilePhoto;
    @SerializedName("device_id")
    private String mDeviceId;
    @SerializedName("distance")
    private float mDistance;
    @SerializedName("token")
    private String mToken;
    @SerializedName("photo2")
    private String mProfilePhoto2;
    @SerializedName("textphoto2")
    private String mPhotoText;
    @SerializedName("color")
    private String mTextColor;


    public User() {
    }

    public User(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    public User(String email, String password, String username, String name) {
        mEmail = email;
        mPassword = password;
        mUsername = username;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getOccupation() {
        return mOccupation;
    }

    public void setOccupation(String occupation) {
        mOccupation = occupation;
    }

    public String getIndustry() {
        return mIndustry;
    }

    public void setIndustry(String industry) {
        mIndustry = industry;
    }

    public String getAbout() {
        return mAbout;
    }

    public void setAbout(String about) {
        mAbout = about;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public float getLat() {
        return mLat;
    }

    public void setLat(float lat) {
        mLat = lat;
    }

    public float getLng() {
        return mLng;
    }

    public void setLng(float lng) {
        mLng = lng;
    }

    public String getLastSeen() {
        return mLastSeen;
    }

    public void setLastSeen(String lastSeen) {
        mLastSeen = lastSeen;
    }

    public boolean isCorporate() {
        return mCorporate;
    }

    public void setCorporate(boolean corporate) {
        mCorporate = corporate;
    }

    public boolean isSearchVisible() {
        return mSearchVisible;
    }

    public void setSearchVisible(boolean searchVisible) {
        mSearchVisible = searchVisible;
    }

    public String getContactsBackup() {
        return mContactsBackup;
    }

    public void setContactsBackup(String contactsBackup) {
        mContactsBackup = contactsBackup;
    }

    public String getProfilePhoto() {
        return mProfilePhoto;
    }
    public String getProfilePhoto2(){
        return mProfilePhoto2;
    }

    public String getColor(){
        return mTextColor;
    }

    public void setColor(String textColor){
        mTextColor = textColor;
    }

    public String getTextPhoto(){
        return mPhotoText;
    }

    public void setTextPhoto(String photoText){
        mPhotoText = photoText;
    }

    public void setProfilePhoto(String profilePhoto) {
        mProfilePhoto = profilePhoto;
    }

    public void setmProfilePhoto2(String profilePhoto){
        mProfilePhoto2 = profilePhoto;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getDistance() {
        String distAway = null;
        if (mDistance == 0) {
            distAway = "Next to you";
        }
        else if (mDistance < 1) {
            //to meters
            distAway = String.format(Locale.getDefault(), "%.1f",( mDistance * 1000)) + "m away";
        } else if (mDistance >= 1) {
            distAway = String.format(Locale.getDefault(), "%.1f", mDistance) + "km away";
        }

        return distAway;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

//    public String getJSON() {
//        Gson gson = new Gson();
//        String json = gson.toJson(User.class);
//        return json;
//    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, User.class);
    }

    protected User(Parcel in) {
        mId = in.readInt();
        mEmail = in.readString();
        mUsername = in.readString();
        mName = in.readString();
        mPhone = in.readString();
        mProfilePhoto = in.readString();
        mProfilePhoto2 = in.readString();
        mAddress = in.readString();
        mAbout = in.readString();
        mOccupation = in.readString();
        mIndustry = in.readString();
        mLastSeen = in.readString();
        mLat = in.readFloat();
        mLng = in.readFloat();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mEmail);
        parcel.writeString(mUsername);
        parcel.writeString(mName);
        parcel.writeString(mPhone);
        parcel.writeString(mProfilePhoto);
        parcel.writeString(mProfilePhoto2);
        parcel.writeString(mAddress);
        parcel.writeString(mAbout);
        parcel.writeString(mOccupation);
        parcel.writeString(mIndustry);
        parcel.writeString(mLastSeen);
        parcel.writeFloat(mLat);
        parcel.writeFloat(mLng);
        parcel.writeString(mContactsBackup);
        parcel.writeByte((byte) (mCorporate ? 1 : 0)); //myBoolean = in.readByte() != 0;
        parcel.writeByte((byte) (mSearchVisible ? 1 : 0)); //myBoolean = in.readByte() != 0;
    }

}
