package com.doxa360.android.betacaller.model;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apple on 28/07/16.
 */
public class UserSuggestionProvider extends ContentProvider {

    private static final String TAG = UserSuggestionProvider.class.getSimpleName();
    List<String> phoneNumbers;
    List<Parse_Contact> contacts;
    HollaNowDbHelper mDbHelper = new HollaNowDbHelper(getContext());

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (contacts == null || contacts.isEmpty()){
            //DO search
            contacts = new ArrayList<Parse_Contact>();
            String query = uri.getLastPathSegment().toLowerCase().trim();
            List<Parse_Contact> queriedContacts = mDbHelper.searchContacts(query);
            Log.e(TAG, queriedContacts.size()+" query results");
            for (int i=0;i<queriedContacts.size();i++) {
                contacts.add(queriedContacts.get(i));
            }
//            getSearch(query);
//            if (android.text.TextUtils.isDigitsOnly(query)) {
//                //search phone numbers
//                mDbHelper.getContactByPhone(query);
//                getSearchPhone(query);
//            } else {
//                //search names
//                mDbHelper.getContactByName(query);
//                getSearchName(query);
//            }
        }

        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );
        if (contacts != null) {
            String query = uri.getLastPathSegment().toLowerCase();
            int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

            int lenght = contacts.size();
            for (int i = 0; i < lenght && cursor.getCount() < limit; i++) {
                Parse_Contact contact = contacts.get(i);
                if (contact.getPhoneNumber().toLowerCase().contains(query)) { //TODO: check other params using ||
                    cursor.addRow(new Object[]{i, contact.getDisplayName(), contact.getId()});
                    Log.e(TAG, contact.getDisplayName()+"");
                }
            }
        }
        return cursor;
    }

    private void getSearch(CharSequence charSequence) {
        ParseQuery<ParseUser> queryName = ParseUser.getQuery();
        queryName.whereStartsWith("name", charSequence.toString());
        queryName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());


        ParseQuery<ParseUser> queryLowerCaseName = ParseUser.getQuery();
        queryLowerCaseName.whereStartsWith("name", charSequence.toString().toLowerCase());
        queryLowerCaseName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseUser> queryEmail = ParseUser.getQuery();
        queryEmail.whereContains("email", charSequence.toString().toLowerCase());
        queryName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseUser> queryPhone = ParseUser.getQuery();
        queryEmail.whereContains("phoneNumber", charSequence.toString().toLowerCase());
        queryName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        List<ParseQuery<ParseUser>> queryUser = new ArrayList<ParseQuery<ParseUser>>();
        queryUser.add(queryName);
        queryUser.add(queryEmail);
        queryUser.add(queryLowerCaseName);
        queryUser.add(queryPhone);


        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queryUser);
        mainQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    Log.e(TAG, objects.size() + " count");
                    if (objects.size() == 0) {

                    } else {
                        for (int i=0;i<objects.size();i++) {
                            phoneNumbers.add(objects.get(i).getString("phoneNumber"));
                        }
                    }
                }
            }
        });


    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

}
