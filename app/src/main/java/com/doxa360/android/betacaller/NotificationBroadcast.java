package com.doxa360.android.betacaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.doxa360.android.betacaller.service.HollaNotificationService;

/**
 * Created by user on 8/3/2017.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent){
       // ConnectivityManager manager = (ConnectivityManager)
        //        context.getSystemService(Context.CONNECTIVITY_SERVICE);
      //  NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        Log.e("NotificationBroadcast", "Broadcast receiver here "+intent.getAction());
        Log.e("NotificationBroadcast", "Broadcast receiver here "+intent.getAction());

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            Intent notificationIntent = new Intent(context, NotificationService.class);
            notificationIntent.putExtra("intent", "service");
            context.startService(notificationIntent);
            Intent hollaContact = new Intent(context, HollaNotificationService.class);
            hollaContact.putExtra("holla", "hollacontacts");
            context.startService(hollaContact);
        }
       // Intent callIntent = new Intent(context, CallNoteServiceNew.class);
       // callIntent.putExtra("callnote_service", "Just show callnote notification");
       // context.startService(callIntent);

    }

}
