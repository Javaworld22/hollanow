package com.doxa360.android.betacaller;

//import com.doxa360.android.betacaller.model.CallNote;
import android.os.Parcel;
import android.os.Parcelable;

import com.doxa360.android.betacaller.model.SalesMarket;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/25/2017.
 */

public class ListSalesMarket implements Parcelable{

    @SerializedName("id")
    private int mId;
    @SerializedName("success")
    private List<SalesMarket> mData = new ArrayList<SalesMarket>();

    public ListSalesMarket(List<SalesMarket> data){
        mData = data;
    }

    public  List<SalesMarket> getSalesMarket(){
        return mData;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags){
        dest.writeInt(mId);
        dest.writeList(mData);
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator(){
              public ListSalesMarket createFromParcel(Parcel in){
                  return new ListSalesMarket(in);
              }
              public ListSalesMarket[] newArray(int size){
                  return new ListSalesMarket[size];
              }
            };

            public ListSalesMarket(Parcel in){
                mId = in.readInt();
                 in.readList(mData, this.getClass().getClassLoader());
            }
            @Override
        public int describeContents(){
                return 0;
            }
}
