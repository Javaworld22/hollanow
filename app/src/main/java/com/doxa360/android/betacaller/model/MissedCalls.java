package com.doxa360.android.betacaller.model;

/**
 * Created by user on 7/20/2018.
 */

public class MissedCalls {

    private static final String TAG = MissedCalls.class.getSimpleName();
    String mDisplayName;
    String mPhoneNumber;
    private String mCreatedDate;
    String mThumbnailUrl;
    private String callNote;

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

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public void setCreatedDate(String createdDate){
        mCreatedDate = createdDate;
    }

    public String getCreatedDate(){
        return mCreatedDate;
    }
}
