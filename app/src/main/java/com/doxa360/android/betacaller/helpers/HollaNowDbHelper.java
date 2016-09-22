package com.doxa360.android.betacaller.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.doxa360.android.betacaller.model.Contact;
import com.doxa360.android.betacaller.model.PhoneCallLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apple on 04/07/16.
 */
public class HollaNowDbHelper extends SQLiteOpenHelper {

    private static final String TAG = HollaNowDbHelper.class.getSimpleName();
    Context mContext;
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "hollanow.db";

    public static final String COLUMN_NULLABLE  = "nullable";

    public static final String TABLE_CONTACTS = "Contacts";
    public static final String COLUMN_ID = "autoId";
    public static final String COLUMN_CONTACT_ID = "contactId";
    public static final String COLUMN_CONTACT_NAME = "name";
    public static final String COLUMN_CONTACT_PHONE = "phone";
    public static final String COLUMN_CONTACT_EMAIL = "email";
    public static final String COLUMN_CONTACT_THUMBNAIL = "thumbnail";
    public static final String COLUMN_CONTACT_TYPE = "accountType";
    private static final String COLUMN_CONTACT_LAT = "COLUMN_CONTACT_LAT";
    private static final String COLUMN_CONTACT_LONG = "COLUMN_CONTACT_LONG";
    public static final String COLUMN_CONTACT_VERSION = "version";

    private static final String TABLE_CALLDIARY = "Calldiary";
    private static final String COLUMN_CALLDIARY_ID = "calldiaryId";
    private static final String COLUMN_CALL_DURATION = "duration";
    private static final String COLUMN_CALL_DATE = "date";
    private static final String COLUMN_CALL_TYPE = "type";

    private static final String SQL_CACHE_CONTACTS=
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " (" +

                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CONTACT_ID + " TEXT," +
                    COLUMN_CONTACT_NAME + " TEXT," +
                    COLUMN_CONTACT_PHONE + " TEXT," +
                    COLUMN_CONTACT_EMAIL + " TEXT," +
                    COLUMN_CONTACT_THUMBNAIL + " TEXT," +
                    COLUMN_CONTACT_TYPE + " TEXT," +
                    COLUMN_CONTACT_LAT + " REAL," +
                    COLUMN_CONTACT_LONG + " REAL," +
                    COLUMN_CONTACT_VERSION + " TEXT," +
                    COLUMN_NULLABLE + " TEXT," +
                    " UNIQUE (" + COLUMN_CONTACT_ID + ") ON CONFLICT REPLACE" +
                    " )";

    private static final String SQL_CACHE_CALLDIARY =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CALLDIARY + " (" +

                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CALLDIARY_ID + " TEXT," +
                    COLUMN_CONTACT_NAME + " TEXT," +
                    COLUMN_CONTACT_PHONE + " TEXT," +
                    COLUMN_CONTACT_THUMBNAIL + " TEXT," +
                    COLUMN_CALL_DURATION + " TEXT," +
                    COLUMN_CALL_DATE + " INTEGER," +
                    COLUMN_CALL_TYPE + " INTEGER," +
                    COLUMN_NULLABLE + " TEXT," +
                    " UNIQUE (" + COLUMN_CALLDIARY_ID + ") ON CONFLICT REPLACE" +
                    " )";

//    private static final String SQL_CACHE_COUNT_ =
//            "CREATE TABLE IF NOT EXISTS " + TABLE_CALLDIARY + " (" +
//
//                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    COLUMN_CALLDIARY_ID + " TEXT," +
//                    COLUMN_CONTACT_NAME + " TEXT," +
//                    COLUMN_CONTACT_PHONE + " TEXT," +
//                    COLUMN_CONTACT_THUMBNAIL + " TEXT," +
//                    COLUMN_CALL_DURATION + " TEXT," +
//                    COLUMN_CALL_DATE + " INTEGER," +
//                    COLUMN_CALL_TYPE + " INTEGER," +
//                    COLUMN_NULLABLE + " TEXT" +
//                    " )";

    private static final String SQL_DELETE_TABLE_CONTACTS =
            "DROP TABLE IF EXISTS " + TABLE_CONTACTS;

    private static final String SQL_DELETE_TABLE_CALLDIARY =
            "DROP TABLE IF EXISTS " + TABLE_CALLDIARY;

    public HollaNowDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public HollaNowDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public HollaNowDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
//        super(context, name, factory, version, errorHandler);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CACHE_CONTACTS);
        sqLiteDatabase.execSQL(SQL_CACHE_CALLDIARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CONTACTS);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CALLDIARY);

        onCreate(sqLiteDatabase);
    }

    public void clearAndRecreateDb(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CONTACTS);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_CALLDIARY);

        onCreate(sqLiteDatabase);
        Log.e("CLEAR AND RECREATE DBs", " - DONE");
    }

    public void clearContacts() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CONTACTS, null, null);
        sqLiteDatabase.close();
        Log.e(TAG, "deleted contacts");
    }

    public void clearCallLogs() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CALLDIARY, null, null);
        sqLiteDatabase.close();
        Log.e(TAG, "deleted call diary");
    }

    public long cacheContacts(Contact contact){

//        Log.e("DB", "im here o");
        SQLiteDatabase db = this.getWritableDatabase();

        String id = contact.getId();
        String name = contact.getDisplayName();
        String phone = contact.getPhoneNumber();
        String email = contact.getEmailAddress();
        String thumbnail = contact.getThumbnailUrl();
        String version = contact.getVersion();
        String accountType = contact.getAccountType()+"";

//        Log.e("DB", id+name+phone+email+thumbnail+accountType);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HollaNowDbHelper.COLUMN_CONTACT_ID, id);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_NAME, name);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_PHONE, phone);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_EMAIL, email);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_THUMBNAIL, thumbnail);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_VERSION, version);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_LAT, 0);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_LONG, 0);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_TYPE, accountType);
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                HollaNowDbHelper.TABLE_CONTACTS,
                HollaNowDbHelper.COLUMN_NULLABLE,
                values);

//        db.close();//TODO Check all db.closes commented later
//        Log.e("CACHE CONTACT NEW:", newRowId+"");
        return newRowId;
    }

    public List<Contact> allContacts(){

        List<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CONTACT_ID,
                COLUMN_CONTACT_NAME,
                COLUMN_CONTACT_PHONE,
                COLUMN_CONTACT_EMAIL,
                COLUMN_CONTACT_THUMBNAIL,
                COLUMN_CONTACT_LAT,
                COLUMN_CONTACT_LONG,
                COLUMN_CONTACT_VERSION,
                COLUMN_CONTACT_TYPE
        };

        String sortOrder = HollaNowDbHelper.COLUMN_CONTACT_LAT + " DESC, " + HollaNowDbHelper.COLUMN_CONTACT_NAME + " ASC";

        Cursor mCursor = db.query(
                HollaNowDbHelper.TABLE_CONTACTS,
                projection,
                null,//COLUMN_CHANNEL_TYPE + "=?",//selection,
                null,//new String[] {channelType},//selectionArgs,
                null,
                null,
                sortOrder
        );

        if (mCursor.moveToFirst()){
            do {
                Contact contact = new Contact();
                String version = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_VERSION));
                String phone = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE));
                String id = mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID));

//                if (getContactByPhone(phone) != null) {
//                    if (!version.equalsIgnoreCase(getContactByPhone(phone).getVersion())) {
//                        updateContact(id);
//                    }
//                }

                contact.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
                contact.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                contact.setPhoneNumber(phone);
                contact.setEmailAddress(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_EMAIL)));
                contact.setThumbnailUrl(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL)));
                contact.setLatitude(mCursor.getFloat(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_LAT)));
                contact.setLongitude(mCursor.getFloat(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_LONG)));
                contact.setAccountType(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_TYPE)));


                contactList.add(contact);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return contactList;
    }

    private void updateContact(String id) {
//        Log.e(TAG, id + " updated");
    }

    public long cachePhoneLog(PhoneCallLog callLog){

        SQLiteDatabase db = this.getWritableDatabase();

        String id = callLog.getId();
        String name = callLog.getDisplayName();
        String phone = callLog.getPhoneNumber();
        String duration = callLog.getDuration();
        String thumbnail = callLog.getThumbnailUrl() != null ? callLog.getThumbnailUrl().toString():null;
        int unixTime = (int) (callLog.getDate()/1000);
        int callType = callLog.getType();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HollaNowDbHelper.COLUMN_CALLDIARY_ID, id);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_NAME, name);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_PHONE, phone);
        values.put(HollaNowDbHelper.COLUMN_CALL_DURATION, duration);
        values.put(HollaNowDbHelper.COLUMN_CONTACT_THUMBNAIL, thumbnail);
        values.put(HollaNowDbHelper.COLUMN_CALL_DATE, unixTime);
        values.put(HollaNowDbHelper.COLUMN_CALL_TYPE, callType);

        long newRowId;
        newRowId = db.insert(
                HollaNowDbHelper.TABLE_CALLDIARY,
                HollaNowDbHelper.COLUMN_NULLABLE,
                values);

//        db.close();
//        Log.e("CACHE CALLDIARY NEW:", newRowId+"");
        return newRowId;
    }

    public List<PhoneCallLog> allPhoneLogs(){

        List<PhoneCallLog> phoneLogsList = new ArrayList<PhoneCallLog>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CALLDIARY_ID,
                COLUMN_CONTACT_NAME,
                COLUMN_CONTACT_PHONE,
                COLUMN_CALL_DURATION,
                COLUMN_CONTACT_THUMBNAIL,
                COLUMN_CALL_DATE+"",
                COLUMN_CALL_TYPE+""
        };

        String sortOrder = HollaNowDbHelper.COLUMN_CALL_DATE + " DESC";

        Cursor mCursor = db.query(
                HollaNowDbHelper.TABLE_CALLDIARY,
                projection,
                null,//COLUMN_CHANNEL_TYPE + "=?",//selection,
                null,//new String[] {channelType},//selectionArgs,
                null,
                null,
                sortOrder
        );
        //TODO: LIMIT pagination

        if (mCursor.moveToFirst()){
            do {
                PhoneCallLog callLog = new PhoneCallLog();
                callLog.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CALLDIARY_ID)));
                callLog.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                callLog.setPhoneNumber(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                callLog.setDuration(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CALL_DURATION)));
                if (mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL))!=null)
                {
                    callLog.setThumbnailUrl(Uri.parse(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL))));
                }
                callLog.setDate((long) mCursor.getInt((mCursor.getColumnIndexOrThrow(COLUMN_CALL_DATE))));
                callLog.setType(mCursor.getInt((mCursor.getColumnIndexOrThrow(COLUMN_CALL_TYPE))));

                phoneLogsList.add(callLog);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return phoneLogsList;
    }

    public Contact getContactByPhone(String phone) {
//        String phoneNumber = phone.replace("*","");
//        phoneNumber = phoneNumber.replace("+243","0");
        String phoneNumber = phone.replace(" ","");
        if (phoneNumber.contains("+")) {
//            phoneNumber = phoneNumber.substring(1);
            Log.e(TAG, "plus here "+ phoneNumber);
        }

        Contact contact = null;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CONTACT_ID,
                COLUMN_CONTACT_NAME,
                COLUMN_CONTACT_PHONE,
                COLUMN_CONTACT_EMAIL,
                COLUMN_CONTACT_THUMBNAIL,
                COLUMN_CONTACT_TYPE
        };

        String sortOrder = HollaNowDbHelper.COLUMN_CONTACT_NAME + " ASC";

        Cursor mCursor = db.query(
                HollaNowDbHelper.TABLE_CONTACTS,
                projection,
                COLUMN_CONTACT_PHONE + " = \'" + phoneNumber + "\'", //COLUMN_CONTACT_PHONE + " = \'" + phoneNumber + "\'"
                null,//new String[] {channelType},//selectionArgs,
                null,
                null,
                sortOrder
        );

        if (mCursor.moveToFirst()){
            do {
                contact = new Contact();
                contact.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
                contact.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                contact.setPhoneNumber(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                contact.setEmailAddress(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_EMAIL)));
                contact.setThumbnailUrl(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL)));
                contact.setAccountType(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_TYPE)));

            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return contact;
    }

    public List<Contact> searchContacts(String search) {
        String query = search.replace("*","");

        List<Contact> contacts = null;
        Contact contact;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mCursor =  db.query(TABLE_CONTACTS,
                new String[] { COLUMN_CONTACT_ID, COLUMN_CONTACT_NAME, COLUMN_CONTACT_PHONE,
                        COLUMN_CONTACT_EMAIL, COLUMN_CONTACT_THUMBNAIL, COLUMN_CONTACT_TYPE },
                COLUMN_CONTACT_PHONE+" like"+"'%"+query+"%' OR "+COLUMN_CONTACT_NAME+" like"+"'%"+query+"%' OR "+COLUMN_CONTACT_EMAIL+" like"+"'%"+query+"%'",
                null, null, null, null);

        if (mCursor.moveToFirst()){
            contacts = new ArrayList<Contact>();
            do {
                contact = new Contact();
                contact.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
                contact.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                contact.setPhoneNumber(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                contact.setEmailAddress(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_EMAIL)));
                contact.setThumbnailUrl(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL)));
                contact.setAccountType(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_TYPE)));

                contacts.add(contact);
//                Log.e(TAG, "DB: searching");
            } while (mCursor.moveToNext());

        }
        mCursor.close();
        return contacts;
    }

    public Contact searchContactByPhoneNumber(String search) {
        String searchQuery = search.replace("*","");
        String query = searchQuery.replace("+","");

        Contact contact = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mCursor =  db.query(TABLE_CONTACTS,
                new String[] { COLUMN_CONTACT_ID, COLUMN_CONTACT_NAME, COLUMN_CONTACT_PHONE,
                        COLUMN_CONTACT_EMAIL, COLUMN_CONTACT_THUMBNAIL, COLUMN_CONTACT_TYPE },
                COLUMN_CONTACT_PHONE+" like"+"'%"+query+"%' ",
                null, null, null, null);

        if (mCursor.moveToFirst()){
            do {
                contact = new Contact();
                contact.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)));
                contact.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                contact.setPhoneNumber(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                contact.setEmailAddress(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_EMAIL)));
                contact.setThumbnailUrl(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL)));
                contact.setAccountType(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_TYPE)));

//                Log.e(TAG, "DB: searching");
            } while (mCursor.moveToNext());

        }
        mCursor.close();
        return contact;
    }

    public void updateLocation(String id, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE "+ TABLE_CONTACTS +" SET "+ COLUMN_CONTACT_LAT + " ='"+latitude+"', " + COLUMN_CONTACT_LONG + " ='"+longitude+"' WHERE "+COLUMN_CONTACT_ID+" ='"+id+"'");

//        db.close();
//        Log.e("Update location:", "done");

    }

    public List<PhoneCallLog> allLogsByPhoneNumber(String phoneNumber) {
        String searchQuery = phoneNumber.replace("*","");
        searchQuery = phoneNumber.replace(" ","");
        String query = searchQuery.replace("+234", "");
        query = query.replace("+", "");
        if (query.startsWith("0")) {
            query = query.replaceFirst("[0]","");
        }

        ArrayList<PhoneCallLog> callLogs = new ArrayList<PhoneCallLog>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor mCursor =  db.query(TABLE_CALLDIARY,
                new String[] { COLUMN_CALLDIARY_ID, COLUMN_CONTACT_NAME, COLUMN_CONTACT_PHONE,
                        COLUMN_CALL_DURATION, COLUMN_CONTACT_THUMBNAIL, COLUMN_CALL_DATE, COLUMN_CALL_TYPE },
                COLUMN_CONTACT_PHONE+" like"+"'%"+query+"%' ",
                null, null, null, null);

        if (mCursor.moveToFirst()){
            do {
                PhoneCallLog callLog = new PhoneCallLog();
                callLog.setId(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CALLDIARY_ID)));
                callLog.setDisplayName(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                callLog.setPhoneNumber(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                callLog.setDuration(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CALL_DURATION)));
                if (mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL))!=null)
                {
                    callLog.setThumbnailUrl(Uri.parse(mCursor.getString(mCursor.getColumnIndexOrThrow(COLUMN_CONTACT_THUMBNAIL))));
                }
                callLog.setDate((long) mCursor.getInt((mCursor.getColumnIndexOrThrow(COLUMN_CALL_DATE))));
                callLog.setType(mCursor.getInt((mCursor.getColumnIndexOrThrow(COLUMN_CALL_TYPE))));

                callLogs.add(callLog);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return callLogs;
    }

//    db.execSQL("UPDATE DB_TABLE SET YOUR_COLUMN='newValue' WHERE id=6 ");
//    Or
//
//    ContentValues newValues = new ContentValues();
//    newValues.put("YOUR_COLUMN", "newValue");
//
//    db.update("YOUR_TABLE", newValues, "id=6", null);
//    Or
//
//    ContentValues newValues = new ContentValues();
//    newValues.put("YOUR_COLUMN", "newValue");
//
//    String[] args = new String[]{"user1", "user2"};
//    db.update("YOUR_TABLE", newValues, "name=? OR name=?", args);
}

