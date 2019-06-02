package com.doxa360.android.betacaller.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 5/28/2018.
 */

public class Post_Model  implements Parcelable {

    ////////////////////////////////////////////////
   /** @SerializedName("user_notification1")
    private String mUserNotification[];
    @SerializedName("mUsernames") // users that are notified
    private String mUsernames[];
    @SerializedName("mPhones")
    private String mPhones[];
    @SerializedName("verify")
    private String mVerify; **/
    ////////////////////////////////////////////////////////
   @SerializedName("calls")
   private List<MissedCalls> mCalls;

    /////  For shout out wish ///////////////////////
    @SerializedName("shout_out")
    private HashMap<String,List<String>> mShoutOut;  // message with the corresponding usernames that saw it
    @SerializedName("msg")
    private List<String> msg; // messages posted
    @SerializedName("msg_date_seen")
    private HashMap<String,List<String>> msg_date_seen; // messages with the corresponding date seen by people
    @SerializedName("msg_date")
    private List<String> mMsgDate;  // date when the post is made
    @SerializedName("msg_like")
    private HashMap <String, List<String>>msg_like;  // messages with their corresponding usernames that liked it
    @SerializedName("user")
    private String mUser;
    @SerializedName("no_of_likes")
    private List<Integer> no_likes;

    ///////////////////////////////////////////////////////////////////

    public Post_Model(){

    }

   /** public void setUserNotification(String userNotification[]) {
        mUserNotification = userNotification;
    }

    public void setmUsernames(String usernames[]){
        mUsernames = usernames;
    }

    public void setmVerify(String verify){
        mVerify = verify;
    }

    public String getmVerify(){
        return mVerify;
    }


    public void setmPhone(String phone[]){
        mPhones = phone;
    }

    public String[] getPhones(){
        return mPhones;
    }

    public String[] getmUsernames(){
        return mUsernames;
    }


    public String[] getUserNotification() {
        return mUserNotification;
    }  **/

    public void setmShoutOut(HashMap<String, List<String>> shoutOut){
        mShoutOut = shoutOut;
    }

    public HashMap<String,List<String>> getShoutOut(){
        return mShoutOut;
    }

    public void setLikes(HashMap<String, List<String>> likes){
        msg_like = likes;
    }

    public HashMap<String,List<String>> getList(){
        return msg_like;
    }

    public List<String> getMessage(){
        return msg;
    }

    public void setMessage(List<String> message){
        msg = message;
    }

    public void setMsgDate(List<String> msgDate){
        mMsgDate = msgDate;
    }

    public List<String> getmMsgDate(){
        return mMsgDate;
    }

    public void setmUser(String user){
        mUser = user;
    }

    public String getmUser(){
        return mUser;
    }

    public void setNo_likes(List<Integer> like){
        no_likes = like;
    }

    public List<Integer> getNo_likes(){
        return no_likes;
    }

    public void setListCall(List<MissedCalls> calls){
        mCalls = calls;
    }

    public List<MissedCalls> getListCalls(){
        return mCalls;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //parcel.writeArray(mUserNotification);

    }

    protected Post_Model(Parcel in) {
      //  mId = in.readInt();
       // mHead_notification = in.readString();
       // mNotification = in.readString();
       // mCounter = in.readInt();
        // mUserNotification = in.readStringArray();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, Post_Model.class);
    }

    public static final Creator<Post_Model> CREATOR = new Creator<Post_Model>() {
        @Override
        public Post_Model createFromParcel(Parcel in) {
            return new Post_Model(in);
        }

        @Override
        public Post_Model[] newArray(int size) {
            return new Post_Model[size];
        }
    };

}
