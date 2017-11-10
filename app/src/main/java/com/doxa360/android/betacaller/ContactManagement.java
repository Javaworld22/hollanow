package com.doxa360.android.betacaller;

import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;

/**
 * Created by user on 8/7/2017.
 */

public class ContactManagement {

    private static final String TAG = ContactManagement.class.getSimpleName();
    private String mToken;
    private String mNumber;
    private HollaNowSharedPref mSharedPref;
    private HollaNowDbHelper helper;

    public ContactManagement(String token, String number){
        mToken = token;
        mNumber = number;
    }
}
