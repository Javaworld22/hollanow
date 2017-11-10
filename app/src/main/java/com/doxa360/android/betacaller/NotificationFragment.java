package com.doxa360.android.betacaller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;

import com.doxa360.android.betacaller.adapter.NotificationAdapter;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.Parse_Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 7/28/2017.
 */

public class NotificationActivity extends Fragment {

    private static final String TAG = "NotificationActivity";
    List<NotificationModel> note;
    private RecyclerView mCallLogRecyclerview;
    private NotificationAdapter mAdapter;
    NotificationModel model2;
    private Context mContext;

    public NotificationActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.activity_notification, container, false);
        note = new ArrayList<NotificationModel>();
        mCallLogRecyclerview = (RecyclerView) rootView.findViewById(R.id.notification_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mCallLogRecyclerview.setHasFixedSize(true);
        mCallLogRecyclerview.setLayoutManager(layoutManager);
        mAdapter = new NotificationAdapter(model2,getContext());
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
        model2 = new NotificationModel("Header for me","Every good message deserves to be read",null);
        //model2.setHeadNotification("Header for me");
       // model2.setNotification("Every good message deserves to be read");
        //model2.getDate(new Date(ne))model2 = NotificationService.model;
        mAdapter = new NotificationAdapter(model2,mContext);
        mCallLogRecyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
