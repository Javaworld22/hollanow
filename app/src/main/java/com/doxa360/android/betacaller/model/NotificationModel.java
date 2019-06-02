package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 5/5/2018.
 */

public class NotificationModel implements Parcelable {

    @SerializedName("id")
    private int mId;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("user_notification")
    private String mUserNotification;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setNotification(String notification){
        mUserNotification = notification;
    }

    public String getNotification(){
        return mUserNotification;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, NotificationModel.class);
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected NotificationModel(Parcel in) {
        mId = in.readInt();
        mUsername = in.readString();
        mUserNotification = in.readString();
        // mUserNotification = in.readStringArray();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mUsername);
        parcel.writeString(mUserNotification);


    }
}
