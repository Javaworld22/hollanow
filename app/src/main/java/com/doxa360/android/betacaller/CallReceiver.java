package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.doxa360.android.betacaller.model.Parse_Contact;

import java.util.Date;

/**
 * Created by Apple on 25/07/16.
 */
public class CallReceiver extends PhoneCallBroadcastReceiver {
    public static String nos;
    public static boolean isMissed = false;
    public static int callEnded;  // to check for answered calls
    public static int number_of_calls;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        super.onIncomingCallStarted(ctx, number, start);
        isMissed = false;
        //Intent callIntent = new Intent("android.intent.action.ACTION_ANSWER");
        Intent callIntent = new Intent(ctx, CallNoteServiceNew.class);
        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
        nos = number;
        Log.e("CallReceiver", "Check Number 1: "+number);
      // LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
        Toast.makeText(ctx,"INCOMING CALL STATE", Toast.LENGTH_SHORT).show();
        //ctx.startService(new Intent(ctx, CallNoteServiceNew.class));
        ctx.startService(callIntent);

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
//        super.onIncomingCallEnded(ctx, number, start, end);
        callEnded = 2;
        //TODO: delete below if redundant
//        Intent callIntent = new Intent(INCOMING_CALL_ENDED);
//        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
//        LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        super.onMissedCall(ctx, number, start);
        Log.e("CallReceiver", "Check Number 2: "+number);
        Intent callIntent = new Intent(ctx, CallNoteServiceNew.class);
        callIntent.putExtra(Parse_Contact.PHONE_NUMBER, number);
        nos = number;
        Log.e("CallReceiver", "Check Number 1: "+number);
        // LocalBroadcastManager.getInstance(ctx).sendBroadcast(callIntent);
        ctx.startService(callIntent);
        isMissed = true;
        number_of_calls++;
    }
}
