package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.Post_Model;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/8/2018.
 */

public class ShoutOutActivity  extends AppCompatActivity {

    private static final String TAG = ShoutOutActivity.class.getSimpleName();
    private TextView marguee;
    private Button postButton;
    private EditText textMsg;
    private HollaNowSharedPref mSharedPref;
    private TextInputLayout textLayout;
    private ProgressBar mProgress;
    private Context mContext;
   // private bimps bip;
    //private static boolean sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext  = getApplicationContext();
        setContentView(R.layout.activity_shout_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_shoutout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSharedPref = new HollaNowSharedPref(this);
        marguee = (TextView) findViewById(R.id.marguee);
        postButton = (Button) findViewById(R.id.post_shout_out);
        textMsg = (EditText) findViewById(R.id.display_shout_out);
        mProgress = (ProgressBar) findViewById(R.id.progress_shoutout);
         textLayout = (TextInputLayout) findViewById(R.id.layout_shoutoutText);

        toolbar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ShoutOutActivity.this, HomeActivity.class);
                intent.putExtra("shout_out","activity_shoutout");
                startActivity(intent);
            }
        });
        marguee.setSelected(true);
        marguee.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(MyToolBox.checkEmptyfield(textMsg.getText().toString())) {
                    textLayout.setErrorEnabled(false);
                    textLayout.setEnabled(false);
                    doPostMessage(mSharedPref.getCurrentUser().getUsername());
                }else
                    Toast.makeText(mContext, "Enter Post", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void notifyUser(final String username, final Post_Model mode, final String dateString){
        List<String> msg = mode.getMessage();
        List<String> stringDate = mode.getmMsgDate();
        if(msg != null) {
            msg.add(textMsg.getText().toString());
            if(stringDate != null)
                stringDate.add(dateString);

            if(stringDate == null){
                msg = new ArrayList<String>();
                stringDate = new ArrayList<String >();
                msg.add(textMsg.getText().toString());
                stringDate.add(dateString);
            }
            if(msg.size() > 7){
                msg = new ArrayList<String>();
                stringDate = new ArrayList<String>();
                msg.add(textMsg.getText().toString());
                stringDate.add(dateString);
            }
        }else {
            msg = new ArrayList<String>();
            stringDate = new ArrayList<String >();
            msg.add(textMsg.getText().toString());
            stringDate.add(dateString);
        }
       // Log.e(TAG, "Text to display on the screen "+textMsg.getText().toString());
        Log.e(TAG, "Text to display on the screen 2 "+msg.toString());
        mode.setMessage(msg);
        mode.setMsgDate(stringDate);
        List<ShoutOutModel> model = mSharedPref.getListShout();
        List<Integer> count = new ArrayList<Integer>();
        List<ShoutOutModel> model2 = new ArrayList<ShoutOutModel>();



        for(int i = 0;i<msg.size();i++){ // add all the post of current user to
            ShoutOutModel m = new ShoutOutModel();
            m.setSender(mSharedPref.getCurrentUser().getUsername());
            m.setMessage(msg.get(i));
            m.setDate(stringDate.get(i));
            m.setUsername(username);
            model2.add(m);
           // count.add(i);
        }

        if(model == null)
            model = new ArrayList<ShoutOutModel>();

        for(int i=0;i<model.size();i++){ //remove all current users post
            if(model.get(i).getSender().equals(mSharedPref.getCurrentUser().getUsername())) {
                Log.e(TAG, "Remove text by the poster  "+model.remove(i)+"  "+i +"  "+model.size());
                model.add(i,null);
            }
        }

       // Log.e(TAG, "Remove text by the poster  2 "+model.toString());
        while(model.contains(null))
            model.remove(null);


        for(int j=0;j<model2.size();j++){
            model.add(model2.get(j));
            Log.e(TAG, "Remove text by the poster  1 "+model.toString());
        }




        mSharedPref.setListShout(model.toString());


        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.notifyUserContact(username, mode.toString());

        call.enqueue(new Callback<JsonElement>() {
            String mBip =  null;
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                String msg = textMsg.getText().toString();
                String date = dateString;
                String sender = mSharedPref.getCurrentUser().getUsername();
                List<ShoutOutModel> mode = new ArrayList<ShoutOutModel>();

                if (response.code() == 200) {
                    mProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "Posted", Toast.LENGTH_LONG).show();
                    Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                    // bimps bip =   model.getNotification().get(0);
                    Log.e(TAG, "Whats saved on the notification "+response.code());
                  //  if(mSharedPref.getCurrentUser().getUsername().equals(model1.getUsername()))
                   //     mSharedPref.setNotificationUser(bip.toString());
                  //  Log.e(TAG, "Whats saved on the notification "+mode.toString());



                    Intent intnt = new Intent();
                    intnt.setAction(FragmentShoutOut.BROADCAST_ACTION);
                    intnt.putExtra("broadcast_intent","refresh");
                    mContext.sendBroadcast(intnt);
                   // Log.e(TAG, "what to broadcast  "+mSharedPref.getListShout());

                    Intent intent = new Intent(ShoutOutActivity.this, HomeActivity.class);
                    intent.putExtra("shout_out","activity_shoutout");
                    startActivity(intent);
                }else{
                    mProgress.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Whats saved on the notification "+response.code());
                   // Log.e(TAG, "Whats saved on the notification "+response.code());
                  //  Log.e(TAG, "Whats saved on the notification "+username);
                    Toast.makeText(mContext, "Error occurred", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(mContext, "Error occurred", Toast.LENGTH_LONG).show();

            }

        });
    }




    private void receiveNotification(final String username, final String dateString){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <List<NotificationModel>> call = hollaNowApiInterface.receiveNotification(username);
        mProgress.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.code() == 200) {
                    NotificationModel model = response.body().get(0);
                    // mSharedPref.setNotificationUser(bips.toString());
                    //Type collectionType = new TypeToken<Notification_>(){}.getType();
                    //List<bimps> model = new GsonBuilder().create().fromJson(response.body().toString(), List.class);
                    //  JsonElement model1 = new GsonBuilder().create().fromJson(response.body().toString(), JsonElement.class);
                    //  Notification_ model2 = new Notification_(model);
                    Log.e(TAG, "Whats received on the notification "+response.code());
                   // Log.e(TAG, "Whats received on the notification "+response.body().size());
                   // Log.e(TAG, "Whats received on the notification "+model.toString());
                   // Log.e(TAG, "Whats received on the notification "+model.getUsername());
                   // Log.e(TAG, "Whats received on the notification "+model.getNotification());
                    Post_Model mode = null;
                    List<List<String>> items = null;
                    List<String> msg = null;
                    try {
                        mode = new GsonBuilder().create().fromJson(response.body().get(0).getNotification(), Post_Model.class);
                       // if(b != null)
                       // bip = b;
                        //    items = b.getShoutOut();
                        msg = mode.getMessage();
                       // Log.e(TAG, "list of messages sent "+items.toString());
                        //Log.e(TAG, "list of messages sent "+msg.toString());
                    }catch (JsonSyntaxException e){
                        Log.e(TAG, "JsonSyntaxException Occured here "+e.getMessage());
                    }catch(NullPointerException ex){
                        Log.e(TAG, "NullPointerException Occured here "+ex.getMessage());
                    }
                    if(mode == null)
                        mode = new Post_Model();
                    //mProgress.setVisibility(View.INVISIBLE);
                    // Log.e(TAG, "Whats received on the notification "+b.getmVerify());
                    //if(mSharedPref.getCurrentUser().getUsername().equals(b.getUsername()))
                    // mSharedPref.setNotificationUser(b.toString());
                    notifyUser(username, mode,dateString);
                }else{
                    Log.e(TAG, "Whats received on the notification "+response.code());
                   // Log.e(TAG, "Whats received on the notification "+response.code());
                    mProgress.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
                Log.e(TAG, "Failure "+t);
                mProgress.setVisibility(View.INVISIBLE);
                // bipList = helper.allNotification();

            }

        });
    }

    private void doPostMessage(String username){
        final String strDateFormate = "dd/MM/yyyy h:mm aa";
        Calendar now = Calendar.getInstance();
        String getDate_in_string  =  ""+android.text.format.DateFormat.format(strDateFormate, now);
        receiveNotification(username,getDate_in_string);
    }

    private String dateFormate(String date){
        SimpleDateFormat format  = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
        Calendar cal = Calendar.getInstance();
        final String strTimeFormate = "h:mm aa";
        final String strDateFormate = "dd/MM/yyyy  h:mm aa";
        Date date1 = null;
        try {
            date1 = ((Date) format.parse(date));
        }catch (ParseException ex){

        }

        Calendar now = Calendar.getInstance();
        long msgTimeMills = date1.getTime();

         date = String.valueOf(msgTimeMills);

          return date;
    }



}
