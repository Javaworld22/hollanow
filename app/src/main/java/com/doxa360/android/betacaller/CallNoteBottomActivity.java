package com.doxa360.android.betacaller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.doxa360.android.betacaller.model.Parse_Contact;

public class CallNoteBottomActivity extends AppCompatActivity {
    String name;
    String username;
    String phone;
    String photo;
    private static final String TAG = CallNoteBottomActivity.class.getSimpleName();
    private Activity activity;
   // private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mContext = getApplicationContext();
        activity = this;
        setContentView(R.layout.callnotebackground_activity);
        if(getIntent() != null) {
            phone = getIntent().getStringExtra(Parse_Contact.PHONE_NUMBER);
            username = getIntent().getStringExtra(Parse_Contact.USERNAME);
            name = getIntent().getStringExtra(Parse_Contact.NAME);
            photo = getIntent().getStringExtra(Parse_Contact.PHOTO);

            CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
            FragmentManager fragmentManager =  this.getSupportFragmentManager();
            Bundle args = new Bundle();
            args.putString(Parse_Contact.PHONE_NUMBER, phone);
            args.putString(Parse_Contact.NAME, name);
            args.putString(Parse_Contact.USERNAME, username);
            args.putString(Parse_Contact.PHOTO, photo);
            callNoteBottomSheet.setArguments(args);
            callNoteBottomSheet.show(fragmentManager, "CALL_NOTE");
        }
        activity.setFinishOnTouchOutside(true);
    }
}
