package com.doxa360.android.betacaller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;

import com.doxa360.android.betacaller.adapter.NotificationAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.bimps;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 7/28/2017.
 */

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationActivity";
    List<bimps> note;
    private RecyclerView mCallLogRecyclerview;
    private NotificationAdapter mAdapter;
    //private  List<bimps> bipList;
    bimps model2;
    List<bimps> model1;
    private Context mContext;
    private HollaNowDbHelper helper;
    private HollaNowSharedPref mSharedPref;



    public NotificationFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.activity_notification, container, false);
        mContext = getContext();
        note = new ArrayList<bimps>();
        helper = new HollaNowDbHelper(mContext);
        mSharedPref = new HollaNowSharedPref(mContext);
        mCallLogRecyclerview = (RecyclerView) rootView.findViewById(R.id.notification_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mCallLogRecyclerview.setHasFixedSize(true);
        mCallLogRecyclerview.setLayoutManager(layoutManager);
        note = helper.allNotification();
        if(note.isEmpty())
            mSharedPref.setNotificationCounter(0);
       // mAdapter = new NotificationAdapter(model2,getContext());
        mAdapter = new NotificationAdapter(note,mContext);
        mCallLogRecyclerview.setAdapter(mAdapter);

        return rootView;
    }

    private class syncNotification extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... string) {
            return null;
        }

        @Override
        protected void onPostExecute(Void bytes) {
            super.onPostExecute(bytes);
            mAdapter.notifyDataSetChanged();
            Log.e(TAG, "Adapter call log updated");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        //model1 = NotificationService.model.getNotification();
       // model2 = model1.get(0);
        //model2.setHeadNotification("Header for me");
       // model2.setNotification("Every good message deserves to be read");
        //model2.getDate(new Date(ne))model2 = NotificationService.model;
       // mAdapter = new NotificationAdapter(model2,mContext);
       // mAdapter = new NotificationAdapter(NotificationService.bip,mContext);
       // mCallLogRecyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
