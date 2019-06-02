package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 10/25/2017.
 */

public class SalesMarket implements Parcelable{

    @SerializedName("id")
    private int mId;
    @SerializedName("pixname")
    private String mPhoto;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("color")
    private String mColor;
    @SerializedName("content")
    private String mContent;



    public SalesMarket(String photo, String userName,String color, String content){
        mPhoto = photo;
        mUsername = userName;
        mContent = content;
        mColor = color;
    }

    public void setPhoto(String photo){
        mPhoto = photo;
    }

    public void setUserName(String userName){
        mUsername = userName;
    }
    public void setColor(String color){
        mColor = color;
    }

    public void setContent(String content){
        mContent = content;
    }

    public String getPhoto(){
        return mPhoto;
    }

    public String getUserName(){
        return mUsername;
    }

    public String getContent(){
        return mContent;
    }

    public String getColor(){
        return mColor;
    }

    public SalesMarket(Parcel in){
        //String[] data = new String[4];
        this.mId = in.readInt();
        this.mPhoto = in.readString();
        this.mUsername = in.readString();
        this.mColor = in.readString();
        this.mContent = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(mId);
        dest.writeString(mPhoto);
        dest.writeString(mUsername);
        dest.writeString(mColor);
        dest.writeString(mContent);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<SalesMarket>(){
        public SalesMarket createFromParcel(Parcel in){
            return new SalesMarket(in);
        }
        @Override
        public SalesMarket[] newArray(int size) {
            return new SalesMarket[size];
        }
    };

    @Override
    public int describeContents(){
        return 0;
    }
}
