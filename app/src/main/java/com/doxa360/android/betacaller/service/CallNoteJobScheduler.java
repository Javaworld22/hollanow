package com.doxa360.android.betacaller.service;

/**
 * Created by Michael on 09/02/19.
 */

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.CallNoteBottomActivity;
import com.doxa360.android.betacaller.CallReceiver;
import com.doxa360.android.betacaller.ContactDetailActivity;
import com.doxa360.android.betacaller.HollaNowApiClient;
import com.doxa360.android.betacaller.HollaNowApiInterface;
import com.doxa360.android.betacaller.NotificationActivity;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.bimps;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("NewApi")
public class CallNoteJobScheduler  extends JobService {

    private static final String TAG = CallNoteJobScheduler.class.getSimpleName();
    private WindowManager windowManager;
    private ImageView close;
    private static ConstraintLayout chatheadView;
    private FrameLayout content;
    private LayoutInflater inflater;
    private boolean isHollaNowUser = false;
    private static int i,j;
    private byte[] noteString;
    private Context mContext;
    private HollaNowSharedPref mSharePref;
    private User currentUser;
    private User callNoteUser;
    private ProgressDialog mProgressDialog;
    private boolean isFinished;
    private AdView mAdView ;
    private AlarmManager am;
    private PendingIntent pendingIntent;
    private BroadcastReceiver mReceiver;
    private String checkBroadcast;
    private String checkOncreate;
    private WindowManager.LayoutParams param;

    String callerId, callerUserName, callerEmail, callerName,callerName1, callerNumber, callerPhoto, callNote, phoneNumber;
    private ImageView mCallerPhoto;
    private static TextView mCallerName;
    private static EmojiconTextView mCallNote;
    private static TextView mCallerTag;
    private static TextView mCallerdate;
    private ImageView callBack;

    private HollaNowDbHelper mDbHelper;

    private boolean jobFinished;



    @Override
    public void onCreate() {
        super.onCreate();
        jobFinished = true;
        i = 0;
        checkOncreate = "onCreate";
        mContext = getApplicationContext();
        Log.e(TAG, "Problem 1: "+phoneNumber);
        windowManager = (WindowManager)getSystemService(Service.WINDOW_SERVICE);
        // inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater = LayoutInflater.from(mContext);
        mDbHelper = new HollaNowDbHelper(getApplicationContext());
        mSharePref = new HollaNowSharedPref(getApplicationContext());
        currentUser = mSharePref.getCurrentUser();
        chatheadView = (ConstraintLayout) inflater.inflate(R.layout.on_screen_call_note, null);
        mAdView =  (AdView) chatheadView.findViewById(R.id.adView8) ;
        content = (FrameLayout) chatheadView.findViewById(R.id.content); //FrameLayout

        int LAYOUT_FLAG;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

      //  params.gravity = Gravity.TOP | Gravity.END;
        params.gravity = Gravity.CENTER ;
       // params.x = 0;
       // params.y = 0;
        param = params;

        close = (ImageView) chatheadView.findViewById(R.id.closeBtn);

        mCallerPhoto = (ImageView) chatheadView.findViewById(R.id.caller_photo);
        mCallerName = (TextView) chatheadView.findViewById(R.id.caller_name);
        mCallNote = (EmojiconTextView) chatheadView.findViewById(R.id.call_note);
        mCallerdate = (TextView) chatheadView.findViewById(R.id.caller_date);
        callBack = (ImageView) chatheadView.findViewById(R.id.call_back);



        AdRequest request = new AdRequest.Builder()
               // .addTestDevice("bbb0eb0fce7bf3a8")  //   AdRequest.DEVICE_ID_EMULATOR
               // .addTestDevice("62E45C4B92D0D341956DA7147CACA43D")    // "E0A7012BF382436CB461659B1F229E03"
                .build();
        request.isTestDevice(mContext);
        mAdView.loadAd(request);
        // mProgressDialog = new ProgressDialog(mContext,R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        // mProgressDialog.setMessage("progress...");
    }


    @Override
    public boolean onStartJob(JobParameters parameteers){
          PersistableBundle bundle =  parameteers.getExtras();

        //Log.e(TAG, "Problem 2: " + phoneNumber);
        //Log.e(TAG, "Problem 3: " + checkOncreate);
        if (checkOncreate == null)
            onCreate();
        i++;







        if (bundle != null) {
            phoneNumber = bundle.getString(Parse_Contact.PHONE_NUMBER);
            //  if(phoneNumber == null)
            //   phoneNumber =  CallReceiver.nos;            //CallReceiver.nos;
            //Log.e(TAG, "Proble 3: " + phoneNumber);
        }
        String phone = phoneNumber;

        Log.e(TAG, "Change to +234 rr: " + phone);
        try {
            if (phone.trim().startsWith("0")) {
                phoneNumber = phone.substring(1);
                Log.e(TAG, "Change to +234 rr: " + phoneNumber);
                phoneNumber = "+234".concat(phoneNumber);
                Log.e(TAG, "Change to +234: " + phoneNumber);
            }
        } catch (NullPointerException cc) {
            Log.e(TAG, "NullPointerException Started here : " + cc.getMessage());
        }
        Log.e(TAG, "Service started: " + phoneNumber);

        /////////// chatheadView = (ConstraintLayout) inflater.inflate(R.layout.on_screen_call_note, null);


        //mAdView =  (AdView) chatheadView.findViewById(R.id.adView8) ;
        // AdRequest request = new AdRequest.Builder()
        //        .addTestDevice("bbb0eb0fce7bf3a8")  //   AdRequest.DEVICE_ID_EMULATOR
        //        .addTestDevice("E0A7012BF382436CB461659B1F229E03")    // "E0A7012BF382436CB461659B1F229E03"
        //       .build();
        // mAdView.loadAd(request);
        // mCallerTag = (TextView) chatheadView.findViewById(R.id.textView2);

        try {
            if (mDbHelper.getContactByPhone(phoneNumber) != null) {
                Parse_Contact contact = mDbHelper.getContactByPhone(phoneNumber);
                callerName = contact.getDisplayName();
                callerName1 = callerName;
                callerPhoto = contact.getThumbnailUrl();
            } else {
                String phone1 = phoneNumber;
                Log.e(TAG, "Change to +234 rr: " + phone);
                if (phone1.trim().startsWith("+234")) {
                    phoneNumber = phone1.substring(4);
                    Log.e(TAG, "Change to +234 rr: " + phoneNumber);
                    phoneNumber = "0".concat(phoneNumber);
                    Log.e(TAG, "Change to +234: " + phoneNumber);
                }
                callerName = phoneNumber;
                callerName1 = callerName;
            }
        } catch (Exception e) {

        }
        //  if(!CallReceiver.isMissed)
        //    mCallerTag.setText("Calling");


        if (CallReceiver.isMissed) { ///else if
            if (j++ == 4) {
                i++;
                j = 0;
            }
            //  mCallerTag.setText("Called "+CallReceiver.number_of_calls);
        }

        //  else if(CallReceiver.callEnded == 2)
        //     mCallerTag.setText("Received");
        String check_for_dataString = null;
        int check_for_dataInt = 0;
        //if (intent.getExtras() != null) {
        //    check_for_dataString = intent.getExtras().getString("back_service");
        //    check_for_dataInt = intent.getExtras().getInt("check_Integer");
        //}

        updateUI();
        if (check_for_dataString == null && check_for_dataInt == 0)
            isHollaNowUser = isCallNote();
        else if (check_for_dataString.equals("from_alarmreciever") && check_for_dataInt == 30) {
            String noteJSon = bundle.getString("callnote");
            Type listType = new TypeToken<ArrayList<CallNote>>() {
            }.getType();
            List<CallNote> callNote2 = new GsonBuilder().create().fromJson(noteJSon, listType);
            int size = callNote2.size();
            CallNote note1 = callNote2.get(size - 1);
            callerUserName = note1 != null ? note1.getUsername() : " ";

            callerName = note1 != null ? note1.getName() : callerName1;
            callerPhoto = note1 != null ? note1.getPhoto() : null;

            int unicode = 0x1f60e;
            String emoji = new String(Character.toChars(unicode));

            callNote = note1 != null ? note1.getNote() : getString(R.string.default_callnote) + "  " + emoji; //note.getNote();
            updateUI();
            sendNotification(callNote2);
        }


        content.setOnClickListener(new View.OnClickListener() {
            // @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(chatheadView.getParent() != null)
                   windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteJobScheduler.class));
                CallReceiver.number_of_calls = 0;

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // isCallNote();
                if(chatheadView.getParent() != null)
                   windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteJobScheduler.class));
                CallReceiver.number_of_calls = 0;
            }
        });

        callBack.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                if(chatheadView.getParent() != null)
                    windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteJobScheduler.class));
                List<Parse_Contact> list = mSharePref.getHollaContact();
                String name = null;
                String username = null;
                String phone = null;
                String photo = null;
                for(int i = 0;i < list.size();i++){
                    Log.e(TAG, "Please Count  "+i);
                    Log.e(TAG, "Please Count  "+list.size());
                    if(list.get(i).getSerialUser().getPhone().equals(phoneNumber)){
                        name = list.get(i).getSerialUser().getName();
                        username = list.get(i).getSerialUser().getUsername();
                        phone = list.get(i).getSerialUser().getPhone();
                        photo = list.get(i).getSerialUser().getProfilePhoto();
                        Log.e(TAG, "Yes 00000  ");
                        break;
                    }if(i >= list.size()-1) {
                        name = callerUserName;
                        username = callerUserName;
                        phone = phoneNumber;
                        photo = callerPhoto;
                        Log.e(TAG, "No 00000  ");
                    }
                }

                goToActivity(CallNoteBottomActivity.class,phone,name,username,photo);
            }
        });

        content.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(chatheadView.getParent() != null)
                    windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteJobScheduler.class));
                List<Parse_Contact> list = mSharePref.getHollaContact();
                for(int i = 0;i < list.size();i++){

                    if(list.get(i).getSerialUser().getPhone().equals(phoneNumber)){

                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(BetaCaller.CONTACT_ID, 1);
                        intent.putExtra(BetaCaller.CONTACT_NAME, list.get(i).getSerialUser().getName());
                        intent.putExtra(BetaCaller.CONTACT_PHONE, list.get(i).getSerialUser().getPhone());
                        intent.putExtra(BetaCaller.CONTACT_PHOTO, list.get(i).getSerialUser().getProfilePhoto());
                        mContext.startActivity(intent);
                        break;
                    }if(i >= list.size()-1) {
                        Intent intent = new Intent(mContext, ContactDetailActivity.class);
                        intent.putExtra(BetaCaller.CONTACT_ID, 1);
                        intent.putExtra(BetaCaller.CONTACT_NAME, callerUserName);
                        intent.putExtra(BetaCaller.CONTACT_PHONE, phoneNumber);
                        intent.putExtra(BetaCaller.CONTACT_PHOTO, callerPhoto);
                        mContext.startActivity(intent);
                    }
                }

            }
        });
//        windowManager.updateViewLayout(mCallerTag,params );

        if (bundle != null) {
            CallNote cNote = mSharePref.getLastCallnote();
           // if (intent.getExtras() != null)
                if (bundle.getString("callnote_service") != null) {
                    if (bundle.getString("callnote_service").equals("Just show callnote notification")) {
                        if (cNote != null) {
                            callerUserName = cNote != null ? cNote.getUsername() : " ";

                            callerName = cNote != null ? cNote.getName() : callerName1;
                            callerPhoto = cNote != null ? cNote.getPhoto() : null;

                            int unicode = 0x1f60e;
                            String emoji = new String(Character.toChars(unicode));

                            callNote = cNote != null ? cNote.getNote() : getString(R.string.default_callnote) + "  " + emoji; //note.getNote();
                            mSharePref.setLastCallnote(null);
                            if (chatheadView != null) {
                                windowManager.addView(chatheadView, param);
                                windowManager.updateViewLayout(chatheadView, param);
                            }
                            updateUI();
                        }// else
                           // onDestroy();
                    } else {
                        if (chatheadView != null) {
                            windowManager.addView(chatheadView, param);
                            windowManager.updateViewLayout(chatheadView, param);
                        }
                    }
                } else {
                    if (chatheadView != null) {
                        Log.e(TAG, "ChatheadView  "+chatheadView.isShown());
                        if(!chatheadView.isShown())
                        windowManager.addView(chatheadView, param);
                        else
                        windowManager.updateViewLayout(chatheadView, param);
                    }
                }
        } else{
            if (chatheadView != null) {
                windowManager.addView(chatheadView, param);
                windowManager.updateViewLayout(chatheadView, param);
            }
        }
      jobFinished(parameteers,jobFinished);
        return jobFinished;
    }


    private boolean isCallNote() {

        final boolean[] isCallNote = {false};
        // if(checkBroadcast != null)
        //  if( checkBroadcast == null){
        try {
            String phone = mSharePref.getPhoneEmoji();
            String p = "";
            if (!phone.trim().equals("")) {
                if (phone.startsWith("+234")) {
                    p = phone.substring(4);
                    // "0".concat(phone);
                    //  contactNumber = contactNumber.replaceFirst("[+234]", "0");
                    phone = "0" + p;
                    mSharePref.setPhoneEmoji(phone);
                    Log.e(TAG, "phone number is :" + phone);
                }
            }
            // phone = mSharePref.getCurrentUser().getPhone();

            else if (!(phone = mSharePref.getCurrentUser().getPhone()).equals("")) {
                if (phone.startsWith("+234")) {
                    p = phone.substring(4);
                    // "0".concat(phone);
                    //  contactNumber = contactNumber.replaceFirst("[+234]", "0");
                    phone = "0" + p;
                    mSharePref.setPhoneEmoji(phone);
                    Log.e(TAG, "phone number22 is :" + phone);
                }
            }
        } catch (NullPointerException ss) {
            Log.e(TAG, "NullpointerException Error occured  :" + ss.getMessage());
        }

        Log.e(TAG, "Begin Callnote Processing: ");
        Log.e(TAG, "Begin Callnote Processing: ");

       // jobFinished = false;
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        // if(mSharePref.getCurrentUser() != null)
        if (MyToolBox.isNetworkAvailable(getApplicationContext())) { //

            //Log.e(TAG, "At isCallNote0: " + phoneNumber + " " + mSharePref.getToken() + " " + mSharePref.getUsername());
            Call<JsonElement> call = hollaNowApiInterface.getCallNoteByPhone(mSharePref.getUsername(), mSharePref.getToken());  // "08057270466"
            call.enqueue(new Callback<JsonElement>() {
                String getNote1;

                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    Log.e(TAG, "success of contacts " + response.code() + "");
                    if (response.code() == 200){
                       // jobFinished = false;
                        CallNote note = null;
                        List<CallNote> callNote1 = new ArrayList<CallNote>();
                        List<CallNote> callNote2 = new ArrayList<CallNote>();
                        Log.e(TAG, "Check response: " + response.code());
                        String noteStreams = null;
                        // Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.body() + "");
                        Type listType = new TypeToken<ArrayList<CallNote>>() {
                        }.getType();

                        List<CallNote> model = new GsonBuilder().create().fromJson(response.body().toString(), listType);

                        // Log.e(TAG, "success of contacts " + model.size() + "");
                        //callNote1 = model;


                        try {
                            if (model != null)

                                for (int i = 0; i < model.size(); i++) {
                                    CallNote c = model.get(i);
                                    // Log.e(TAG, "CallNote Messages  " + c.getNote() + " " + c.getPhone());
                                    if (model.size() - i == 1)
                                        note = model.get(i);

                                    String s = c.getNote();
                                    int number_of_byte = 0;
                                    for (int m = 0; m < s.length(); m++) {
                                        char data = s.charAt(m);
                                        if (data == ';')
                                            number_of_byte = number_of_byte + 1;
                                    }
                                    byte[] cv = new byte[number_of_byte];
                                    StringBuilder builder = new StringBuilder();
                                    String message = null;
                                    String rawByte;
                                    int increment = 0;
                                    for (int m = 0; m < s.length(); m++) {
                                        char data = s.charAt(m);
                                        if (data == ';') {
                                            rawByte = builder.toString();
                                            builder = new StringBuilder();
                                            cv[increment] = Byte.decode(rawByte);
                                            increment++;
                                            message = new String(cv);
                                        } else
                                            builder.append(data);

                                    }
                                    //Log.e(TAG, "CallNote Messages 2  " + message);
                                    c.setNote(message);
                                    callNote1.add(c);
                                }

                            Log.e(TAG, "Callnote1 size  " + callNote1.size());
                            callNote2 = confirmCallNote(callNote1);
                            // String chars = noteEditText.getEditableText().toSt;
                            // byte[] cv = chars.getBytes();
                            // int c;
                            // String ss = "";
                            // for(int i=0;i<cv.length;i++) {
                            //    c = cv[i];

                        } catch (NullPointerException e) {
                            Log.e(TAG, "NullPointer Exception  " + e.getMessage() + " ");
                        } catch (IndexOutOfBoundsException a) {
                            Log.e(TAG, "IndexOutofBound Exception   " + a.getMessage() + " ");
                        }//catch (Exception s){
                        //   Log.e(TAG, "IndexOutofBound Exception   " + s.getMessage() + " ");
                        // }

                        try {
                            int size = callNote2.size();
                            CallNote note1 = callNote2.get(size - 1);
                            if(note1 != null)
                                mSharePref.setLastCallnote(note1);
                            callerUserName = note1 != null ? note1.getUsername() : " ";

                            callerName = note1 != null ? note1.getName() : callerName1;
                            callerPhoto = note1 != null ? note1.getPhoto() : null;
                            String date = note1 != null ? note1.getUpdated_at() : null;
                            String fomattedDate = formateCallnoteDate(date);
                            mCallerdate.setText(fomattedDate);
                            int unicode = 0x1f60e;
                            String emoji = new String(Character.toChars(unicode));

                            callNote = note1 != null ? note1.getNote() : getString(R.string.default_callnote) + "  " + emoji; //note.getNote();
                            updateUI();
                            sendNotification(callNote2);
                            //if (size == 0)
                            //   windowManager.removeView(chatheadView);
                        } catch (IndexOutOfBoundsException ex) {
                            //    windowManager.removeView(chatheadView);
                        }
                    }
                }


                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    String err = (t.getMessage() == null)?"Failure, Could not connect to the server":t.getMessage();
                    Log.e(TAG, err);
                    updateUI();
                }
            });

        } else {
            //   if(am == null) {
            // am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            // Intent alermIntent = new Intent(mContext, AlarmReceiver.class);
            // alermIntent.setAction(CallDiaryFragment.BROADCAST_ACTION);
            // alermIntent.putExtra("broadcast_alarm","alarm_service");
            // pendingIntent = PendingIntent.getBroadcast(mContext,0,alermIntent,0);
            // alermIntent.putExtra("pending", pendingIntent);
            // int interval = 8000;
            // am.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),interval,pendingIntent);
            // Toast.makeText(this, "Alarm Set",Toast.LENGTH_SHORT).show();
            //     }

            /**  Intent intent = new Intent(mContext, AlarmReceiver.class);
             intent.setAction(CallDiaryFragment.BROADCAST_ACTION);
             intent.putExtra("broadcast_alarm", "alarm_service");
             Log.e(TAG, "Shift control to AlarmReceiver  ");
             sendBroadcast(intent); **/

           /** new CountDownTimer(12000, 1000) {
                public void onTick(long millisUntilFinishes) {
                    Log.e(TAG, "Work remaining  "+millisUntilFinishes  );
                }

                public void onFinish() {
                    //counter = 0;
                    Log.e(TAG, "Work finished  "  );
                    isCallNote();

                }
            }.start(); */


            //   try {
            //     Thread.sleep(2000);
            //     Log.e(TAG, "Thread is still running  ");
            //  }catch(Exception e){
            //     Log.e(TAG, "Sleep Thread " );
            // }


        }
        // }

        return isCallNote[0];
    }


    private List<CallNote> confirmCallNote(List<CallNote> remoteSaved){
        List<CallNote> callnote = new ArrayList<CallNote>();
        List<CallNote> phoneSaved = mSharePref.getListCallNote();
        int checkCount_for_callnotes = 0;
        // Log.e(TAG, "Saved contacts  " + phoneSaved.toString());
        Iterator<CallNote> phoneSaved_it = null;
        if(phoneSaved != null) {
            for(int i = 0; i < phoneSaved.size(); i++){
                // Log.e(TAG, "Saved Callnote  " + phoneSaved.get(i).getNote());
            }
            phoneSaved_it = phoneSaved.iterator();
            Log.e(TAG, "CallNote phoneSaved  " + phoneSaved.size());
        }
        Log.e(TAG, "CallNote remoteSaved  " +remoteSaved.size());
        try {
            if (phoneSaved == null || phoneSaved.size() == 0) {
                String jSon = new GsonBuilder().create().toJson(remoteSaved, List.class);
                Log.e(TAG, "Saved contacts 1  " + remoteSaved.toString());
                mSharePref.setListCallNote(jSon);
                return remoteSaved;
            }
        }catch(NullPointerException ex){
            String jSon = new GsonBuilder().create().toJson(remoteSaved, List.class);
            Log.e(TAG, "Saved contacts 2  " + remoteSaved.toString());
            mSharePref.setListCallNote(jSon);
            return remoteSaved;
        }
        boolean from,to,note,username,name,updated;
        from = false;
        to = false;
        note = false;
        username = false;
        name = false;
        //photo = false;
        // created = false;
        updated = false;
        Iterator<CallNote> remoteSaved_it = remoteSaved.iterator();
        while (remoteSaved_it.hasNext()){
            CallNote remote = remoteSaved_it.next();
            while (phoneSaved_it.hasNext()) {
                from = false;
                to = false;
                note = false;
                username = false;
                name = false;
                CallNote phone = phoneSaved_it.next();
                if (remote.getNote().equals(phone.getNote())) {
                    note = true;
                    //  Log.e(TAG, "Remote.getNote  " + remote.getNote());
                    //  Log.e(TAG, "Phone.getNote  " + phone.getNote());
                }
                // if(remote.getNote().length() == phone.getNote().length()) {
                //   note = true;
                Log.e(TAG, "Remote.getNote  " + remote.getNote().length());
                Log.e(TAG, "Phone.getNote  " + phone.getNote().length());
                // }
                if (remote.getPhone().equals(phone.getPhone())) {
                    from = true;
                    //  Log.e(TAG, "Remote.getPhone  " + remote.getPhone());
                    //  Log.e(TAG, "Phone.getPhone  " + phone.getPhone());
                }
                // if (remote.getCreated_at().equals(phone.getCreated_at()))
                //   created = true;
                if (remote.getName().equals(phone.getName())) {
                    name = true;
                    // Log.e(TAG, "Remote.getName  " + remote.getName());
                    // Log.e(TAG, "Phone.getName  " + phone.getName());
                }

                // if (remote.getPhoto().equals(phone.getPhoto())) {
                //    photo = true;
                //   Log.e(TAG, "Remote.getPhoto  " + remote.getPhoto());
                //    Log.e(TAG, "Phone.getPhoto  " + phone.getPhoto());
                // }
                if (remote.getReceiverPhone().equals(phone.getReceiverPhone())) {
                    to = true;
                    // Log.e(TAG, "Remote.getReceiverPhone  " + remote.getReceiverPhone());
                    // Log.e(TAG, "Phone.getReceiverPhone  " + phone.getReceiverPhone());
                }
                if (remote.getUpdated_at().equals(phone.getUpdated_at())) {
                    updated = true;
                    // Log.e(TAG, "Remote.getUpdated_at  " + remote.getUpdated_at());
                    // Log.e(TAG, "Phone.getUpdated_at  " + phone.getUpdated_at());
                }
                if (remote.getUsername().equals(phone.getUsername())) {
                    username = true;
                    // Log.e(TAG, "Remote.getUsername  " + remote.getUsername());
                    // Log.e(TAG, "Phone.getUsername  " + phone.getUsername());
                }
                if (!(note && from && name && to && updated && username)) {
                    ++checkCount_for_callnotes;
                    // Log.e(TAG, "CheckCount_for_callnotes  " + checkCount_for_callnotes);
                    // Log.e(TAG, "note  " + note);
                    // Log.e(TAG, "from  " + from);
                    // Log.e(TAG, "name  " + name);
                    // Log.e(TAG, "to  " + to);
                    // Log.e(TAG, "Updated  " + updated);
                    //  Log.e(TAG, "Username  " + username);
                    // Log.e(TAG, "Photo  " + photo);
                }

                if ((note && from && name && to && updated && username)) {
                    //  Log.e(TAG, "Got the match  " +checkCount_for_callnotes);
                    //  Log.e(TAG, "Remote.getNote 1 " + remote.getNote());
                    //  Log.e(TAG, "Phone.getNote  1 " + phone.getNote());
                }

            }
            if(checkCount_for_callnotes == phoneSaved.size()) {
                // Log.e(TAG, "...doesnt have match   " + checkCount_for_callnotes);
                callnote.add(remote);
            }
            if(phoneSaved != null)
                phoneSaved_it = phoneSaved.iterator();
            checkCount_for_callnotes = 0;
        }
        String jSon = new GsonBuilder().create().toJson(remoteSaved, List.class);
        // Log.e(TAG, "Saved contacts 3  " + remoteSaved.toString());
        mSharePref.setListCallNote(jSon);
        return  /**remoteSaved;**/ callnote;
    }



    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(List<CallNote> callNote){
        HollaNowDbHelper helper = new HollaNowDbHelper(getApplicationContext());
        List<bimps> bip = helper.allNotification();
        int counter = 0;
        for(int i = 0; i<bip.size();i++){
            // Log.e(TAG, "Saved Notification on Hollanow  "+i +"  "+ bip.get(i).getCounter());
            counter = bip.get(0).getCounter();
            counter++;
        }

        int mId = 0Xff00;
        Intent intent = new Intent(this, NotificationActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(40, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri note = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mSharePref.setNotification_flag(callNote.size());

        for(int i = 0; i<callNote.size();i++) {
            String name = callNote.get(i).getName();
            String date = callNote.get(i).getUpdated_at();
            String cNote =  callNote.get(i).getNote();
            int color1 = 0XFF9800;
            bimps storeCallnote = new bimps("HollaNow Callnote", cNote+" from "+name,counter, date);
            try {
                helper.cacheNotification(storeCallnote); ////////
            }catch(SQLiteException ex){
                Log.e(TAG, "SLiteException occurred here: " + ex.getMessage());
            }

            NotificationCompat.Builder mNotification1 = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("HollaNow Callnote")
                    .setSound(note)
                    .setContentText(name+" called. Tap to confirm. ");

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification23);

            mNotification1.setSmallIcon(R.drawable.notification12);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mNotification1.setColor(color1);
                // mNotification.setSmallIcon(R.drawable.notification2);  //R.drawable.notit
                // mNotification.setLargeIcon(bm);
            } else
                mNotification1.setSmallIcon(R.drawable.notification2);

            mNotification1.setContentIntent(pendingIntent)/**.setColor(color1)**/.setLargeIcon(bm);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mId--;
            String NOTIFICATION_CHANNEL_ID = "10001";
            String NOTIFICATION_CHANNEL_NAME = "CallNote";
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setLightColor(Color.RED);
                // NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(mNotification1 != null)
                    mNotification1.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(channel);
            }
            if(mNotification1 != null)
                mNotificationManager.notify(mId, mNotification1.build());
        }

    }

    private void updateUI() {
        if (callerPhoto!=null) {
            Picasso.with(getApplicationContext())
                    .load(BetaCaller.PHOTO_URL + callerPhoto)
                    .resize(50, 50)
                    .centerCrop()
                    .into(mCallerPhoto);
        } else {
            Picasso.with(getApplicationContext())
                    .load(R.drawable.wil_profile)
                    .resize(50, 50)
                    .centerCrop()
                    .into(mCallerPhoto);
        }
        if (callerName == null) {
            callerName = phoneNumber;
        }
        mCallerName.setText(callerName+"");

        if (callNote == null) {
            int unicode = 0x1f60e;
            String emoji = new String(Character.toChars(unicode));
            String text1 = "Let's talk ";
            EmojiconEditText emojiText = new EmojiconEditText(getBaseContext());
            emojiText.setText(emoji);
            mCallNote.setText(text1+"  "+emoji);
        }
        else {
            // String note = new String(callNote.getBytes());

            // Log.e(TAG, "IndexOutofBound Exception   " +  + " ");
            mCallNote.setText(callNote);
        }


    }

    private String formateCallnoteDate(String dateFormat){
        String mLastSeen = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Date date = ((Date) format.parse(dateFormat));
            long msgTimeMills = date.getTime();
            cal.setTimeInMillis(msgTimeMills);
            Calendar now = Calendar.getInstance();
            final String strTimeFormate = "h:mm aa";
            final String strDateFormate = "dd/MM/yyyy  h:mm aa";
            if (now.get(Calendar.DATE) == cal.get(Calendar.DATE) && (now.get(Calendar.MONTH) == cal.get(Calendar.MONTH))
                    && (now.get(Calendar.YEAR) == cal.get(Calendar.YEAR))) {
                mLastSeen = "today at " + android.text.format.DateFormat.format(strTimeFormate, cal);
                // Log.e(TAG, "today at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "today at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "today at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "today at " + android.text.format.DateFormat.format(strTimeFormate, cal));
            } else if ((now.get(Calendar.DATE) - cal.get(Calendar.DATE)) == 1
                    && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                    && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))) {
                // Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                // Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                mLastSeen = "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal);
            } else {
                mLastSeen = " " + android.text.format.DateFormat.format(strDateFormate, cal);
                Log.e(TAG, " " + android.text.format.DateFormat.format(strDateFormate, cal));
            }
        }catch (java.text.ParseException ex){
            //Log.e(TAG, "Exception year olf study "+time);
            Log.e(TAG, "Exception year olf study "+ex.getMessage());
            ex.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        return  mLastSeen;
    }

    @Override
    public boolean onStopJob(JobParameters parameters){
        Log.e(TAG, "OnstopJob1 here  ");

        return true;
    }


    private void goToActivity(Class<?> cls,String phoneNumber,String name, String username, String photo) {
        Intent intent = new Intent(this, cls);
        if (cls == CallNoteBottomActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Parse_Contact.NAME,name);
            intent.putExtra(Parse_Contact.USERNAME,username);
            intent.putExtra(Parse_Contact.PHONE_NUMBER, phoneNumber);
            intent.putExtra(Parse_Contact.PHOTO, photo);
        }
        startActivity(intent);
    }



}