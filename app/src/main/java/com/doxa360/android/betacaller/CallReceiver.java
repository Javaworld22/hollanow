package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.doxa360.android.betacaller.model.Contact;

import java.util.Date;

/**
 * Created by Apple on 25/07/16.
 */
public class CallReceiver extends PhoneCallBroadcastReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
//        super.onIncomingCallStarted(ctx, number, start);
        Intent callIntent = new Intent(INCOMING_CALL);
        callIntent.putExtra(Contact.PHONE_NUMBER, number);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
//        super.onIncomingCallEnded(ctx, number, start, end);
        Intent callIntent = new Intent(INCOMING_CALL_ENDED);
        callIntent.putExtra(Contact.PHONE_NUMBER, number);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        super.onMissedCall(ctx, number, start);
    }
}
