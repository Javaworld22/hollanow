package com.doxa360.android.betacaller.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 7/28/2017.
 */

public class NotificationModel {

    @SerializedName("id")
    private int mId;
    @SerializedName("title")
    private String mHead_notification;
    @SerializedName("message")
    private String mNotification;
    @SerializedName("create_at")
    private String mDate;

    public NotificationModel() {

    }

    public  NotificationModel(String head_notification, String notification, String date){
        mHead_notification = head_notification;
        mNotification = notification;
        mDate = date;
    }

    public void setHeadNotification(String mHead_notification) {
        mHead_notification = mHead_notification;
    }

    public void setNotification(String mNotification) {
        mNotification = mNotification;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getHeadNotification(){
        return mHead_notification;
    }

    public String getNotification(){
        return mNotification;
    }

    public String getDate(){
        return mDate;
    }
}
