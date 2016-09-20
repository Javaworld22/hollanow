package com.doxa360.android.betacaller;

import android.app.Application;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.doxa360.android.betacaller.model.CallNote;
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


    public static final String PARSE_APP_ID = "anSwU1zWd1pJvRnm7ajbkPr7L83ZBsZex04uPm5j";
    public static final String PARSE_CLIENT_KEY = "SOWnAIYJWbN5lmbapcEvoaYFwd4d4t1dHhrIDHRq";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_PHONE = "CONTACT_PHONE";
    public static final String CONTACT_PHOTO = "CONTACT_PHOTO";
    public static final int LOCATION_DISTANCE_CLOSE = 10;
    public static final int LOCATION_DISTANCE_MEDIUM = 25;
    public static final int LOCATION_DISTANCE_FARTHER = 50;

    @Override
    public void onCreate() {
        super.onCreate();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
                Fabric.with(BetaCaller.this, new TwitterCore(authConfig), new Digits(), new Crashlytics());

            }
        };
        runnable.run();

        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(CallNote.class);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
