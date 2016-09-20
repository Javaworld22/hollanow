package com.doxa360.android.betacaller.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Apple on 12/04/16.
 */
public class PhoneCallLog {

    byte[] mThumbnailBytes;
    String mId;
    String mDisplayName;
    String mPhoneNumber;
    String mDuration;
    long mDate;
    Uri mThumbnailUrl;
    int mType;



    public PhoneCallLog() {
    }

    public PhoneCallLog(String id, String displayName, String phoneNumber) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
    }

    public PhoneCallLog(String id, String displayName, Uri thumbnailUrl, String phoneNumber, String duration, int type) {
        mId = id;
        mDisplayName = displayName;
        mThumbnailUrl = thumbnailUrl;
        mPhoneNumber = phoneNumber;
        mDuration = duration;
        mType = type;
    }

    public PhoneCallLog(String id, String displayName, String phoneNumber, String duration, long date, int type) {
        mId = id;
        mDisplayName = displayName;
        mPhoneNumber = phoneNumber;
        mDuration = duration;
        mDate = date;
        mType = type;
    }

    public PhoneCallLog(String id, String displayName, Uri thumbnailUrl, String phoneNumber, String duration, long date, int type) {
        mId = id;
        mDisplayName = displayName;
        mThumbnailUrl = thumbnailUrl;
        mPhoneNumber = phoneNumber;
        mDuration = duration;
        mDate = date;
        mType = type;
    }

    public PhoneCallLog(String id, String displayName, byte[] thumbnailBytes, String phoneNumber, String duration, long date, int type) {
        mId = id;
        mDisplayName = displayName;
        mThumbnailBytes = thumbnailBytes;
        mPhoneNumber = phoneNumber;
        mDuration = duration;
        mDate = date;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public byte[] getThumbnailBytes() {
        return mThumbnailBytes;
    }

    public void setThumbnailBytes(byte[] thumbnailBytes) {
        mThumbnailBytes = thumbnailBytes;
    }

    public Uri getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(Uri thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
