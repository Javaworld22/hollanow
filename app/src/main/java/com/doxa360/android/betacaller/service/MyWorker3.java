package com.doxa360.android.betacaller.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.doxa360.android.betacaller.service.HollaAlarmService;

@SuppressLint("NewApi")
public class MyWorker3 extends JobService {

    private static final String TAG = "MyWorker2";
    private Context context;
    private IntentFilter filter;
    private BroadcastReceiver mReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "Periodic3 has started 1");
        Log.e(TAG, "Periodic3 has started 1");
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters parameteers){
        if(context == null) {
            // Log.e(TAG, "OnstartJob context  ");
            context = getApplicationContext();
        }
        if(filter == null) {
            // Log.e(TAG, "OnstartJob action ");
            filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction("android.intent.action.READ_PHONE_STATE");
        }
        // Log.e(TAG, "OnstartJob1 here  ");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Phone state receiver ");
            }
        };
        context.registerReceiver(mReceiver, filter);
        Log.e(TAG, "Periodic3 has started ");
        Log.e(TAG, "Periodic3 has started ");

        HollaAlarmService.cancelAlarm(context);
        HollaAlarmService.setAlarm(true,context);
        jobFinished(parameteers,false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters parameters){
        Log.e(TAG, "OnstopJob1 here  ");

        return true;
    }

}