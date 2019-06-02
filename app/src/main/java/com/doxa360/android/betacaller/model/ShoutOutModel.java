package com.doxa360.android.betacaller.model;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by michael on 5/13/2018.
 */

public class ShoutOutModel {

    @SerializedName("sender")
    private String sender_name;
    @SerializedName("date")
    private String mDate;
    @SerializedName("photo")
    private String mPhoto;
    @SerializedName("msg")
    private String msg;
    @SerializedName("likes")
    private int no_of_likes;
    @SerializedName("receiver")
    private String receiver_name;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("user_like")
    private boolean current_user_like;

    public ShoutOutModel(){

    }

    public void setSender(String name){
        sender_name = name;
    }

    public String getSender(){
        return sender_name;
    }

    public void setDate(String date){
        mDate = date;
    }

    public String getDate(){
        return mDate;
    }

    public void setPhoto(String photo){
        mPhoto = photo;
    }

    public String getPhoto(){
        return mPhoto;
    }

    public void setMessage(String message){
        msg = message;
    }

    public String getMsg(){
        return msg;
    }

    public void setNoOfLikkes(int likes){
        no_of_likes = likes;
    }

    public int getNoOfLikes(){
        return no_of_likes;
    }

    public void setReceiver(String name){
        receiver_name = name;
    }

    public String getReceiver(){
        return receiver_name;
    }

    public void setUsername(String username){
        mUsername = username;
    }

    public String getUsername(){
        return mUsername;
    }

    public void setCurrent_user_like(boolean  like){
        current_user_like = like;
    }

    public boolean getCurrent_user_like(){
        return current_user_like;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ShoutOutModel.class);
    }
}
