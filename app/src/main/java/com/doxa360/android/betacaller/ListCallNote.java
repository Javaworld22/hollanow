package com.doxa360.android.betacaller;

import com.doxa360.android.betacaller.model.bimps;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import com.doxa360.android.betacaller.model.CallNote;

/**
 * Created by user on 8/19/2017.
 */

public class ListCallNote {

    @SerializedName("success")
    private List<CallNote> mData;

    public ListCallNote(List<CallNote> data){
        mData = data;
    }

    public void setCallNote(List<CallNote> data){
        mData = data;
    }

    public  List<CallNote> getCallNote(){
        return mData;
    }

}
