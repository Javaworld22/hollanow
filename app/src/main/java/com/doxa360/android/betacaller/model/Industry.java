package com.doxa360.android.betacaller.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Apple on 05/01/2017.
 */
public class Industry {

    @SerializedName("id")
    private int mId;
    @SerializedName("industry")
    private String mIndustry;

    public Industry() {
    }

    public Industry(String industry) {
        mIndustry = industry;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getIndustry() {
        return mIndustry;
    }

    public void setIndustry(String industry) {
        mIndustry = industry;
    }
}
