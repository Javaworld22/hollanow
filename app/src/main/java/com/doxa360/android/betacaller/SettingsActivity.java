package com.doxa360.android.betacaller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private HollaNowSharedPref mSharedPref;
    private HollaNowApiInterface hollaNowApiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mSharedPref = new HollaNowSharedPref(this);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, new GeneralPreferenceFragment())
                .commit();
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String TAG = GeneralPreferenceFragment.class.getSimpleName();
        ProgressDialog mProgressDialog;
        ProgressDialog progressDialog;
        Context mContext;

        private String android_id;

        private File file;

        private HollaNowApiInterface hollaNowApiInterface;
        private HollaNowSharedPref mSharedPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            mProgressDialog = new ProgressDialog(mContext,R.style.ThemeOverlay_AppCompat_Dialog_Alert);
            hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            mSharedPref = new HollaNowSharedPref(mContext);

            progressDialog = new ProgressDialog(mContext);
           //progressDialog.setCancelable(false);

            findPreference(mContext.getString(R.string.pref_backup_contacts_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.e(TAG, "Backup contacts 4");
                    if (MyToolBox.isNetworkAvailable(mContext)) {
                        Log.e(TAG, "Backup contacts 5");
                       /** android.support.v7.app.**/AlertDialog.Builder builder = new /**android.support.v7.app.*/AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                        builder.setTitle("HollaNow BackUp Contacts");
                        builder.setMessage("Do you want to backup your Contacts?");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException a){
                                    Log.e(TAG, "InterruptException here by file");
                                }
                                mProgressDialog.dismiss();
                                mProgressDialog = new ProgressDialog(mContext,R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                                mProgressDialog.setMessage("Progress... ");
                                mProgressDialog.setIndeterminate(false);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setMax(100);
                                mProgressDialog.show();
                                new  ReadContacts().execute();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                      //  backupContacts(); /////////////////////////////////////////////////////////
                    } else {
                        Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });
            findPreference(mContext.getString(R.string.pref_sync_contacts_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (MyToolBox.isNetworkAvailable(mContext)) {
                        syncContacts();
                    } else {
                        Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });
//            findPreference(mContext.getString(R.string.pref_delete_contacts_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    if (MyToolBox.isNetworkAvailable(mContext)) {
//                        deleteAccountDialog();
//                    } else {
//                        Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
//                    }
//                    return false;
//                }
//            });

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
////                startActivity(new Intent(getActivity(), HomeActivity.class));
////                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }


        @Override
        public void onAttach(Activity activity) {
            mContext = activity;
            super.onAttach(activity);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            String message = "oldie";
            SharedPreferences prefs = sharedPreferences;

            if (s.equals(mContext.getString(R.string.pref_location_key))) {
                if (prefs.getBoolean(mContext.getString(R.string.pref_location_key), false)) {
                    message = "Location services will be requested by default";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

//                    installation.put(WhatILyke.COMMENT_NOTIFICATION, true);
//                    installation.saveEventually();
                } else {
                    message = "You have disabled location services";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            } else if (s.equals(mContext.getString(R.string.pref_privacy_key))) {
                if (prefs.getBoolean(mContext.getString(R.string.pref_privacy_key), false)) {
                    message = "Your contact will be visible in public searches";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    User user = mSharedPref.getCurrentUser();
                    user.setSearchVisible(true);
                    mSharedPref.setCurrentUser(user.toString());
                    updateUserInfo(mSharedPref.getCurrentUser());
                } else {
                    message = "Your contact will be hidden from public searches";
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    User user = mSharedPref.getCurrentUser();
                    user.setSearchVisible(false);
                    mSharedPref.setCurrentUser(user.toString());
                    updateUserInfo(mSharedPref.getCurrentUser());
                }
            } else if (s.equals(mContext.getString(R.string.pref_backup_contacts_key))) {
                Log.e(TAG, "Backup contacts 2");
                if (MyToolBox.isNetworkAvailable(mContext)) {
                    Log.e(TAG, "Backup contacts 3");
                  /**  android.support.v7.app.**/AlertDialog.Builder builder = new /**android.support.v7.app.**/AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    builder.setTitle("HollaNow BackUp Contacts");
                    builder.setMessage("Do you want to backup your Contacts?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException a){
                                        Log.e(TAG, "InterruptException here by file");
                                    }
                                    mProgressDialog.dismiss();
                                    mProgressDialog = new ProgressDialog(mContext,R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                                    mProgressDialog.setMessage("Progress... ");
                                    mProgressDialog.setIndeterminate(false);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setMax(100);
                                    mProgressDialog.show();
                                    new  ReadContacts().execute();
                                }
                            });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                  //  backupContacts(); /////////////////////////////////////////////////
                } else {
                    Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
                }
            } else if (s.equals(mContext.getString(R.string.pref_sync_contacts_key))) {
                if (MyToolBox.isNetworkAvailable(mContext)) {
                    syncContacts();
                } else {
                  //  Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
                }
            }
//            else if (s.equals(mContext.getString(R.string.pref_delete_contacts_key))) {
//                if (MyToolBox.isNetworkAvailable(mContext)) {
//                    deleteAccountDialog();
//                } else {
//                    Toast.makeText(mContext, "Connection error. Check your connection and try again", Toast.LENGTH_LONG).show();
//                }
//            }

            else {
                message = "nothing here...";
//                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }

        }

        private void deleteAccountDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Delete Account")
                    .setMessage("Deleting your account will remove all information previously stored on HollaNow.\nAre you sure you want to continue?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog.setMessage("Deleting...");
                            deleteAccount();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteAccount() {

        }

        private void updateUserInfo(User user) {
            Call<User> call = hollaNowApiInterface.editUserProfile(user, mSharedPref.getToken());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        Log.e(TAG, "done");
                        Log.e(TAG, "Response: "+response.code());
                        Log.e(TAG, "Response: "+response.code());
                        Log.e(TAG, "Response: "+response.code());
//                        Toast.makeText(mContext, "A backup of your contacts has been saved remotely", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Response: "+response.code());
                        Log.e(TAG, "Response: "+response.code());
                        Log.e(TAG, "Response: "+response.code());
//                        Toast.makeText(mContext, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
//                    Toast.makeText(mContext, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, t.getMessage());
                    Log.e(TAG, "Backup contacts 2 "+t.getMessage());
                    Log.e(TAG, "Backup contacts 2 "+t.getMessage() );
                    Log.e(TAG, "Backup contacts 2 "+t.getMessage());
                    Log.e(TAG, "Backup contacts 2 "+t.getMessage());
                }
            });

        }

    /**    private void backupContacts() {
            Log.e(TAG, "Contact initialization 1");
            progressDialog.setTitle("Backup");
            progressDialog.setMessage("Backing up contacts...");
            progressDialog.setIndeterminate(true);
            Log.e(TAG, "Contact initialization 2");
            progressDialog.show();
            HollaNowDbHelper dbHelper = new HollaNowDbHelper(mContext);
            Log.e(TAG, "Contact initialization 3");
            String contacts = new GsonBuilder().create().toJson(dbHelper.allContacts(), new TypeToken<List<Parse_Contact>>(){}.getType());

            User user = mSharedPref.getCurrentUser();
            user.setContactsBackup(contacts);
            mSharedPref.setCurrentUser(user.toString());
            String tokenss = mSharedPref.getToken();
           // Call<User> call = hollaNowApiInterface.editUserProfile(user, mSharedPref.getToken());
            Call<List<User>> call = hollaNowApiInterface.getUsersByContactList("07087870288", tokenss);
            Log.e(TAG, "HolllaNowApiInterface Response "+tokenss);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "A backup of your contacts has been saved remotely", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Contact initialization 4 "+response.code());
                    } else {
                        Toast.makeText(mContext, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Contact initialization 5 "+response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(mContext, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, t.getMessage());
                    Log.e(TAG, "Contact initialization 6 "+t.getMessage());
                }
            });
            progressDialog.dismiss();


        } **/

        private void syncContacts() {
            progressDialog.setTitle("Sync");
            progressDialog.setMessage("Downloading contacts from Hollanow servers to your device...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            if (mSharedPref.getCurrentUser().getContactsBackup() == null || mSharedPref.getCurrentUser().getContactsBackup().isEmpty()) {
                Toast.makeText(mContext, "You do not have a previously saved backup of your contacts", Toast.LENGTH_LONG).show();
            } else {
                saveContacts(mSharedPref.getCurrentUser().getContactsBackup());
            }

            progressDialog.dismiss();
        }

        private void saveContacts(String backup) {
            ArrayList<Parse_Contact> backupList = null;
            try {
                 backupList =
                        new Gson().fromJson(backup, new TypeToken<List<Parse_Contact>>() {
                        }.getType());
            }catch (JsonSyntaxException e){
                Log.e(TAG, "Error at Gson, JsonSyntaxException: "+e.getMessage() );
            }

            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<ContentProviderOperation>();

            for (int i=0;i<backupList.size();i++) {
                Parse_Contact contact = backupList.get(i);
                String contactId = contact.getId();
                String contactName = contact.getDisplayName();
                String contactPhone = contact.getPhoneNumber();
                String contactEmail;
                if (contact.getEmailAddress()!=null) {
                    contactEmail = contact.getEmailAddress();
                } else {
                    contactEmail = "";
                }

                Log.e(TAG, "sync attempt: " + contactId + " - " + contactName );

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, contactId)
                        .withValue(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, contactName)
//                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contactEmail)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone)
                        .build());
            }
            try {
                Log.e(TAG, "ops size:" + ops.size());
                mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                Log.e(TAG, "sync attempt: fiinished" );
                Toast.makeText(mContext, "Your contacts has been synced successfully with your device ", Toast.LENGTH_LONG).show();
            } catch (RemoteException | OperationApplicationException e ) {
                e.printStackTrace();
            }

        }


        private class ReadContacts extends AsyncTask<Void, Integer,String> {

            // Uri uri;
            HollaNowDbHelper dbHelper = new HollaNowDbHelper(mContext);
            String phone = null;
            List<Parse_Contact> contactList = new ArrayList<Parse_Contact>();
            String phoneNumber = null;
            String name = null;
            final String start = "START ";
            final String end = "END ";
            Integer j;


            @Override
            protected  String doInBackground(Void... argo) {

                if (!contactList.isEmpty())
                    contactList.clear();
                if (dbHelper != null)
                    contactList = dbHelper.allContacts();
                else {
                    dbHelper = new HollaNowDbHelper(mContext);
                    contactList = dbHelper.allContacts();
                }
             //   if (contactList.isEmpty()) {
             //       if (!allContacts.isEmpty())
              //          contactList = allContacts;
             /**   }**/ // if(contactList.size() < allContacts.size())
                  //  contactList = allContacts;
                if (mSharedPref.getCurrentUser() != null)
                    name = mSharedPref.getCurrentUser().getUsername();
                Log.e(TAG, "File copy: " + phone);
                Log.e(TAG, "File copy: " + contactList.size());
                if (contactList.size() > 0) {
                    try {
                        //Thread.sleep(1000);
                        if (name != null) {
                            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + "Holla" + name + ".txt");
                          //  name = mSharedPref.getUsername();
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
                    } catch (IOException e) {
                        Log.e(TAG, "IOException here by file");
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

                if (file != null) {
                    mProgressDialog = new ProgressDialog(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    mProgressDialog.setMessage("saving contact... ");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();
                    backUpContacts(file, android_id);
                }
                   // Toast.makeText(mContext, "Contact Saved ", Toast.LENGTH_LONG).show();

            }

        }

        private void backUpContacts(File contacts, String id){
            String username = null;
            if(mSharedPref.getCurrentUser() != null) {
                username = mSharedPref.getCurrentUser().getUsername();
            }


            Log.e(TAG, "Intro to contact management " );
            Log.e(TAG, "Intro to contact management " );
            Log.e(TAG, "Intro to contact management" );
            Log.e(TAG, "Intro to contact management " );
            Log.e(TAG, "Intro to contact management " );
            Log.e(TAG, "Check some parameters "+username+"  "+id+" " );
            //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //     + File.separator + "mikoko3.pdf");


            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), contacts);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("zip", contacts.getName(), requestFile);
            if (MyToolBox.isNetworkAvailable(mContext)) {
                Log.e(TAG, "Intro to contact management "+contacts.getName()+" "+contacts.length() );
                HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
              //  if (token != null && id != null){
                    Call<ResponseBody> call = hollaNowApiInterface.contactManagement(body,id,username);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                mProgressDialog.dismiss();
                                Log.e(TAG, "success of contacts " + response.code() + "");
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
                                mProgressDialog.dismiss();

                                Toast.makeText(mContext, "Contact Not Saved", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Device id update failed: " + t.getMessage());
                            Log.e(TAG, "Device id update failed: " + t.getMessage());
                            Log.e(TAG, "Device id update failed: " + t);
                            Log.e(TAG, "Device id update failed: " + t.getMessage());
                            mProgressDialog.dismiss();

                    Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

                        }
                    });
             //   }
            }

        }



    }   ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), OldSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), OldSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
