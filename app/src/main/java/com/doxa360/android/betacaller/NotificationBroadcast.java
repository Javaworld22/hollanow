package com.doxa360.android.betacaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by user on 8/3/2017.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent){
       // ConnectivityManager manager = (ConnectivityManager)
        //        context.getSystemService(Context.CONNECTIVITY_SERVICE);
      //  NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            Intent callIntent = new Intent(context, NotificationService.class);
           // callIntent.putExtra("", number);
            context.startService(callIntent);

    }

}
