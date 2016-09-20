package com.doxa360.android.betacaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Contact;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        IntentFilter intentFilter = new IntentFilter(PhoneCallBroadcastReceiver.INCOMING_CALL);
        IntentFilter endIntentFilter = new IntentFilter(PhoneCallBroadcastReceiver.INCOMING_CALL_ENDED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPhoneReceiver, intentFilter);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mEndPhoneReceiver, endIntentFilter);

//        HollaNowDbHelper dbHelper = new HollaNowDbHelper(this);
//        dbHelper.clearAndRecreateDb();
//
//        Handler handler = new Handler();
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (ParseUser.getCurrentUser() != null) {
//                    goToActivity(HomeActivity.class);
//                }else{
//                    goToActivity(SlideIntro.class);
//                }
//            }
//        };
//
//        handler.postDelayed(runnable, 1000);
        if (ParseUser.getCurrentUser() != null) {
            goToActivity(HomeActivity.class);
        }else{
            goToActivity(SlideIntro.class);
        }


    }

    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    String phoneNumber;
    private BroadcastReceiver mPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            phoneNumber = intent.getStringExtra(Contact.PHONE_NUMBER);
            getCallNote(phoneNumber);
        }
    };
//    private BroadcastReceiver mEndPhoneReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            phoneNumber = intent.getStringExtra(Contact.PHONE_NUMBER);
//            Intent endIntent = new Intent(getApplicationContext(), CallNoteService.class);
//            Log.e(TAG, "mainactivity call note: "+ phoneNumber);
//        }
//    };

    private void getCallNote(final String phoneNumber) {
        final Intent intent = new Intent(getApplicationContext(), CallNoteService.class);
        intent.putExtra(CallNote.CALLER_NUMBER, phoneNumber);
        Log.e(TAG, "mainactivity call note: " + phoneNumber);
        startService(intent);
//        if (MyToolBox.isNetworkAvailable(MainActivity.this)) {
//            ParseQuery<CallNote> query = CallNote.getQuery();
//            query.whereEqualTo("callerNumber", phoneNumber);
//            query.getFirstInBackground(new GetCallback<CallNote>() {
//                @Override
//                public void done(CallNote callNote, ParseException e) {
//                    if (e == null) {
//                        if (callNote == null) {
//                            Log.e(TAG, "call note is null");
//
//                        }
//                        String callerId = callNote != null ? callNote.getCallerId() : null;
//                        String callerUserName = callNote != null ? callNote.getCallerUserName() : null;
//                        String callerEmail = callNote != null ? callNote.getCallerEmail() : null;
//                        String callerName = callNote != null ? callNote.getCallerName() : null;
//                        String callerNumber = callNote != null ? callNote.getCallerNumber() : null;
//                        String callerPhoto = callNote != null ? callNote.getCallerPhoto() : null;
//                        String callNoteNote = callNote != null ? callNote.getCallNote() : null;
//                        intent.putExtra(CallNote.CALLER_ID, callerId);
//                        intent.putExtra(CallNote.CALLER_USER_NAME, callerUserName);
//                        intent.putExtra(CallNote.CALLER_EMAIL, callerEmail);
//                        intent.putExtra(CallNote.CALLER_NAME, callerName);
//                        intent.putExtra(CallNote.CALLER_PHOTO, callerPhoto);
//                        intent.putExtra(CallNote.CALL_NOTE, callNoteNote);
//                        if (callNote != null) {
//                            Log.e(TAG, "query successful");
//                        } else {
//                            Log.e(TAG, "query empty");
//                        }
//                        startService(intent);
//                    } else {
//                        Log.e(TAG, "query NOT successful");
//                        startService(intent);
//                    }
//
//
//                }
//            });
//        } else {
//
//        }

    }
}
