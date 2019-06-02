package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.vending.billing.IInAppBillingService;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ViewDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michael on 10/19/2017.
 */

public class UploadActivity extends AppCompatActivity {


    private static final String TAG = "UploadActivity";
    private ImageView mPhoto;
    private TextView textPhoto;
    private Button post;
    private HollaNowApiInterface hollaNowApiInterface;

    private HollaNowSharedPref mSharedPref;

    private int color;
    private String text;
    private String pic_name;
    private File picture;
    private int reward;
    private String amount;

    private Context context;

    private ProgressDialog mProgressDialog;

    private static int no_of_upload = -1;
    private Intent intent;
    private int checkRoute = 10;

   // IInAppBillingService mService;
    ServiceConnection mServiceConn;

    ViewDialog alert;


    public UploadActivity() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_service);
        context = getApplicationContext();
       // final Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent = getIntent();

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mSharedPref = new HollaNowSharedPref(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");

         alert = new ViewDialog();
        if(intent != null){
            checkRoute = intent.getExtras().getInt("sending");
            if(checkRoute == 4){
                picture = (File) intent.getExtras().get("picture");
                text = intent.getStringExtra("content");
                color = intent.getExtras().getInt("color");
                pic_name = intent.getStringExtra("pic_name");
                reward = intent.getExtras().getInt("amount");
                amount = intent.getStringExtra("name");
                mSharedPref.setDateOfUpload(updateDate());
                mProgressDialog.show();
                uploadFile(picture);
            }
        }


        mPhoto = (ImageView) findViewById(R.id.backdrop);
        textPhoto = (TextView) findViewById(R.id.image_text1);
        post = (Button) findViewById(R.id.post_service);
        if(!(checkRoute == 4)) {
            picture = (File) getIntent().getExtras().get("picture");
            text = getIntent().getExtras().getString("content");
            pic_name = getIntent().getExtras().getString("picture_name");
            color = getIntent().getExtras().getInt("color");

        }
       // no_of_upload = getIntent().getExtras().getInt("count2");
        if(picture != null)
            Picasso.with(UploadActivity.this)
                    .load(picture)
                    .placeholder(R.drawable.wil_profile)
                    .error(R.drawable.wil_profile)
                    .into(mPhoto);

        Log.e("UploadActivity", "Text photo to appear : "+text);

        textPhoto.setText(text);
        textPhoto.setTextColor(color);
//        FacebookSdk.sdkInitialize(mContext);
//        callbackManager = CallbackManager.Factory.create();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mProgressDialog.show();
                String expiringDate = mSharedPref.getDateOfUpload();
                String col1 = "";
                if(compareExpiredDate(expiringDate)) {
                    alert.showDialog(UploadActivity.this, " To Unlock and push unlimited images for 1week, watch our 30sec video",
                            picture, text, color, pic_name);
                }else{
                    mProgressDialog.show();
                    uploadFile(picture);
                }


              /**  mServiceConn = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        mService = IInAppBillingService.Stub.asInterface(service);
                       // flag_google_intent = true;
                      //  new CheckSubscriptionValid().execute();
                        Log.e("UploadActivity", "Binding services here ok: "+name.getPackageName() +" ");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        mService = null;
                        Log.e("UploadActivity", "Binding services here disconnected: "+name.getPackageName());
                    }
                }; **/

              //  serviceIntent.setPackage("com.android.vending");
             //   bindService(serviceIntent,mServiceConn, Context.BIND_AUTO_CREATE);
              //  if((!(no_of_upload > 3)) && no_of_upload != -1) {  //>
              //      uploadFile(picture);
              //  }
           /**     else {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(UploadActivity.this," To push unlimited images to yours work gallery. USE GoPro");

            **/
                /**    if(mColor.length() != 0){
                        for(int i = 0; i < mColor.length();i++) {
                            char c = mColor.charAt(i);
                            if(c != '_'){
                                if(!flag_count)
                                dateColor.add(c);
                                if(flag_count)
                                    dateColor1.add(c);
                            }else if(c == '_'){
                                flag_count = true;
                            }
                        }
                    }**/
                 /**   Iterator<Character> it = dateColor.iterator();
                    Iterator<Character> it1 = dateColor1.iterator();
                    String col = "";

                    while(it.hasNext()){
                        col = col+it.next();
                    }
                    while (it1.hasNext()){
                        col1 = col1+it1.next();
                    } **/

               //     mProgressDialog.dismiss();
                //    Toast.makeText(UploadActivity.this, "You have exceeded number of upload ", Toast.LENGTH_SHORT).show();
              //  }



            }
        });

    }

    private void uploadFile(final File photo) {
        MultipartBody.Part body = null;
        try {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), photo);

            body =
                    MultipartBody.Part.createFormData("pixname", photo.getName(), requestFile);
        }catch (NullPointerException e){
            Log.e(TAG, "Error Occured Here: "+e.getMessage());
        }

        final String fileName = photo.getName();
        RequestBody fileNameBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fileName);
        String mColor = String.valueOf(color);

        Date now = new Date();
        String timestamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(now);
        //mColor.concat("_"+timestamp);
        mColor = mColor+"_"+timestamp;
        int creditCount = mSharedPref.getCreditCount();
        creditCount = creditCount + reward;
        mSharedPref.setCreditCount(creditCount);

        //convert creditcount to String
        String creditCount_to_string = String.valueOf(creditCount);
        mColor = mColor+"_"+creditCount_to_string; // store in d remote server

        //get the next expiring date
       // String expringDate = updateDate();
       // mColor = mColor+"_"+expringDate;

        String username = mSharedPref.getCurrentUser().getUsername();

        Call<ResponseBody> call = hollaNowApiInterface.sendSalesPictures(body, username, mColor, text);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //TODO: set photo to imageview, toast success
                if (response.code() == 200) {
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Log.e(TAG, "Retrofit: "+photo.getName());
                    Picasso.with(UploadActivity.this)
                            .load(photo)
                            .placeholder(R.drawable.wil_profile)
                            .error(R.drawable.wil_profile)
                            .into(mPhoto);
                    mProgressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Picture successfully updated ", Toast.LENGTH_SHORT).show();
                    goActivity();
                    // currentUser.setProfilePhoto(fileName);
                    // mSharedPref.setCurrentUser(currentUser.toString());
                } else {
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Log.e(TAG, "Retrofit: "+ response.code());
                    Toast.makeText(UploadActivity.this, "Could not update picture", Toast.LENGTH_SHORT).show();
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Toast.makeText(UploadActivity.this, "Error uploading photo. Try again", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

                mProgressDialog.dismiss();
            }
        });

    }

    public void goActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        //intent.putExtra("count",no_of_upload);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
   /** private class CheckSubscriptionValid extends AsyncTask<Void, Integer,String> {

        @Override
        protected  String doInBackground(Void... argo) {

            try {
                Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);
                int response = ownedItems.getInt("RESPONSE_CODE");
                ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
                ArrayList continuation = ownedItems.getStringArrayList("INAPP_CONTINUATION_TOKEN");

                no_of_upload = purchaseDataList.size();


                Log.e("UploadActivity", "response  >>>>>>>: " + response + " ");
                Log.e("UploadActivity", "response List PurchaseData >>>>>>>: " + purchaseDataList.size() + " ");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseDate = (String) purchaseDataList.get(i);
                    String signature = (String) signatureList.get(i);
                    String sku = (String) ownedSkus.get(i);
                    String continu = (String) continuation.get(i);
                    Log.e("UploadPictureActivity", "response List PurchaseData >>>>>>>: " + purchaseDate + " ");
                    Log.e("UploadPictureActivity", "response List sku >>>>>>>: " + sku + " ");
                    Log.e("UploadPictureActivity", "response List signature >>>>>>>: " + signature + " ");
                    Log.e("UploadPictureActivity", "response List continue >>>>>>>: " + continu + " ");
                    Log.e("UploadPictureActivity", "response List purchaseData >>>>>>>: " + purchaseDate + " ");
                    Log.e("UploadPictureActivity", "response List sku >>>>>>>: " + sku + " ");
                }**/

           /**     if(no_of_upload > 0 && no_of_upload != -1) {  //>
                    mProgressDialog.dismiss();
                    uploadFile(picture);
                }else{
                    if(no_of_upload == 0) {
                        mProgressDialog.dismiss();
                        // ViewDialog alert = new ViewDialog();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                alert.showDialog(UploadActivity.this, " To push unlimited images to yours work gallery. USE GoPro");
                            }};
                        runnable.run();
                    }
                } **/

           /** }catch (RemoteException ex){
                Log.e("UploadPictureActivity", "Error occured at Remote Exception: "+ex.getMessage() +" ");
            }

            return "finish";
        }

        @Override
        public void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if (no_of_upload > 0 && no_of_upload != -1) {  //>
                mProgressDialog.dismiss();
                uploadFile(picture);
            } else {
                if (no_of_upload == 0) {
                    mProgressDialog.dismiss();
                    // ViewDialog alert = new ViewDialog();
                    UploadActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert.showDialog(UploadActivity.this, " To push unlimited images to yours work gallery. USE GoPro");
                        }
                    });


                }
            }
        }


    } **/

    @Override
    public void onDestroy(){
        super.onDestroy();
      //  if(mService != null)
         //   if(mServiceConn != null)
          //      unbindService(mServiceConn);
       // Log.e("UploadActivity", "OnDestroy: " +" ");
       // Log.e("UploadActivity", "OnDestroy: " +" ");
      //  Log.e("UploadActivity", "OnDestroy: " +" ");
        Log.e("UploadActivity", "OnDestroy: " +" ");

    }

    private String updateDate(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());// Todays date
        c.add(Calendar.DATE, 3); // Adding 3days
        String expire = String.format("yyyyMMdd",c.getTime());
        return expire;
    }

    private boolean compareExpiredDate(String postDate){
        boolean compare = false;
        Date simpleDate = null;
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMdd");
        try {
             simpleDate = timestamp.parse(postDate);
        }catch(ParseException e){
            compare = true;
            return compare;
        }
        Calendar postCal = Calendar.getInstance();
        Calendar present = Calendar.getInstance();
        postCal.setTime(simpleDate);
        if(postCal.before(present)){ // true if date present is leading
            compare = true;
            return compare;
        }
        return compare;

    }



}
