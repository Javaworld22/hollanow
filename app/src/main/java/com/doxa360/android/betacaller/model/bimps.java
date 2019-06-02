package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 7/28/2017.
 */

public class bimps implements Parcelable {

    @SerializedName("id")
    private int mId;
    @SerializedName("head")
    private String mHead_notification;
    @SerializedName("body")
    private String mNotification;
    @SerializedName("counter")
    private int mCounter;
    @SerializedName("created_at")
    private  String mDate1;
    @SerializedName("country")
    private String mCountry;



    public bimps() {

    }

    public bimps(String head_notification, String notification, int counter,String date){
        mHead_notification = head_notification;
        mNotification = notification;
        mDate1 = date;
        mCounter = counter;
    }

   // public List<String> getNotification_(){
   //     return mDate1;
   // }


    public void setHeadNotification(String Head_notification) {
        mHead_notification = Head_notification;
    }

    public void setNotification(String Notification) {
        mNotification = Notification;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }

    public void setCreatedDate(String date1) {
        mDate1 = date1;
    }

    public String getHeadNotification(){
        return mHead_notification;
    }

    public String getNotification(){
        return mNotification;
    }

    public int getCounter(){
        return mCounter;
    }

    public String getCreatedDate(){
        return mDate1;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }





    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, bimps.class);
    }

    public static final Creator<bimps> CREATOR = new Creator<bimps>() {
        @Override
        public bimps createFromParcel(Parcel in) {
            return new bimps(in);
        }

        @Override
        public bimps[] newArray(int size) {
            return new bimps[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected bimps(Parcel in) {
        mId = in.readInt();
        mHead_notification = in.readString();
        mNotification = in.readString();
        mCounter = in.readInt();
       // mUserNotification = in.readStringArray();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mHead_notification);
        parcel.writeString(mNotification);
        parcel.writeInt(mCounter);
       // parcel.writeArray(mUserNotification);

    }
}
