package com.doxa360.android.betacaller;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.HollaContactAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.SerializableUser;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallDiaryFragment extends Fragment {


    private static final String TAG = "CallDiaryFragment";
    private Context mContext;
    private Cursor callCursor;
    private static List<Parse_Contact> allHollaContacts;
    private List<Parse_Contact> allHollaContacts1;
    private List<User> allHollaContactsParse = new ArrayList<User>();
    //    private String logNumber;
//    private String logName;
//    private String logDuration;
//    private int logType;
    private RecyclerView mRecyclerView;
    private static HollaContactAdapter mAdapter;

    private HollaNowDbHelper dbHelper;
    private HollaNowSharedPref mSharedPref;
    private TextView mTooltipAnchor;
    // private TextView mEmptyText;
    private ListView list;
    private static List<Parse_Contact> listContacts = new ArrayList<Parse_Contact>();

    private ProgressBar mProgressBar;

    private HollaNowApiInterface hollaNowApiInterface;
    private List<String> listUsernames;
 //   private List<String> removeDuplicate;
    List<Parse_Contact> removeDuplicate1;


   // private static Set<String> hollaContacts;

    private static int countData;


    //private static List<String> phoneNumbers_add1;
    private Set<Parse_Contact> orderedList;
    private static boolean regulateContacts;
    private Cursor mCursor;

    private static int doOnceSurf;
    private int checkmate = 0;

    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath;
    private String dataPath1 = SDPath;
    private static int BUFFER_SIZE = 4096;
    public static final String BROADCAST_ACTION = "com.doxa360.android.CallDiaryFragment";
    public static final String BROADCAST_ACTION1 = "com.doxa360.android.CallDiaryFragment1";
    private BroadcastReceiver mReceiver;

    private static final int STORAGE_PERMISSION_CODE = 23;
    private static final int WRITE_PERMISSION_CODE = 10;
    private static final int PHONE_STATE_PERMISSION = 13;
    //private static final int OUTGOING_CALLS_PERMISSION = 15;
    private static final int READ_PHONE_STATE = 17;
    //private static final int READ_PHONE_STATE_CHANGED = 14;



    String[] itemname = {
            "Invite friends"
    };

    Integer[] imgid = {
            R.drawable.ic_share_icon
    };
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1091;

    public CallDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_call_diary, container, false);

        mSharedPref = new HollaNowSharedPref(mContext);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        if(Build.VERSION.SDK_INT >= 23) {
            if (isReadStorageAllowed()) {
               // Toast.makeText(mContext, "You already have the permission", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "You already have the permission  " );
            } else
                requestStoragePermission();

            if (isWriteStorageAllowed()) {
               // Toast.makeText(mContext, "You already have the permission", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "You already have the permission  " );
            } else
                requestWritePermission();
            if(isPhoneStateAllowed()){
                Log.e(TAG, "You already have the permission for phone " );
            }else
                requestPhoneCallPermission();
           // if(isOutGoingCallAllowed()){
            //    Log.e(TAG, "You already have the permission for outgoing calls " );
           // }
          //  else
              //  requestProcessCallPermission();
            if(isReadPhoneStateAllowed()){
                Log.e(TAG, "You already have the permission for read phone state  " );
            }else
                requestPhoneCallStatePermission();
        }

        dataPath = null;
        dataPath1 = null;
        dataPath = SDPath;
        dataPath1 = SDPath;
        try {
            if (mSharedPref.getCurrentUser().getUsername() != null)
                dataPath = dataPath + "/HollaNow/" + mSharedPref.getCurrentUser().getUsername() + "/zip/";
            if (mSharedPref.getCurrentUser().getUsername() != null)
                dataPath1 = dataPath1 + "/HollaNow/" + mSharedPref.getCurrentUser().getUsername() + "/regis/";
        }catch (NullPointerException ex){Log.e(TAG, "Exception occurred here" );}
        dbHelper = new HollaNowDbHelper(mContext);
        orderedList = new HashSet<Parse_Contact>();

        listUsernames = new ArrayList<String>();
        // list = (ListView)rootView.findViewById(R.id.callLog_listview);

        allHollaContacts = new ArrayList<Parse_Contact>();
        allHollaContacts1 = new ArrayList<Parse_Contact>();
        listContacts.clear();
        //mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
        //mRecyclerView.setAdapter(mAdapter);

//        mTooltipAnchor = (TextView) rootView.findViewById(R.id.tooltip_anchor);
        // mEmptyText = (TextView) rootView.findViewById(R.id.empty_text);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.callLog_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        removeDuplicate1 = new ArrayList<Parse_Contact>();
       // new syncDb("sync call logs").execute("yes");

        // LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        // if (mListView !=null) {
        //    mListView.setHasFixedSize(true);
        //    mCallLogRecyclerview.setLayoutManager(layoutManager);

        //}


        return rootView;
    }




    private List<Parse_Contact> fetchCallLogDb() {
        dbHelper = new HollaNowDbHelper(mContext);
//        dbHelper.clearAndRecreateDb();
        // allCallLogs = dbHelper.allPhoneLogs();
        if (!allHollaContacts.isEmpty() && allHollaContacts.size() == mSharedPref.getCallLogCount()) {
//            fetchCallLogDb();
            Log.e(TAG, "fetchCallLogsDB - true " + allHollaContacts.size());
        } else {
//            dbHelper.clearAndRecreateDb();
//            allCallLogs = fetchCallLog();
            Log.e(TAG, "fetchCallLogsDB - false " + allHollaContacts.size());
        }
        return allHollaContacts;
    }




        public Uri openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(photoUri,
                    new String[]{ContactsContract.Contacts.Photo.PHOTO_THUMBNAIL_URI}, null, null, null);
        } catch (IllegalArgumentException e) {
            //  Log.e(TAG, e.getMessage());
        }

        if (cursor == null) {
            return null;
        }
        try {
            for (int i = 0; i < 100; i++) {
                if (cursor.moveToFirst()) {
                    String data = cursor.getString(0);
                    if (data != null) {
//                        Log.e(TAG, data);
                        return Uri.parse(data);
                    }
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
       // getAllContacts(dataPath);
        //doOnceSurf++;
        allHollaContacts.clear();
       // Intent intnt = new Intent();
       // intnt.setAction(CallDiaryFragment.BROADCAST_ACTION);
       // intnt.putExtra("broadcast_HollaContact","holla_service");
      //  mContext.sendBroadcast(intnt);
        dbHelper = new HollaNowDbHelper(mContext);


        //   try {

        //  } catch (SQLiteDatabaseLockedException e) {
          Log.e(TAG, "Resume here");
       /** try {
            allHollaContacts = dbHelper.allHollaContacts();
           /// if(allHollaContacts != null)
           // allHollaContacts.removeAll(null);
            Log.e(TAG, "AllHollaContacts is  " + allHollaContacts.size());
            Log.e(TAG, "AllHollaContacts is  " + allHollaContacts.size());
            Log.e(TAG, "AllHollaContacts is  " + allHollaContacts.size());
            Log.e(TAG, "AllHollaContacts is  " + allHollaContacts.size());
            allHollaContacts.add(null);
           // if(mAdapter == null)
           // if(allHollaContacts.size() != 0  &&  mAdapter == null) {
                Log.e(TAG, "AllHollaContacts is  4 " + allHollaContacts.size());
                Log.e(TAG, "AllHollaContacts is  4 " + allHollaContacts.size());
                Log.e(TAG, "AllHollaContacts is  4 " + allHollaContacts.size());
                if(allHollaContacts.contains(null))
                    allHollaContacts.remove(null);
                Log.e(TAG, "AllHollaContacts is  5 " + allHollaContacts.size());
                Log.e(TAG, "AllHollaContacts is  5 " + allHollaContacts.size());
                Log.e(TAG, "AllHollaContacts is  5 " + allHollaContacts.size());
                allHollaContacts.add(null);
                mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

        }catch(SQLiteException ex){
            Log.e(TAG, "exception occurred " + ex.getMessage());
            Log.e(TAG, "AllHollaContacts is  1 " + allHollaContacts.size());
            Log.e(TAG, "AllHollaContacts is  1 " + allHollaContacts.size());
            Log.e(TAG, "AllHollaContacts is  1 " + allHollaContacts.size());
        } **/
        //   }
        getAllContacts(dataPath);
        getAllContacts(dataPath1);
        //for(int i=0;i<allHollaContacts.size();i++){
        //    Log.e(TAG, "CallDiaryFragment here  "+allHollaContacts.get(i).getSerialUser().getName());
        //}
        mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
       // fetchContactsByBits();


       // Log.e(TAG, "Size of allHollaContacts  " + allHollaContacts.size());
      //  Log.e(TAG, "Size of allHollaContacts  " + allHollaContacts.size());
      //  Log.e(TAG, "Size of allHollaContacts  " + allHollaContacts.size());
     //   Log.e(TAG, "Size of allHollaContacts  " + allHollaContacts.size());
        //if(allHollaContacts.size() <= 1)
          //    readHollaFromExternal(SDPath + "/HollaNow/"
           //           + mSharedPref.getCurrentUser().getUsername() + "/zip/");

        /////////////// The retrieveContacts gets all the Hollacontact stored on the server
   if(allHollaContacts.size() <= 1) {
            retrieveContacts();
        }else{
       saveHollaContact(SDPath+"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/zip/".trim()
               , mSharedPref.getCurrentUser().getUsername(),allHollaContacts);
   }

   if(allHollaContacts.size() >= 1){
       mSharedPref.setHollaContact(allHollaContacts);
   }
       // if(doOnceSurf <= 1) {

      //  }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null)
                    if(intent.getStringExtra("broadcast_HollaContact1") != null)
                        if(intent.getStringExtra("broadcast_HollaContact1").equals("refresh")) {
                            Log.e(TAG, "Intent is " + intent.getStringExtra("broadcast_HollaContact1"));
                            Log.e(TAG, "This resume resets everything to stsrt afresh ");
                            //new syncDb("get contacts").cancel(true);
                            //new syncDb("get contacts").execute("yes");


                        }

            }
        };
        mContext.registerReceiver(mReceiver, filter);
       //     allHollaContacts1.clear();
      //    if(allHollaContacts.contains(null))
      //        allHollaContacts.remove(null);
     //   for(int i = allHollaContacts.size()-1; i >= 0; i--){
     //       allHollaContacts1.add(allHollaContacts.get(i));
    //    }
     //   allHollaContacts = allHollaContacts1;
     //   allHollaContacts.add(null);
       // saveHollaContact(SDPath+"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/zip/".trim()
         //       , mSharedPref.getCurrentUser().getUsername(),allHollaContacts);


        Log.e(TAG, "GetItemCount 2 "+allHollaContacts.size());
       // Log.e(TAG, "GetItemCount 2 "+allHollaContacts.size());

        new syncContactDb("sync contact").execute("yes");

      /**  if(allHollaContacts != null){
            for(int i =0; i < allHollaContacts.size();i++) {
                try {
                    if (allHollaContacts.get(i).getSerialUser().getPhone() == null)
                        allHollaContacts.clear();
                }catch (NullPointerException ex){
                    Log.e(TAG, "NullPointer Exception "+ex.getMessage());
                }
            }
        } **/

       // readHollaFromExternal(SDPath + "/HollaNow/"
         //               + mSharedPref.getCurrentUser().getUsername() + "/zip/");

        if(allHollaContacts != null){
        allHollaContacts.remove(null);
        if(allHollaContacts.contains(null))
            allHollaContacts.remove(null);
            if(allHollaContacts.contains(null))
                allHollaContacts.remove(null);
        allHollaContacts.add(null);
        if(allHollaContacts.size() != 0) {
            if (allHollaContacts.contains(null))
                 allHollaContacts.remove(null);
            if(!allHollaContacts.contains(null))
            allHollaContacts.add(null);
            mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        }


        if(mAdapter != null) {
            mAdapter.setAdapterList(allHollaContacts);
            mAdapter.notifyDataSetChanged();
        }

        mProgressBar.setVisibility(View.INVISIBLE);
       // mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
       // mRecyclerView.setAdapter(mAdapter);
        // mCallLogRecyclerview.setAdapter(mAdapter);
        //  if (allCallLogs.size()==0) {
        //      mEmptyText.setText("No recent calls...");
        //  } else {
        //     mEmptyText.setText("");
        //  }
        removeDuplicate1.clear();


    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
       // Log.e(TAG, "AllHollaContacts is 3 " + allHollaContacts.size());
      //  Log.e(TAG, "AllHollaContacts is 3 " + allHollaContacts.size());
       // Log.e(TAG, "AllHollaContacts is 3 " + allHollaContacts.size());
       // Log.e(TAG, "AllHollaContacts is 3 " + allHollaContacts.size());
    }



    @Override
    public void onStop() {
        try {
            super.onStop();
            dbHelper.close();
            //countData1 = 0;
            mContext.unregisterReceiver(mReceiver);
        } catch (NullPointerException e) {
            Log.e(TAG, "AN Error Occured here:  " + e.getMessage());
        }catch(IllegalArgumentException ex){
            Log.e(TAG, "Receiver not registered :  " + ex.getMessage());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

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
                    //fetchCallLog();  fetchCallLog is a long task so requires doing in background
                 //   new syncDb("sync call logs").execute("yes");

                } else {
                    if(mAdapter != null)
                        mAdapter.notifyDataSetChanged();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }case STORAGE_PERMISSION_CODE:
               // if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
               //     Toast.makeText(mContext, "Permission Granted! ",Toast.LENGTH_LONG);
               // else
                //    Toast.makeText(mContext, "Oops you just denied the permission! ",Toast.LENGTH_LONG);
                break;

            case WRITE_PERMISSION_CODE:
               // if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
               //     Toast.makeText(mContext, "Permission Granted! ",Toast.LENGTH_LONG);
               // else
                //    Toast.makeText(mContext, "Oops you just denied the permission! ",Toast.LENGTH_LONG);
                break;

            case PHONE_STATE_PERMISSION:
                Toast.makeText(mContext, "Permission Granted for Phone State! ",Toast.LENGTH_LONG);
                break;

           // case OUTGOING_CALLS_PERMISSION:
               // Toast.makeText(mContext, "Permission Granted for outgoing calls! ",Toast.LENGTH_LONG);
              //  break;
            // other 'case' lines to c
            // heck for other
            // permissions this app might request

            case READ_PHONE_STATE:
                Toast.makeText(mContext, "Permission Granted for Read phone state ",Toast.LENGTH_LONG);
                break;

        }
    }

    private void requestStoragePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mContext, "Read external storage permission allows us to read contacts. " +
                    "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private boolean isReadStorageAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }

    private void requestWritePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(mContext, "Write external storage permission allows us to store contacts. " +
                    "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
    }

    private boolean isWriteStorageAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }



    private void requestPhoneCallPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)){
            Toast.makeText(mContext,
                    "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE}, PHONE_STATE_PERMISSION);
    }

   // private void requestProcessCallPermission() {
     //   if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.PROCESS_OUTGOING_CALLS)){
       //     Toast.makeText(mContext,
      //              "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
     //   }

    //    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, OUTGOING_CALLS_PERMISSION);
   // }

    private boolean isPhoneStateAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }

    private boolean isReadPhoneStateAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }

    private void requestPhoneCallStatePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)){
            Toast.makeText(mContext,
                    "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
    }


   // private boolean isOutGoingCallAllowed(){
    //    int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.PROCESS_OUTGOING_CALLS);
        //if permission is granted return true
     //   if(result == PackageManager.PERMISSION_GRANTED)
     //       return true;

        // else return false
     //   return  false;
   // }




    ////////////////////////////////////////////////////////////////////////////////////////////////

   // public void fetchContactsByBits(){
     ///   phoneNumbers = new ArrayList<String>();
    //    removeDuplicate = new ArrayList<String>();

      //  List<Parse_Contact> dbContacts = dbHelper.allContacts();

    //    for (Parse_Contact contact : dbContacts) {
    //        String phone = contact.getPhoneNumber();
     //       if(!removeDuplicate.contains(phone)) {
          //      phoneNumbers.add(phone);
           //     removeDuplicate.add(phone);
        //    }
    //    }
      //   phoneNumbers_add = new ArrayList<List<String>>();
    //     phoneNumbers_add1 = new ArrayList<String>();
    //     removeDuplicate.clear();

  //      allHollaContacts.removeAll(null);

   // }

    private byte[] getByteArrayImage(String url){
        ByteArrayOutputStream outStream = null;
        byte[] fileBytes = null;
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

             outStream = new ByteArrayOutputStream();

            byte[] bytesFromFile = new byte[1024 * 1024]; // buffer size (1 MB)
            assert bis != null;
            int bytesRead = bis.read(bytesFromFile);
            while (bytesRead != -1) {
                outStream.write(bytesFromFile, 0, bytesRead);
                bytesRead = bis.read(bytesFromFile);
            }
        }catch (MalformedURLException ex){
            Log.e(TAG, "Error: MalformedUrlException Occured");
        }catch (IOException e){
            Log.e(TAG, "Error: IOEXception Occured");
        }
        try {
             fileBytes = outStream.toByteArray();
        }catch (NullPointerException a){
            Log.e(TAG, "Error: NullPointerException Occured");
        }
        return  fileBytes;
    }





        private void getAllContacts(String path) {
        String username = mSharedPref.getCurrentUser().getUsername();
           // Log.e(TAG, "Username is  " + username);  //
           // Log.e(TAG, "File to read output here for file 2 " + path); //
        if(username != null){
            File files = new File(path);
            File mFile = null;
            File[] fileList = files.listFiles();
            if (fileList != null)
                for (File file : fileList) {
                   // Log.e(TAG, "File to read output here for file " + file.getPath()); //
                    if (file.isDirectory())
                        if (file.getPath().contains(username))
                            getAllContacts(file.getPath());

                   // Log.e(TAG, "Lemme see the files here 3 " + file.getPath()); //


                    mFile = file;
                   // Log.e(TAG, "File to read output here for file " + file.getPath()); //


                    /**  StringBuilder builder = new StringBuilder();
                     char c;
                     try {
                     if (mFile.getPath().contains(".txt")) {
                     String currentFile;
                     reader = new FileReader(mFile);
                     buf = new BufferedReader(reader);
                     while ((currentFile = buf.readLine()) != null) {
                     builder.append(currentFile);
                     }
                     }
                     } catch (FileNotFoundException ex) {

                     } catch (IOException a) {

                     } **/
                    //StringBuilder builder = new StringBuilder(count);

            if(mFile.isDirectory()) {
                Parse_Contact contact = new Parse_Contact();
               // Log.e(TAG, "Lemme see the files here 3 " + mFile.listFiles().length); //
               // Log.e(TAG, "Lemme see the files here 3 " + file.listFiles().length); //
                for (File fil : mFile.listFiles()) {
                   // Log.e(TAG, "Lemme see the files here 5 " + fil.getPath()); ///
                      countData++;
                    try {

                        SerializableUser user;
                        if (fil.getPath().contains(".ser")) {
                            FileInputStream fileInputStream = new FileInputStream(fil);
                            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                            user = (SerializableUser) objectInputStream.readObject();
                        //    Log.e(TAG, "Lemme see the contacts here  " + user.getUsername());
                           // if(user.getUsername() != null) {
                                contact.setSerialUser(user);
                           // }
                            objectInputStream.close();
                            fileInputStream.close();



                        }
                    } catch (IOException z) {
                        Log.e(TAG, "IOException FileInputStream  " + z.getMessage());
                    } catch (ClassNotFoundException zx) {
                        Log.e(TAG, " ClassNotFoundException " + zx.getMessage());
                    } catch (ClassCastException cc){
                        Log.e(TAG, " ClassCastException " + cc.getMessage());
                    }

                    /** if(file.getPath().contains("occupat")) {
                     contact.setOccupation(builder.toString());
                     countData++;
                     }
                     else if(file.getPath().contains("phone")) {
                     contact.setPhoneNumber(builder.toString());
                     countData++;
                     }
                     else if(file.getPath().contains("name")) {
                     contact.setDisplayName(builder.toString());
                     countData++;
                     } else if(file.getPath().contains("last")) {
                     contact.setLastSeen(builder.toString());
                     countData++;
                     }  **/

                    if (fil.getPath().contains(".jpg")) {
                        //Bitmap map = BitmapFactory.decodeFile(file.getPath());
                       // Log.e(TAG, "Image file is  " + fil.getPath());
                        contact.setFile(fil);
                        contact.setThumbnailUrl(fil.getPath());
                        //countData++;
                    }
                 //   if (countData >= fileList.length) {
                        //  if(removeDuplicate1.contains(contact)) {
                                if(countData >= mFile.listFiles().length) {
                                    listUsernames.add(contact.getSerialUser().getUsername());
                                    //for(int i=0;i<allHollaContacts.size();i++){
                                     //   Log.e(TAG, "CallDiaryFragment here  "+allHollaContacts.get(i).getSerialUser().getName());
                                   // }
                                    allHollaContacts.add(contact);
                                    String usernames = new GsonBuilder().create().toJson(listUsernames, List.class);
                                    mSharedPref.setListUsernames(usernames);

                                    if(mAdapter != null)
                                    mAdapter.notifyDataSetChanged();
                                 //   Log.e(TAG, "Check from CallDiaryFragment  " + contact.getSerialUser().getName());
                                  //  Log.e(TAG, "Check from CallDiaryFragment  " + allHollaContacts.size());
                                   // Log.e(TAG, "Usernames here   " + usernames);
                                   // Log.e(TAG, "save this files   " + countData);
                                    countData = 0;
                                }
                        //listUsernames.add(contact.getSerialUser().getUsername());
                       // allHollaContacts.add(contact);
                       // String usernames =  new GsonBuilder().create().toJson(listUsernames, List.class);
                       // mSharedPref.setListUsernames(usernames);
                       // Log.e(TAG, "Usernames here   " + usernames);
                        //  removeDuplicate1.add(contact);
                        //  }


                  //  }
                }
            }

                }
        }

        }





    //////////////////////////////////////////////////////////////////////////

    private boolean deleteFile(User user, String path){
        boolean saveFile = false;
        File files = new File(path);
        File[] fileList = files.listFiles();
        for (File file : fileList) {
            if (file.isDirectory())
                if(file != null) {
                    try {
                        if (file.getPath().contains(user.getUsername().trim()))
                            FileUtils.deleteDirectory(file);
                        //saveFile =  file.delete();
                        Log.e(TAG, "File delete here " + saveFile);
                        saveFile = true;

                    }catch (IOException ex){
                        Log.e(TAG, "Error Occured " + ex.getMessage());
                    }
                }
        }
        return saveFile;

    }

    public void saveHollaContact(String path, String username, List<Parse_Contact> list){
        Parse_Contact contact;
        SerializableUser seruser = new SerializableUser();
        if(list != null)
            if(list.contains(null))
                list.remove(null);
       seruser.setmUserArrayList(list);
        File userFile = new File(path+ username + ".ser".trim());
        try {
            if (!userFile.exists())
                userFile.createNewFile();
            FileOutputStream fileOutputStream1 = new FileOutputStream(userFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream1);
            objectOutputStream.writeObject(seruser);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream1.close();
        }catch(IOException ex){
            Log.e(TAG,"EXception occured 7 "+ ex.getMessage());
        }
    }


    /// RetrieveContacts from remote server.
    private void retrieveContacts(){
        //mProgressDialog.show();
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


        ////  This
        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            Call<JsonElement> call = hollaNowApiInterface.retrieve_contact(username, token);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.code() == 200) {
                        // mProgressDialog.dismiss();

                        Log.e(TAG, "success of contacts " + response.body() + "");
                        // Log.e(TAG, "success of contacts " + response.body().string() + "");
                   //     Log.e(TAG, "success of contacts " + response.body().toString() + "");
                        final ListRetrieveContact list = new GsonBuilder().create().fromJson(response.body().toString(), ListRetrieveContact.class);


                        if(list != null) {
                           new Background_downloadContact(list).execute();
                        }

                    } else{
                            //mProgressDialog.dismiss();
                            Log.e(TAG, "error from contacts: " + "error updating device id");
                       //     Log.e(TAG, "error from contacts: " + response.code());

                          //  Log.e(TAG, "error from contacts: " + response.errorBody());
                          //  Log.e(TAG, "error from contacts: " + response.code());
                          //  Log.e(TAG, "error from contacts: " + response.errorBody().toString());
                          //  Log.e(TAG, "error from contacts: " + response.code());
                           // Log.e(TAG, "error from contacts: " + response.errorBody().toString());

                            Toast.makeText(mContext, "Error updating Device id", Toast.LENGTH_SHORT).show();
                        }

                }
                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    //mProgressDialog.dismiss();
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                  //  Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private class Background_downloadContact extends AsyncTask<Void, Integer, String>{
        String mPath;
        ListRetrieveContact mList;
        String fileUrl;

       // SerializableUser serial = new SerializableUser();

       // @Override
      //  protected void onPreExecute(){

       // }
        public Background_downloadContact(ListRetrieveContact list){
           // mPath = path;
            mList = list;
            listContacts.clear();
           // allHollaContacts.clear();
        }
  ////////////////////  /////////////////////////////////////////////////////
        /////  mList contans arrays of files that are stored on nthe server
        //////  This retreives the files stored on the server /////////////////////////////
        @Override
        protected  String doInBackground(Void... params) {
            try{
            for (int i = 0; i < mList.getFile().size(); i++) {
                String file = mList.getFile().get(i).getmFile();
                Log.e(TAG, "List all the files " + file + "");
                if (file.equals((mSharedPref.getCurrentUser().getUsername() + ".txt").trim())) {
                    Log.e(TAG, "Got it here " + file + "");
                    fileUrl = BetaCaller.FILEURL + file;
                    mPath = fileUrl;
                }
            }
        }catch(NullPointerException ex){Log.e(TAG, "Nullpointer Exception just occured " + ex.getMessage() + "");}

            try{
               // if (!image.exists())
                //    image.createNewFile();

            URL url = new URL(mPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input3 = connection.getInputStream();
               // BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input3));
                Scanner sc = new Scanner(new InputStreamReader(input3));
                String line;

                while(sc.hasNextLine()){
                     Parse_Contact contacts = new Parse_Contact();
                    SerializableUser serial = new SerializableUser();
                    line = sc.nextLine();
                   // if(line.equals("START")){
                       // line = sc.nextLine();
                        while(!line.equals("END")) {
                            if (line.contains("HollaName")) {
                                line = line.substring("HollaName".length(),line.length());
                                serial.setName(line);
                            }
                            else if(line.contains("HollaPhone")) {
                                line = line.substring("HollaPhone".length(),line.length());
                                serial.setPhone(line);
                            }
                            else if(line.contains("HollaUsername")) {
                                line = line.substring("HollaUsername".length(),line.length());
                                serial.setUserName(line);
                            }
                            else if(line.contains("HollaOccupation")) {
                                line = line.substring("HollaOccupation".length(),line.length());
                                serial.setOccupation(line);
                            }
                            else if(line.contains("HollaAbout")) {
                                line = line.substring("HollaAbout".length(),line.length());
                                serial.setAbout(line);
                            }
                            else if(line.contains("HollaProfilePhoto")) {
                                line = line.substring("HollaProfilePhoto".length(), line.length());
                                if (!(line.length() < 7)){
                                    int j = 3;
                                    int k = line.length();
                                    String line1 = null;
                                    while (j < k) {
                                   //     Log.e(TAG,"Photo Sting 1 "+ line1);
                                   //     Log.e(TAG,"Photo Sting 1 "+ j);
                                     //   Log.e(TAG,"Photo Sting 1 "+ k);
                                        j++;
                                        line1 = line.substring(k-j);
                                        if(line1.contains("/")) {
                                            line1 = line1.substring(1,line1.length());
                                            break;
                                        }
                                    }
                                    line = line1;
                            }
                                serial.setProfilePhoto(line);

                            }
                            else if(line.contains("HollaIndustry")) {
                                line = line.substring("HollaIndustry".length(),line.length());
                                serial.setIndustry(line);
                            }
                            else if(line.contains("HollaEmail")) {
                                line = line.substring("HollaEmail".length(),line.length());
                                serial.setEmail(line);
                            }
                            else if(line.contains("HollaCountry")) {
                                line = line.substring("HollaCountry".length(),line.length());
                                serial.setCountry(line);
                            }
                            else if(line.contains("HollaUpdate")) {
                                line = line.substring("HollaUpdate".length(),line.length());
                                serial.setCountry(line);
                            }
                            else if(line.equals("true")) {
                                serial.setIndicateSaved(true);
                            }
                            else if(line.equals("zip"))
                                serial.setPath("zip");

                            if(sc.hasNextLine())
                                line = sc.nextLine();



                      //  }
                            if(line.equals("END")) {
                                contacts.setSerialUser(serial);
                              //  Log.e(TAG,"see the objects here 4 "+ line);
                                Log.e(TAG,"see the objects here 5 "+contacts.getSerialUser().getUsername());
                                Log.e(TAG,"see the objects here 6 "+contacts.getSerialUser().getPhone());
                               // allHollaContacts.add(contacts);
                                listContacts.add(contacts);

                                    String destinationPath = SDPath + "/HollaNow/"
                                            + mSharedPref.getCurrentUser().getUsername() + "/zip/";

                                    String destinationPath1 = SDPath + "/HollaNow/"
                                            + mSharedPref.getCurrentUser().getUsername() + "/regis/";
                                    new File(destinationPath).mkdirs();
                                    new File(destinationPath1).mkdirs();
                                    File file1 = new File(destinationPath);
                                    File file2 = new File(destinationPath1);


                                try {
                                    if (!file1.exists())
                                        file1.createNewFile();

                                    if (!file2.exists())
                                        file2.createNewFile();
                                }catch (IOException ex){
                                    Log.e(TAG,"EXception occured 31 "+ ex.getMessage());

                                }
                             if(serial.getPath() != null)
                                if(serial.getPath().equals("zip")){
                                    String destination = destinationPath+"/"
                                            +serial.getUsername()+"/".trim();
                                    new File(destination).mkdirs();
                                    destination = destination +serial.getUsername() +".ser";

                                    File userFile = new File(destination);
                                    if (!userFile.exists())
                                        userFile.createNewFile();
                                    FileOutputStream fileOutputStream1 = new FileOutputStream(userFile);
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream1);
                                    objectOutputStream.writeObject(serial);
                                    objectOutputStream.flush();
                                    objectOutputStream.close();
                                    fileOutputStream1.close();

                                      File image = null;
                                     try{

                                        // Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
                                         //Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
                                         //Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
                                        // Log.e(TAG,"This is the path 2 "+ BetaCaller.PHOTO_URL+serial.getProfilePhoto());
                                         Log.e(TAG,"This is the path 2 "+ BetaCaller.PHOTO_URL+serial.getProfilePhoto());
                                     if(serial.getProfilePhoto() != null){
                                         URL url1 =  new URL(BetaCaller.PHOTO_URL+serial.getProfilePhoto().trim());
                                         String destination1 = destinationPath+"/"
                                                 +serial.getUsername()+"/".trim();
                                         new File(destination).mkdirs();

                                         destination1 = destination1 +serial.getProfilePhoto().trim();
                                        // Log.e(TAG,"This is the path  "+ destination1);
                                        // Log.e(TAG,"This is the path  "+ destination1);
                                        // Log.e(TAG,"This is the path  "+ destination1);
                                        // Log.e(TAG,"This is the path  "+ destination1);
                                         Log.e(TAG,"This is the path  "+ destination1);
                                     image = new File(destination1);
                                     if (!image.exists()) {
                                     image.createNewFile();
                                     HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                                     connection1.setDoInput(true);
                                     connection1.connect();
                                     InputStream input1 = connection1.getInputStream();
                                     Bitmap bitmap = BitmapFactory.decodeStream(input1);
                                         IOUtils.toByteArray(input1);
                                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                     bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                                     byte[] bitmapdata = bos.toByteArray();
                                     FileOutputStream fos = new FileOutputStream(image);
                                     fos.write(bitmapdata);
                                     fos.flush();
                                     fos.close();
                                     }
                                     }
                                     }catch(IOException a){
                                     Log.e(TAG,"EXception occured 4 "+ a.getMessage());
                                         getImage( serial,  destinationPath,  destination);
                                     }catch (NullPointerException ex){
                                         Log.e(TAG,"EXception occured at image saving contact "+ ex.getMessage());
                                         getImage( serial,  destinationPath,  destination);
                                     }

                                }else if(serial.getPath().equals("regis")){
                                    String destination = destinationPath1+"/"
                                            +serial.getUsername()+"/".trim();

                                    new File(destination).mkdirs();
                                    destination = destination + serial.getUsername()+".ser";
                                    File userFile = new File(destination);
                                    if (!userFile.exists())
                                        userFile.createNewFile();
                                    FileOutputStream fileOutputStream1 = new FileOutputStream(userFile);
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream1);
                                    objectOutputStream.writeObject(serial);
                                    objectOutputStream.flush();
                                    objectOutputStream.close();
                                    fileOutputStream1.close();
                                }

                              /**  File image = null;
                                try{
                                    URL url =  new URL(BetaCaller.PHOTO_URL+user.getProfilePhoto());
                                    if(user.getProfilePhoto() != null){
                                        image = new File(destination+"/"+user.getProfilePhoto().trim());
                                        if (!image.exists()) {
                                            image.createNewFile();
                                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                            connection.setDoInput(true);
                                            connection.connect();
                                            InputStream input3 = connection.getInputStream();
                                            Bitmap bitmap = BitmapFactory.decodeStream(input3);

                                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                                            byte[] bitmapdata = bos.toByteArray();
                                            FileOutputStream fos = new FileOutputStream(image);
                                            fos.write(bitmapdata);
                                            fos.flush();
                                            fos.close();
                                        }
                                    }
                                }catch(IOException a){
                                    Log.e(TAG,"EXception occured 4 "+ a.getMessage());
                                }**/


                                }

                    }
                   // allHollaContacts.add(contacts);








                }
                if(input3 != null)
                    input3.close();
                if(sc != null)
                    sc.close();



                readHollaFromExternal(SDPath + "/HollaNow/"
                        + mSharedPref.getCurrentUser().getUsername() + "/zip/");

            }catch(IOException ex){
            Log.e(TAG,"EXception occured 43 "+ ex.getMessage());
        }

            return "finish";
        }

        @Override
        protected  void onPostExecute(String results){
            try {
                saveHollaContact(SDPath + "/HollaNow/" + mSharedPref.getCurrentUser().getUsername() + "/zip/".trim()
                        , mSharedPref.getCurrentUser().getUsername(), listContacts);
                Log.e(TAG, "see the objects here 25 " + results);

                readHollaFromExternal(SDPath + "/HollaNow/"
                        + mSharedPref.getCurrentUser().getUsername() + "/zip/");
            }catch (NullPointerException ex){Log.e(TAG, "NullPointer occurred at  " +ex.getMessage());}

        }


    }

    private void readHollaFromExternal(String path){
       // allHollaContacts.clear();
        Parse_Contact contact = new Parse_Contact();
        List<Parse_Contact> parseContact ;
        List<Parse_Contact> parse_contact = new ArrayList<Parse_Contact>();
        File files = new File(path);
        File mFile = null;
        SerializableUser user ;
        File[] fileList = files.listFiles();
        if (fileList != null)
            for (File file : fileList) {
                //Log.e(TAG, "List of files in readHollaFromExternal 4 " + file.getPath());
                //Log.e(TAG, "List of files in readHollaFromExternal 4 " + file.getPath());
             //   Log.e(TAG, "List of files in readHollaFromExternal 4 " + file.getPath());

                if (file.getPath().contains(".ser")) {
                   // allHollaContacts.clear();
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        user =  (SerializableUser) objectInputStream.readObject();
                        parseContact = user.getmUserArrayList();
                        allHollaContacts.addAll(parseContact);
                        if(allHollaContacts.contains(null))
                            allHollaContacts.remove(null);
                        Iterator<Parse_Contact> it = allHollaContacts.iterator();
                        while (it.hasNext()){
                            Parse_Contact cont = it.next();
                            if(cont != null)
                                if(cont.getSerialUser() != null)
                                    if(cont.getSerialUser().getPhone() != null)
                                         parse_contact.add(cont);
                        }
                        allHollaContacts = parse_contact;

                        getActivity().runOnUiThread(new Runnable() {
                                                                               @Override
                                                                               public void run() {
                                                                                   if (mAdapter != null)
                                                                                       mAdapter.notifyDataSetChanged();
                                                                               }
                                                                           });

                      //  Log.e(TAG, "HollaContacts  2 " + file.length());
                      //  Log.e(TAG, "HollaContacts  2 " + file.length()/1024);
                      /**  getActivity().runOnUiThread(new Runnable() {
                                @Override
                               public void run() {
                                    if(allHollaContacts.size() != 0 && mAdapter == null) {
                                        if (allHollaContacts.contains(null))
                                            allHollaContacts.remove(null);
                                        allHollaContacts.add(null);
                                        mAdapter = new HollaContactAdapter(mContext, allHollaContacts);
                                        mRecyclerView.setAdapter(mAdapter);
                                        mAdapter.notifyDataSetChanged();
                                    }else if(allHollaContacts.size() != 0 && mAdapter != null){
                                        allHollaContacts.remove(null);
                                        allHollaContacts.add(null);
                                        mAdapter.notifyDataSetChanged();
                                    }
                             }
                            });**/

                        //countData++;
                    } catch (IOException z) {
                        Log.e(TAG, "IOException FileInputStream 2 " + z.getMessage());
                  //      Log.e(TAG, "IOException FileInputStream 2 " + file.length()/1024);
                    } catch (ClassNotFoundException zx) {
                        Log.e(TAG, " ClassNotFoundException 2 " + zx.getMessage());
                    }catch (NullPointerException x) {
                        Log.e(TAG, " NullPointer Exception  2 " + x.getMessage());
                    }
                }
            }
    }

    public void getImage(SerializableUser serial, String destinationPath, String destination){
        File image = null;
        if(serial.getProfilePhoto().contains(".jpg")){
            String imageDisplay = serial.getProfilePhoto();
            imageDisplay = imageDisplay.substring(0, imageDisplay.length()-".jpg".length());
            serial.setProfilePhoto(imageDisplay);
        }
        try{

           // Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
            //Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
           // Log.e(TAG,"This is the path 2 "+ serial.getProfilePhoto());
          //  Log.e(TAG,"This is the path 2 "+ BetaCaller.PHOTO_URL+serial.getProfilePhoto());
           // Log.e(TAG,"This is the path 2 "+ BetaCaller.PHOTO_URL+serial.getProfilePhoto());
            if(serial.getProfilePhoto() != null){
                URL url1 =  new URL(BetaCaller.PHOTO_URL+serial.getProfilePhoto().trim());
                String destination1 = destinationPath+"/"
                        +serial.getUsername()+"/".trim();
                new File(destination).mkdirs();

                destination1 = destination1 +serial.getProfilePhoto()+".jpg".trim();
               // Log.e(TAG,"This is the path  "+ destination1);
               // Log.e(TAG,"This is the path  "+ destination1);
               // Log.e(TAG,"This is the path  "+ destination1);
               // Log.e(TAG,"This is the path  "+ destination1);
                Log.e(TAG,"This is the path  "+ destination1);
                image = new File(destination1);
                if(image.exists())
                    FileUtils.forceDelete(image);
                if (!image.exists()) {
                    image.createNewFile();
                    HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                    connection1.setDoInput(true);
                    connection1.connect();
                    InputStream input1 = connection1.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input1);
                    IOUtils.toByteArray(input1);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(image);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                }
            }
        }catch(IOException a){
            Log.e(TAG,"EXception occured 4 "+ a.getMessage());
        }catch (Exception ex){
            Log.e(TAG,"EXception occured at image saving contact "+ ex.getMessage());
        }
    }


    private void notifyUsers(String username, final String phone){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <List<NotificationModel>> call = hollaNowApiInterface.receiveNotification(username);

        call.enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.code() == 200) {
                  // bimps bips = response.body().get(0);

                   // mSharedPref.setNotificationUser(bips.toString());
                    //Type collectionType = new TypeToken<Notification_>(){}.getType();
                    //List<bimps> model = new GsonBuilder().create().fromJson(response.body().toString(), List.class);
                    //  JsonElement model1 = new GsonBuilder().create().fromJson(response.body().toString(), JsonElement.class);
                    //  Notification_ model2 = new Notification_(model);
                    Log.e(TAG, "Whats received on the notification "+response.code());
                   // Log.e(TAG, "Whats received on the notification "+response.body().size());
                    // bimps bip =   model.getNotification().get(0);
                    //Log.e(TAG, "Whats received on the notification "+response.body().get(0).getUsername());
                    //Log.e(TAG, "Whats received on the notification "+response.body().get(0).getUserNotification());
                   // sendNotifyUsers(bips.getUsername(),bips,phone);

                }else{
                    Log.e(TAG, "Whats received on the notification "+response.code());
                   // Log.e(TAG, "Whats received on the notification "+response.code());
                }


            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                Log.e(TAG, "Failure "+t);
                // bipList = helper.allNotification();

            }

        });
    }

    private void sendNotifyUsers(final String username,final Post_Model model){
       // Todo: Username and phone number of the 2nd party
        ////Todo: Here the new user checks for people on his phone list and send them notification


        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <JsonElement> call = hollaNowApiInterface.notifyUserContact(username, model.toString());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {

                    //Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                    // bimps bip =   model.getNotification().get(0);
                    Log.e(TAG, "Whats saved on the notification "+response.code());
                    //  Log.e(TAG, "Whats saved on the notification "+model.toString());
                }else{
                    Log.e(TAG, "Whats saved on the notification "+response.code());
                  //  Log.e(TAG, "Whats saved on the notification "+response.code());

                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure "+t);

                // bipList = helper.allNotification();

            }

        });
    }

    private class syncContactDb extends AsyncTask<String, String, Void> {

        Uri uri;

        public syncContactDb(String string) {
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

                // findNearbyUsersByContactList();
            }
        }

   // }



    private int fetchPhoneContacts() {
        Log.e("FPC", "in here...");
//        dbHelper.clearContacts();
       // if(!flag_contactList) {
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

            }
            return contactList.size();
        //}else {
         //   Log.e(TAG, "fetchPhoneContacts() when taking from database contacts" );
         //   contactList1 = dbHelper.allContacts();
         //   return contactList1.size();
        }
   // }






}
