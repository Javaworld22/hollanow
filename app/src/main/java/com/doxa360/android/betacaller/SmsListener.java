package com.doxa360.android.betacaller;


import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by user on 9/9/2017.
 */

public class SmsListener extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    public static String mobile, body;
   // public static String;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try{  // Start of try block
            if(bundle != null){
                final Object[] pdusobj = (Object[]) bundle.get("pdus");
                for(int i = 0; i < pdusobj.length;i++){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusobj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     mobile = phoneNumber;
                     body = currentMessage.getDisplayMessageBody();
                    Log.e("Smsreceiver", "SenderNum: "+mobile+" ; Message "+body);
                }
            }
        } //End of try block
        catch(Exception e){
            Log.e("Smsreceiver", "Exception SmsReciver "+e.getMessage());
        }
    }
}
