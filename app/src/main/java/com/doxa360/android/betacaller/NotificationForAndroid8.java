package com.doxa360.android.betacaller;

/**
 * Created by Michael on 09/02/19.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.bimps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NewApi")
public class NotificationForAndroid8 extends JobService{

    private static final String TAG = NotificationForAndroid8.class.getSimpleName();
    public static Notification_ model;
    private HollaNowSharedPref mSharedPref;
    private User currentUser;
    private Context mContext;
    //private Set<Integer> setCounter;
    private final int mId = 0Xfff4;
    //Altered
    private final int mId2 = 0Xfff6;
    private final int mId3 = 0Xfff8;
    private NotificationCompat.Builder mNotification;
    private Notification.Builder mNotification1;
    private Notification.Builder mNotification2;
    private static String headNote;
    private PendingIntent pendingIntent;
    private PendingIntent pendingIntent1;
    private PendingIntent pendingIntent2;
    private HollaNowDbHelper helper;
    private List mX;
    private String android_id;
    private bimps bips;
    private static int line;
    private static int count;
    private static int mCount;  // For the Holla Contacts
    private  bimps mBip;
    private List<Post_Model> shoutOutData;
    private List<ShoutOutModel> shout1;
    private List<ShoutOutModel> shout;
    private static int shoutoutCount;
    //private Intent mIntent;
    private BroadcastReceiver mReceiver;
    private boolean jobFinished;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        jobFinished = true;


        Intent intent = new Intent(this, NotificationActivity.class);
        Intent intent1 = new Intent(this, HomeActivity.class);
        intent1.putExtra("return_notification","note");
        Intent intent2 = new Intent(this, HomeActivity.class);
        intent2.putExtra("return_shoutout","shout");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(this);
        TaskStackBuilder stackBuilder2 = TaskStackBuilder.create(this);
        shout1 = new ArrayList<ShoutOutModel>();
        shout = new ArrayList<ShoutOutModel>();
        shoutOutData = new ArrayList<Post_Model>();

        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder1.addParentStack(HomeActivity.class);
        stackBuilder2.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(intent);
        stackBuilder1.addNextIntentWithParentStack(intent1);
        stackBuilder2.addNextIntentWithParentStack(intent2);
        pendingIntent = stackBuilder.getPendingIntent(20, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent1 = stackBuilder1.getPendingIntent(21, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = stackBuilder2.getPendingIntent(22, PendingIntent.FLAG_UPDATE_CURRENT);
        mSharedPref = new HollaNowSharedPref(this);
        helper = new HollaNowDbHelper(this);
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(mSharedPref != null)
            currentUser = mSharedPref.getCurrentUser();
        //  notifyUsers();
        // receiveNotification(currentUser.getUsername(), " ");
        // new Background_downloadContact().execute();
        // new syncDb().execute("yes");
        //notifyUser(currentUser.getUsername(), "I'm here now");
        // new Background_downloadContact().execute();
        //sendContacts(android_id);
        // if(mSharedPref != null)
        //  if(mSharedPref.getPhone() != null)
        //        updatePhoneNumber();


        // notifyUsers();
    }

    @Override
    public boolean onStartJob(JobParameters parameteers){
        Log.e(TAG, "OnstartJob for notification ");
        PersistableBundle bundle =  parameteers.getExtras();
        jobFinished = true;
       // mIntent = intent;
        IntentFilter filter = new IntentFilter();
        filter.addAction(CallDiaryFragment.BROADCAST_ACTION1);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Intent is "+intent.getStringExtra("broadcast_HollaContact"));
                if(intent.getStringExtra("broadcast_HollaContact") != null)
                    if(intent.getStringExtra("broadcast_HollaContact").equals("holla_service")) {
                        Log.e(TAG, "This resume resets everything to start afresh ");
                        Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        int color1 = 0XFF9800;
                        mNotification1 = new Notification.Builder(getApplicationContext())
                                .setContentTitle("New Contact added")
                                .setSound(note)
                                .setContentText("Someone you know joined HollaNow");

                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);

                        mNotification1.setSmallIcon(R.drawable.notification12);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mNotification1.setColor(color1);
                            // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
                            // mNotification.setLargeIcon(bm);
                        } else
                            mNotification1.setSmallIcon(R.drawable.notification2);
                        String NOTIFICATION_CHANNEL_ID = "10001";
                        String NOTIFICATION_CHANNEL_NAME = "Holla_Chanel";

                        mNotification1.setContentIntent(pendingIntent1)/**.setColor(color1)**/.setLargeIcon(bm);
                        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_DEFAULT);
                            channel.enableLights(true);
                            channel.enableVibration(true);
                            channel.setLightColor(Color.RED);
                            mNotification.setChannelId(NOTIFICATION_CHANNEL_ID);
                            mNotificationManager.createNotificationChannel(channel);
                        }
                        mNotificationManager.notify(mId2,mNotification1.build());
                    }
                // new syncDb("get contacts").execute("yes");

            }
        };
        mContext.registerReceiver(mReceiver, filter);
        try {
            if (!bundle.getString("service_start").equals("for_shouout")) {
                Log.e(TAG, "service starts : ");
               // hollaNotifyUsers();
                notifyUsers();
            }
        }catch (NullPointerException ex){
            Log.e(TAG, "Error occured : ");
           // hollaNotifyUsers();
            notifyUsers();
        }

       /** if(bundle != null) // This is for only shoutout alone
            if(bundle.getString("service_start") != null)
                if(bundle.getString("service_start").equals("for_shouout")) {
                    Log.e(TAG, "Start broadcast in the service side: ");
                    new NotificationService.syncDb().execute("yes");

                }**/

        if(bundle != null)
            if(bundle.getString("intent") != null)
                if(bundle.getString("intent").equals("service")) {
                    Log.e(TAG, "Start broadcast in the service side is here : ");
                    //new syncDb().execute("yes");
                    notifyUsers();


                }

       /** if(bundle != null)
            if(bundle.getString(CallReceiver.INTENTCALL) != null)
                if(bundle.getString(CallReceiver.INTENTCALL).equals(CallReceiver.INTENTVALUE)){
                    Log.e(TAG, "Start service from OutGoingCalls: ");
                    new NotificationService.Background_downloadContact().execute();
                }**/

                return jobFinished;
    }


    private void notifyUsers() {
        // saveCallNote();  // Send
        //isCallNote();  //  Receive
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.notifyUser1();
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
                        reader = new FileReader(new File("{'counter':'5','head':'Hopefully this works','body':'Amen','created_at':'2017-07-31 10:48:11'}"));
                    } catch (IOException e) {

                    }
                    model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                    // Log.e(TAG, "Notification response: " + response.code());
                    headNote = model.getNotification().get(0).getNotification();
                    Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    int color1 = 0XFF9800;
                    mNotification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle("HollaNow")
                            .setSound(note)
                            .setContentText(model.getNotification().get(0).getHeadNotification());

                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);

                    mNotification.setSmallIcon(R.drawable.notification12);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mNotification.setColor(color1);
                        // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
                        // mNotification.setLargeIcon(bm);
                    } else
                        mNotification.setSmallIcon(R.drawable.notification2);
                    // mNotification.setLargeIcon(bm);

                    mNotification.setContentIntent(pendingIntent)/**.setColor(color1)**/.setLargeIcon(bm);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // mNotificationManager.notify(mId,mNotification.build());
                    // helper.clearAndRecreateDb();
                    // mSharedPref.setNotificationCounter(1);
                    String NOTIFICATION_CHANNEL_ID = "10001";
                    String NOTIFICATION_CHANNEL_NAME = "Notification";
                    //jobFinished = false;

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT);
                        channel.enableLights(true);
                        channel.enableVibration(true);
                        channel.setLightColor(Color.CYAN);
                        if(mNotification != null)
                            mNotification.setChannelId(NOTIFICATION_CHANNEL_ID);
                        mNotificationManager.createNotificationChannel(channel);
                        Log.e(TAG, "Please launch notification for android 8: "+2);
                    }

                    int message_count = mSharedPref.getNotificationCounter();
                   // if (message_count != model.getNotification().get(0).getCounter())
                      // if (model.getNotification().get(0).getCounter() > message_count) { //
                            bimps bip = new bimps(model.getNotification().get(0).getHeadNotification(),
                                    model.getNotification().get(0).getNotification(),
                                    model.getNotification().get(0).getCounter(),
                                    model.getNotification().get(0).getCreatedDate());
                            int value = mSharedPref.getNotification_flag();
                            value++;
                            mSharedPref.setNotification_flag(value);
                            mSharedPref.setNotificationCounter(model.getNotification().get(0).getCounter());
                            line = model.getNotification().get(0).getCounter();


                            try {
                                helper.cacheNotification(bip); /////////////////
                            }catch(SQLiteException ex){
                                Log.e(TAG, "SLiteException occurred here: " + ex.getMessage());
                            }
                            Log.e(TAG, "Please launch notification for android 8: "+1);
                            mNotificationManager.notify(mId, mNotification.build());

                       // }


                    // Log.e(TAG, "Notification response: " + response.body().getDate());
                    //Log.e(TAG, "Notification response: " + model.getNotification().get(0).getNotification());
                    // Log.e(TAG, "Notification response: " + model.getNotification().get(0).getNotification() + "   " + message_count);
                    //Log.e(TAG, "Notification response country: " + model.getNotification().get(0).getCountry());
                    try {
                        // bipList = helper.allNotification();
                    } catch (Exception e) {

                    }
                } else {

                    Log.e(TAG, "Error Notification " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure " + t);

                // bipList = helper.allNotification();

            }

        });

        Post_Model mode = mSharedPref.getNotificationUser();
        String mUsername[] = null;
        String mCheck[] = null;


        Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // for(int i=0;i<noteList.size();i++) {
        //if (list.size() > 0){
        mNotification1 = new Notification.Builder(getApplicationContext())
                .setContentTitle("New Contact added")
                .setSound(note)
                .setContentText("Someone you know joined HollaNow");
        // }
        int color1 = 0XFF9800;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);
        mNotification1.setSmallIcon(R.drawable.notification12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotification1.setColor(color1);
            // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
            // mNotification.setLargeIcon(bm);
        } else
            mNotification1.setSmallIcon(R.drawable.notification2);

        mNotification1.setContentIntent(pendingIntent1)/**.setColor(color1)**/.setLargeIcon(bm);
        //}

        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strDateFormate = "yyyy-MM-dd HH:mm:ss";
        Calendar now = Calendar.getInstance();
        // long msgTimeMills = date.getTime();
        //now.setTimeInMillis(msgTimeMills);
        CharSequence data = android.text.format.DateFormat.format(strDateFormate, now);


    }

    @Override
    public boolean onStopJob(JobParameters parameters){
        Log.e(TAG, "OnstopJob for notification here  ");

        return true;
    }
}