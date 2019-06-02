package com.doxa360.android.betacaller.broadcast_receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.doxa360.android.betacaller.CallNoteServiceNew;
import com.doxa360.android.betacaller.HollaNowApiClient;
import com.doxa360.android.betacaller.HollaNowApiInterface;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 9/22/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static int i;
    private PendingIntent pendingIntent;
    private HollaNowSharedPref mSharePref;
    private Intent callNoteIntent;
    private List<CallNote> callNote2 = new ArrayList<CallNote>() ;

    @Override
    public void onReceive(Context context, Intent intent){
        mSharePref = new HollaNowSharedPref(context);

        if(intent != null) {
            if(intent.getStringExtra("broadcast_alarm") != null)
             if (intent.getStringExtra("broadcast_alarm").equals("alarm_service")) {
                Toast.makeText(context, "I'm running " + i++, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Alarm is still running: "+i );
                if ( MyToolBox.isNetworkAvailable(context)) { //
                    //callNoteIntent = new Intent(context, CallNoteServiceNew.class);
                   // callNoteIntent.putExtra(Parse_Contact.PHONE_NUMBER, i);
                   // callNoteIntent.putExtra(Parse_Contact.USERNAME, "AlarmReceiver");
                   // context.startService(callNoteIntent);

                HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
                 if(mSharePref.getCurrentUser() != null)
                    i = 10;  // Stop alarm repeating
                    //Log.e(TAG, "At isCallNote0: " +phoneNumber+" "+mSharePref.getToken()+" "+mSharePref.getUsername());
                    Log.e(TAG, "Where does the stack happens  "  + "");
                    Call<JsonElement> call = hollaNowApiInterface.getCallNoteByPhone(mSharePref.getUsername(), mSharePref.getToken());  // "08057270466"
                    Log.e(TAG, "Where does the stack happens  2 "  + "");
                    call.enqueue(new Callback<JsonElement>() {
                        String getNote1;
                         //Log.e(TAG, "Where does the stack happens  2 "  + "");
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            CallNote note = null;
                            List<CallNote> callNote1 = new ArrayList<CallNote>() ;
                            Log.e(TAG, "Check response: " +response.code());
                            String noteStreams = null;
                            Log.e(TAG, "success of contacts " + response.code() + "");
                            Log.e(TAG, "success of contacts " + response.body() + "");
                            Type listType = new TypeToken<ArrayList<CallNote>>(){}.getType();

                            List<CallNote>  model =  new GsonBuilder().create().fromJson(response.body().toString(),listType);

                            Log.e(TAG, "success of contacts " + model.size() + "");
                            //callNote1 = model;


                            try {
                                if (model != null)

                                    for(int i=0; i<model.size();i++){
                                        CallNote c = model.get(i);
                                        Log.e(TAG, "CallNote Messages  " + c.getNote()+" "+c.getPhone());
                                        if(model.size() - i == 1)
                                            note = model.get(i);

                                        String s = c.getNote();
                                        int number_of_byte = 0;
                                        for(int m = 0;m < s.length(); m++ ){
                                            char data = s.charAt(m);
                                            if(data == ';')
                                                number_of_byte = number_of_byte + 1;
                                        }
                                        byte[] cv =  new byte[number_of_byte];
                                        StringBuilder builder = new StringBuilder();
                                        String message = null;
                                        String rawByte;
                                        int increment = 0;
                                        for(int m = 0;m < s.length(); m++ ){
                                            char data = s.charAt(m);
                                            if(data == ';') {
                                                rawByte = builder.toString();
                                                builder = new StringBuilder();
                                                cv[increment] = Byte.decode(rawByte);
                                                increment++;
                                                message = new String(cv);
                                            } else
                                                builder.append(data) ;

                                        }
                                        Log.e(TAG, "CallNote Messages 2  " +message );
                                        c.setNote(message);
                                        callNote1.add(c);
                                    }

                                callNote2 = confirmCallNote(callNote1);


                            }catch (NullPointerException e){
                                Log.e(TAG, "NullPointer Exception  " + e.getMessage() + " ");
                            }catch (IndexOutOfBoundsException a){
                                Log.e(TAG, "IndexOutofBound Exception   " + a.getMessage() + " ");
                            }catch (Exception s){
                                Log.e(TAG, "IndexOutofBound Exception   " + s.getMessage() + " ");
                            }


                           // int size = callNote2.size();
                           // CallNote note1 = callNote2.get(size-1);
                           // callerUserName = note1 != null ? note1.getUsername() : " ";

                           // callerName = note1 != null ? note1.getName() : callerName1;
                           // callerPhoto = note1 != null ? note1.getPhoto() : null;
                           // int unicode = 0x1f60e;
                           // String emoji = new String(Character.toChars(unicode));

                           // callNote = note1 != null ? note1.getNote() :  getString(R.string.default_callnote)+"  "+emoji; //note.getNote();
                           // updateUI();
                           // sendNotification(callNote2);
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Log.e(TAG, t.getMessage());
                           // updateUI();
                        }
                    });
                     String jsonCallnote =  new GsonBuilder().create().toJson(callNote2,List.class);
                    String alarmObject = new GsonBuilder().create().toJson(this,AlarmReceiver.class);

                    Intent callIntent = new Intent(context, CallNoteServiceNew.class);
                    callIntent.putExtra("back_service", "from_alarmreciever");
                    callIntent.putExtra("callnote", jsonCallnote);
                    callIntent.putExtra("ObjectAlarm", alarmObject);
                    callIntent.putExtra("check_Integer", 30);
                    context.startService(callIntent);



                }else{
                     if(i <= 3){
                         //  throw back again
                         Log.e(TAG, "Re-direct AlarmReceiver ");
                        // Intent callIntent = new Intent();
                         //intent.setAction(CallDiaryFragment.BROADCAST_ACTION);
                        // intent.putExtra("broadcast_alarm","alarm_service");
                         Log.e(TAG, "Shift control to AlarmReceiver 2 " );
                         try {
                             Thread.sleep(1000);
                         }catch(Exception e){
                             Log.e(TAG, "Sleep Thread " );
                         }
                        // context.sendBroadcast(callIntent);
                         onReceive( context,  intent);
                     }
                }

                }



            }
        }


    private List<CallNote> confirmCallNote(List<CallNote> remoteSaved){
        List<CallNote> callnote = new ArrayList<CallNote>();
        List<CallNote> phoneSaved = mSharePref.getListCallNote();
        Iterator<CallNote> phoneSaved_it = null;
        if(phoneSaved != null)
            phoneSaved_it = phoneSaved.iterator();
        if(phoneSaved == null  ||  phoneSaved.size() == 0)
            return remoteSaved;
        boolean from,to,note,username,name,photo,created,updated;
        from = false;
        to = false;
        note = false;
        username = false;
        name = false;
        photo = false;
        created = false;
        updated = false;
        Iterator<CallNote> remoteSaved_it = remoteSaved.iterator();
        while (remoteSaved_it.hasNext()){
            CallNote remote = remoteSaved_it.next();
            while (phoneSaved_it.hasNext()){
                CallNote phone = phoneSaved_it.next();
                if(remote.getNote().equals(phone.getNote()))
                    note = true;
                if(remote.getPhone().equals(phone.getPhone()))
                    from = true;
                if(remote.getCreated_at().equals(phone.getCreated_at()))
                    created = true;
                if(remote.getName().equals(phone.getName()))
                    name = true;
                if(remote.getPhoto().equals(phone.getPhoto()))
                    photo = true;
                if(remote.getReceiverPhone().equals(phone.getReceiverPhone()))
                    to = true;
                if(remote.getUpdated_at().equals(phone.getUpdated_at()))
                    updated = true;
                if(remote.getUsername().equals(phone.getUsername()))
                    username = true;
                if(!(note && from && created && name && photo && to && updated && username))
                    callnote.add(remote);

            }
        }
       // String jSon = new GsonBuilder().create().toJson(remoteSaved, List.class);
       // mSharePref.setListCallNote(jSon);
        return callnote;
    }

}

