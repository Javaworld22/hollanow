package com.doxa360.android.betacaller.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Apple on 12/04/16.
 */
@ParseClassName("CallNote")

public class CallNote extends ParseObject {

    public static final String CALL_NOTE = "CALL_NOTE";
    public static final String CALL_NOTE_ID = "CALL_NOTE_ID";
    public static final String CALLER_ID = "CALLER_ID";
    public static final String CALLER_USER_NAME = "CALLER_USER_NAME";
    public static final String CALLER_EMAIL = "CALLER_EMAIL";
    public static final String CALLER_NAME = "CALLER_NAME";
    public static final String CALLER_NUMBER = "CALLER_NUMBER";
    public static final String CALLER_PHOTO = "CALLER_PHOTO";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    public CallNote() {
    }

    public String getCallNote() {
        return getString("callNote");
    }

    public void setCallNote(String value) {
        put("callNote", value);
    }

    public String getCallerId() {
        return getString("callerId");
    }

    public void setCallerId(String value) {
        put("callerId", value);
    }

    public String getCallerUserName() {
        return getString("callerUserName");
    }

    public void setCallerUserName(String value) {
        put("callerUserName", value);
    }

    public String getCallerEmail() {
        return getString("callerEmail");
    }

    public void setCallerEmail(String value) {
        put("callerEmail", value);
    }

    public String getCallerName() {
        return getString("callerName");
    }

    public void setCallerName(String value) {
        put("callerName", value);
    }

    public String getCallerNumber() {
        return getString("callerNumber");
    }

    public void setCallerNumber(String value) {
        put("callerNumber", value);
    }


    public String getCallerPhoto() {
        return getString("callerPhoto");
    }

    public void setCallerPhoto(String value) {
        put("callerPhoto", value);
    }

    public String getUser() {
        return getString("user");
    }

    public void setUser(String value) {
        put("user", value);
    }

    public String getUserNumber() {
        return getString("userNumber");
    }

    public void setUserNumber(String value) {
        put("userNumber", value);
    }

    public static ParseQuery<CallNote> getQuery() {
        return ParseQuery.getQuery(CallNote.class);
    }



}
