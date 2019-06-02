package com.doxa360.android.betacaller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import android.net.Uri;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.doxa360.android.betacaller.model.bimps;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    //Altered
    private final int mId2 = 0Xfff6;
    private final int mId3 = 0Xfff8;
    private Notification.Builder mNotification;
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
    private Intent mIntent;
    private BroadcastReceiver mReceiver;


    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
        if(mContext != null)
            mContext.unregisterReceiver(mReceiver);
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
    public int onStartCommand(Intent intent,  int flags, int startId) {
        mIntent = intent;
        IntentFilter filter = new IntentFilter();
        filter.addAction(CallDiaryFragment.BROADCAST_ACTION1);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Intent is "+intent.getStringExtra("broadcast_HollaContact"));
                if(intent.getStringExtra("broadcast_HollaContact") != null)
                if(intent.getStringExtra("broadcast_HollaContact").equals("holla_service")) {
                    Log.e(TAG, "This resume resets everything to stsrt afresh ");
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

                    mNotification1.setContentIntent(pendingIntent1)/**.setColor(color1)**/.setLargeIcon(bm);
                    NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                      mNotificationManager.notify(mId2,mNotification1.build());
                }
                // new syncDb("get contacts").execute("yes");

            }
        };
        mContext.registerReceiver(mReceiver, filter);
        try {
            if (!mIntent.getStringExtra("service_start").equals("for_shouout")) {
                Log.e(TAG, "service starts : ");
                hollaNotifyUsers();
                notifyUsers();
               // receiveNotification(currentUser.getUsername(), " ");
                //new Background_downloadContact().execute();
            }
        }catch (NullPointerException ex){
            Log.e(TAG, "Error occured : ");
            hollaNotifyUsers();
            notifyUsers();
           // receiveNotification(currentUser.getUsername(), " ");
           // new Background_downloadContact().execute();
        }

        if(mIntent != null) // This is for only shoutout alone
            if(mIntent.getStringExtra("service_start") != null)
               if(mIntent.getStringExtra("service_start").equals("for_shouout")) {
                   Log.e(TAG, "Start broadcast in the service side: ");
                   new syncDb().execute("yes");

            }

            if(intent != null)
                if(intent.getStringExtra("intent") != null)
                    if(intent.getStringExtra("intent").equals("service")) {
                        Log.e(TAG, "Start broadcast in the service side is here : ");
                        //new syncDb().execute("yes");
                        notifyUsers();


                    }

        if(intent != null)
            if(intent.getStringExtra(CallReceiver.INTENTCALL) != null)
                if(intent.getStringExtra(CallReceiver.INTENTCALL).equals(CallReceiver.INTENTVALUE)){
                    Log.e(TAG, "Start service from OutGoingCalls: ");
                    new Background_downloadContact().execute();
                }

        return START_STICKY;
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
                    mNotification = new Notification.Builder(getApplicationContext())
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

                    int message_count = mSharedPref.getNotificationCounter();
                    if (message_count != model.getNotification().get(0).getCounter())
                        if (model.getNotification().get(0).getCounter() > message_count) { //
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
                            mNotificationManager.notify(mId, mNotification.build());

                        }


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
       // if(mode != null)
         //  mUsername = mode.getmUsernames();
        //String mUsername[] = new String[]{"Lykeiyke","boscoo"};
       // if(mode != null)
       //  mCheck = mode.getPhones();
        //String mCheck[] = new String[]{"no","no"};
       // List<String> list = new ArrayList<String>();
       // if (mUsername != null && mCheck != null)
          //  for (int i = 0; i < mUsername.length; i++) {
             //   if (mCheck[i].equals("no"))
                //    list.add(mUsername[i]);
               // if (list.contains(mSharedPref.getCurrentUser().getUsername()))
                //    list.remove(mSharedPref.getCurrentUser().getUsername());
               // Log.e(TAG, "Check parameters : " + list);
               // Log.e(TAG, "Check parameters : " + mCheck[i]);
           // }
        // String noteList = list.size()" new HollaNow Users identified"
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

      //  if (list.size() > 0){
       // NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      //  mNotificationManager.notify(mId2,mNotification1.build());
      //  for(int i =0;i<list.size();i++) {
       //     bimps b = new bimps();
        //    b.setHeadNotification("New User " + list.get(i));
         //   b.setNotification("@" + list.get(i) + " saved your contact");
         //   b.setCreatedDate("" + data);
        //    b.setCounter(line - (3 + i));
           // helper.cacheNotification(b);
       // }
       // }
    }






    private void sendContacts(String id) {
        //mProgressDialog.show();
        String username = null;
        String file = "HollaContacts.txt";
        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/HollaNow/";
        SDPath = SDPath+"HollaContacts.txt";
        File file1 = new File(SDPath);
        String fileUrl = (BetaCaller.FILEURL ).trim();

        new File(fileUrl).mkdirs();
        fileUrl = (fileUrl + file).trim();
        if (mSharedPref.getCurrentUser() != null) {
            username = mSharedPref.getCurrentUser().getUsername();
        }

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("zip", file1.getName(), requestFile);

        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            //   if (token != null && id != null){
            Call<ResponseBody> call = hollaNowApiInterface.contactManagement(body, id, username);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        //mProgressDialog.dismiss();
                       // Log.e(TAG, "success of contacts " + response.code() + "");
                       // Log.e(TAG, "success of contacts " + response.code() + "");
                       // Log.e(TAG, "success of contacts " + response.code() + "");
                       // Log.e(TAG, "success of contacts " + response.code() + "");
                      //  Log.e(TAG, "success of contacts " + response.code() + "");
                        // Toast.makeText(mContext, "Contact Saved", Toast.LENGTH_SHORT).show();
                        try {
                            Log.e(TAG, "success of contacts " + response.body().string() + "");
                            // Log.e(TAG, "success of contacts " + response.body().string() + "");
                            Log.e(TAG, "success of contacts " + response.body() + "");

                        } catch (IOException e) {
                            Toast.makeText(mContext, "Error updating Device", Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "error from contacts: " + "error updating device id");
                       // Log.e(TAG, "error from contacts: " + response.code());
                       // Log.e(TAG, "error from contacts: " + response.code());
                       // Log.e(TAG, "error from contacts: " + response.code());
                       // Log.e(TAG, "error from contacts: " + response.code());
                        //mProgressDialog.dismiss();


                        Toast.makeText(mContext, "Contact Not Saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                   // Log.e(TAG, "Device id update failed: " + t.getMessage());
                   // Log.e(TAG, "Device id update failed: " + t.getMessage());
                    // mProgressDialog.dismiss();
                    Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

                }
            });
            //   }
        } ////////////////////////////





      /**  try {
            if(!file1.exists())
                file1.createNewFile();
            FileInputStream inputStream = new FileInputStream(file1);
            Scanner sc = new Scanner(new InputStreamReader(inputStream));
            URL url = new URL(fileUrl);
           // File files = new File(url.toURI());
          //  if(!files.exists())
            //    files.createNewFile();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            OutputStream output = connection.getOutputStream();
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                output.write(line.getBytes());
                output.flush();
            }
            output.close();
            sc.close();
        }catch (IOException ex){
            Log.e(TAG, "Error Occured at Service send 2 " + ex.getMessage());
            Log.e(TAG, "Error Occured at Service send 2 " + ex.getMessage());
            Log.e(TAG, "Error Occured at Service send 2 " + ex.getMessage());
        }//catch (URISyntaxException x){ **/
         //   Log.e(TAG, "Error Occured at Service send 1 " + x.getMessage());
          //  Log.e(TAG, "Error Occured at Service send 1 " + x.getMessage());
          //  Log.e(TAG, "Error Occured at Service send 1 " + x.getMessage());
       // }


    }

        private void retrieveContacts(){

    }

    private class Background_downloadContact extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            List<String> list;
            if (mSharedPref.getListHollaContact() != null){
                if (mSharedPref.getListHollaContact().size() > 0){
                    list = mSharedPref.getListHollaContact();
        }
        else
            list = mSharedPref.getListHollaContact();
    }
     else
         list = mSharedPref.getListHollaContact();
           // List<String> list = mSharedPref.getListUsernames();
            Log.e(TAG, "List contents "+list);
           // int count = 1;
            if(list != null){
                for(int i=0;i<list.size();i++) {
                   // receiveNotification(list.get(i),"");
                    while(mCount != 5);
                    mCount = 1;
                    List<String> mList = new ArrayList<String>();
                    mList = list;
                    mList.remove(list.get(i));
                    String user = new GsonBuilder().create().toJson(mList, List.class);
                    mSharedPref.setListHollaContact(user);
                }
            }


            return "Finished";
        }

        @Override
        protected void onPostExecute(String results) {

        }
    }

    private void receiveNotification(final String username, String userNotification){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <List<NotificationModel>> call = hollaNowApiInterface.receiveNotification(username);

        call.enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.code() == 200) {
                    NotificationModel model = null;
                    if(response.body().size() > 0) {
                        model = response.body().get(0);
                        // mSharedPref.setNotificationUser(bips.toString());
                        //Type collectionType = new TypeToken<Notification_>(){}.getType();
                        //List<bimps> model = new GsonBuilder().create().fromJson(response.body().toString(), List.class);

                        Post_Model mode = null;
                        try {
                            mode = new GsonBuilder().create().fromJson(response.body().get(0).getNotification(), Post_Model.class);
                           // if (mode != null)
                               // if (mode.getmVerify() != null)
                                  //  Log.e(TAG, "Whats received on the notification " + mode.getmVerify());
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "JsonSyntaxException Occured here " + e.getMessage());
                        }
                        if (mode == null)
                            mode = new Post_Model();
                        // Log.e(TAG, "Whats received on the notification "+b.getmVerify());
                        //if(mSharedPref.getCurrentUser().getUsername().equals(b.getUsername()))
                        // mSharedPref.setNotificationUser(b.toString());
                        mCount = 3;
                        notifyUser(username, mode, model);

                    }}else{
                    Log.e(TAG, "Whats received on the notification "+response.code());
                    Log.e(TAG, "Whats received on the notification "+response.code());
                    mCount = 5;
                }


            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                Log.e(TAG, "Failure "+t);
                mCount = 5;
                // bipList = helper.allNotification();

            }

        });
    }

    private void notifyUser(final String username, final Post_Model mode,final NotificationModel model1){
       // bip = mSharedPref.getNotificationUser();
        //bip
      /**  String[] mUsernames = null;
        String[] mCheck = null;
        int j = 0;
        if(mode.getmVerify() == null)
            mode.setmVerify("no");

        try {
            if (mSharedPref.getNotificationUser().getmVerify().equals("yes"))
                if (mode.getmUsernames() != null) {
                    for (int i = 0; i < mode.getmUsernames().length; i++) {
                        if (mode.getmUsernames()[i].equals(mSharedPref.getCurrentUser().getUsername())) {
                            j = 5;
                           // Log.e(TAG, "I saw it " + mode.getmUsernames()[i]);
                            break;
                        }
                    }
                    if (j == 5) {
                        try {
                            mUsernames = mode.getmUsernames();
                            mUsernames[mode.getmUsernames().length - 1] = mSharedPref.getCurrentUser().getUsername();
                            mode.setmUsernames(mUsernames);
                            mCheck = mode.getPhones();
                            mCheck[mode.getmUsernames().length - 1] = "no";
                            mode.setmPhone(mCheck);
                           // Log.e(TAG, "I didnt see it ");

                            j = 0;
                        } catch (NullPointerException ex) {
                            Log.e(TAG, "Writting to a null array ");
                            if (mCheck == null) {
                                mCheck = new String[]{"no"};
                                mode.setmPhone(mCheck);
                            }
                        }
                    }
                } else {
                    mUsernames = new String[]{mSharedPref.getCurrentUser().getUsername()};
                    mode.setmUsernames(mUsernames);
                    mCheck = new String[]{"no"};
                    mode.setmPhone(mCheck);
                   // Log.e(TAG, "Im new to HollaNow ");
                }
        }catch (NullPointerException ee){
            if(mode.getmVerify() == null)
                mode.setmVerify("no");
            Log.e(TAG, "NullException just occurred ");
        } **/

        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <JsonElement> call = hollaNowApiInterface.notifyUserContact(username, mode.toString());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {

                    Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                   // bimps bip =   model.getNotification().get(0);
                   // Log.e(TAG, "Whats saved on the notification "+response.code());
                    if(mSharedPref.getCurrentUser().getUsername().equals(model1.getUsername()))
                         mSharedPref.setNotificationUser(mode.toString());
                    mCount = 5;
                    //Log.e(TAG, "Whats saved on the notification "+bip.toString());
                }else{
                    Log.e(TAG, "Whats saved on the notification "+response.code());
                    mCount = 5;
                   // Log.e(TAG, "Whats saved on the notification "+response.code());
                   // Log.e(TAG, "Whats saved on the notification "+username);
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                mCount = 5;


                // bipList = helper.allNotification();

            }

        });
    }

    public void hollaNotifyUsers(){
        Intent intnt = new Intent();
        intnt.setAction(CallDiaryFragment.BROADCAST_ACTION);
        intnt.putExtra("broadcast_HollaContact1","refresh");
        mContext.sendBroadcast(intnt);
    }


    private class syncDb extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... string) {
            // getWishes();

            // private void getWishes(){
            bimps mbip = null;
            Set<String> names = new HashSet<String>();
            List<String> usernames = null;


           // searchPhoto("08154965498");
           // searchVisibilty();

            Log.e(TAG, "This is getWishes  "+mbip);
           // if(bip == null)
             //   bip = new ArrayList<bimps>();
            if(mSharedPref.getListUsernamesToTest() != null)
             if(mSharedPref.getListUsernamesToTest().size() <= 0)
                 usernames = mSharedPref.getListUsernames();
            else if(mSharedPref.getListUsernamesToTest().size() > 0)
                usernames = mSharedPref.getListUsernamesToTest();

            if(usernames == null)
                usernames = mSharedPref.getListUsernames();
            for(int i=0;i<usernames.size();i++)
                names.add(usernames.get(i));
            usernames.clear();
            Iterator<String> it = names.iterator();
            while(it.hasNext())
                usernames.add(it.next());

          //  Log.e(TAG, "This is getWishes  2 "+usernames);
            if(usernames != null){
               // if(usernames.contains(mSharedPref.getCurrentUser().getUsername()))
               //     usernames.remove(mSharedPref.getCurrentUser().getUsername());
                //getLikes(mSharedPref.getCurrentUser().getUsername(), "Happy Weekend", 2, 2);
                for(int i=0;i<usernames.size(); i++){
                    String username = usernames.get(i);
                    if(usernames.get(i) != null) {
                        receiveShoutOutMessages(usernames.get(i));
                        while(count < i);
                        Log.e(TAG, "This is getWishes  3 "+i);
                        Log.e(TAG, "Username is  "+usernames.get(i));
                        // if(mbip != null)
                        //    bip.add(mbip);
                    }
                    List<String> list = new ArrayList<String>();
                    list = usernames;
                    list.remove(username);
                    String user = new GsonBuilder().create().toJson(list, List.class);
                    mSharedPref.setListUsernamesToTest(user);

                }

            }

            //  }

            return "Finish";
        }

        @Override
        protected void onPostExecute(String bytes) {
            super.onPostExecute(bytes);
            Log.e(TAG, "Post Execute here  " + bytes);
            // Log.e(TAG, "Post Execute here  " + bytes);
            // Log.e(TAG, "Post Execute here  " + bytes);
            if(!shout1.isEmpty())
                shout1.clear();
            if(shoutOutData != null){
                if(shoutOutData.size() > 0)
                for(int i=0;i<shoutOutData.size();i++){// for a particular username
                  //  Log.e(TAG, "Post Execute here  " + shoutOutData.size());
                    for(int j=0;j< shoutOutData.get(i).getMessage().size(); j++) {// iterate and get post sent by the user
                        String msg =   shoutOutData.get(i).getMessage().get(j);
                        String date =   shoutOutData.get(i).getmMsgDate().get(j);
                        String sender = shoutOutData.get(i).getmUser();


                        SimpleDateFormat format  = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
                        Calendar cal = Calendar.getInstance();
                        final String strTimeFormate = "h:mm aa";
                        final String strDateFormate = "dd/MM/yyyy  h:mm aa";
                        Date date1 = null;
                        try {
                            date1 = ((Date) format.parse(date));
                        }catch (ParseException ex){

                        }
                        Calendar now = Calendar.getInstance();
                        long msgTimeMills = date1.getTime();
                        cal.setTimeInMillis(msgTimeMills);

                        if((now.get(Calendar.DATE) - cal.get(Calendar.DATE)) == 1
                                && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                                && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))) {
                        //    Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                            date = "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal);
                        }
                        else if((now.get(Calendar.DATE) == cal.get(Calendar.DATE))
                                && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                                && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))){
                          //  Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                            date = "Today at " + android.text.format.DateFormat.format(strTimeFormate, cal);
                        }


                        String receiver = "Hello "+mSharedPref.getCurrentUser().getName();
                        ShoutOutModel sh = new ShoutOutModel();
                        sh.setDate(date);
                        sh.setMessage(msg);
                        sh.setReceiver(receiver);
                        sh.setSender(sender);


                        shout1.add(sh);
                    }

                }
            }

            if(!shout.isEmpty())
                shout.clear();
            shout.addAll(shout1);
           // if(shout != null && shout.size() > 0)
            List<ShoutOutModel> model = mSharedPref.getListShout(); // collect all the shoutout post
            List<ShoutOutModel> post = new ArrayList<ShoutOutModel>();
            if(model != null)
            for(int i=0;i<model.size();i++){
                ShoutOutModel mode = model.get(i);
                if(mode.getSender().equals(mSharedPref.getCurrentUser().getUsername()))
                    shout.add(mode); //pls dont delete my post
            }
              //  mSharedPref.setListShout(shout.toString());
              //  Intent intnt = new Intent();
              //  intnt.setAction(FragmentShoutOut.BROADCAST_ACTION);
              //  intnt.putExtra("broadcast_intent","refresh");
              //  mContext.sendBroadcast(intnt);

            // return bip;
        // sound notification here for shoutout
            Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotification2 = new Notification.Builder(getApplicationContext())
                    .setContentTitle("New Shoutout to you")
                    .setSound(note)
                    .setContentText("Tap to see");

            int color1 = 0XFF9800;
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);
            mNotification2.setSmallIcon(R.drawable.notification12);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mNotification2.setColor(color1);
                // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
                // mNotification.setLargeIcon(bm);
            } else
                mNotification2.setSmallIcon(R.drawable.notification2);

            mNotification2.setContentIntent(pendingIntent2)/**.setColor(color1)**/.setLargeIcon(bm);
         if(shoutoutCount > 0) {
             NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
             mNotificationManager.notify(mId3, mNotification2.build());
         }
        }
    }

    private void receiveShoutOutMessages(final String username){
        mBip = null;
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<List<NotificationModel>> call = hollaNowApiInterface.receiveNotification(username);

        call.enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.code() == 200) {
                    NotificationModel model = null;
                    Log.e(TAG, "Whats received on the notification 3 "+response.body().size());
                    Post_Model mode = null;
                    if(response.body().size() > 0) {
                        model = response.body().get(0);
                        // mSharedPref.setNotificationUser(bips.toString());
                        //Type collectionType = new TypeToken<Notification_>(){}.getType();
                        //List<bimps> model = new GsonBuilder().create().fromJson(response.body().toString(), List.class);
                        //  JsonElement model1 = new GsonBuilder().create().fromJson(response.body().toString(), JsonElement.class);
                        //  Notification_ model2 = new Notification_(model);
                        Log.e(TAG, "Whats received on the notification 3 " + response.code());
                      //  Log.e(TAG, "Whats received on the notification 3 " + response.body().size());
                      //  Log.e(TAG, "Whats received on the notification 3 " + model.toString());
                      //  Log.e(TAG, "Whats received on the notification 3 " + model.getUsername());
                        Log.e(TAG, "Whats received on the notification 3 " + model.getNotification());

                        List<List<String>> items = null;
                        List<String> msg = null;
                        try {
                            mode = new GsonBuilder().create().fromJson(model.getNotification(), Post_Model.class);
                            if (mode != null) {
                                // if(b.getMessage() != null)
                                mode.setmUser(model.getUsername());
                                notifyUser1(username, mode);
                                // if(mBip != null)
                                //    mBip.setmUser(response.body().get(0).getUsername());
                                //return bbip;
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "JsonSyntaxException Occured here " + e.getMessage());
                        } //catch (NullPointerException ex) {
                          //  Log.e(TAG, "NullPointerException Occured here " + ex.getMessage());
                       // }


                    }else {
                        mode = new Post_Model();
                        mode.setmUser(username);
                        notifyUser1(username, mode);
                    }
                }else{
                    Log.e(TAG, "Whats received on the notification "+response.code());
                    Log.e(TAG, "Whats received on the notification "+response.code());
                    count++;
                }

            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                Log.e(TAG, "Failure  receive "+t);
                count++;
                // bipList = helper.allNotification();

            }

        });
        //  if(adapter != null)
        //      adapter.notifyDataSetChanged();
        //  else {
        //   adapter = new ShoutoutAdapter(mContext, shoutOutData);
        //  adapter.notifyDataSetChanged();
        //   }
        // return mBip;
    }


    private void notifyUser1(final String username, final Post_Model mode) {

        HashMap<String, List<String>> shoutOut = mode.getShoutOut();
        List<String> message = new ArrayList<String>();
        List<List<String>> user = new ArrayList<List<String>>();
        List<List<String>> cloneUser = new ArrayList<List<String>>();
        if (shoutOut != null) {
            for (Map.Entry<String, List<String>> m : shoutOut.entrySet()) {//
                message.add(m.getKey());
                user.add(m.getValue());
                Log.e(TAG, "Waris in message " + message.toString());
                Log.e(TAG, "Waris in user " + user.toString());
            }
        } else if (shoutOut == null)
            shoutOut = new HashMap<String, List<String>>();
        if (mode.getMessage() != null) //check number of messages posted and number of messages read by people
            if (mode.getMessage().size() > message.size()) {
                int j = mode.getMessage().size() - message.size();
                if (!message.isEmpty())
                    message.clear();
                message.addAll(mode.getMessage());
                for (int i = 0; i < j; i++) {// iterate and add the current user username to the Hasmap indicating
                    // the user has read the message
                    List<String> current = new ArrayList<String>();
                    current.add(mSharedPref.getCurrentUser().getUsername());
                    user.add(current);
                    shoutoutCount++;
                }

            }



        for (int i = 0; i < message.size(); i++) { // check wether this user has read the message, if no
            // add the user to the list of users that have read the message
            List<String> receiver = user.get(i);
            if (!receiver.contains(mSharedPref.getCurrentUser().getUsername())) {
                receiver.add(mSharedPref.getCurrentUser().getUsername());
                shoutoutCount++;
            }
            cloneUser.add(receiver); // clone the usernames that have read the message
        }
        if (shoutOut != null)
            if (!shoutOut.isEmpty())
                shoutOut.clear();
        for (int i = 0; i < message.size(); i++) {
            shoutOut.put(message.get(i), cloneUser.get(i));
        }
        //shoutOut.putAll(message,cloneUser);
        mode.setmShoutOut(shoutOut);

        final Post_Model model = deleteMsg(username, mode); // use date to delete users' post and return new non-expired post

        // Add Users that recently liked ur post
        /////////////////////////////////////////////////////////////////////////////////////////

        HashMap<String, List<String>> likes = model.getList();

        List<String> message_likes = new ArrayList<String>();
        List<List<String>> username_likes = new ArrayList<List<String>>();

        if (likes != null) {
            for (Map.Entry<String, List<String>> m : likes.entrySet()) {//
                message_likes.add(m.getKey());
                username_likes.add(m.getValue());
             //   Log.e(TAG, "Waris in message likes " + message_likes.toString());
             //   Log.e(TAG, "Waris in user likes" + username_likes.toString());
            }
        } else if (likes == null)
            likes = new HashMap<String, List<String>>();
        List<ShoutOutModel> shoutoutModel = mSharedPref.getListShout();
        if(shoutoutModel != null )
        for(int i =0;i<shoutoutModel.size();i++) { // iterate over Recycler view showing
            String username_ = model.getmUser();
            if(username_.equals(shoutoutModel.get(i).getUsername())){
                List<String> date = model.getmMsgDate();
                List<Integer> updateLikes = model.getNo_likes();
                String postDate = shoutoutModel.get(i).getDate();
                List<Integer> pre_Likes = mSharedPref.getPre_Likes();
                for(int j=0;j<date.size();j++){ // iterate over get the particular post by user
                    if(postDate.equals(date.get(j))){
                        int like = shoutoutModel.get(i).getNoOfLikes();
                        int mLikes = pre_Likes.get(i);
                        int diff = mLikes - like;
                        if(diff == 1) {    // subtract 1 from main like
                           int likeUpdate = updateLikes.get(j);
                           likeUpdate = likeUpdate - 1;
                           updateLikes.add(j,likeUpdate);
                           model.setNo_likes(updateLikes);
                        }else if(diff == -1){
                            int likeUpdate = updateLikes.get(j);
                            likeUpdate = likeUpdate + 1;
                            updateLikes.add(j,likeUpdate);
                            model.setNo_likes(updateLikes);
                        }

                    }
                }
            }
        }

       ///////////////////////////////////////////////////////////////////// ////////////////////////
        //Log.e(TAG, "Whats saved on the notification " + bip.getMessage().toString());
        if(model.getMessage() != null) {// message to store must not be null;
            if (model.getMessage().size() >= 0) { // Must have message to store
                HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
                Call<JsonElement> call = hollaNowApiInterface.notifyUserContact(username, model.toString());

                call.enqueue(new Callback<JsonElement>() {

                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.code() == 200) {

                            // Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                            // bimps bip =   model.getNotification().get(0);
                            Log.e(TAG, "Whats saved on the notification 3 " + response.code());
                            //  if(mSharedPref.getCurrentUser().getUsername().equals(model1.getUsername()))
                            //     mSharedPref.setNotificationUser(bip.toString());
                           // mBip = bimp;
                            List<ShoutOutModel> model2 = mSharedPref.getListShout();
                          List<ShoutOutModel> model = new ArrayList<ShoutOutModel>();
                          String sender = mode.getmUser();
                            for(int i = 0;i<mode.getMessage().size();i++){ // add all the post of current user to
                                ShoutOutModel m = new ShoutOutModel();
                                Log.e(TAG, "Whats saved on the notification here " + mode.getMessage().toString());
                               Log.e(TAG, "Whats saved on the notification here  1 " + mode.getmMsgDate().toString());
                                m.setSender(sender);
                                try {
                                    m.setMessage(mode.getMessage().get(i));
                                    m.setDate(mode.getmMsgDate().get(i));
                                }catch (IndexOutOfBoundsException ex){

                                }
                                m.setReceiver(mSharedPref.getCurrentUser().getUsername());
                                m.setUsername(username);
                                model.add(m);
                                // count.add(i);
                            }

                            if(model2 != null)
                            for(int i=0;i<model2.size();i++){ //remove all current users post
                                if(model2.get(i).getSender().equals(sender))
                                    model2.remove(i);
                            }

                            for(int j=0;j<model.size();j++){
                                model2.add(model.get(j));
                            }

                            if(model2 != null)
                            mSharedPref.setListShout(model2.toString());

                            Intent intnt = new Intent();
                            intnt.setAction(FragmentShoutOut.BROADCAST_ACTION);
                            intnt.putExtra("broadcast_intent","refresh");
                            mContext.sendBroadcast(intnt);
                           // Log.e(TAG, "what to broadcast  "+mSharedPref.getListShout());

                            //shoutOutData.add(mBip);

                            Log.e(TAG, "Whats saved on the notification 3 " + mode.toString());
                            count++;

                        } else {
                            Log.e(TAG, "Whats saved on the notification 3 " + response.code());
                           // Log.e(TAG, "Whats saved on the notification 3 " + response.code());
                           // Log.e(TAG, "Whats saved on the notification 3 " + username);
                            mBip = null;
                            count++;
                        }


                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.e(TAG, "Failure notify " + t);
                        mBip = null;
                        count++;

                    }

                });
            } else {
                count++;
            }
        }else
            count++;
        //return mBip;
    }





    public Post_Model deleteMsg(final String username, final Post_Model mode){
        List<String> date = mode.getmMsgDate();
        List<String> cloneDate = new ArrayList<String>();
        List<String> msg = mode.getMessage();
        List<String> msgDate = new ArrayList<String>();
        if(date == null || date.size() <= 0){
           // Log.e(TAG, "Pls, delete the message here "+mode.getMessage()+" "+mode.getShoutOut()+" "+date);
            if(msg != null)
                mode.getMessage().clear();
            if(mode.getShoutOut() != null)
                mode.getShoutOut().clear();
            if(mode.getNo_likes() != null)
                mode.getNo_likes().clear();
        }
        ///////////////////////////////////////////////////////////////////////////////////
        if(mode.getmMsgDate() != null || mode.getMessage() != null || mode.getNo_likes() != null)
        if(date.size() != msg.size()){
            mode.getMessage().clear(); // delete message
            mode.getShoutOut().clear(); //delete msg with thw corresponding username that saw it.
            mode.getmMsgDate().clear(); //
            mode.getNo_likes().clear();
        }
       ////////////////////////////////////////////////////////////////////////////////////////

        if(date != null)
            for(int i = 0; i < date.size(); i++){
                String mDate = null;
                String key = null;
            try {
                 mDate = date.get(i);
                Log.e(TAG, "For the first  " + i);
                 key = msg.get(i);
                Log.e(TAG, "For the second " + i);
                Log.e(TAG, "For the third  " + date.size());
            }catch (IndexOutOfBoundsException ex){
                if(mode != null) {
                    mode.getMessage().clear(); // delete message
                    mode.getShoutOut().clear(); //delete msg with thw corresponding username that saw it.
                    mode.getmMsgDate().clear(); //
                    mode.getNo_likes().clear();
                }
            }
                SimpleDateFormat format  = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
                Calendar cal = Calendar.getInstance();
                final String strTimeFormate = "h:mm aa";
                final String strDateFormate = "dd/MM/yyyy  h:mm aa";
                Date date1 = null;
                try {
                    date1 = ((Date) format.parse(mDate));
                }catch (ParseException ex){
                    Log.e(TAG, "ParseException Occurred " +ex.getMessage());
                }
                Calendar now = Calendar.getInstance();
                long msgTimeMills = date1.getTime();
                cal.setTimeInMillis(msgTimeMills);

                if((now.get(Calendar.DATE) - cal.get(Calendar.DATE)) == 1
                        && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                        && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))) {
                  //  Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                    cloneDate.add(mDate);
                }
                else if((now.get(Calendar.DATE) == cal.get(Calendar.DATE))
                        && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                        && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))){
                   // Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                   // cloneDate.add(mDate);

                    //date.remove(i);
                    mode.getMessage().remove(i); // delete message
                    mode.getShoutOut().remove(key); //delete msg with thw corresponding username that saw it.
                    mode.getmMsgDate().remove(i); //  delete date when the post was made.
                    if(mode.getNo_likes() != null)
                         mode.getNo_likes().remove(i);

                }


            }
            mode.setMsgDate(cloneDate);
        return mode;
    }



    private void getLikes(String username, String msg, int likes, int no_post){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.postLikes(username,msg,likes,no_post);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    // mSharedPref.setNotificationUser(bips.toString());
                    //Type collectionType = new TypeToken<Notification_>(){}.getType();
                    //List<bimps> model = new GsonBuilder().create().fromJson(response.body().toString(), List.class);
                    //  JsonElement model1 = new GsonBuilder().create().fromJson(response.body().toString(), JsonElement.class);
                    //  Notification_ model2 = new Notification_(model);
                    Log.e(TAG, "Whats received on the notification 5 "+response.code());
                    Log.e(TAG, "Whats received on the notification 5 "+response.body());
                    Log.e(TAG, "Whats received on the notification 5 "+response.body().toString());


                }else{
                    Log.e(TAG, "Whats received on the notification 5 "+response.code());
                    Log.e(TAG, "Whats received on the notification 5 "+response.code());
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure 5 "+t);
                Log.e(TAG, "Failure 5 "+t);
                // bipList = helper.allNotification();

            }

        });
    }

    private String dateFormate(String date){
        SimpleDateFormat format  = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
        Calendar cal = Calendar.getInstance();
        final String strTimeFormate = "h:mm aa";
        final String strDateFormate = "dd/MM/yyyy  h:mm aa";
        Date date1 = null;
        try {
            date1 = ((Date) format.parse(date));
        }catch (ParseException ex){

        }

        Calendar now = Calendar.getInstance();
        long msgTimeMills = date1.getTime();

        date = String.valueOf(msgTimeMills);

        return date;
    }


    private boolean isCallNote() {

        final boolean[] isCallNote = {false};


        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
            Call<JsonElement> call = hollaNowApiInterface.getCallNoteByPhone("08161137527", "");  // "08057270466"
            call.enqueue(new Callback<JsonElement>() {
                String getNote1;
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    CallNote note = null;
                    Log.e(TAG, "Check response: " +response.code());
                    String noteStreams = null;
                    Log.e(TAG, "success of contacts " + response.code() + "");
                    // Log.e(TAG, "success of contacts " + response.code() + "");
                    //Log.e(TAG, "success of contacts " + response.body() + "");
                     Log.e(TAG, "success of contacts " + response.body() + "");
                      Log.e(TAG, "success of contacts " + response.body() + "");
                    ListCallNote model =  new GsonBuilder().create().fromJson(response.body().toString(),ListCallNote.class);

                    /**  Log.e(TAG, "success of contacts " + response.code() + "");
                     Log.e(TAG, "success of contacts " + model.getCallNote().get(0).getUsername()+ "");
                     Log.e(TAG, "success of contacts " + model.getCallNote().get(0).getName() + "");
                     Log.e(TAG, "success of contacts " + model.getCallNote().get(0).getNote() + ""); **/

                    try {
                        if (model.getCallNote() != null)
                            note = model.getCallNote().get(0);
                    }catch (NullPointerException e){
                        Log.e(TAG, "NullPointer Exception  " + e.getMessage() + " ");
                    }catch (IndexOutOfBoundsException a){
                        Log.e(TAG, "IndexOutofBound Exception   " + a.getMessage() + " ");
                    }catch (Exception s){
                        Log.e(TAG, "IndexOutofBound Exception   " + s.getMessage() + " ");
                    }

//                    callerUserName = note != null ? note.getUsername() : " ";
                    //  callerUserName = note != null ? note.getUsername() : " ";
                   // callerUserName = note != null ? model.getCallNote().get(0).getUsername() : " ";
//                    callerEmail = note != null ? note.getEmail() : " ";
                   // callerName = note != null ? model.getCallNote().get(0).getName() : callerName1;
                   // callerPhoto = note != null ? note.getPhoto() : null;
                    int unicode = 0x1f60e;
                    String emoji = new String(Character.toChars(unicode));
                    //  callNote = note != null ? getNote1 :  getString(R.string.default_callnote)+"  "+emoji; //note.getNote();
                   // callNote = note != null ? model.getCallNote().get(0).getNote() :  getString(R.string.default_callnote)+"  "+emoji; //note.getNote();
                   // updateUI();
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
//                    Log.e(TAG, t.getMessage());
                   // updateUI();
                }
            });

        }

        return isCallNote[0];
    }


    private void saveCallNote() {
        //finishBtn.setText("...");
        //TelephonyManager tmr;

        //  String chars = noteEditText.getText().toString().trim();
        // byte[] cv = chars.getBytes();
        /** for(int i=0;i<cv.length;i++){
         char c = chars.charAt(i);

         Log.e(TAG, "Test the emoji of these here 11111111111 " +c);
         //  Log.e(TAG, "Test the emoji of these here 11111111111 " +Integer.decode(chars));
         Log.e(TAG, "Test the emoji of these here 11111111111 " +(int)c);
         Log.e(TAG, "Test the emoji of these here 11111111111 " +cv[i]);
         Log.e(TAG, "Test the emoji of these here 11111111111 " +~cv[i]);
         }**/
        //  byte[] cc1 = {73,32,119,105,115,104,32,85,32,103,111,111,100,32,-16,-97,-104,-115};
        //   String ss1 = new String(cc1);
        //  Log.e(TAG,"Check emoji out "+ss1);
        // noteEditText.setText(ss1);







        //byte[] cc1 = {73,32,119,105,115,104,32,85,32,103,111,111,100,32,-16,-97,-104,-115};
        // String ss1 = new String(cc1);
        //Log.e(TAG,"Check emoji out "+ss1);
        // noteEditText.setText(ss1);
        // Log.e(TAG,"Check emoji out "+noteEditText.getText().toString().trim());
        /**    Call<CallNote> call = hollaNowApiInterface.saveCallNote(mSharedPref.getToken(), callNote);
         call.enqueue(new Callback<CallNote>() {

        @Override
        public void onFailure(Call<CallNote> call, Throwable t) {
        //                Log.e(TAG, "failed: "+t.getMessage());
        Toast.makeText(mContext, "Network error. Note not saved. Try again.", Toast.LENGTH_LONG).show();
        }
        });  **/


// At the receiving end, "from" receives the note.

        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class); //token,from,to,note
            Call<JsonElement> call = hollaNowApiInterface.callnotePhone(mSharedPref.getCurrentUser().getToken(),/**"08057270466"**/"08161137527",
                    /**"08154965498"**/"08154965498","Just tested here "/**noteEditText.getText().toString().trim()**/,currentUser.getUsername(),currentUser.getName(),currentUser.getProfilePhoto());

           // Log.e(TAG, "Current User Printout1111: "+token+" "+phone+" "+number+" "+noteEditText.getText().toString().trim()+" "+currentUser.getUsername()+" "+currentUser.getName()+" "+currentUser.getProfilePhoto());

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.code() == 200) {
                        // mProgressDialog.dismiss();
                        Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.code() + "");

                        Toast.makeText(mContext, "Note sent", Toast.LENGTH_SHORT).show();
                       // getDialog().dismiss();
                       // makeCall();
                        //Log.e(TAG, "success of contacts " + response.body().string() + "");
                        // Log.e(TAG, "success of contacts " + response.body().string() + "");
                          Log.e(TAG, "success of contacts " + response.body().toString() + "");
                          Log.e(TAG, "success of contacts " + response.body().toString() + "");

//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "error from contacts: " + "error updating device id");
                        Log.e(TAG, "error from contacts: " + response.code());


                        Toast.makeText(mContext, "Note not saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());

//                    Toast.makeText(getApplicationContext(), "Network error. Try again", Toast.LENGTH_LONG).show();

                }
            });
        }



       // finishBtn.setText("Save &amp; Continue");

    }



}
