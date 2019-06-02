package com.doxa360.android.betacaller.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by michael on 8/20/2017.
 */

public class RetrieveFile {

    @SerializedName("id")
    private int mId;
    @SerializedName("zip")
    private String mFile;
    @SerializedName("device_id")
    private String mDeviceId;

    public RetrieveFile(String file, String deviceId){
        mFile = file;
        mDeviceId = deviceId;
    }

    public void setFile(String file){
        mFile = file;
    }
    public void setmDeviceId(String deviceId){
        mDeviceId = deviceId;
    }
    public String getmFile(){
        return mFile;
    }
    public String getmDeviceId(){
        return mDeviceId;
    }
}
