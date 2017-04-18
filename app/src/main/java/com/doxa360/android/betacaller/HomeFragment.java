package com.doxa360.android.betacaller;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.ContactAdapter;
import com.doxa360.android.betacaller.helpers.FastScroller;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Contact;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1044;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1090;
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    ContactAdapter adapter;
    List<Parse_Contact> allContacts;
    private ContentResolver resolver;
    private Cursor mCursor;
    private HollaNowDbHelper dbHelper;
    private HollaNowSharedPref mSharedPref;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private FastScroller fastScroller;
    private TextView mTooltipAnchor;
    private HollaNowApiInterface hollaNowApiInterface;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = new HollaNowSharedPref(mContext);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 5 second, in milliseconds


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
        checkLocationSettings();
        /***
         * 1. Check if GPS turned on
         * 2. If GPS not turned, ask permission
         * 3. If permission given or already on...
         *  a. save current user's location
         *  b. //heckLocation() now findNearbyUsersByContactList();
         * 4. If permission not given, ignore location features
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_home2, container, false);


        Log.e(TAG + "Shared Pref:", mSharedPref.getLongtitude() + " : " + mSharedPref.getLattitude());

        Log.e(TAG + "Shared Pref user:", mSharedPref.getToken() + " : " + mSharedPref.getCurrentUser().getName()+ " : " + mSharedPref.getCurrentUser().toString());

        allContacts = new ArrayList<Parse_Contact>();
        resolver = mContext.getContentResolver();
//        mCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        mTooltipAnchor = (TextView) rootView.findViewById(R.id.tooltip_anchor);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contacts_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        fastScroller=(FastScroller) rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(mRecyclerView);


//        fetchPhoneContacts();


//        Log.e(TAG, "size "+fetchPhoneContacts());
//        if (allContacts.size() != mSharedPref.getContactCount() || allContacts.size() == 0) {
//            fetchPhoneContacts();
//            Log.e(TAG+" FETCH_CONTACTS: ", "true. allContacts: "+ allContacts.size()+ " sharedPref "+ mSharedPref.getContactCount());
//        } else {
//            Log.e(TAG+" FETCH_CONTACTS: ", "false. allContacts: "+ allContacts.size()+ " sharedPref "+ mSharedPref.getContactCount());
//        }


        return rootView;
    }

    private class syncDb extends AsyncTask<String, String, Void> {

        Uri uri;

        public syncDb(String string) {
            Log.e(TAG, string);
        }

        @Override
        protected Void doInBackground(String... string) {

            //request permission for android 6 +

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {


                } else {

                    // No explanation needed, we can request the permission.
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                }
            } else {
                fetchPhoneContacts();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void bytes) {
            super.onPostExecute(bytes);
            allContacts.clear();
            allContacts.addAll(dbHelper.allContacts());
            for (int i = 0; i < allContacts.size(); i++) {
                String phoneNumber = allContacts.get(i).getPhoneNumber();
                if (phoneNumber.equalsIgnoreCase("08036428999")) {
                    Log.e(TAG, "post exec 08036428999 is among");
                } else if (phoneNumber.equalsIgnoreCase("+2348036428999")) {
                    Log.e(TAG, "post exec +2348036428999 is among");
                }
            }
            mSharedPref.setContactCount(allContacts.size());
            adapter.notifyDataSetChanged();
            Log.e(TAG, "Adapter updated "+ allContacts.size());
            if (MyToolBox.isNetworkAvailable(mContext)) {
//                checkLocation();
                findNearbyUsersByContactList();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    fetchPhoneContacts();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private List<Parse_Contact> fetchPhoneContactsDb() {
//        dbHelper = new HollaNowDbHelper(mContext);
//        dbHelper.clearAndRecreateDb();
        allContacts = dbHelper.allContacts();
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter = new ContactAdapter(allContacts, mContext);
        mRecyclerView.setAdapter(adapter);
//        List<Contact> contacts = fetchPhoneContacts();
//        if (allContacts.size()!=contacts.size()) {
//            allContacts = contacts;
//            adapter.notifyDataSetChanged();
//        }
//        if (!allContacts.isEmpty() && allContacts.size() == mSharedPref.getContactCount()) {
////            fetchPhoneContactsDb();
//            Log.e(TAG, "fetchDB - true " + allContacts.size());
//        } else {
////            dbHelper.clearAndRecreateDb();
//            allContacts = fetchPhoneContacts();
//            Log.e(TAG, "fetchDB - false " + allContacts.size());
//        }

        return allContacts;
    }

    private int fetchPhoneContacts() {
        Log.e("FPC", "in here...");
//        dbHelper.clearContacts();
        Long timer = System.currentTimeMillis();

        mCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        List<Parse_Contact> contactList = new ArrayList<Parse_Contact>();
        if (mCursor != null) {
            try {
                final int idIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                final int nameIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                final int phoneNumberIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                final int versionIndex = mCursor.getColumnIndex(ContactsContract.RawContacts.VERSION);
                final int emailAddressIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                final int image_thumbIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
//                final int typeIndex = mCursor.getInt(ContactsContract.CommonDataKinds.Phone.TYPE_ISDN);
                final int accountTypeIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET);

//                final int displayNameIndex = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//                final int emailIndex = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                long contactId;
                String id, name, phoneNumber, version, emailAddress, image_thumb, accountType;
                int type;
                Bitmap bit_thumb = null;

                dbHelper.clearContacts();

                while (mCursor.moveToNext()) {
                    id = mCursor.getString(idIndex);
                    name = mCursor.getString(nameIndex);
                    phoneNumber = mCursor.getString(phoneNumberIndex);
                    version = "null";//mCursor.getString(versionIndex);
                    emailAddress = mCursor.getString(emailAddressIndex);
                    image_thumb = mCursor.getString(image_thumbIndex);
//                    try {
//                        if (image_thumb != null) {
//                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
//                        } else {
////                            Log.e(TAG, "no image thumbnail to display");
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    type = mCursor.getInt(typeIndex);
                    accountType = mCursor.getString(accountTypeIndex);

                    if (phoneNumber!=null && !phoneNumber.isEmpty()) {
                        Parse_Contact contact = new Parse_Contact();
                        contact.setId(id);
                        contact.setDisplayName(name);
                        if (phoneNumber.equalsIgnoreCase("08036428999")) {
//                            Log.e(TAG, "cache 08036428999 is among");
                        }
                        if (phoneNumber.startsWith("0")) {
                            phoneNumber = phoneNumber.replaceFirst("[0]", "+234");
//                            Log.e(TAG, "Cache Replacing: "+phoneNumber + "-"+phoneNumber.replace(" ", ""));
                        }
                        contact.setPhoneNumber(phoneNumber.replace(" ", ""));
                        if (phoneNumber.equalsIgnoreCase("+2348036428999")) {
//                            Log.e(TAG, "cache +2348036428999 is among"+"-"+phoneNumber.replace(" ", ""));
                        }
                        contact.setEmailAddress(emailAddress);
                        contact.setVersion(version);
                        contact.setThumbnailUrl(image_thumb);
//                        contact.setThumbnail(bit_thumb);
                        contact.setAccountType(accountType);

                        dbHelper.cacheContacts(contact);
                        contactList.add(contact);

                    }

                }

            } finally {
                mCursor.close();
            }
            Log.e("PhoneContactsLoader", "Thread execution time: " + (System.currentTimeMillis() - timer) + " ms");

//            allContacts = contactList;
//            if (contactList.size()!=mSharedPref.getContactCount()) {
//                Log.e(TAG, "sharedPref notify dataset changed" +contactList.size() + " - " + mSharedPref.getContactCount());
////                adapter.notify();
//                adapter.notifyDataSetChanged();
//            } else {
//                Log.e(TAG+" sharedPref get contact", contactList.size()+ " - " +mSharedPref.getContactCount()+"");
//            }
//            allContacts = dbHelper.allContacts();
//            mSharedPref.setContactCount(allContacts.size());
//            adapter.notifyDataSetChanged();
        }
        return contactList.size();
    }

//    public List<Contact> fetchContacts() {

//        if (mCursor != null) {
//
//            Log.e("count", "" + mCursor.getCount());
//            if (mCursor.getCount() == 0) {
//                Toast.makeText(mContext, "No contacts on your device contact list.", Toast.LENGTH_LONG).show();
//            }
//
//            for (int i = 0; i < mCursor.getCount(); i++) {
//                while (mCursor.moveToNext()) {
//                    Bitmap bit_thumb = null;
//                    String id = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                    String name = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                    String phoneNumber = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    String version = mCursor.getString(mCursor.getColumnIndex(ContactsContract.RawContacts.VERSION));
//                    String emailAddress = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                    String image_thumb = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
////                    try {
////                        if (image_thumb != null) {
////                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
////                        } else {
//////                            Log.e(TAG, "no image thumbnail to display");
////                        }
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//                    int type = mCursor.getInt(mCursor.getInt(ContactsContract.CommonDataKinds.Phone.TYPE_ISDN));
//                    String accountType = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET));
////                    Log.e(TAG, name + " - " + phoneNumber + " - " + accountType + " - " + version );
//                    if (accountType.equalsIgnoreCase("com.google") || accountType.equalsIgnoreCase("vnd.sec.contact.phone")) {
//                        Contact contact = new Contact();
//                        contact.setId(id);
//                        contact.setDisplayName(name);
//                        contact.setPhoneNumber(phoneNumber.replace(" ", ""));
//                        contact.setEmailAddress(emailAddress);
//                        contact.setVersion(version);
//                        contact.setThumbnailUrl(image_thumb);
////                        contact.setThumbnail(bit_thumb);
//                        contact.setAccountType(accountType);
//
//
////                        Log.e("FPC", id+name+contact.getPhoneNumber());
//                        dbHelper.cacheContacts(contact);
//
//                    }
//
//                }
//            }
//            mSharedPref.setContactCount(dbHelper.allContacts().size());
//        } else {
//            Log.e(TAG, "phone close?");
//        }
//        mCursor.close();

//
//
//
//        String phoneNumber = null;
//        String email = null;
//
//        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
//        String _ID = ContactsContract.Contacts._ID;
//        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
//        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//
//        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//        StringBuffer output = new StringBuffer();
//
//        ContentResolver contentResolver = mContext.getContentResolver();
//
//        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
//
//        // Loop for every contact in the phone
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                Contact contact = new Contact();
//                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
//                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
//                if (hasPhoneNumber > 0) {
//                    //TODO: output.append("\n First Name:" + name);
//                    // Query and loop for every phone number of the contact
//                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
//                    if (phoneCursor.moveToFirst()) {
//                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//                        //TODO: output.append("\n Phone number:" + phoneNumber);
//                        contact.setDisplayName(name);
//                        contact.setPhoneNumber(phoneNumber);
//
//                    }
//
//                    phoneCursor.close();
//
//                }
//                if (contact!=null)
//                    allContacts.add(contact);
//
//            }
//            adapter = new ContactAdapter(allContacts, mContext);
//            mRecyclerView.setAdapter(adapter);
//
//
//        }
//        Log.e("Contacts", allContacts.get(0).getDisplayName()+allContacts.get(1).getDisplayName());
//        return allContacts;
//    }

    private String removeCharacter(String word) {
        String[] specialCharacters = { "[", "}" ,"]",",","."};
        StringBuilder sb = new StringBuilder(word);
        for (int i = 0;i < sb.toString().length() - 1;i++){
            for (String specialChar : specialCharacters) {
                if (sb.toString().contains(specialChar)) {
                    int index = sb.indexOf(specialChar);
                    sb.deleteCharAt(index);
                }
            }
        }
        return sb.toString();
    }
//    public void checkLocation() {
//        Log.e(TAG, "checking location");
//        List<String> phoneNumbers = new ArrayList<String>();
//        List<Parse_Contact>dbContacts = dbHelper.allContacts();
//        for (Parse_Contact c:dbHelper.allContacts()) {
//            String contactPhone = c.getPhoneNumber();
//            Log.e(TAG, "CONTACT charset - "+ contactPhone.length());
////            String ss="";
////            try {
////                ss = URLEncoder.encode(c.getPhoneNumber(), "UTF8");
////            } catch (UnsupportedEncodingException e) {
////                e.printStackTrace();
////            }
////            if (ss.equals("+2348036428999")) {
////                Log.e(TAG, "CONTACT found - "+ contactPhone);
////            }
//
//            if (contactPhone.startsWith("%2B234")) {
//                contactPhone = contactPhone.replace("%2B234", "+234");
//                Log.e(TAG, "new contact - " + contactPhone);
//            }
//            if (contactPhone.startsWith("+234")) {
//                contactPhone = contactPhone.replace("%2B234", "+234");
//                Log.e(TAG, "new contact+ - " + contactPhone);
//            }
//            if (contactPhone.startsWith("0")) {
//                contactPhone = contactPhone.replaceFirst("[0]", "+234");
//                Log.e(TAG, "new contact - " + contactPhone);
//            }
//
//            phoneNumbers.add(contactPhone);//.replaceAll("\\P{Print}",""));
//
//        }
////        phoneNumbers.add("+2348036428999");
//        Log.e(TAG, " phoneNumbers "+ phoneNumbers.size() + " - "+MyToolBox.listToString(phoneNumbers));
//        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
//        String phoneN = MyToolBox.listToString(phoneNumbers);
////        query.whereContainedIn("phoneNumber", phoneNumbers);
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                if (e == null) {
//                    Log.e(TAG, "successfull objects - "+ objects.size() );
//                    for (int i=0;i<objects.size();i++) {
//                        ParseUser user = objects.get(i);
//                        ParseGeoPoint geoPoint = user.getParseGeoPoint("lastSeen");
//                        Log.e(TAG, user.getEmail() + " - " + geoPoint + "" + "phone - "+ user.getString("phoneNumber")+ " - +2348036428999");
//                        String remotePhone = user.getString("phoneNumber").trim();
////                        if (remotePhone.startsWith("+234")) {
////                            remotePhone = remotePhone.replaceFirst("[+234]", "0");
////                        }
//                        if (geoPoint!=null) {
//                            Parse_Contact contact = dbHelper.searchContactByPhoneNumber(remotePhone);//("+2348036428999\u202C");
//                            if (contact!=null) {
//                                Log.e(TAG, "non null contact "+ contact.getId()+ " - " + contact.getPhoneNumber());
//                                String contactId = contact.getId();
//                                dbHelper.updateLocation(contactId, geoPoint.getLatitude(), geoPoint.getLongitude());
//                                Log.e(TAG, "updated location");
//                                allContacts.clear();
//                                allContacts.addAll(dbHelper.allContacts());
//                                adapter.notifyDataSetChanged();
//                            } else {
//                                Log.e(TAG, "no contact");
//                            }
//                        } else {
//
//                        }
//
//                    }
//                    Log.e(TAG, "null locations");
//                } else {
//                    Log.e(TAG, "something wrong: " + e.getMessage());
//                }
//            }
//        });
//    }

    private void findNearbyUsersByContactList() {
        Log.e(TAG, "checking location");
        List<String> phoneNumbers = new ArrayList<String>();
        List<Parse_Contact>dbContacts = dbHelper.allContacts();

        for (Parse_Contact contact:dbContacts) {
            String phone = contact.getPhoneNumber();
            if (contact.getPhoneNumber().startsWith("+234")) {
                phone = contact.getPhoneNumber().replace("+234", "");
            }
//            if (contact.getPhoneNumber().startsWith("+234")) {
//                phone = contact.getPhoneNumber().replace("+234", "");
//            }
            phoneNumbers.add(phone);
        }
        Log.e(TAG, "contacts size: "+phoneNumbers.size());
        if (MyToolBox.isNetworkAvailable(mContext)) {
            Log.e(TAG, "list of contacts in json" + new GsonBuilder().create().toJson(phoneNumbers));
            Call<List<User>> call = hollaNowApiInterface.getUsersByContactList(new GsonBuilder().create().toJson(phoneNumbers), mSharedPref.getToken());
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.code() == 200) {
                        //TODO: check location thingy
                        response.body();// users on hollanow
                        for (User user:response.body()) {
                            if (user.getLat() != 0 && user.getLng() != 0) {
                                Parse_Contact contact = dbHelper.searchContactByPhoneNumber(user.getPhone());//("+2348036428999\u202C");
                                if (contact!=null) {
                                    Log.e(TAG, "non null contact "+ contact.getId()+ " - " + contact.getPhoneNumber());
                                    String contactId = contact.getId();
                                    dbHelper.updateLocation(contactId, user.getLat(), user.getLng());
                                    Log.e(TAG, "updated location");
                                    allContacts.clear();
                                    allContacts.addAll(dbHelper.allContacts());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "no contacts with lat and lng");
                                }

                            }
                        }

                    } else {
                        Log.e(TAG, "user by contact error");
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
//                    Log.e(TAG, "user by contact error "+ t.getMessage());
                }
            });


        }
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e(TAG, "Location services connected. ");

        if (mCurrentLocation!=null) {
            handleNewLocation(mCurrentLocation);
        } else {
            if (!mGoogleApiClient.isConnected()) {
                startUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e(TAG, connectionResult.getErrorMessage());
        if (!connectionResult.isSuccess()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
//            if (!mGoogleApiClient.isConnected()) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//                Toast.makeText(mContext, "Something went wrong with your device\'s location services", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Something went wrong with your device\'s location services");
//            }
        }
        else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.e(TAG, "Location services connected. ");

            handleNewLocation(mCurrentLocation);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()) {

            }
        }
    }

    private void showNextToolTip() {
            /**
             Tool tip here...
             **/
            Tooltip.make(mContext,
                    new Tooltip.Builder(103)
                            .anchor(mTooltipAnchor, Tooltip.Gravity.TOP)
//                        .closePolicy(new Tooltip.ClosePolicy()
//                                .insidePolicy(true, true)
//                                .outsidePolicy(true, false), 0)
                            .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME,0)
//                        .activateDelay(800)
//                        .showDelay(300)
                            .withCallback(new Tooltip.Callback() {
                                @Override
                                public void onTooltipClose(Tooltip.TooltipView tooltipView, boolean b, boolean b1) {
//                                    showNewToolTip();
                                }

                                @Override
                                public void onTooltipFailed(Tooltip.TooltipView tooltipView) {

                                }

                                @Override
                                public void onTooltipShown(Tooltip.TooltipView tooltipView) {

                                }

                                @Override
                                public void onTooltipHidden(Tooltip.TooltipView tooltipView) {

                                }
                            })
                            .text(getString(R.string.tooltip_three))
                            .maxWidth(500)
                            .withArrow(false)
                            .withOverlay(false)
//                        .typeface(mYourCustomFont)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
//            mSharedPref.setTutorial3(false);

    }

    private void showNewToolTip() {
        Tooltip.make(mContext,
                new Tooltip.Builder(104)
                        .anchor(mTooltipAnchor, Tooltip.Gravity.TOP)
                        .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME,0)
                        .text(getString(R.string.tooltip_four))
                        .maxWidth(500)
                        .withArrow(false)
                        .withOverlay(false)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
//            mSharedPref.setTutorial3(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        dbHelper = new HollaNowDbHelper(mContext);
        try {
            allContacts = dbHelper.allContacts();
        } catch (SQLiteDatabaseLockedException e) {
            Log.e(TAG, e.getMessage());
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter = new ContactAdapter(allContacts, mContext);
        mRecyclerView.setAdapter(adapter);

        if (getUserVisibleHint()) {
            if (mSharedPref == null) {
                mSharedPref = new HollaNowSharedPref(mContext);
            }
            if (!mSharedPref.isTutorial()) {
                /**
                 Tool tip here...
                 **/
                Tooltip.make(mContext,
                        new Tooltip.Builder(101)
                                .anchor(mTooltipAnchor, Tooltip.Gravity.TOP)
                                //                        .closePolicy(new Tooltip.ClosePolicy()
                                //                                .insidePolicy(true, true)
                                //                                .outsidePolicy(true, false), 0)
                                .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                                .withCallback(new Tooltip.Callback() {
                                    @Override
                                    public void onTooltipClose(Tooltip.TooltipView tooltipView, boolean b, boolean b1) {
                                        showNextToolTip();
                                    }

                                    @Override
                                    public void onTooltipFailed(Tooltip.TooltipView tooltipView) {

                                    }

                                    @Override
                                    public void onTooltipShown(Tooltip.TooltipView tooltipView) {

                                    }

                                    @Override
                                    public void onTooltipHidden(Tooltip.TooltipView tooltipView) {

                                    }
                                })
                                //                        .activateDelay(800)
                                //                        .showDelay(300)
                                .text(getString(R.string.tooltip_one))
                                .maxWidth(500)
                                .withArrow(false)
                                .withOverlay(false)
                                //                        .typeface(mYourCustomFont)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
                mSharedPref.setTutorial(true);
            }
        }

        new syncDb("sync contact").execute("yes");



    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location services requested. ");
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        //Log.d(TAG, location.toString());

        if (location!=null) {
            mCurrentLocation = location;
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            String lastSeenAddress = null;
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                List<Address> addressList2 = geocoder.getFromLocation(6.5767137,3.3568001, 1);
                lastSeenAddress = addressList.get(0).getFeatureName() + ", "+ addressList.get(0).getSubLocality() + ", " + addressList.get(0).getLocality()+ ", "+ addressList.get(0).getAdminArea() + ", "+ addressList.get(0).getCountryName();
                String lastSeenAddress2 = "Feature name: "+addressList2.get(0).getFeatureName()+ ", Sublocality name:" +
                        addressList2.get(0).getSubLocality() + ", AdminArea name:" + addressList2.get(0).getAdminArea()+
                        ", Locality name:" +  addressList2.get(0).getLocality()+ addressList2.get(0);
                Log.e(TAG+" last seen",lastSeenAddress + " --- " + lastSeenAddress2);

            } catch(IOException e) {
                e.printStackTrace();
            }


            User currentUser = mSharedPref.getCurrentUser();
            mSharedPref.setLattitude((float) location.getLatitude());
            mSharedPref.setLongtitude((float) location.getLongitude());
            currentUser.setLat((float) location.getLatitude());
            currentUser.setLng((float) location.getLongitude());
            currentUser.setLastSeen(lastSeenAddress);
            //update current user in shared pref
            mSharedPref.setCurrentUser(currentUser.toString());

            Log.e("LOCATION",  mSharedPref.getCurrentUser().getLastSeen() +" : "+mSharedPref.getCurrentUser().getLat()+ mSharedPref.getCurrentUser().getLng()+":location-location,mCurrentlo,sharedLoc:" + location.getLatitude() + mCurrentLocation.getLatitude() + mSharedPref.getLattitude());
            updateUser();

        }


    }

    private void updateUser() {
        User currentUser = mSharedPref.getCurrentUser();
        Call<User> call = hollaNowApiInterface.editUserProfile(currentUser, mSharedPref.getToken());
        Log.e("EDITED_USER=> ", currentUser.toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "user and location updated "+ response.body().toString());
                } else {
                    Log.e(TAG, "location update error: " + response.code() + response.message());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.e(TAG, "location update failed: "+t.getMessage());
            }
        });

    }


    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e(TAG, "All location settings are satisfied.");
//                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        startUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void startUpdates(){
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds
    }

    //TODO: search hollanow db by list of contact phone numbers... and get their users...



}
