package com.doxa360.android.betacaller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Contact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView mContactName, mContactPhone, mHollanowUsername, mHollanowBio, mHollanowOccupation, mHollanowAddress;
    private Button mHollanowInvite;
    private LinearLayout mHollanowLayout;
    private FloatingActionButton fab;
    private ImageView mContactPhoto;
    String contactId;
    String contactName;
    String contactPhone;
    String contactThumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do call note here...
                CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle args = new Bundle();
                args.putString(Contact.PHONE_NUMBER, contactPhone);
                callNoteBottomSheet.setArguments(args);
                callNoteBottomSheet.show(fragmentManager,"CALL_NOTE");
//                placeCall(contactPhone);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                sendSMSIntent(contactPhone, "Hello friends, I now use the HollaNow app. So whenever you want to talk to me, just holla @"+ParseUser.getCurrentUser().getUsername()+ ". It\'s cooler.");
            }
        });

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

        if (!getHollanowDetails(contactPhone)) {
            mHollanowInvite.setVisibility(View.VISIBLE);
            mHollanowInvite.setEnabled(true);
        } else {
            mHollanowInvite.setVisibility(View.INVISIBLE);
        }

    }

    private void getContactDetails(String contactPhone) {
        HollaNowDbHelper dbHelper = new HollaNowDbHelper(this);
        Contact contact = dbHelper.getContactByPhone(contactPhone);
        contactId = contact.getId();
        contactName = contact.getDisplayName();
        Log.e(TAG+" getcontact details", contactName);
        contactThumbnail = contact.getThumbnailUrl();

        updateUI();

    }

    private boolean getHollanowDetails(String contactPhone) {
        Log.e(TAG, PhoneNumberUtils.stripSeparators(contactPhone));
        final boolean[] hasDetails = {false};
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("phoneNumber", PhoneNumberUtils.stripSeparators(contactPhone));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
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
                }
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

}
