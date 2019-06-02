package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 5/18/2018.
 */

public class Post_ShoutoutModel  implements Parcelable {
    @SerializedName("name")
    private String mName;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("post")
    private String mPost;
    @SerializedName("likes")
    private int mLikes;
    @SerializedName("no_post")
    private int mNo_post;
    @SerializedName("photo")
    private String mPhoto;

    public void setName(String name){
        mName = name;
    }

    public void setmUsername(String username){
        mUsername = username;
    }

    public  void setPost(String post){
        mPost = post;
    }
    public void setLikes(int likes){
        mLikes = likes;
    }

    public void setNo_of_post(int no_of_post){
        mNo_post = no_of_post;
    }

    public void setPhoto(String photo){
        mPhoto = photo;
    }

    public String getName(){
        return mName;
    }

    public String getUsername(){
        return mUsername;
    }

    public String getPost(){
        return mPost;
    }

    public int getLikes(){
        return mLikes;
    }

    public int getNo_of_Post(){
        return mNo_post;
    }

    public String getPhoto(){
        return mPhoto;
    }

    public Post_ShoutoutModel(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post_ShoutoutModel> CREATOR = new Creator<Post_ShoutoutModel>() {
        @Override
        public Post_ShoutoutModel createFromParcel(Parcel in) {
            return new Post_ShoutoutModel(in);
        }

        @Override
        public Post_ShoutoutModel[] newArray(int size) {
            return new Post_ShoutoutModel[size];
        }
    };

    protected Post_ShoutoutModel(Parcel in) {
       // mId = in.readInt();
        mName = in.readString();
        mUsername = in.readString();
        mPost = in.readString();
        mLikes = in.readInt();
        mNo_post = in.readInt();
        mPhoto = in.readString();

    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mUsername);
        parcel.writeString(mPost);
        parcel.writeInt(mLikes);
        parcel.writeInt(mNo_post);
        parcel.writeString(mPhoto);
    }
}
