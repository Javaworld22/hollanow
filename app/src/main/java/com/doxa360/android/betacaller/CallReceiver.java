package com.doxa360.android.betacaller;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.service.CallNoteJobScheduler;

import java.util.Date;

/**
 * Created by Apple on 25/07/16.
 */
public class CallReceiver extends PhoneCallBroadcastReceiver {
    public static String nos;
    public static String INTENTCALL = "OUTGOING";
    public static String INTENTVALUE = "OUTGOING";
    public static boolean isMissed = false;
    public static int callEnded;  // to check for answered calls
    public static int number_of_calls;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        super.onIncomingCallStarted(ctx, number, start);
        isMissed = false;
        //Intent callIntent = new Intent("android.intent.action.ACTION_ANSWER");
        Intent callIntent = new Intent(ctx, CallNoteServiceNew.class);
        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
        nos = number;
        Log.e("CallReceiver", "Check Number 1: "+number);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
               ctx.startService(callIntent);
      // LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
        Toast.makeText(ctx,"INCOMING CALL STATE", Toast.LENGTH_SHORT).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            startNotification(ctx,number);
        }


    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
//        super.onIncomingCallEnded(ctx, number, start, end);
        callEnded = 2;
        //TODO: delete below if redundant
//        Intent callIntent = new Intent(INCOMING_CALL_ENDED);
//        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
//        LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        super.onMissedCall(ctx, number, start);
        Log.e("CallReceiver", "Check Number 2: "+number);
        Intent callIntent = new Intent(ctx, CallNoteServiceNew.class);
        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
        nos = number;
        Log.e("CallReceiver", "Check Number 1: "+number);
        // LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
       // ctx.startService(callIntent);ii
        isMissed = true;
        number_of_calls++;
    }

    /**@Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){
        super.onOutgoingCallEnded(ctx,number,start,end);
        Log.e("CallReceiver", "Outgoing Call: "+number);
        Intent callIntent = new Intent(ctx, NotificationService.class);
        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
        callIntent.putExtra(INTENTCALL, INTENTVALUE);
        ctx.startService(callIntent);
    } **/

    @SuppressLint("NewApi")
    public static void startNotification(Context context, String number){
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(Parse_Contact.PHONE_NUMBER,number);
        ComponentName name = new ComponentName(context, CallNoteJobScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(2,name).setExtras(bundle);
        builder.setRequiresDeviceIdle(true).setRequiresCharging(true);
        builder.setPersisted(true);
        builder.setMinimumLatency(1*1000);
        builder.setOverrideDeadline(2*1000);
        Log.e("StartNotification", "This is Jobscheduler for CallNote. ");
        JobScheduler jobSheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobSheduler.cancel(2);
        jobSheduler.schedule(builder.build());
    }

}
