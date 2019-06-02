package com.doxa360.android.betacaller;


import android.Manifest;
import android.app.Activity;
//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


import it.sephiroth.android.library.tooltip.Tooltip;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    private File file;
    private String android_id;
    private ProgressDialog mProgressDialog;
    private boolean flag_postExecute;
    private  String fileUrl ;
    private  String deviceId;
    private boolean flag_contactList;
    private  List<Parse_Contact> contactList1;
    private static int refresh;

    public HomeFragment() {
        dbHelper = new HollaNowDbHelper(mContext);
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Log.e(TAG, "onCreate () -1-1-1-1-1-1-1-1-1-1-1" );
        mContext = getContext();
        mSharedPref = new HollaNowSharedPref(mContext);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();  //////////////////////////////////////  Just Added this
////////////////////////////////////////////////////////////////////////////
     //   mLocationRequest = LocationRequest.create()
      //          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
       //         .setInterval(10 * 1000)        // 10 seconds, in milliseconds
        //        .setFastestInterval(5 * 1000); // 5 second, in milliseconds/////////////
        /////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////
     //   LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
     //   builder.addLocationRequest(mLocationRequest);
     //   builder.setAlwaysShow(true);
     //   mLocationSettingsRequest = builder.build();
      //  checkLocationSettings();
        /////////////////////////////////////////////////////////////////////////
    /**    try{
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "Holla07037304321.txt" );
            file.createNewFile();
            byte[] data = "Name: Iheruome Michael; Phone: 07037304321".getBytes();
            if (file.exists()) {
                OutputStream out = new FileOutputStream(file);
                out.write(data);
                out.close();
            }
        }catch(FileNotFoundException fe){
            Log.e(TAG, "FileNotfoundException here by file");
        }
        catch (IOException e){
            Log.e(TAG, "IOException here by file");
        }**/

        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "Device Id of the itel1516 "+android_id);
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
       // Log.e(TAG, "onCreateView () 000000000000" );

        if(mSharedPref != null)
        flag_contactList = mSharedPref.getAnotherDevice_flag();
        else{
            mSharedPref = new HollaNowSharedPref(mContext);
            flag_contactList = mSharedPref.getAnotherDevice_flag();
        }

        //Log.e(TAG + "Shared Pref:", mSharedPref.getLongtitude() + " : " + mSharedPref.getLattitude());

       // Log.e(TAG + "Shared Pref user:", mSharedPref.getToken() + " : " + mSharedPref.getCurrentUser().getName()+ " : " + mSharedPref.getCurrentUser().toString());

        allContacts = new ArrayList<Parse_Contact>();
        resolver = mContext.getContentResolver();
        mProgressDialog = new ProgressDialog(getContext(),R.style.ThemeOverlay_AppCompat_Dialog_Alert);
       // mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating ...");
//        mCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        mTooltipAnchor = (TextView) rootView.findViewById(R.id.tooltip_anchor);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contacts_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        fastScroller=(FastScroller) rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(mRecyclerView, fetchPhoneContactsDb());


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
            //Log.e(TAG, "onPostExecute 111111111111 " );
            if (allContacts == null) {
                allContacts = new ArrayList<Parse_Contact>();
            }
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
                Log.e(TAG, "Adapter updated " + allContacts.size());
                if (MyToolBox.isNetworkAvailable(mContext)) {
//                checkLocation();
                   // findNearbyUsersByContactList();
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
        dbHelper = new HollaNowDbHelper(getContext());
//        dbHelper.clearAndRecreateDb();
     //   if(!flag_contactList)
        allContacts = dbHelper.allContacts();
      //  else if(flag_contactList)
       //     allContacts = contactList1;
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter = new ContactAdapter(allContacts, mContext);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        if(!flag_contactList) {
            Log.e(TAG, "fetchPhoneContacts() when taking from local contacts" );
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

                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
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

                            if(!contactList.contains(contact)) {
                                dbHelper.cacheContacts(contact);
                                contactList.add(contact);
                            }

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
        }else {
            Log.e(TAG, "fetchPhoneContacts() when taking from database contacts" );
            contactList1 = dbHelper.allContacts();
            return contactList1.size();
        }
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
           // Log.e(TAG, "list of contacts in json" + new GsonBuilder().create().toJson(phoneNumbers));
            Call<List<User>> call = hollaNowApiInterface.getUsersByContactList(new GsonBuilder().create().toJson(phoneNumbers), mSharedPref.getToken());
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.code() == 200) {
                        //TODO: check location thingy
                        response.body();// users on hollanow
                        Log.e(TAG, "Users on HollaNow "+response.body().size());
                        Log.e(TAG, "Users on HollaNow "+response.body().size());
                        Log.e(TAG, "Users on HollaNow "+response.body().size());
                        for (User user:response.body()) {
                            Log.e(TAG, "Users on HollaNow "+user.getUsername());
                            if (user.getLat() != 0 && user.getLng() != 0) {
                                Parse_Contact contact = dbHelper.searchContactByPhoneNumber(user.getPhone());//("+2348036428999\u202C");
                                if (contact!=null) {
                                   // Log.e(TAG, "non null contact "+ contact.getId()+ " - " + contact.getPhoneNumber());
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
        Log.e(TAG, "onAttach () 222222222 " );

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu,inflater);
       // MenuItem itemMessage = menu.findItem(R.id.action_refresh);
        ////////////////////////////////////////////////////////////////////////////////////////////////
       String re =  mSharedPref.getFlagContacts();
       // if(re.equals("Update"))
        //if(mSharedPref.getrefresh_flag())
      //  itemMessage.setVisible(true);
      //  else if(!(re.equals("Update")))
       //     itemMessage.setVisible(false);
       // MenuInflater inflater = getSuppo
       // return true;
    }

   /** @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case  R.id.action_refresh:
                Fragment fragment =   getFragmentManager().findFragmentById(R.id.fragment_container_home);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(fragment);
                        ft.attach(fragment);
                                ft.commit();

                return true;
        }
        return false;
    } **/


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
        Log.e(TAG, "onStart () 33333333333 "  );
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
        Log.e(TAG, "onResume () 555555555555555" );
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        //findNearbyUsersByContactList();

        dbHelper = new HollaNowDbHelper(mContext);
        try {
           // if(!flag_contactList)
            allContacts = dbHelper.allContacts();
          //  else  if(flag_contactList)
           //     allContacts = contactList1;
        } catch (SQLiteDatabaseLockedException e) {
            Log.e(TAG, e.getMessage());
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter = new ContactAdapter(allContacts, mContext);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

        String i = mSharedPref.getFlagContacts();
        if(i.equals("Update")) {
            mProgressDialog.setMessage("Updating...");
           // mProgressDialog.show();
            mSharedPref.setFlagContacts("No Update");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
            builder.setTitle("HollaNow Update Contacts");
            builder.setMessage("This would be done automatically");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException a){
                        Log.e(TAG, "InterruptException here by file");
                    }

                   // mProgressDialog.dismiss();
                      //////////////
                    refresh++;
                  retrieveContacts();
                }
            });
           // builder.setNegativeButton("Cancel", null);
            builder.show();
           // mProgressDialog.dismiss();
        }else if(i.equals("BackUp")){
            mSharedPref.setFlagContacts("No Update");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
            builder.setTitle("HollaNow BackUp Contacts");
            builder.setMessage("Do you want to backup your Contacts?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException a){
                        Log.e(TAG, "InterruptException here by file");
                    }
                    mProgressDialog.dismiss();
                    mProgressDialog = new ProgressDialog(getContext(),R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    mProgressDialog.setMessage("Progress... ");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMax(100);
                    mProgressDialog.show();
                    new  ReadContacts().execute();

                  //  backUpContacts(file, android_id);
                   // callnote();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
       // getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        Log.e(TAG, "onPause () 666666666" );
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e(TAG, "onStop () 777777777777" );
        dbHelper.close();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy () 88888888888888" );
        refresh = 0;
        dbHelper.close();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location services requested. ");
        if(mSharedPref != null)
            mSharedPref.setlocation(location);
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        //Log.d(TAG, location.toString());

        if (location!=null) {
            mCurrentLocation = location;
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            String lastSeenAddress = null;
            //location.getLatitude()  , location.getLongitude(), 1
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                List<Address> addressList2 = geocoder.getFromLocation(6.5767137,3.3568001, 1);
                lastSeenAddress = addressList.get(0).getFeatureName() + ", "+ addressList.get(0).getSubLocality() + ", " + addressList.get(0).getLocality()+ ", "+ addressList.get(0).getAdminArea() + ", "+ addressList.get(0).getCountryName()+",  "+addressList.get(0).getAddressLine(0);
                String lastSeenAddress2 = "Feature name: "+addressList2.get(0).getFeatureName()+ ", Sublocality name:" +
                        addressList2.get(0).getSubLocality() + ", AdminArea name:" + addressList2.get(0).getAdminArea()+
                        ", Locality name:" +  addressList2.get(0).getLocality()+ addressList2.get(0);
               // Log.e(TAG+" last seen",lastSeenAddress + " --- " /**+ lastSeenAddress2**/);

            } catch(IOException e) {
                e.printStackTrace();
            }catch (IndexOutOfBoundsException a){
                Log.e(TAG, "Error Occured here:  "+a.getMessage());
            }


            User currentUser = mSharedPref.getCurrentUser();
            mSharedPref.setLattitude((float) location.getLatitude());
            mSharedPref.setLongtitude((float) location.getLongitude());
            if(currentUser != null) {
                currentUser.setLat((float) location.getLatitude());
                currentUser.setLng((float) location.getLongitude());
                currentUser.setLastSeen(lastSeenAddress);
                //update current user in shared pref
                mSharedPref.setCurrentUser(currentUser.toString());
            }
            //////////////////////////////////////////////////////////////////////////////////

            //Log.e("LOCATION",  mSharedPref.getCurrentUser().getLastSeen() +" : "+mSharedPref.getCurrentUser().getLat()+ mSharedPref.getCurrentUser().getLng()+":location-location,mCurrentlo,sharedLoc:" + location.getLatitude() + mCurrentLocation.getLatitude() + mSharedPref.getLattitude());
            updateUser();

        }


    }

    private void updateUser() {
        User currentUser = mSharedPref.getCurrentUser();
        Call<User> call = hollaNowApiInterface.editUserProfile(currentUser, mSharedPref.getToken());
        try {
            Log.e("EDITED_USER=> ", currentUser.toString());
        }catch (NullPointerException e){
            Log.e(TAG, "Error Occured here:  "+e.getMessage());
        }
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
                }catch (NullPointerException a){
                    Log.e(TAG, "PendingIntent unable to execute request."+a.getMessage());
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

    private void backUpContacts(File contacts, String id){
        String username = null;
        if(mSharedPref.getCurrentUser() != null) {
            username = mSharedPref.getCurrentUser().getUsername();
        }



            Log.e(TAG, "Intro to contact management " );
        Log.e(TAG, "Check some parameters "+username+"  "+id+" " );
            //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //     + File.separator + "mikoko3.pdf");


            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), contacts);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("zip", contacts.getName(), requestFile);
            if (MyToolBox.isNetworkAvailable(mContext)) {
                HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
             //   if (token != null && id != null){
                    Call<ResponseBody> call = hollaNowApiInterface.contactManagement(body,id,username);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            mProgressDialog.dismiss();
                            Log.e(TAG, "success of contacts " + response.code() + "");
                            Toast.makeText(mContext, "Contact Saved", Toast.LENGTH_SHORT).show();
                            try {
                                Log.e(TAG, "success of contacts " + response.body().string() + "");
                                // Log.e(TAG, "success of contacts " + response.body().string() + "");
                                Log.e(TAG, "success of contacts " + response.body() + "");
                                //Log.e(TAG, "success of contacts " + response.body().string() + "");
                                // Log.e(TAG, "success of contacts " + response.body().string() + "");
                                //  Log.e(TAG, "success of contacts " + response.body().string() + "");
                                //  Log.e(TAG, "success of contacts " + response.body().string() + "");
                            } catch (IOException e) {
                                Toast.makeText(mContext, "Error updating Device idj nk nikniknk", Toast.LENGTH_SHORT).show();
                            }
//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "error from contacts: " + "error updating device id");
                            Log.e(TAG, "error from contacts: " + response.code());
                            Log.e(TAG, "error from contacts: " + response.code());
                            Log.e(TAG, "error from contacts: " + response.code());
                            Log.e(TAG, "error from contacts: " + response.code());


                            Toast.makeText(mContext, "Contact Not Saved", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
//                    Toast.makeText(getApplicationContext(), "Network error. Try again", Toast.LENGTH_LONG).show();

                    }
                });
         //   }
            }

    }


    private void retrieveContacts(){
        mProgressDialog.show();
        String username = null;
        String token = null;
        if(mSharedPref.getCurrentUser() != null) {
            token = mSharedPref.getCurrentUser().getToken();
        }


        Log.e(TAG, "Intro to retrieving contacts " );
        // Log.e(TAG, "Check some parameters "+token+"  "+id+" " );
        //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        //     + File.separator + "mikoko3.pdf");

        try {
             username = mSharedPref.getCurrentUser().getUsername();
        }catch (NullPointerException ee){
            Toast.makeText(mContext, "could not update ", Toast.LENGTH_SHORT).show();
        }

        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            Call<JsonElement> call = hollaNowApiInterface.retrieve_contact(username, token);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.code() == 200) {
                        mProgressDialog.dismiss();

                        Log.e(TAG, "success of contacts " + response.body() + "");
                        // Log.e(TAG, "success of contacts " + response.body().string() + "");
                        Log.e(TAG, "success of contacts " + response.body().toString() + "");

                        ListRetrieveContact list = new GsonBuilder().create().fromJson(response.body().toString(),ListRetrieveContact.class);
                        if(list.getFile() != null) {
                            try {
                            fileUrl = BetaCaller.FILEURL + list.getFile().get(0).getmFile();

                            deviceId = list.getFile().get(0).getmDeviceId();
                           // Log.e(TAG, "success of contacts " + fileUrl + "");
                           // Log.e(TAG, "success of contacts " + deviceId + "");
                          //  Log.e(TAG, "success of contacts " + android_id + "");
                            Integer i = 0;
                           final  byte b = 0;
                            //final URL url;
                            try {
                               final URL url = new URL(fileUrl);
                                // Scanner sc = new Scanner(url.openStream());
                                //   while(sc.hasNext())
                                //       i = i +  sc.next().length();
                                //  b =  i.byteValue();
                                if(deviceId.equals(android_id)) { // android_id
                                    flag_contactList  = false;

                                    new GetContacts(url, b).execute();
                                }
                                else {
                                    if(!(refresh >= 2)) {
                                        Log.e(TAG, "This is when refresh is = "+refresh +" 8888888888888888888888888888888888" );
                                        flag_contactList = true;
                                        mSharedPref.setRefresh_flag(true);
                                        mSharedPref.setAnotherDevice_flag(flag_contactList); // save the boolean
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                                        builder.setTitle("HollaNow Import Contacts");
                                        builder.setMessage("New device dectected.  Would You like to import contacts on this device? ");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mSharedPref.setRefresh_flag(true);
                                                // new ReadContacts().execute();
                                                mProgressDialog.setMessage("Importing contact ...");
                                                mProgressDialog.show();
                                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                startActivity(intent);
                                                new GetContacts(url, b).execute();
                                            }
                                        });
                                        builder.setNegativeButton("No", null);
                                        builder.show();
                                    }else {
                                        Log.e(TAG, "This is when refresh is = 0   9999999999999999999999999999 " );
                                        refresh = 0;
                                    }
                                }

                            } catch (MalformedURLException w) {
                                Log.e(TAG, "MalformedURLException exception here " + w.getMessage());
                            } //catch (IOException a) {
                              //  Log.e(TAG, "MalformedURLException exception here " + a.getMessage());
                            }catch(IndexOutOfBoundsException a){
                                Toast.makeText(mContext, "Error Uploading File ", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "error retieving file : " );
                            }
                        }else
                            Toast.makeText(mContext, "Update failed ", Toast.LENGTH_SHORT).show();


//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressDialog.dismiss();
                        Log.e(TAG, "error from contacts: " + "error updating device id");
                        Log.e(TAG, "error from contacts: " + response.code());

                        Log.e(TAG, "error from contacts: " + response.errorBody());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.errorBody().toString());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.errorBody().toString());

                        Toast.makeText(mContext, "Error updating Device id", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }



    private class ReadContacts extends AsyncTask<Void, Integer,String>{

       // Uri uri;
        String phone = null;
        List<Parse_Contact> contactList = new ArrayList<Parse_Contact>();
        String phoneNumber = null;
        String name = null;
        final String start = "START ";
        final String end = "END ";
        Integer j;


        @Override
        protected  String doInBackground(Void... argo) {
           // mProgressDialog.show();

            if (!contactList.isEmpty())
                contactList.clear();
            if (dbHelper != null)
                contactList = dbHelper.allContacts();
            else {
                dbHelper = new HollaNowDbHelper(mContext);
                contactList = dbHelper.allContacts();
            }
            if (contactList.isEmpty()) {
                if (!allContacts.isEmpty())
                    contactList = allContacts;
            }else if(contactList.size() < allContacts.size())
                contactList = allContacts;
            if (mSharedPref.getCurrentUser() != null)
                name = mSharedPref.getCurrentUser().getUsername();
            Log.e(TAG, "File copy: " + name);
            Log.e(TAG, "File copy: " + contactList.size());
            if (contactList.size() > 0) {
                try {
                    //Thread.sleep(1000);
                    if (name != null) {
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "Holla" + name + ".txt");
                       // name =
                    }
                  //  else if (name != null) {
                   //     file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "Holla" + name + ".txt");
                    //    name = mSharedPref.getCurrentUser().getEmail();
                  //  }
                  //  else if (name != null)
                   //     file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "Holla" + name + ".txt");
                   //     file.createNewFile();

                    OutputStream out = new FileOutputStream(file);
                    if (file.exists()) {
                        for (int i = 0; i < contactList.size(); i++) {
                            //  byte[] data = "Name: Iheruome Michael; Phone: 07037304321".getBytes();
                            Log.e(TAG, "File copy: " + contactList.size());
                            Log.e(TAG, "File copy: " + contactList.size());
                            Log.e(TAG, "File copy: " + contactList.size());
                            name = contactList.get(i).getDisplayName()+" ";
                            phoneNumber = contactList.get(i).getPhoneNumber()+" ";
                            out.write(start.getBytes());
                            out.write("\n".getBytes());
                            out.write(name.getBytes());
                            out.write("\n".getBytes());
                            //out.write("MIDDLE ".getBytes());
                            out.write(phoneNumber.getBytes());
                            out.write("\n".getBytes());
                            out.write(end.getBytes());
                            out.write("\n".getBytes());
                            j = (int) ((i * 100)/  contactList.size());
                            Log.e(TAG, "PublishProgress here by file "+j);
                           // mProgressDialog.setMessage("Compiled " + j);
                           // mProgressDialog.show();
                             publishProgress(j);
                        }
                    }
                    out.close();
                    contactList.clear();
                } catch (FileNotFoundException fe) {
                    Log.e(TAG, "FileNotfoundException here by file");
                   // Toast.makeText(mContext, "File Not Saved", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e(TAG, "IOException here by file");
                    //Toast.makeText(mContext, "File Not Saved", Toast.LENGTH_SHORT).show();
                }catch (NoSuchFieldError fieldError){
                    Log.e(TAG, "FieldError occurred here by file");
                  //  Toast.makeText(mContext, "File Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
            return "progress";
        }

        @Override
        public   void onProgressUpdate(Integer... a){
            super.onProgressUpdate(a);
            Log.e(TAG, "You are at onProgressUpdate "+a[0]);
            mProgressDialog.setProgress(a[0]);

        }
    @Override
        public void onPostExecute(String result){
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.e(TAG, "You are at PostExecute "+result);
        flag_postExecute = true;

        if(!flag_contactList) {
            if (flag_postExecute) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException a) {
                    Log.e(TAG, "InterruptException here by file");
                }
                if (file != null) {
                    mProgressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    mProgressDialog.setMessage("saving contact... ");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();
                    backUpContacts(file, android_id);
                }
            }
            Toast.makeText(mContext, "Contact Saved ", Toast.LENGTH_LONG).show();
        }else if(flag_contactList){
            mProgressDialog.dismiss();
            Toast.makeText(mContext, "Refreshed Contacts", Toast.LENGTH_LONG).show();
           // Intent intent = new Intent(mContext, H)
            //FragmentManager fm = mContext.;
      //     android.app.Fragment fragment = /**(android.support.v4.app.Fragment)**/getActivity().getFragmentManager().findFragmentById(R.id.fragment_container_home);
           // if(fragment instanceof HomeFragment){
        //        android.app.FragmentTransaction trans = (getActivity()).getFragmentManager().beginTransaction();
             //   trans.detach(fragment);
              //  trans.attach(fragment);
              //  trans.commit();
           // }
          //  Bundle args = new Bundle();
          //  if (!isCurrentUser) {
          //      args.putString(USER_ID, userIdIntent);
           //     args.putString(USER_NAME, usernameIntent);
            //    args.putString(FULL_NAME, fullnameIntent);
             //   args.putString(PHONE, phoneIntent);
            //FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.detach(fragment).attach(fragment).commit();
           // mSharedPref.setRefresh_flag(true);
          //  refresh++;
          //  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
            //        new HomeFragment());

        }
        }

        }

    private class GetContacts extends AsyncTask<Void, Integer,String> {

        File file;
        float file_size;
        BufferedReader reader;
        FileReader fr;
        Scanner sc;
         boolean flag_count;
        Parse_Contact contact;
        String name;
        String  phone, checkStart;
        List<Parse_Contact> contactList1;
        URL mURL;
        long mB;

        public GetContacts(URL url, byte b){
            mURL = url;
            mB = b;
            mProgressDialog.dismiss();
        }

        @Override
        protected String doInBackground(Void... argo) {
            contactList1 = new ArrayList<Parse_Contact>();
            contact = new Parse_Contact();
            try {
            file = new File(mURL.getFile());
           // fr = new FileReader(mURL.getFile());
           // reader = new BufferedReader(fr);

                sc = new Scanner(mURL.openStream());
            }catch(FileNotFoundException e){
                Log.e(TAG, "FileNotFoundException Doent exist "+e.getMessage());
            }catch (IOException a){
                Log.e(TAG, "FileNotFoundException Doent exist "+a.getMessage());
            }

            String name = null;
            Integer b = 0;
         //   if(file.exists()){
                file_size = (float)Integer.parseInt(String.valueOf(file.length()));
            Log.e(TAG, " Fille Size   "+file_size);
              //  if(mSharedPref.getFileSize() > file_size){
                    //update file
            Log.e(TAG, " About to start  ");
               // name = reader.readLine();
             //   mB = mB + name.length();
            if(dbHelper != null) {
                dbHelper.clearContacts();
            }
                while (sc.hasNext()) {
                    flag_count = false;
                    Log.e(TAG, " .....  has started     ");
                    name = sc.nextLine().trim();
                    Log.e(TAG, " .....  has started     "+name);
                    if (name.equals("START")) {
                        flag_count = true;
                        name = sc.nextLine().trim();
                        mB = mB + name.length();
                        phone = sc.nextLine().trim();
                        mB = mB + phone.length();
                        contact.setDisplayName(name);
                        contact.setPhoneNumber(phone);
                        Log.e(TAG, " Contacts  "+name+"   "+phone);
                    }
                  //  name = sc.nextLine().trim();
                  //  mB = mB + name.length();
               // int i = (int)((mB/file_size)*100);
                //    publishProgress(i);
                    if(flag_count)
                    contactList1.add(contact);
                    if(dbHelper != null) {
                        //dbHelper.clearContacts();
                        dbHelper.cacheContacts(contact);
                    }

                    Log.e(TAG, " Contacts  List"   +contactList1.size());
                } //end of hasNext()

                sc.close();

            //  if(file.)
            return "perfect";
        }

        @Override
        public void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.e(TAG, "You are at onProgressUpdate "+a[0]);
            mProgressDialog.setProgress(a[0]);
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Toast.makeText(mContext, "Import Succesful", Toast.LENGTH_SHORT).show();
            int i ;
            Log.e(TAG, "onPostExecute "+result);
            mSharedPref.setRefresh_flag(false);
            try {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }catch(NullPointerException ee){
                Log.e(TAG, "PostExecute NullpointerException Error ");
            }
          /**  if (dbHelper != null)
                i = dbHelper.allContacts().size();
            else {
                dbHelper = new HollaNowDbHelper(mContext);
                i = dbHelper.allContacts().size();
            } **/
         /**   if(i > contactList1.size()){
                if(!mSharedPref.getrefresh_flag()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    builder.setTitle("HollaNow Update Contacts");
                    builder.setMessage("Your contacts needs update. Click Ok to continue.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mProgressDialog.setMessage("Updating contact ...");
                            mProgressDialog.show();
                            new ReadContacts().execute();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
            } **/

           // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
            //        new HomeFragment());

        }

    }


}
