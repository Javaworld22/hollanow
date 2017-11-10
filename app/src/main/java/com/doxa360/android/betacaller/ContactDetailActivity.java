package com.doxa360.android.betacaller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

import com.doxa360.android.betacaller.adapter.PhoneCallLogAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.PhoneCallLog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;


import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView mContactName, mContactPhone, mHollanowUsername, mHollanowBio, mHollanowOccupation, mHollanowAddress, mEmptyText;
    private Button mHollanowInvite;
    private LinearLayout mHollanowLayout;
    private FloatingActionButton fab;
    private ImageView mContactPhoto;
    private RecyclerView mCallHistoryRecyclerView;
    String contactId;
    String contactName;
    String contactPhone;
    String contactThumbnail;
    HollaNowDbHelper mDbHelper;
    private PhoneCallLogAdapter mAdapter;
    private EditText noteEditText;
    String mCurrentUser,mCurrentUser1;
    private NativeExpressAdView mAdView ;


    //CallNoteBottomSheet callNoteBottomSheet1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
       // noteEditText = CallNoteBottomSheet.noteEditText;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do call note here...

                    CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Bundle args = new Bundle();
               // callNoteBottomSheet1 = callNoteBottomSheet;
                    args.putString("PHONE_NUMBER", contactPhone);
                Log.e(TAG,"aT FAB check contact phone" +contactPhone);
                    callNoteBottomSheet.setArguments(args);
                    callNoteBottomSheet.show(fragmentManager, "CALL_NOTE");
                //Context context = getApplicationContext();
              // Intent i = new Intent(ContactDetailActivity.this,CallNoteBottomSheet.class);
              //  startActivity(i);


//                placeCall(contactPhone);// not among
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = new HollaNowDbHelper(this);

        contactId = getIntent().getStringExtra(BetaCaller.CONTACT_ID);
        contactName = getIntent().getStringExtra(BetaCaller.CONTACT_NAME);
        contactPhone = getIntent().getStringExtra(BetaCaller.CONTACT_PHONE);
        contactThumbnail = getIntent().getStringExtra(BetaCaller.CONTACT_PHOTO);


        mContactName = (TextView) findViewById(R.id.contact_name);
        mContactPhone = (TextView) findViewById(R.id.contact_phone);
        mContactPhoto = (ImageView) findViewById(R.id.contact_photo);
        mHollanowUsername = (TextView) findViewById(R.id.hollanow_username);
        mHollanowBio = (TextView) findViewById(R.id.hollanow_bio);
        mHollanowOccupation = (TextView) findViewById(R.id.hollanow_occupation);
        mHollanowAddress= (TextView) findViewById(R.id.hollanow_address);
        mHollanowInvite = (Button) findViewById(R.id.hollanow_invite);
        mHollanowLayout = (LinearLayout) findViewById(R.id.hollanow_layout);
        mEmptyText = (TextView) findViewById(R.id.empty_text);
        mCallHistoryRecyclerView = (RecyclerView) findViewById(R.id.call_history_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mCallHistoryRecyclerView.setLayoutManager(layoutManager);

        final HollaNowSharedPref pref = new HollaNowSharedPref(this);


        toolbar.setTitle(contactName);
        getSupportActionBar().setTitle(contactName);

        if (contactId == null) {
            getContactDetails(contactPhone);
            Log.e(TAG, contactPhone);
        } else {
            updateUI();
        }

        mHollanowInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ParseQuery<ParseUser> query = ParseUser.getQuery();



                // Log.e(TAG, "ParseUser: "+ParseUser.getCurrentUser().getUsername().toString());
                sendSMSIntent(contactPhone, "Hello friends, I now use the HollaNow app. So whenever you want to talk to me, just holla @"+/**ParseUser.getCurrentUser().getUsername()**/ pref.getUsername().toString().trim()+ ". It\'s cooler. link  "+"http://bit.ly/2rDEq84");
            }
        });

        mAdView = (NativeExpressAdView) findViewById(R.id.adView6) ;

        AdRequest request = new AdRequest.Builder()
                   //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  //  bbb0eb0fce7bf3a8
                 //  .addTestDevice("E0A7012BF382436CB461659B1F229E03")    // "E0A7012BF382436CB461659B1F229E03"
                .build();
        mAdView .loadAd(request);

    }

    private void setCallHistoryUI() {
        List<PhoneCallLog> callLogs = mDbHelper.allLogsByPhoneNumber(contactPhone);
        if (callLogs!=null && callLogs.size()!=0) {
            mEmptyText.setText("");
            mAdapter = new PhoneCallLogAdapter(callLogs, this, false);
            mCallHistoryRecyclerView.setAdapter(mAdapter);
        } else {
        }
    }

    private void updateUI() {

        if (contactName != null ) {
            mContactName.setText(contactName);
            Log.e(TAG, contactName);
        }
        if (contactPhone!=null) {
            mContactPhone.setText(contactPhone);
        }

        if (contactThumbnail != null) {
            Picasso.with(this).load(contactThumbnail).into(mContactPhoto);
        } else {
            Picasso.with(this).load(R.drawable.wil_profile).into(mContactPhoto);
        }
        boolean hollaDet = getHollanowDetails(contactPhone);
        Log.e(TAG, "getHollanowDetails "+hollaDet+" "+mCurrentUser+" "+mCurrentUser1);
        if (!hollaDet) {
            mHollanowInvite.setVisibility(View.VISIBLE);
            mHollanowInvite.setEnabled(true);
        } else {
            mHollanowInvite.setVisibility(View.INVISIBLE);
        }

        setCallHistoryUI();

    }

    private void getContactDetails(final String contactPhone) {
        HollaNowDbHelper dbHelper = new HollaNowDbHelper(this);
        Parse_Contact contact = dbHelper.getContactByPhone(contactPhone);
        if (contact!=null) {
            contactId = contact.getId();
            contactName = contact.getDisplayName();
            Log.e(TAG + " getcontact details", contactName);
            contactThumbnail = contact.getThumbnailUrl();
        } else {
//            contactId = "";
            contactName = "Save Contact";
            mContactName.setTextColor(getResources().getColor(R.color.accent));
            mContactName.setTypeface(null, Typeface.BOLD);
            mContactName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveContactToPhone(contactPhone);
                }
            });
        }

        updateUI();

    }

    private void saveContactToPhone(String contactPhone) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contactPhone);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_ISPRIMARY, true);
        startActivity(intent);
    }

    private boolean getHollanowDetails(String contactPhone) {
        Log.e(TAG, "PhoneNumberUtils "+PhoneNumberUtils.stripSeparators(contactPhone));
        final boolean[] hasDetails = {false};
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.fromLocalDatastore();
       // query.getI.getI
        query.whereContains("phoneNumber", PhoneNumberUtils.stripSeparators(contactPhone));
        query.findInBackground(new FindCallback<ParseUser>() {
         //   @Override
           public void done(List<ParseUser> objects, ParseException e) {
               if (e == null) {
                    Log.e(TAG, "ParseException1 "+e.toString());
                    if (objects.size() > 0) {
                        mCurrentUser = objects.get(0).getUsername();
                        mCurrentUser1 = ParseUser.getCurrentUser().getUsername();
                        mHollanowUsername.setText("@"+ objects.get(0).getUsername() + " (" + objects.get(0).getString("name") + ")");
                        if (objects.get(0).getString("bio")!=null) {
                            mHollanowBio.setText("About: " + objects.get(0).getString("bio"));
                        }
                        if (objects.get(0).getString("occupation")!=null) {
                            mHollanowOccupation.setText("Occupation: " + objects.get(0).getString("occupation"));
                        }
                        if (objects.get(0).getString("address")!=null) {
                            mHollanowAddress.setText("Address: " + objects.get(0).getString("address"));
                        }
//                        ArrayList<String> tagList = new ArrayList<String>();
//                        if (objects.get(0).getList("tags")!=null) {
//                            for (int i = 0; i < objects.get(0).getList("tags").size(); i++) {
//                                tagList.add((String) objects.get(0).getList("tags").get(i));
//                            }
//                        }
//                        mHollanowTags.setText("Tags: " + MyToolBox.listToTaggedString(tagList));
//                        if (objects.get(0).getParseFile("photo") != null) {
//                            try {
//                                Picasso.with(ContactDetailActivity.this)
//                                        .load(objects.get(0).getParseFile("photo").getUrl())
//                                        .into(mContactPhoto);
//                            } catch (OutOfMemoryError oomE) {
//                                Log.e(TAG, oomE.getMessage());
//                            }
//                        }
                        mHollanowInvite.setVisibility(View.INVISIBLE);
                        hasDetails[0] = true;
                    } else {
                        mHollanowInvite.setVisibility(View.VISIBLE);
                        hasDetails[0] = false;
                    }
                }else
                    Log.e(TAG, "ParseException2 "+e.toString());
            }
        });
        return hasDetails[0];
    }

    public void sendSMS(String number, String msg) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, msg)));
    }

    private void sendSMSIntent(String number, String msg) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        startActivity(it);

    }

    private void placeCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);

        }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }

}
