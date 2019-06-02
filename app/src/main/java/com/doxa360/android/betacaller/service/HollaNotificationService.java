package com.doxa360.android.betacaller.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.HollaNowApiClient;
import com.doxa360.android.betacaller.HollaNowApiInterface;
import com.doxa360.android.betacaller.HomeActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.SerializableUser;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 8/2/2018.
 */

@SuppressLint("NewApi")
public class HollaNotificationService extends JobService {

    private static final String TAG = HollaNotificationService.class.getSimpleName();
    private Context mContext;
    private static int number_of_times;
    private HollaNowDbHelper dbHelper;
    private HollaNowSharedPref mSharedPref;
    private final int mId2 = 0Xfff6;
    private static Set<String> phoneNumbers1;
    private static List<String> phoneNumbers_add;
    private static Iterator<String> it;
    private static int countData1;
    private static List<Parse_Contact> allHollaContacts;
    private HollaNowApiInterface hollaNowApiInterface;
    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath;
    private String dataPath1 = SDPath;
    private Notification.Builder mNotification;
    private PendingIntent pendingIntent;
    private boolean onFinish;
    // private static Set<String> numbers;


    //@Override
   // public IBinder onBind(Intent intent) {
        // Not used
     //   return null;
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();
        countData1 = 0;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        onFinish = true;
        dbHelper = new HollaNowDbHelper(mContext);
        mSharedPref = new HollaNowSharedPref(mContext);
        if(mSharedPref.getCurrentUser() != null)
          if(mSharedPref.getCurrentUser().getUsername() != null)
              dataPath1 = dataPath1+"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/regis/";
        Intent intent1 = new Intent(this, HomeActivity.class);
        intent1.putExtra("return_notification","note");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntentWithParentStack(intent1);
        pendingIntent = stackBuilder.getPendingIntent(21, PendingIntent.FLAG_UPDATE_CURRENT);
        phoneNumbers_add = new ArrayList<String>();
        phoneNumbers1 = new HashSet<String>();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        allHollaContacts = mSharedPref.getHollaContact();
        if(allHollaContacts == null)
            allHollaContacts = new ArrayList<Parse_Contact>();

    }

    @Override
    public boolean onStartJob(JobParameters parameteers) {
     // String iPhone = mSharedPref.getPhone();
     // User user = mSharedPref.getCurrentUser();
     // String token = mSharedPref.getToken();
      //String country = mSharedPref.getCountry();
     // String phone = mSharedPref.getPhone();


        syncDb syn = new syncDb("Get Contacts");
        if(!syn.isAlive())
            syn.start();
        else if(syn.isInterrupted())
            syn.start();
        return onFinish;
    }



    private class syncDb extends Thread {

        Uri uri;
        int count = 0;
        // Iterator<String> it;

        public syncDb(String string) {
            Log.e(TAG, string);


        }

        @Override
        public void run(){

       /**     Call<JsonElement> call = hollaNowApiInterface.newsFeed("ng","0a44e5a4c32f4f81a2ed20f5ba1ef8cd");

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, final Response<JsonElement> response) {
                    Log.e(TAG, "News Feed response code " + response.code());
                    Log.e(TAG, "News Feed response code " + response.code());
                    Log.e(TAG, "News Feed response code " + response.code());
                    Log.e(TAG, "News feed Json Message " + response.body());
                }
                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    //  checkmate = 5;
                      Log.e(TAG, "Newws feed error " + t.getMessage());
                    Log.e(TAG, "Newws feed error " + t.getMessage());
                    Log.e(TAG, "Newws feed error " + t.getMessage());
                }
            });  **/




            Log.e(TAG, "How many contacts so far  " );
            List<Parse_Contact> dbContacts = dbHelper.allContacts();
            Log.e(TAG, "How many contacts so far  " + dbContacts.size());
           // Log.e(TAG, "People in contact list : " + allHollaContacts.toString());

            for (Parse_Contact contact : dbContacts) {
                String phone = contact.getPhoneNumber();
                phoneNumbers1.add(phone);
            }
            Log.e(TAG, "How many phoneNumbers1   " + phoneNumbers1.size());
            number_of_times = phoneNumbers1.size()/10;
            it = phoneNumbers1.iterator();
            Calendar now = Calendar.getInstance();
            // Log.e(TAG, "Check time now  " +now.get(Calendar.DATE));
            int oldDate = mSharedPref.getDate_for_every_check();
            int newDate = now.get(Calendar.DATE);
            if(newDate > oldDate)
              findNearbyUsersByContactList();
        }


    }


    private void findNearbyUsersByContactList() {
        Log.e(TAG, "checking location");
        // Set<String> numbers = new HashSet<String>();

        // List<String> phoneBook = new ArrayList<String>();
        try {
            for (int i = 0; i < 11; i++) {
                // while (it.hasNext()) {
                String ph = it.next();  /// Dump phone numbers here
                phoneNumbers_add.add(ph);
                //}
                Log.e(TAG, "It.next()  " + it.hasNext());
            }
            // numbers.removeAll(phoneNumbers_add);
            countData1++;
        } catch (NoSuchElementException e) {
            Log.e(TAG, "NoSuchElement error just occurred " + e.getStackTrace().toString());
            Post_Model model = mSharedPref.getNotificationUser();
            // if(model != null)
            // if (model.getmVerify().equals("no")) {
            //  model.setmVerify("yes");
            //  mSharedPref.setNotificationUser(model.toString());
            //  sendNotifyUsers(mSharedPref.getCurrentUser().getUsername(),
            //         model);
            //  return;
            // }
            // return;
        }


        Log.e(TAG, "contacts size: " + phoneNumbers_add.size());
        if (MyToolBox.isNetworkAvailable(mContext)) {


// phoneNumbers1
            Call<List<User>> call = hollaNowApiInterface.getUsersByContactList(new GsonBuilder().create().toJson(phoneNumbers_add), mSharedPref.getToken());
           ////////// checkmate = 0;/////////////////
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, final Response<List<User>> response) {
                    phoneNumbers_add.clear();
                    /** Log.e(TAG, "Users on HollaNow " + response.code());
                     Log.e(TAG, "Users on HollaNow " + response.code());
                     Log.e(TAG, "Users on HollaNow " + response.code());
                     Log.e(TAG, "user by contact error 4 "+phoneNumbers1.size());
                     Log.e(TAG, "Users on HollaNow " + response.body().size()); **/
                    Log.e(TAG, "Users on HollaNow " + response.code());
                    if (response.body() != null)
                        Log.e(TAG, "Users on HollaNow " + response.body().size());
                    if (response.code() == 200) {
                   ///////////     checkmate = 5;////////
                        // phoneBook.clear();
                        //TODO: check location thingy
                        response.body();// users on hollanow
                        // AsyncTask.execute(new Runnable() {
                        //     @Override
                        //    public void run() {
                        if (response.body().size() < 100)
                            for (User user : response.body()) {
                                Log.e(TAG, "Username from server   " + user.getUsername());
                                /**  Log.e(TAG, "Updated date is  " + user.getmUpdatedDate());
                                 Log.e(TAG, "Size of what is coming from server side  " + response.body().size());
                                 Log.e(TAG, "Size of what is coming from server side  " + response.body().size());
                                 Log.e(TAG, "Size of what is coming from server side  " + response.body().size());
                                 **/
                                //  if (user.getLat() != 0 && user.getLng() != 0) {

                                Parse_Contact contact = dbHelper.getContactByPhone(user.getPhone());//("+2348036428999\u202C");
                                //Parse_Contact contact = fetchPhoneContacts(user.getPhone());
                                //allHollaContacts.add(contact);
                                if (contact != null)
                                    Log.e(TAG, "Check the contacts " + contact.getPhoneNumber());


                                if (contact != null) {
                                    // countData++;
                                    //contactRegistered(user, dataPath1);
                                    ///////////////////////////////////////////////////////
                                    /// Scan through and know if there is a new HollaContact
                                    for (int i = 0; i < allHollaContacts.size(); i++) {
                                        String phone = null;
                                        Parse_Contact cont = allHollaContacts.get(i);
                                        if (cont != null)
                                            phone = cont.getSerialUser().getPhone();
                                        if (phone != null)
                                            if (contact.getPhoneNumber().equals(phone))
                                                break;
                                        if (allHollaContacts.size() - i == 1) {
                                            //Intent intnt = new Intent();
                                           // intnt.setAction(CallDiaryFragment.BROADCAST_ACTION1);
                                            //intnt.putExtra("broadcast_HollaContact", "holla_service");
                                           // mContext.sendBroadcast(intnt);
                                            showNotification();
                                            Log.e(TAG, "Just Added now " + user.getUsername());
                                            //Log.e(TAG, "Just Added now " + user.getUsername());
                                            //Log.e(TAG, "Just Added now " + user.getName());
                                            syncContactRegistered registered = new syncContactRegistered(user, dataPath1);
                                           // if (!registered.isAlive())
                                                registered.start();
                                           // else if (registered.isInterrupted())
                                               // registered.start();
                                        }

                                    }


                                    /**   try {
                                     File mediaFile = null;
                                     byte[] fileBytes = getByteArrayImage(BetaCaller.PHOTO_URL + user.getProfilePhoto());
                                     Bitmap bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
                                     File file = new File(fileBytes);
                                     contact.setThumbnail(bitmap);
                                     if(!mSharedPref.equals(""))
                                     if(!contact.newUsers(user.getCreatedDate())){
                                     mSharedPref.setNewUser(contact.getDisplayName());
                                     Intent intent  = new Intent(mContext,NewCreatedUserService.class);
                                     mContext.startService(intent);
                                     }


                                     } catch (NullPointerException ex) {
                                     Log.e(TAG, "Error Occured here: Nullpointer " + ex.getMessage());
                                     } **/


                                    //  getActivity().runOnUiThread(new Runnable() {
                                    //    @Override
                                    //   public void run() {
                                    //      if(mAdapter != null) {
                                    //         mAdapter.setAdapterList(allHollaContacts);
                                    //       mAdapter.notifyDataSetChanged();
                                    //   }
                                    // }
                                    //});


                                    // Log.e(TAG, "non null contact "+ contact.getId()+ " - " + contact.getPhoneNumber());
                                    String contactId = contact.getId();
                                    // dbHelper.updateLocation(contactId, user.getLat(), user.getLng());
                                    Log.e(TAG, "updated location");

                                   // regulateContacts = true;


                                    // allContacts.clear();
                                    // allContacts.addAll(dbHelper.allContacts());

                                    // adapter.notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "no contacts with lat and lng");
                                }

                                //  }
                            }
                        // }
                        //  });
                        Log.e(TAG, "Know these numbers " + countData1 + " and" + number_of_times);
                        if (countData1 == number_of_times) {
                            Post_Model model = mSharedPref.getNotificationUser();
                            Calendar now = Calendar.getInstance();
                           // Log.e(TAG, "Check time now  " +now.get(Calendar.DATE));
                           // Log.e(TAG, "Check number of times  "+number_of_times );
                           // Log.e(TAG, "Count data 1  "+countData1 );
                            mSharedPref.setDate_for_every_check(now.get(Calendar.DATE));
                        }
                        if (number_of_times > countData1)
                              findNearbyUsersByContactList();

                         onFinish = false;

                    } else {
                       // checkmate = 5;

                        // Log.e(TAG, "user by contact error 1");
                        // Log.e(TAG, "user by contact error 2 "+response.code());
                        // Log.e(TAG, "user by contact error 3 "+phoneNumbers1.size());
                      //  regulateContacts = true;
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                  //  checkmate = 5;
                    //  Log.e(TAG, "user by contact error 3 " + t.getMessage());
                   // regulateContacts = true;
                    // phoneNumbers_add1.clear();
                }
            });
        }
    }


            private class syncContactRegistered extends Thread/**AsyncTask<String, String, String> **/{
                User mUser;
                String mPath;

                public syncContactRegistered(User user, String path){
                    mUser = user;
                    mPath = path;
                }
                @Override
                public void run(){
                    Log.e(TAG,"DoinBackground commences here ");
                   // Log.e(TAG,"DoinBackground commences here ");
                   // Log.e(TAG,"DoinBackground commences here ");
                    contactRegistered(mUser, mPath);
                }

            }


    private void saveCurrentUser(User user, String token) {
        mSharedPref.setCurrentUser(user.toString());
        mSharedPref.setToken(token);
        //  sharedPref.setPhone(phone);
        mSharedPref.setPhoneEmoji(user.getPhone());
        mSharedPref.setFlagContacts("BackUp");
       // Log.e(TAG, "Response 1009 ");
    }


    private boolean contactRegistered(User user, String destinationPath){
        new File(destinationPath).mkdirs();
        if(!destinationPath.endsWith("/"))
            destinationPath +="/";
        String destination = destinationPath + user.getUsername().trim();
        new File(destination).mkdirs();
        if(!destination.endsWith("/"))
            destination +="/";
        //  Log.e(TAG,"Path of the file "+ destination);
        File file = new File(destination);
        try {
            if (!file.exists())
                file.createNewFile();
        }catch (IOException ex){

        }
        zipFile(destination, user);
        return true;
    }


    private void zipFile(final String destination, final User user){

        /////////////////////////  For Image alone   //////////////////////////////


        File image = null;
        try{
            URL url =  new URL(BetaCaller.PHOTO_URL+user.getProfilePhoto());
            if(user.getProfilePhoto() != null){
                image = new File(destination+"/"+user.getProfilePhoto().trim());
                if (!image.exists()) {
                    image.createNewFile();
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input3 = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input3);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(image);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                }
            }
        }catch(IOException a){
            Log.e(TAG,"EXception occured 4 "+ a.getMessage());
        }catch (NullPointerException ex){
            Log.e(TAG,"EXception occured 5 "+ ex.getMessage());
        }

        /////////////////////////  For Object alone   //////////////////////////////
        Parse_Contact contact = new Parse_Contact();
        SerializableUser seruser = new SerializableUser();
        seruser.setEmail(user.getEmail());
        seruser.setUserName(user.getUsername());
        seruser.setAbout(user.getAbout());
        seruser.setAddress(user.getAddress());
        seruser.setPhone(user.getPhone());
        seruser.setOccupation(user.getOccupation());
        seruser.setIndustry(user.getIndustry());
        seruser.setProfilePhoto(BetaCaller.PHOTO_URL+user.getProfilePhoto());
        seruser.setName(user.getName());
        seruser.setIndicateSaved(false);
        contact.setLastSeen(user.getmUpdatedDate());
        seruser.setUpdate(contact.getLastSeen());
        if(image != null)
            seruser.setProfilePhoto(image.getPath());
        try{
            File userFile = new File(destination+ user.getUsername() + ".ser".trim());
            if (!userFile.exists())
                userFile.createNewFile();
            FileOutputStream fileOutputStream1 = new FileOutputStream(userFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream1);
            objectOutputStream.writeObject(seruser);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream1.close();
        }catch (IOException ex) {
            Log.e(TAG,"EXception occured 6 "+ ex.getMessage());
        }

    }

    private void showNotification(){
        Log.e(TAG, "This resume resets everything to start afresh ");
        Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int color1 = 0XFF9800;
        mNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle("New Contact added")
                .setSound(note)
                .setContentText("Someone you know joined HollaNow");

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        mNotification.setSmallIcon(R.drawable.notification12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotification.setColor(color1);
            // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
            // mNotification.setLargeIcon(bm);
        } else
            mNotification.setSmallIcon(R.drawable.notification2);
        String NOTIFICATION_CHANNEL_ID = "10001";
        String NOTIFICATION_CHANNEL_NAME = "Holla_Chanel";

        mNotification.setContentIntent(pendingIntent)/**.setColor(color1)**/.setLargeIcon(bm);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.RED);
            mNotification.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager.notify(mId2,mNotification.build());
    }

    @Override
    public boolean onStopJob(JobParameters parameters){
        return true;
    }



}
