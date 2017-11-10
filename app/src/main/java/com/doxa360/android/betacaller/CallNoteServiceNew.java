package com.doxa360.android.betacaller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v8.renderscript.Byte4;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.beta.Beta;
import com.doxa360.android.betacaller.adapter.UserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Natarajan on 15-06-2015.
 */
public class CallNoteServiceNew extends Service {

    private static final String TAG = CallNoteServiceNew.class.getSimpleName();
    private WindowManager windowManager;
    private ImageView close;
    private static RelativeLayout chatheadView;
    private FrameLayout content;
    private LayoutInflater inflater;
    private boolean isHollaNowUser = false;
    private static int i,j;
    private byte[] noteString;
    private Context mContext;
    private  HollaNowSharedPref mSharePref;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    String callerId, callerUserName, callerEmail, callerName,callerName1, callerNumber, callerPhoto, callNote, phoneNumber;
    private ImageView mCallerPhoto;
    private static TextView mCallerName;
    private static EmojiconTextView mCallNote;
    private static TextView mCallerTag;


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        callerId = intent.getStringExtra(CallNote.CALLER_ID);
//        callerName = intent.getStringExtra(CallNote.CALLER_NAME);
//        callerPhoto = intent.getStringExtra(CallNote.CALLER_PHOTO);
//        callerNumber = intent.getStringExtra(CallNote.CALLER_NUMBER);
//        callNote = intent.getStringExtra(CallNote.CALL_NOTE);
//        if (intent != null) {
//            phoneNumber = intent.getStringExtra(CallNote.CALLER_NUMBER);
//        }
//        Log.e(TAG, "Service started: "+phoneNumber);
//
//        return START_STICKY;
//    }

    private HollaNowDbHelper mDbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        i = 0;
        Log.e(TAG, "Problem 1: "+phoneNumber);
        windowManager = (WindowManager)getSystemService(Service.WINDOW_SERVICE);
//        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        mDbHelper = new HollaNowDbHelper(getApplicationContext());
        mSharePref = new HollaNowSharedPref(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Problem 2: "+phoneNumber);
        i++;
        if(i >= 2)
            windowManager.removeView(chatheadView);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 0;
        params.y = 0;


        if (intent != null) {
            phoneNumber = intent.getStringExtra(Parse_Contact.PHONE_NUMBER);
          //  if(phoneNumber == null)
             //   phoneNumber =  CallReceiver.nos;            //CallReceiver.nos;
            Log.e(TAG, "Proble 3: "+phoneNumber);
        }
        String phone = phoneNumber;
        Log.e(TAG, "Change to +234 rr: "+phone);
        try {
            if (phone.trim().startsWith("0")) {
                phoneNumber = phone.substring(1);
                Log.e(TAG, "Change to +234 rr: " + phoneNumber);
                phoneNumber = "+234".concat(phoneNumber);
                Log.e(TAG, "Change to +234: " + phoneNumber);
            }
        }catch (NullPointerException cc){
            Log.e(TAG, "NullPointerException Started here : "+cc.getMessage());
        }
        Log.e(TAG, "Service started: "+phoneNumber);

        chatheadView = (RelativeLayout) inflater.inflate(R.layout.on_screen_call_note, null);
        close=(ImageView)chatheadView.findViewById(R.id.closeBtn);

        mCallerPhoto = (ImageView)chatheadView.findViewById(R.id.caller_photo);
        mCallerName = (TextView) chatheadView.findViewById(R.id.caller_name);
        mCallNote = (EmojiconTextView) chatheadView.findViewById(R.id.call_note);
        mCallerTag = (TextView) chatheadView.findViewById(R.id.textView2);

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
}catch (Exception e){

}
        if(!CallReceiver.isMissed)
            mCallerTag.setText("Calling");


        else if(CallReceiver.isMissed) {
            if(j++ == 4) {
                i++;
                j = 0;
            }
            mCallerTag.setText("Called "+CallReceiver.number_of_calls);
        }

        else if(CallReceiver.callEnded == 2)
            mCallerTag.setText("Received");

        updateUI();
        isHollaNowUser = isCallNote();
//        updateUI();

        content=(FrameLayout)chatheadView.findViewById(R.id.content); //FrameLayout
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(chatheadView);
                CallReceiver.number_of_calls = 0;
//                if (isHollaNowUser) {
//                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                    intent.putExtra(ProfileActivity.USER_ID, callerId);
//                    intent.putExtra(ProfileActivity.USER_NAME, callerUserName);
//                    intent.putExtra(ProfileActivity.FULL_NAME, callerName);
//                    intent.putExtra(ProfileActivity.PHONE, callerNumber);
//                    intent.putExtra(ProfileActivity.EMAIL, callerEmail);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } else {
                //////////////////////////////
                    Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                   intent.putExtra(BetaCaller.CONTACT_PHONE, phoneNumber);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                ///////////////////////////////////
//                }
                stopService(new Intent(getApplicationContext(), CallNoteServiceNew.class)); // open here
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteServiceNew.class));
                CallReceiver.number_of_calls = 0;
            }
        });
//        windowManager.updateViewLayout(mCallerTag,params );



        windowManager.addView(chatheadView, params);
        windowManager.updateViewLayout(chatheadView,params );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(chatheadView!=null){
            try {
                windowManager.removeView(chatheadView);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        }
//        stopService(new Intent(getApplicationContext(), CallNoteService.class));
    }

    private boolean isCallNote() {

        final boolean[] isCallNote = {false};
        try {
        String phone = mSharePref.getPhoneEmoji();
        String p = "";
        if(!phone.trim().equals("")){
            if(phone.startsWith("+234")) {
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
        }catch (NullPointerException ss){
            Log.e(TAG, "NullpoointerException Error occured  :" +ss.getMessage());
        }

        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
            Log.e(TAG, "At isCallNote0: " +phoneNumber+" "+mSharePref.getToken());
            Call<JsonElement> call = hollaNowApiInterface.getCallNoteByPhone(phoneNumber);  // "08057270466"
            call.enqueue(new Callback<JsonElement>() {
                String getNote1;
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    CallNote note = null;
                    Log.e(TAG, "Check response: " +response.code());
                    String noteStreams = null;
                    Log.e(TAG, "success of contacts " + response.code() + "");
                    Log.e(TAG, "success of contacts " + response.code() + "");
                    Log.e(TAG, "success of contacts " + response.body() + "");
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
                    callerUserName = note != null ? model.getCallNote().get(0).getUsername() : " ";
//                    callerEmail = note != null ? note.getEmail() : " ";
                    callerName = note != null ? model.getCallNote().get(0).getName() : callerName1;
                    callerPhoto = note != null ? note.getPhoto() : null;
                    int unicode = 0x1f60e;
                    String emoji = new String(Character.toChars(unicode));
                  //  callNote = note != null ? getNote1 :  getString(R.string.default_callnote)+"  "+emoji; //note.getNote();
                    callNote = note != null ? model.getCallNote().get(0).getNote() :  getString(R.string.default_callnote)+"  "+emoji; //note.getNote();
                    updateUI();
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
//                    Log.e(TAG, t.getMessage());
                    updateUI();
                }
            });

        }

        return isCallNote[0];
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
            String ss1 = "";
            int j = 0;
            byte []bs;
            List<Byte> b = new ArrayList<Byte>();
            Log.e(TAG, "IndexOutofBound Exception   " + callNote + " ");
            for(i = 0;i < callNote.length();i++){
                char c = callNote.charAt(i);
                if(c == ';'){
                  //  c = ' ';
                    j++;
                   b.add(Byte.decode(ss1));
                    ss1 = "";
                }else
                ss1 = ss1+String.valueOf(c);


            }
            bs = new byte[b.size()];
            for(int i = 0;i<b.size();i++){
                bs[i] = b.get(i);
                Log.e(TAG, "IndexOutofBound Exception   " +bs[i]  + " ");
            }
           // Log.e(TAG, "IndexOutofBound Exception   " +  + " ");
            mCallNote.setText(new String(bs));
        }


    }



}

