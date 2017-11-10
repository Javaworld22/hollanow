package com.doxa360.android.betacaller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;

import android.net.Uri;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.bimps;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;


/**
 * Created by user on 7/28/2017.
 */

public class NotificationService extends Service {

    private static final String TAG = NotificationService.class.getSimpleName();
    public static Notification_ model;
    private HollaNowSharedPref mSharedPref;
    private User currentUser;
    private Context mContext;
    //private Set<Integer> setCounter;
    private final int mId = 0Xfff4;
    private Notification.Builder mNotification;
    private static String headNote;
    private PendingIntent pendingIntent;
    private HollaNowDbHelper helper;

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
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
        Intent intent = new Intent(this, NotificationActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(intent);
         pendingIntent = stackBuilder.getPendingIntent(22, PendingIntent.FLAG_UPDATE_CURRENT);
        mSharedPref = new HollaNowSharedPref(this);
        helper = new HollaNowDbHelper(this);
        if(mSharedPref != null)
        currentUser = mSharedPref.getCurrentUser();
        notifyUsers();
       // if(mSharedPref != null)
      //  if(mSharedPref.getPhone() != null)
        //        updatePhoneNumber();


        // notifyUsers();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        notifyUsers();
       // if(mSharedPref != null)
           // if(mSharedPref.getPhone() != null)
            //    updatePhoneNumber();
        //Register notification here for the notification bar

        return START_STICKY;
    }



    private void notifyUsers(){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <JsonElement> call = hollaNowApiInterface.notifyUser1();
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    List<bimps> list = null;
                    Reader reader = null;
                   // model = (bimps) response.body();
                   // InputStream is = new ByteArrayInputStream(new InputStreamReader());
                   // new BufferedReader(new InputStreamReader()
                    try {
                         reader = new FileReader (new File("{'counter':'5','head':'Hopefully this works','body':'Amen','created_at':'2017-07-31 10:48:11'}"));
                    }catch (IOException e){

                    }
                    model =  new GsonBuilder().create().fromJson(response.body().toString(),Notification_.class);
                    Log.e(TAG, "Notification response: " + response.code());
                    headNote = model.getNotification().get(0).getNotification();
                    Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    int color1 = 0XFF9800;
                    mNotification = new Notification.Builder(getApplicationContext())
                            .setContentTitle("HollaNow")
                            .setSound(note)
                            .setContentText(model.getNotification().get(0).getHeadNotification());

                    Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.notification23);

                    mNotification.setSmallIcon(R.drawable.notification12);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mNotification.setColor(color1);
                       // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
                       // mNotification.setLargeIcon(bm);
                    }else
                        mNotification.setSmallIcon(R.drawable.notification2);
                       // mNotification.setLargeIcon(bm);

                     mNotification.setContentIntent(pendingIntent)/**.setColor(color1)**/.setLargeIcon(bm) ;

                    NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                   // mNotificationManager.notify(mId,mNotification.build());
                   // helper.clearAndRecreateDb();
                   // mSharedPref.setNotificationCounter(1);

                    int message_count = mSharedPref.getNotificationCounter();
                    if(message_count != model.getNotification().get(0).getCounter())
                        if( model.getNotification().get(0).getCounter() > message_count) { //
                            bimps bip = new bimps(model.getNotification().get(0).getHeadNotification(),
                                    model.getNotification().get(0).getNotification(),
                                    model.getNotification().get(0).getCounter(),
                                    model.getNotification().get(0).getCreatedDate());
                            mSharedPref.setNotification_flag(false);
                            mSharedPref.setNotificationCounter(model.getNotification().get(0).getCounter());

                            //Iterator<String> head =  mSharedPref.getSetHead().iterator();
                           // Iterator<String> body =  mSharedPref.getSetBody().iterator();
                           // Iterator<String> date =  mSharedPref.getSetDate1().iterator();
                            helper.cacheNotification(bip);
                            mNotificationManager.notify(mId,mNotification.build());
                          //  try{
                          //      Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),note);
                          //      r.play();
                          //  }catch(Exception e){
                          //      e.printStackTrace();
                          //  }
                        }





                   // try {
                        // list = tArrayAdapter.read( response);
                   // }catch (IOException e){
                   //     Log.e(TAG, "Notification response: " + e);
                   //     Log.e(TAG, "Notification response: " + e);
                   // }

                   // Log.e(TAG, "Notification response: " + response.body().getDate());
                    Log.e(TAG, "Notification response: " + model.getNotification().get(0).getNotification());
                    Log.e(TAG, "Notification response: " + model.getNotification().get(0).getNotification()+"   "+message_count);
                    try {
                       // bipList = helper.allNotification();
                    }catch (Exception e){

                    }
                }
                else {

                    Log.e(TAG, "Error Notification " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure "+t);

               // bipList = helper.allNotification();

            }

        });
    }

  /**  private void updatePhoneNumber() {
        currentUser.setPhone(mSharedPref.getPhone());
        if(MyToolBox.isNetworkAvailable(mContext) && (currentUser.getPhone() != null)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            Call<User> call = hollaNowApiInterface.editUserPhone(currentUser, mSharedPref.getToken());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        Log.e(TAG, "Notification response: " + response.code());
                        //currentUser.setPhone(response.body().getPhone());
                       // mSharedPref.setCurrentUser(currentUser.toString());
                        //mPhone.setText(response.body().getPhone());
                       // Toast.makeText(mContext, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        //mPhone.setText(phoneIntent);
                       // Toast.makeText(mContext, "Phone number not updated", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    //mPhone.setText(phoneIntent);
                    Toast.makeText(mContext, "Network error.", Toast.LENGTH_LONG).show();
                }
            });
        }
    } **/

    private void alignNotification(){
        Iterator<String> it;
    }




}
