package com.doxa360.android.betacaller;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Contact;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by Natarajan on 15-06-2015.
 */
public class CallNoteService extends Service {

    private static final String TAG = CallNoteService.class.getSimpleName();
    private WindowManager windowManager;
    private ImageView close;
    private RelativeLayout chatheadView;
    private FrameLayout content;
    private LayoutInflater inflater;
    private boolean isHollaNowUser = false;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    String callerId, callerUserName, callerEmail, callerName, callerNumber, callerPhoto, callNote, phoneNumber;
    private ImageView mCallerPhoto;
    private TextView mCallerName, mCallNote;

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

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        mDbHelper = new HollaNowDbHelper(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
            phoneNumber = intent.getStringExtra(CallNote.CALLER_NUMBER);
        }
        Log.e(TAG, "Service started: "+phoneNumber);

        chatheadView = (RelativeLayout) inflater.inflate(R.layout.on_screen_call_note, null);
        close=(ImageView)chatheadView.findViewById(R.id.closeBtn);

        mCallerPhoto = (ImageView)chatheadView.findViewById(R.id.caller_photo);
        mCallerName = (TextView) chatheadView.findViewById(R.id.caller_name);
        mCallNote = (TextView) chatheadView.findViewById(R.id.call_note);


        if (mDbHelper.getContactByPhone(phoneNumber)!=null) {
            Contact contact = mDbHelper.getContactByPhone(phoneNumber);
            callerName = contact.getDisplayName();
            callerPhoto = contact.getThumbnailUrl();
        } else {
            callerName = phoneNumber;
        }
        isHollaNowUser = isCallNote();
        updateUI();

        content=(FrameLayout)chatheadView.findViewById(R.id.content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowManager.removeView(chatheadView);

                if (isHollaNowUser) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra(ProfileActivity.USER_ID, callerId);
                    intent.putExtra(ProfileActivity.USER_NAME, callerUserName);
                    intent.putExtra(ProfileActivity.FULL_NAME, callerName);
                    intent.putExtra(ProfileActivity.PHONE, callerNumber);
                    intent.putExtra(ProfileActivity.EMAIL, callerEmail);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                    intent.putExtra(BetaCaller.CONTACT_PHONE, phoneNumber);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                stopService(new Intent(getApplicationContext(), CallNoteService.class));
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(chatheadView);
                stopService(new Intent(getApplicationContext(), CallNoteService.class));
            }
        });

        windowManager.addView(chatheadView, params);
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
        if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
            ParseQuery<CallNote> query = CallNote.getQuery();
            query.whereEqualTo("callerNumber", phoneNumber);
            query.getFirstInBackground(new GetCallback<CallNote>() {
                @Override
                public void done(CallNote note, ParseException e) {
                    if (e == null) {
                        if (callNote == null) {
                            Log.e(TAG, "call note is null");

                        }
                        callerId = note != null ? note.getCallerId() : null;
                        callerUserName = note != null ? note.getCallerUserName() : null;
                        callerEmail = note != null ? note.getCallerEmail() : null;
                        callerName = note != null ? note.getCallerName() : null;
                        callerPhoto = note != null ? note.getCallerPhoto() : null;
                        callNote = note != null ? note.getCallNote() : null;

                        if (note != null) {
                            Log.e(TAG, "query successful");
                            isCallNote[0] = true;
                            updateUI();

                        } else {
                            Log.e(TAG, "query empty");
                        }

                    } else {
                        Log.e(TAG, "query NOT successful");

                    }


                }
            });
        } else {

        }
        return isCallNote[0];
    }

    private void updateUI() {
        if (callerPhoto!=null) {
            Picasso.with(getApplicationContext())
                    .load(callerPhoto)
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
            mCallNote.setText(R.string.default_callnote);
        }
        else {
            mCallNote.setText(callNote);
        }


    }



}

