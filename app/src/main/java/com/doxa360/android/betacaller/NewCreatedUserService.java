package com.doxa360.android.betacaller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;

/**
 * Created by user on 2/25/2018.
 */

public class NewCreatedUserService extends Service {

    private static final String TAG = NewCreatedUserService.class.getSimpleName();

    private Context mContext;
    private Notification.Builder mNotification;
    private HollaNowSharedPref mSharedPref;
    private final int mId = 0Xfff6;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
       // pendingIntent = stackBuilder.getPendingIntent(32, PendingIntent.FLAG_UPDATE_CURRENT);
        mSharedPref = new HollaNowSharedPref(this);
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        notifyUsers();


        return START_STICKY;
    }

    private void notifyUsers(){
        Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle("HollaNow")
                .setSound(note)
                .setContentText(mSharedPref.getNewUser()+" joined HollaNow, you can now exchange expression on calls.");

        int color1 = 0XFF9800;
        mNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle("HollaNow")
                .setSound(note)
                .setContentText(mSharedPref.getNewUser()+" joined HollaNow, you can now exchange expression on calls.");

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.notification23);

        mNotification.setSmallIcon(R.drawable.notification12);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotification.setColor(color1);
            // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
            // mNotification.setLargeIcon(bm);
        }else
            mNotification.setSmallIcon(R.drawable.notification2);
        // mNotification.setLargeIcon(bm);

        //mNotification.setContentIntent(pendingIntent)/**.setColor(color1)**/.setLargeIcon(bm) ;

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId,mNotification.build());

    }
}
