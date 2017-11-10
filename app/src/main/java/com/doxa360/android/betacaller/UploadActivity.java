package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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

    private ProgressDialog mProgressDialog;

    private int no_of_upload;


    public UploadActivity() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_service);

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mSharedPref = new HollaNowSharedPref(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");


        mPhoto = (ImageView) findViewById(R.id.backdrop);
        textPhoto = (TextView) findViewById(R.id.image_text1);
        post = (Button) findViewById(R.id.post_service);
        picture = (File) getIntent().getExtras().get("picture");
         text = getIntent().getExtras().getString("content");
         pic_name = getIntent().getExtras().getString("picture_name");
         color = getIntent().getExtras().getInt("color");
        no_of_upload = getIntent().getExtras().getInt("count2");
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
                mProgressDialog.show();
                String col1 = "";
                if(!(no_of_upload > 3)) {
                    uploadFile(picture);
                }
                else {
                    List<Character> dateColor = new ArrayList<Character>();
                    List<Character> dateColor1 = new ArrayList<Character>();
                    boolean flag_count = false;
                    String mColor = String.valueOf(color);
                    Date now = new Date();
                    String timestamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(now);
                    Log.e(TAG, "TimeStamp 2: "+ timestamp);
                    Log.e(TAG, "TimeStamp 2: "+ timestamp);
                    Log.e(TAG, "TimeStamp 2: "+ timestamp);
                    Log.e(TAG, "TimeStamp 2: "+ timestamp);
                    mColor = mColor+"_"+timestamp;
                    Log.e(TAG, "TimeStamp 1: "+ mColor);
                    Log.e(TAG, "TimeStamp 1: "+ mColor);
                    Log.e(TAG, "TimeStamp 1: "+ mColor);
                    Log.e(TAG, "TimeStamp 1: "+ mColor);
                    Log.e(TAG, "TimeStamp 1: "+ mColor);
                    if(mColor.length() != 0){
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
                    }
                    Iterator<Character> it = dateColor.iterator();
                    Iterator<Character> it1 = dateColor1.iterator();
                    String col = "";

                    while(it.hasNext()){
                        col = col+it.next();
                    }
                    while (it1.hasNext()){
                        col1 = col1+it1.next();
                    }
                    Log.e(TAG, "Final answer 1: "+ col.trim());
                    Log.e(TAG, "Final answer 1: "+ col.trim());
                    Log.e(TAG, "Final answer 1: "+ col.trim());
                    Log.e(TAG, "Final answer 1: "+ col.trim());
                    Log.e(TAG, "Final answer 2: "+ col1.trim());
                    Log.e(TAG, "Final answer 2: "+ col1.trim());
                    Log.e(TAG, "Final answer 2: "+ col1.trim());
                    Log.e(TAG, "Final answer 2: "+ col1.trim());
                    mProgressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "You have exceeded number of upload ", Toast.LENGTH_SHORT).show();
                }
                String date = col1.trim();
                String year = "".trim();
                String month= "".trim();
                String days= "".trim();
                String calendar = "".trim();
                for (int i = 0;i<date.length();i++) {

                    if (i <= 3)
                        year = year + date.charAt(i);
                    else if (i <= 5 && i >= 4)
                        month = month + date.charAt(i);
                    else if (i <= 7 && i >= 6)
                        days = days + date.charAt(i);
                }
                if(days.equals("01"))
                    calendar = calendar + "1st";
                else if(days.equals("02"))
                    calendar = calendar + "2nd";
                else if(days.equals("03"))
                    calendar = calendar + "3rd";
                else if(days.equals("04"))
                    calendar = calendar + "4th";
                else if(days.equals("05"))
                    calendar = calendar + "5th";
                else if(days.equals("06"))
                    calendar = calendar + "6th";
                else if(days.equals("07"))
                    calendar = calendar + "7th";
                else if(days.equals("08"))
                    calendar = calendar + "8th";
                else if(days.equals("09"))
                    calendar = calendar + "9th";
                else if(days.equals("10"))
                    calendar = calendar + "10th";
                else if(days.equals("11"))
                    calendar = calendar + "11th";
                else if(days.equals("12"))
                    calendar = calendar + "12th";
                else if(days.equals("13"))
                    calendar = calendar + "13th";
                else if(days.equals("14"))
                    calendar = calendar + "14th";
                else if(days.equals("15"))
                    calendar = calendar + "15th";
                else if(days.equals("16"))
                    calendar = calendar + "16th";
                else if(days.equals("17"))
                    calendar = calendar + "17th";
                else if(days.equals("18"))
                    calendar = calendar + "18th";
                else if(days.equals("19"))
                    calendar = calendar + "19th";
                else if(days.equals("20"))
                    calendar = calendar + "20th";
                else if(days.equals("21"))
                    calendar = calendar + "21th";
                else if(days.equals("22"))
                    calendar = calendar + "22th";
                else if(days.equals("23"))
                    calendar = calendar + "23th";
                else if(days.equals("24"))
                    calendar = calendar + "24th";
                else if(days.equals("25"))
                    calendar = calendar + "25th";
                else if(days.equals("26"))
                    calendar = calendar + "26th";
                else if(days.equals("27"))
                    calendar = calendar + "27th";
                else if(days.equals("28"))
                    calendar = calendar + "28th";
                else if(days.equals("29"))
                    calendar = calendar + "29th";
                else if(days.equals("30"))
                    calendar = calendar + "30th";
                else if(days.equals("31"))
                    calendar = calendar + "31th";

                calendar = calendar + " ";

                if(month.equals("01"))
                    calendar = calendar + "January";
                else if(month.equals("02"))
                    calendar = calendar + "Febuary";
               else  if(month.equals("03"))
                    calendar = calendar + "March";
               else  if(month.equals("04"))
                    calendar = calendar + "April";
               else if(month.equals("05"))
                    calendar = calendar + "May";
                else if(month.equals("06"))
                    calendar = calendar + "June";
                else if(month.equals("07"))
                    calendar = calendar + "July";
                else if(month.equals("08"))
                    calendar = calendar + "August";
               else if(month.equals("09"))
                    calendar = calendar + "September";
                else if(month.equals("10"))
                    calendar = calendar + "October";
                else if(month.equals("11"))
                    calendar = calendar + "November";
                else if(month.equals("12"))
                    calendar = calendar + "December";

                calendar = calendar + ", ";
                calendar = calendar + year;
                Log.e(TAG, "The real date: "+ calendar);
                Log.e(TAG, "The real date: "+ calendar);
                Log.e(TAG, "The real date: "+ calendar);
                Log.e(TAG, "The real date: "+ calendar);


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
        mColor.concat("_"+timestamp);

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

              //  mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Toast.makeText(UploadActivity.this, "Error uploading photo. Try again", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

               // mProgressDialog.dismiss();
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




}
