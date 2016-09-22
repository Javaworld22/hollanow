package com.doxa360.android.betacaller;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.doxa360.android.betacaller.adapter.PhoneCallLogAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.PhoneCallLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallDiaryFragment extends Fragment {


    private static final String TAG = "CallDiaryFragment";
    private Context mContext;
    private Cursor callCursor;
    private List<PhoneCallLog> allCallLogs;
//    private String logNumber;
//    private String logName;
//    private String logDuration;
//    private int logType;
    private ProgressBar mProgressBar;
    private RecyclerView mCallLogRecyclerview;
    private PhoneCallLogAdapter mAdapter;

    private HollaNowDbHelper dbHelper;
    private HollaNowSharedPref mSharedPref;

    public CallDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_call_diary, container, false);

        mSharedPref = new HollaNowSharedPref(mContext);

        allCallLogs = new ArrayList<PhoneCallLog>();

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mCallLogRecyclerview = (RecyclerView) rootView.findViewById(R.id.callLog_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        if (mCallLogRecyclerview !=null) {
            mCallLogRecyclerview.setHasFixedSize(true);
            mCallLogRecyclerview.setLayoutManager(layoutManager);
        }

        dbHelper = new HollaNowDbHelper(mContext);
        allCallLogs = dbHelper.allPhoneLogs();
        mProgressBar.setVisibility(View.INVISIBLE);
        mAdapter = new PhoneCallLogAdapter(allCallLogs, mContext);
        mCallLogRecyclerview.setAdapter(mAdapter);

        new syncDb("sync call logs").execute("yes");

        return rootView;
    }

    private class syncDb extends AsyncTask<String, String, Void> {

        Uri uri;
        public syncDb(String string) {
            Log.e(TAG, string);
        }

        @Override
        protected Void doInBackground(String... string) {
            fetchCallLog();
            return null;
        }

        @Override
        protected void onPostExecute(Void bytes) {
            super.onPostExecute(bytes);
            allCallLogs.clear();
            allCallLogs.addAll(dbHelper.allPhoneLogs());
            mSharedPref.setContactCount(allCallLogs.size());
            mAdapter.notifyDataSetChanged();
            Log.e(TAG, "Adapter call log updated");
        }
    }

    private List<PhoneCallLog> fetchCallLogDb() {
        dbHelper = new HollaNowDbHelper(mContext);
//        dbHelper.clearAndRecreateDb();
        allCallLogs = dbHelper.allPhoneLogs();
        if (!allCallLogs.isEmpty() && allCallLogs.size() == mSharedPref.getCallLogCount()) {
//            fetchCallLogDb();
            Log.e(TAG, "fetchCallLogsDB - true " + allCallLogs.size());
        } else {
//            dbHelper.clearAndRecreateDb();
//            allCallLogs = fetchCallLog();
            Log.e(TAG, "fetchCallLogsDB - false " + allCallLogs.size());
        }
        return allCallLogs;
    }


    private void fetchCallLog() {
        Uri allCallsUri = Uri.parse("content://call_log/calls");
        Uri limitedCallLogUri = CallLog.Calls.CONTENT_URI.buildUpon()
                .appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY, "50").build();
        ContentResolver resolver = mContext.getContentResolver();
        Cursor c  = resolver.query(allCallsUri, null, null, null, null);

//        dbHelper.clearCallLogs();

        if (c != null) {
//            if (c.isClosed()) {
//
//            }
            final int logNumberIndex = c.getColumnIndex(CallLog.Calls.NUMBER);
            final int logNameIndex = c.getColumnIndex(CallLog.Calls.CACHED_NAME);
            final int logDurationIndex = c.getColumnIndex(CallLog.Calls.DURATION);
            final int logDateIndex = c.getColumnIndex(CallLog.Calls.DATE);
            final int logTypeIndex = c.getColumnIndex(CallLog.Calls.TYPE);

            dbHelper.clearCallLogs();
            while(c.moveToNext()) {
                if (c.getPosition() < 200) {
//                String logId = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID));// for  id
                    String logNumber = c.getString(logNumberIndex).replace(" ","");// for  number
                    if (logNumber.startsWith("0")) {
                        logNumber = logNumber.replaceFirst("[0]", "+234");
                    }
                    String logName = c.getString(logNameIndex);// for name
//                String logThumb = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI));// for  photo
                    String logDuration = c.getString(logDurationIndex);// for duration
                    long logDate = c.getLong(logDateIndex);
                    int logType = Integer.parseInt(c.getString(logTypeIndex));// for call type, Incoming or out going.
                    long logId = getContactIDFromNumber(logNumber, mContext);

//                Log.e(TAG, null + " - " + logNumber + logName + logDuration + logType);
//                Toast.makeText(mContext, logNumber+logName+logDuration+logType, Toast.LENGTH_SHORT).show();
                    PhoneCallLog phoneCallLog = new PhoneCallLog(null, logName, openPhoto(logId), logNumber, logDuration, logDate, logType);
                    dbHelper.cachePhoneLog(phoneCallLog);
                }

            }
            c.close();

        }
    }

    public static long getContactIDFromNumber(String contactNumber, Context context) {
        if (contactNumber.startsWith("+234")) {
            contactNumber = contactNumber.replaceFirst("[+234]", "0");
        }
        String UriContactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, UriContactNumber),
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (contactLookupCursor != null) {
                while (contactLookupCursor.moveToNext() && contactLookupCursor.getCount()<10) {
//                    for (int i=0;i<10;i++) {
//                    Log.e(TAG, contactLookupCursor.getCount()+ " - count");
                    phoneContactID = contactLookupCursor.getLong(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
//                    }
                }
        }
        if (contactLookupCursor != null) {
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    public Uri getPhotoUri(String contactId) {
//        try {
//            Cursor cur = mContext.getContentResolver().query(
//                    ContactsContract.Data.CONTENT_URI,
//                    null,
//                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
//                            + ContactsContract.Data.MIMETYPE + "='"
//                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
//                    null);
//            if (cur != null) {
//                if (!cur.moveToFirst()) {
//                    return null; // no photo
//                }
//            } else {
//                return null; // error in cursor process
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
//                .parseLong(contactId));
//        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        return null; //ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

    }

    public Uri openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(photoUri,
                    new String[]{ContactsContract.Contacts.Photo.PHOTO_THUMBNAIL_URI}, null, null, null);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        }

        if (cursor == null) {
            return null;
        }
        try {
            for (int i=0;i<100;i++) {
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
}
