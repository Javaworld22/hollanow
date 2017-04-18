package com.doxa360.android.betacaller.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Apple on 06/01/2017.
 */

public class CallNote {
    public static final String CALL_NOTE = "CALL_NOTE";
    public static final String CALL_NOTE_ID = "CALL_NOTE_ID";
    public static final String CALLER_ID = "CALLER_ID";
    public static final String CALLER_USER_NAME = "CALLER_USER_NAME";
    public static final String CALLER_EMAIL = "CALLER_EMAIL";
    public static final String CALLER_NAME = "CALLER_NAME";
    public static final String CALLER_NUMBER = "CALLER_NUMBER";
    public static final String CALLER_PHOTO = "CALLER_PHOTO";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    @SerializedName("id")
    private int mId;
    @SerializedName("note")
    private String mNote;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("name")
    private String mName;
    @SerializedName("from")
    private String mPhone;
    @SerializedName("industry")
    private String mIndustry;
    @SerializedName("photo")
    private String mPhoto;
    @SerializedName("to")
    private String mReceiverPhone;
    //TODO: expiry times on call notes - or check duration of call note from created at date.

    public CallNote() {

    }

    public CallNote(String note, String username, String name, String phone,
                    String industry, String photo, String receiverPhone) {
        mNote = note;
        mUsername = username;
        mName = name;
        mPhone = phone;
        mIndustry = industry;
        mPhoto = photo;
        mReceiverPhone = receiverPhone;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
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

    public String getIndustry() {
        return mIndustry;
    }

    public void setIndustry(String industry) {
        mIndustry = industry;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getReceiverPhone() {
        return mReceiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        mReceiverPhone = receiverPhone;
    }

}
