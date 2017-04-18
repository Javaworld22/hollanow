package com.doxa360.android.betacaller;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Category;
import com.parse.Parse;
import com.parse.ParseObject;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Apple on 12/04/16.
 */
public class BetaCaller extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "qD7VMOEhMBMrUz7uT2wnMm78J";
    private static final String TWITTER_SECRET = "aCcLnHZwQqbBv5ljQZ7UB55IcBNmHteZAGDb3lJ4AHNTw6vIZN";

    public static final String SERVER_BASE_URL = "http://www.hollanow.com/";
//    http://www.hollanow.com/doxa360/api/v1/user/authenticate?
    public static final String SIGN_UP_API = "doxa360/api/v1/user/signup"; //method = post; params = email,username,name,password
    public static final String SIGN_IN_API = "doxa360/api/v1/user/authenticate"; //method = post; params = email,password
    public static final String SIGN_IN_USER_API = "doxa360/api/v1/user/authenticate_username"; //method = post; params = username,password
    public static final String GET_USER_DETAILS_API = "doxa360/api/v1/user/authenticate"; //method = get; params = token
    public static final String EDIT_PROFILE_API = "doxa360/api/v1/user/edit_profile";
    public static final String EDIT_PHONE_API = "doxa360/api/v1/user/edit_profile";

    public static final String SEARCH_PHONE_API = "doxa360/api/v1/user/search_phone";
    public static final String SEARCH_API = "doxa360/api/v1/user/search";

    public static final String GET_INDUSTRY_API = "doxa360/api/v1/industry"; //method = get; params = token

    public static final String UPLOAD_PHOTO_API = "doxa360/api/v1/user/upload_photo";

    public static final String TOKEN = "token";
    public static final String SEARCH = "search";

//    public static final String PARSE_APP_ID = "anSwU1zWd1pJvRnm7ajbkPr7L83ZBsZex04uPm5j";
//    public static final String PARSE_CLIENT_KEY = "SOWnAIYJWbN5lmbapcEvoaYFwd4d4t1dHhrIDHRq";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_PHONE = "CONTACT_PHONE";
    public static final String CONTACT_PHOTO = "CONTACT_PHOTO";
    public static final int LOCATION_DISTANCE_CLOSE = 1;
    public static final int LOCATION_DISTANCE_MEDIUM = 2;
    public static final int LOCATION_DISTANCE_FARTHER = 4;
    public static final String PHOTO_URL = "http://hollanow.com/assets/photos/users/";
    public static final String USER_PROFILE = "USER_PROFILE";
    public static final String GET_USER_BY_INDUSTRY_API = "doxa360/api/v1/user/industry";
    public static final String INDUSTRY = "industry";
    public static final String NEARBY_USERS_API = "doxa360/api/v1/user/nearby_users";
    public static final String LATTITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String DISTANCE_KM = "distKm";
    public static final String CALLNOTE_BY_PHONE = "doxa360/api/v1/user/callnote";
    public static final String USERS_BY_CONTACTS = "doxa360/api/v1/user/contacts";
    public static final String CONTACT_LIST = "contacts";
    public static final String PHONE = "phone";
    public static final String PASSWORD_RESET = "http://hollanow.com/password/reset";
    public static final String SAVE_CALLNOTE = "doxa360/api/v1/user/callnote/create";


    @Override
    public void onCreate() {
        super.onCreate();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(BetaCaller.this, new TwitterCore(authConfig), new Crashlytics(), new Digits.Builder().build());

            }
        };
        runnable.run();

    }
}
