package com.doxa360.android.betacaller;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Category;
import com.doxa360.android.betacaller.service.MyWorker2;
import com.doxa360.android.betacaller.service.MyWorker3;
import com.parse.Parse;
import com.parse.ParseObject;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Apple on 12/04/16.
 */
public class BetaCaller extends android.support.multidex.MultiDexApplication { //Application
//project Id hollanow1022
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "qD7VMOEhMBMrUz7uT2wnMm78J";
    private static final String TWITTER_SECRET = "aCcLnHZwQqbBv5ljQZ7UB55IcBNmHteZAGDb3lJ4AHNTw6vIZN";

    public static final String SERVER_BASE_URL = "https://www.hollanow.com/";
//    http://www.hollanow.com/doxa360/api/v1/user/authenticate?
    public static final String SIGN_UP_API = "doxa360/api/v1/user/signup"; //method = post; params = email,username,name,password
    public static final String SIGN_IN_API = "doxa360/api/v1/user/authenticate"; //method = post; params = email,password
    public static final String SIGN_IN_USER_API = "doxa360/api/v1/user/authenticate_username"; //method = post; params = username,password
    public static final String GET_USER_DETAILS_API = "doxa360/api/v1/user/authenticate"; //method = get; params = token
    public static final String EDIT_PROFILE_API = "doxa360/api/v1/user/edit_profile";
    public static final String EDIT_PHONE_API = "doxa360/api/v1/user/edit_profile"; //edit_profile

    public static final String SEARCH_PHONE_API = "doxa360/api/v1/user/search_phone";
    public static final String SEARCH_API = "doxa360/api/v1/user/search";

    public static final String GET_INDUSTRY_API = "doxa360/api/v1/industry"; //method = get; params = token

    public static final String UPLOAD_PHOTO_API = "doxa360/api/v1/user/upload_photo";

    public static final String TOKEN = "token";
    public static final String SEARCH = "search";

//    public static final String PARSE_APP_ID = "anSwU1zWd1pJvRnm7ajbkPr7L83ZBsZex04uPm5j";
//    public static final String PARSE_CLIENT_KEY = "SOWnAIYJWbN5lmbapcEvoaYFwd4d4t1dHhrIDHRq";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_PHONE = "CONTACT_PHONE";
    public static final String CONTACT_PHOTO = "CONTACT_PHOTO";
    public static final int LOCATION_DISTANCE_CLOSE = 1;
    public static final int LOCATION_DISTANCE_MEDIUM = 2;
    public static final int LOCATION_DISTANCE_FARTHER = 4;
    public static final String PHOTO_URL = "https://hollanow.com/assets/photos/users/";
    public static final String FILEURL = "https://hollanow.com/assets/photos/contact/";
    public static final String SALES_URL = "https://hollanow.com/assets/photos/marketingpix/";
    public static final String USER_PROFILE = "USER_PROFILE";
    public static final String GET_USER_BY_INDUSTRY_API = "doxa360/api/v1/user/industry";
    public static final String INDUSTRY = "industry";
    public static final String NEARBY_USERS_API = "doxa360/api/v1/user/nearby_users";
    public static final String LATTITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String DISTANCE_KM = "distKm";
    public static final String CALLNOTE_BY_PHONE = "doxa360/api/v1/user/callnote";
    public static final String USERS_BY_CONTACTS = "doxa360/api/v1/user/contacts";
    public static final String CONTACT_LIST = "contacts";
    public static final String PHONE = "phone";
    public static final String NOTIFICATION_NUMBER = "notify_number";
    public static final String PASSWORD_RESET = "https://hollanow.com/password/reset";
    public static final String SAVE_CALLNOTE = "doxa360/api/v1/user/callnote/create";
    public static final String NOTIFICATION ="doxa360/api/v1/user/notificationpage/sent";
    public static final String CONTACT_MANAGEMENT ="doxa360/api/v1/user/sent";
    public static final String CALLNOTE ="doxa360/api/v1/user/callnote/create";
    public static final String RETRIEVE_CONTACT = "doxa360/api/v1/user/get_stored_contacts";
    public static final String SALES_PICTURES = "doxa360/api/v1/user/store_marketing";
    public static final String RETRIEVE_SALES_PICTURES = "doxa360/api/v1/user/get_stored_pictures";
    public static final String SEND_USER_NOTIFICATION =  "doxa360/api/v1/user/sent/notification";
    public static final String RECEIVE_USER_NOTIFICATION = "doxa360/api/v1/user/get";
    public static final String NO_OF_LIKES = "doxa360/api/v1/user/post_shout_out";
    public static final String SEARCH_VISIBILITY = "doxa360/api/v1/user/togles";
    public static final String NIGERIAN_NEWS = "https://newsapi.org/v2/top-headlines";


    @Override
    public void onCreate() {
        super.onCreate();
        //mContext = getApplicationContext();
        //Context mContext = getApplicationContext();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
               // if(mContext != null) {
                  //  startJob(mContext);
                  //  startNotification(mContext);
               // }
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(BetaCaller.this, new TwitterCore(authConfig), new Crashlytics(), new Digits.Builder().build());
               // startJobPeroidic(BetaCaller.this.getApplicationContext());
               // startJob(BetaCaller.this.getApplicationContext());
            }
        };
        runnable.run();

    }

    @SuppressLint("NewApi")
    public static void startJob(Context context){
        ComponentName name = new ComponentName(context,MyWorker.class);
        JobInfo.Builder builder = new JobInfo.Builder(0,name);
        builder.setPersisted(true)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false);
        builder.setMinimumLatency(1*1000);
        builder.setOverrideDeadline(2*1000);
       // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        Log.e("Bettercaller", "This is Jobscheduler for lollipop. ");
        JobScheduler jobScheduler =  (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(0);
        jobScheduler.schedule(builder.build());
       // CallNoteWindowManager wind = new CallNoteWindowManager(context.);

    }

    @SuppressLint("NewApi")
    public static void startJobPeroidic(Context context){
        ComponentName name = new ComponentName(context,MyWorker.class);
        JobInfo.Builder builder = new JobInfo.Builder(5,name);
        builder.setPersisted(true)
                .setPeriodic(60*15*1000,5*60*1000);
        // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        Log.e("Bettercaller", "This is Jobscheduler for lollipop. ");
        JobScheduler jobScheduler =  (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(5);
        int result = jobScheduler.schedule(builder.build());
        if(result == JobScheduler.RESULT_SUCCESS)
            Log.e("Bettercaller", "StartJobPeriodic Successful ");
        // CallNoteWindowManager wind = new CallNoteWindowManager(context.);
    }

    @SuppressLint("NewApi")
    public static void startJobPeroidic2(Context context){
        ComponentName name = new ComponentName(context,MyWorker2.class);
        JobInfo.Builder builder = new JobInfo.Builder(11,name);
        builder.setPersisted(true)
                .setPeriodic(60*25*1000,5*60*1000);
        // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        Log.e("Bettercaller", "This is Jobscheduler for lollipop. ");
        JobScheduler jobScheduler =  (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(11);
        int result = jobScheduler.schedule(builder.build());
        if(result == JobScheduler.RESULT_SUCCESS)
            Log.e("Bettercaller", "StartJobPeriodic2 Successful ");
        // CallNoteWindowManager wind = new CallNoteWindowManager(context.);
    }

    @SuppressLint("NewApi")
    public static void startJobPeroidic3(Context context){
        ComponentName name = new ComponentName(context,MyWorker3.class);
        JobInfo.Builder builder = new JobInfo.Builder(12,name);
        builder.setPersisted(true)
                .setPeriodic(60*45*1000,5*60*1000);
        // .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        Log.e("Bettercaller", "This is Jobscheduler for lollipop. ");
        JobScheduler jobScheduler =  (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(12);
        int result = jobScheduler.schedule(builder.build());
        if(result == JobScheduler.RESULT_SUCCESS)
            Log.e("Bettercaller", "StartJobPeriodic3 Successful ");
        // CallNoteWindowManager wind = new CallNoteWindowManager(context.);
    }

    @SuppressLint("NewApi")
    public static void startNotification1(Context context){
        ComponentName name = new ComponentName(context, NotificationForAndroid8.class);
        JobInfo.Builder builder = new JobInfo.Builder(1,name);
        builder.setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .setRequiresBatteryNotLow(true)
                .setPersisted(true)
                //.setPeriodic(300000);
                .setMinimumLatency(1*1000)
                .setOverrideDeadline(3*1000);
        // Log.e("Bettercaller", "This is Jobscheduler for Notification. ");
        JobScheduler jobSheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        jobSheduler.cancel(1);
        jobSheduler.schedule(builder.build());
    }


}
