package com.doxa360.android.betacaller;

import com.doxa360.android.betacaller.model.RetrieveFile;
import com.doxa360.android.betacaller.model.bimps;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 8/20/2017.
 */

public class ListRetrieveContact {

    @SerializedName("success")
    private List<RetrieveFile> mData;

    public ListRetrieveContact(List<RetrieveFile> data){
        mData = data;
    }

    public void setFile(List<RetrieveFile> data){
        mData = data;
    }

    public  List<RetrieveFile> getFile(){
        return mData;
    }
}
