package com.doxa360.android.betacaller.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.doxa360.android.betacaller.BetaCaller;

public class HollaAlarmService extends BroadcastReceiver {
    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
   // public static final String CUSTOM_INTENT1 = "android.intent.action.BOOT_COMPLETED";
   // public static final String CUSTOM_INTENT2 = "android.intent.action.USER_UNLOCKED";
    public static Context ctx ;

    @Override
    public void onReceive(Context context, Intent intent){
        ctx = context;
        Log.e("PhoneCallBroadcast", "HollaAlarmService is here 222222222");
        Log.e("PhoneCallBroadcast", "Broadcast receiver here "+intent.getAction());
       // BetaCaller.startJob(context);
       // BetaCaller.startJobPeroidic(context);
    }
    public static void cancelAlarm(Context context){
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(getPendingIntent(context));
    }
    private static PendingIntent getPendingIntent(Context ctx){
        Intent alarmIntent = new Intent(ctx, HollaAlarmService.class);
        alarmIntent.setAction(CUSTOM_INTENT);
       // alarmIntent.setAction(CUSTOM_INTENT1);
        return PendingIntent.getBroadcast(ctx,0,alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @SuppressLint("NewApi")
    public static void setAlarm(boolean force, Context context){
        ctx = context;
        cancelAlarm(context);
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long delay = (1000 * 30);
        long when = System.currentTimeMillis();
        //if(!force){
            when += delay;
       // }
       // alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, getPendingIntent(context));
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (1000*5),
                1000*5,getPendingIntent(context));
    }

}
