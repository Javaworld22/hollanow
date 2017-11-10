package com.doxa360.android.betacaller;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.doxa360.android.betacaller.model.bimps;

import java.util.List;

/**
 * Created by user on 7/30/2017.
 */

public class Notification_{
    @SerializedName("success")
    private List<bimps> mData;

    public Notification_(List<bimps> data){
        mData = data;
    }

    public void setNotification(List<bimps> data){
        mData = data;
    }

    public  List<bimps> getNotification(){
        return mData;
    }

}

