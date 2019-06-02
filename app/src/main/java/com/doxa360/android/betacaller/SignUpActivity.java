package com.doxa360.android.betacaller;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;


import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import android.support.v4.app.FragmentManager;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.facebook.accountkit.AccountKit;
//import com.facebook.accountkit.ui.AccountKitActivity;
//import com.facebook.accountkit.ui.AccountKitConfiguration;
//import com.facebook.accountkit.ui.LoginType;
//import com.facebook.accountkit.AccountKitLoginResult;
//import com.facebook.accountkit.AccountKitError;

import static com.doxa360.android.betacaller.helpers.MyToolBox.isExternalStorageAvailable;


public class SignUpActivity extends AppCompatActivity {


    //    private EditText mEmail,mPhone;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText mName;
    private EditText mPassword;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mNameLayout;
    private TextInputLayout mEmailLayout;
    private ProgressDialog mProgressDialog;
    private Button mSignUp;
    private EditText mUserName;
    private EditText mEmail;
    private ImageView change_sigup_photo;
    private ImageView sigup_photo;
    //    private TextInputLayout mEmailLayout, mPhoneLayout;
    private String email;

    private static String token1;
    protected Uri mMediaUri;
    private File mediaFile = null;

    private HollaNowApiInterface hollaNowApiInterface;
    private static User mResponse;
    private static int presentActivity;
    private String phone;
    private HollaNowSharedPref share;
    private  Runnable runnable;
    private DigitsSession mSession;
    private boolean flag_updateUser, flag_getUserdetails, flag_phone, flag_createuser;
    private HollaNowSharedPref sharedPref;
    private static int check_activity;
    private Context mContext;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public  static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB
    private byte[] fileBytes;
    private AlertDialog.Builder builder;
    private User currentUser;
    private HollaNowSharedPref mSharedPref;

    public static int APP_REQUEST_CODE = 99;


    View.OnClickListener editPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cameraChoices();
        }
    };


    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            //Capture Image
                            Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //cos of the broadcast...
                            if (mMediaUri == null){
                                Toast.makeText(SignUpActivity.this, "There was a problem capturing your photo", Toast.LENGTH_LONG).show();
                            }
                            else{
                                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(captureImageIntent, TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1:
                            //Choose Image
                            Intent chooseImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseImageIntent.setType("image/*");
                            //Toast.makeText(getActivity(), "The size of your video must be less than 10MB", Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseImageIntent, CHOOSE_PHOTO_REQUEST);
                            break;
                    }
                }

                private Uri getOutputMediaFileUri(int mediaType) {
                    if(isExternalStorageAvailable()){
                        //storage dir
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                getString(R.string.app_name));
                        //subdir
                        if(!mediaStorageDir.exists()){
                            if(!mediaStorageDir.mkdirs()){
                                return null;
                            }
                        }
                        //file name and create the file
                        //mediaFile;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now);

                        String path = mediaStorageDir.getPath() + File.separator;
                        if (mediaType == MEDIA_TYPE_IMAGE){
                            mediaFile = new File(path+"IMG_"+timestamp+".jpg");

                        }
                        else if (mediaType == MEDIA_TYPE_VIDEO){
                            mediaFile = new File(path+"VID_"+timestamp+".mp4");
                        }
                        else{
                            return null;
                        }
                        Toast.makeText(SignUpActivity.this,"File: "+Uri.fromFile(mediaFile),Toast.LENGTH_LONG).show();
                        return Uri.fromFile(mediaFile);
                    }
                    else{
                        return null;
                    }
                }

                private boolean isExternalStorageAvailable(){
                    String state = Environment.getExternalStorageState();

                    if (state.equals(Environment.MEDIA_MOUNTED)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();
        phone = null;
        mResponse = null;
        sharedPref = new HollaNowSharedPref(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPref.setFlagPhoneAuth(true);
        Intent intent = getIntent();
        if (intent!=null) email = intent.getStringExtra("EMAIL");
        Log.e(TAG, "onCreate is here");
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        builder = new AlertDialog.Builder(SignUpActivity.this);
        mSharedPref = new HollaNowSharedPref(this);
        currentUser = mSharedPref.getCurrentUser();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing up ...");
//        mPhone = (EditText)  findViewById(R.id.phone);
//        mEmail = (EditText)  findViewById(R.id.email_address);
        mEmail = (EditText)  findViewById(R.id.email_signup);
        mUserName = (EditText)  findViewById(R.id.user_name);
        mName = (EditText)  findViewById(R.id.full_name);
        mPassword = (EditText)  findViewById(R.id.password);
        change_sigup_photo = (ImageView)findViewById(R.id.change_sigup_photo);
        sigup_photo = (ImageView) findViewById(R.id.signup_photo);
//        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
//        mPhoneLayout = (TextInputLayout) findViewById(R.id.layout_phone);
        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_signup);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mSignUp = (Button)  findViewById(R.id.sign_up_button);

        change_sigup_photo.setOnClickListener(editPhoto);


        sigup_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // View rowView = null;
                try {
                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.custom_dialog, null);
                    ImageView mImage = (ImageView) dialoglayout.findViewById(R.id.signup_image_view);
                    Picasso.with(SignUpActivity.this).invalidate(mediaFile);
                    Picasso.with(SignUpActivity.this)
                            .load(mediaFile)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.wil_profile)
                            .into(mImage);
                    builder.setView(dialoglayout);
                    builder.show();
                }catch(NullPointerException ex){

                }catch(Exception a){

                }
            }
        });

        if(email != null) {
            mSignUp.setText("Sign up as " + email);
            mEmail.setText(email);
        }

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(SignUpActivity.this)) {
                   // mProgressDialog.show();
                    if (!MyToolBox.isMinimumCharacters(mEmail.getText().toString().trim(), 2)) {
                        mEmailLayout.setError("Your email address");
                       mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isEmailValid(mEmail.getText().toString().trim())) {
                        mEmailLayout.setError("Invalid email address");
                        mProgressDialog.dismiss();
                    }
//                      else if (!MyToolBox.isMinimumCharacters(mPhone.getText().toString().trim(), 2)) {
//                        mPhoneLayout.setError("Your phone number");
//                        mProgressDialog.dismiss();
//                    }
                    else if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString().trim(), 5)) {
                        mPasswordLayout.setError("Your password should have at least 6 characters");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mUserName.getText().toString().trim(), 5)) {
                        mUsernameLayout.setError("Your username should have at least 6 characters");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mName.getText().toString().trim(), 5)) {
                        mName.setError("Your full name should have more characters");
                        mProgressDialog.dismiss();
                    }

                   // else if(!MyToolBox.isNameWithLetters(mName.getText().toString().trim())){
                    //    mName.setError("Name may only require letters");
                    //    mProgressDialog.dismiss();
                    //    Toast.makeText(SignUpActivity.this, "Error at Name field", Toast.LENGTH_LONG).show();
                   // }
                   // else if(!MyToolBox.isNameSpace1(mName.getText().toString().trim())){
                   //     mName.setError("Not more than one is required");
                   //     mProgressDialog.dismiss();
                   //     Toast.makeText(SignUpActivity.this, "Error at Name field", Toast.LENGTH_LONG).show();
                   // }
                    else {
                        mUsernameLayout.setErrorEnabled(false);
                        mPasswordLayout.setErrorEnabled(false);
                        mNameLayout.setErrorEnabled(false);
                        //mSignUp.setEnabled(false);
                       // phoneVerify("+2348154965498");

                       // getSupportFragmentManager().beginTransaction()
                       //         .add(R.id.fragment_phone_verify,new PhoneInputFragment())
                          //    .commit();
                       // Intent intent = new Intent(mContext, PhoneVerificationActivity.class);
                       // startActivity(intent);

//                        mEmailLayout.setErrorEnabled(false);
//                        mPhoneLayout.setErrorEnabled(false);
//                        verifyPhoneNumber(phone);
                      //  if(phone != null) {
                        if(mediaFile == null)
                            Toast.makeText(SignUpActivity.this,  "You didn't Upload a picture", Toast.LENGTH_LONG).show();
                    // else
                       // FirebaseAuth.getInstance().signOut();
                           // createUser(mEmail.getText().toString(), mPassword.getText().toString(),
                               //    mUserName.getText().toString(), mName.getText().toString());

                        User user = new User(mEmail.getText().toString(), mPassword.getText().toString(),
                                mUserName.getText().toString(), mName.getText().toString());



                        PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                        Bundle args = new Bundle();
                         args.putParcelable("data1", user);
                         if(mediaFile != null) {
                             String path = mediaFile.getPath();
                             args.putString("url", path);
                         }
                        callNoteBottomSheet.setArguments(args);
                        FragmentManager fm = getSupportFragmentManager();
                        callNoteBottomSheet.show(fm,"REFS");

                        //phoneLogin();


                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }


            }
        });


            ++check_activity;
        Log.e(TAG, "onCreate is here "+check_activity);

    }

   /** private void verifyPhoneNumber(String phone1, String token1) {
            sharedPref.setPhone(" ");

       // Log.e(TAG, "Response 10000012 "+mResponse.getToken());
        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(new AuthCallback() {

                    @Override
                    public void success(DigitsSession session, String phoneNumber) {
                        //TODO: check phone availablity?
                        Log.e(TAG, "Response 10000013 ");
                        phone = phoneNumber;
                        mSession = session;
                        sharedPref.setPhoneEmoji(phoneNumber);
                        presentActivity = 1;
                        sharedPref.setPhone(phoneNumber);
                        flag_phone = true;
                       // share = new HollaNowSharedPref(getApplicationContext());
                       // share.setPhone(phoneNumber);
                       // mProgressDialog.show();
                       // token1 = mResponse.getToken();
                      //  Log.e(TAG, "Response 1006 "+token);
                       // if(token != null) {
                            Log.e(TAG, "Response 10000014 ");
                        //   getUserDetails(token, phoneNumber);

                         //   updateUser(mResponse, phoneNumber, token);
                         //   saveCurrentUser(mResponse, token);
                            Log.e(TAG, "Response 1007 ");
                        //    goToActivity(HomeActivity.class);
                    //    }
                        if(phoneNumber != null  && mResponse != null) {
                          //  getUserDetails(mResponse.getToken(), phone);

                         //   updateUser(mResponse, phone, mResponse.getToken());
                            saveCurrentUser(mResponse, mResponse.getToken());
                            Log.e(TAG, "Response 1000000007646");
                            goToActivity(HomeActivity.class);
                        }
                     //   else if(token == null){
                    //        Toast.makeText(SignUpActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                      //      goToActivity(HomeActivity.class);
                      //  }
                    }

                    @Override
                    public void failure(DigitsException error) {
                        //still go to app;
//                        goToActivity(HomeActivity.class);
                    }
                })
                .withPhoneNumber(phone);

        Digits.authenticate(authConfigBuilder.build());

    } **/




    private void createUser(final String email, final String password, final String username, final String name) {
        Log.e(TAG, ""+email+password+username+name);
        mProgressDialog.show();
        mResponse = null;
        // phone = null;
        User user = new User(email, password, username, name);
        Call<User> call = hollaNowApiInterface.signUpUser(user);
        Log.e(TAG, "signing up");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    //  phone = null;
                   // Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());

                    Log.e(TAG, "response: " + response.body());
                    mResponse = response.body(); // containing token
                   // mResponse.setName(name);
                    //  flag_createuser = true;
                    Log.e(TAG, "Response 1001");
                    mProgressDialog.dismiss();
                    saveCurrentUser(mResponse, mResponse.getToken());
                   // if(mediaFile == null)
                     //   Toast.makeText(SignUpActivity.this, "Upload a picture", Toast.LENGTH_LONG).show();
                   // else
                    if(mediaFile != null)
                    uploadFile(mediaFile);
                    else{
                        PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                        //Bundle args = new Bundle();
                        // args.putParcelable("data1", data);
                        //callNoteBottomSheet.setArguments(args);
                        FragmentManager fm = getSupportFragmentManager();
                        callNoteBottomSheet.show(fm,"REFS");
                    }


                    Log.e(TAG, "Response 1000043");

                    // if(phone != null) {

                    // }
                } else {
//                    Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                    Log.e(TAG, "Response 1002");
                    Toast.makeText(SignUpActivity.this, "User with email/username exists - "+ response.body(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    mSignUp.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if(t instanceof IOException){
                    Log.e(TAG, "Response 1002 "+t.getMessage());
                    Toast.makeText(SignUpActivity.this, "Data Error ", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
                else if(t instanceof SocketTimeoutException){
                    Log.e(TAG, "Response 1003 "+t.getMessage() );
                    Toast.makeText(SignUpActivity.this, "Timeout!!", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
                else if(t instanceof NetworkErrorException){
                    Log.e(TAG, "Response 1003 "+t.getMessage() );
                    Toast.makeText(SignUpActivity.this, "Oops Network Error!", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
                else{ Toast.makeText(SignUpActivity.this, "User with email/username already been used", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response 1004 "+t.getMessage());
                    mProgressDialog.dismiss();}
                mSignUp.setEnabled(true);
            }
        });


    }








    private void saveCurrentUser(User user, String token) {
        HollaNowSharedPref sharedPref = new HollaNowSharedPref(this);
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
       // sharedPref.setPhone(phone);
        sharedPref.setPhoneEmoji(user.getPhone());
        sharedPref.setFlagContacts("BackUp");
        currentUser = sharedPref.getCurrentUser();
        Log.e(TAG, "Response 1009 ");
    }

    private void cameraChoices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void goToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (cls == HomeActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e(TAG, "Onresume here ");
     /**   if(mResponse != null && phone != null) {
            mProgressDialog.show();
            Log.e(TAG, "Onresume here at phone an mresponse == null ");
           // check_activity = 0;
            if (flag_updateUser)
                updateUser(mResponse, phone, mResponse.getToken());
            else if (flag_getUserdetails) {
                getUserDetails(mResponse.getToken(), phone);
                saveCurrentUser(mResponse, mResponse.getToken());
                Toast.makeText(SignUpActivity.this, "Welcome " + mResponse.getName(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                goToActivity(HomeActivity.class);
            }
        }else if(sharedPref.getPhone().equals(" ") && flag_phone) {
            flag_phone = false;
            Log.e(TAG, "Onresume here flag_phone");
            check_activity = 0;
            verifyPhoneNumber(phone, "");
        }
        else if(mResponse == null && flag_createuser){
            flag_createuser = false;
           // check_activity = 0;
           // sharedPref = new HollaNowSharedPref(this);
            mResponse = sharedPref.getCurrentUser();
            Log.e(TAG, "Respone is null");
            mProgressDialog.dismiss();
        if(phone == null) {
            phone = sharedPref.getPhone();
            getUserDetails(mResponse.getToken(), phone);
            updateUser(mResponse, phone, mResponse.getToken());
        }
            saveCurrentUser(mResponse, mResponse.getToken());
          //  goToActivity(HomeActivity.class);
        }else if(check_activity > 1){
            check_activity = 0;
            Log.e(TAG, "check_activity "+check_activity);
          //  goToActivity(HomeActivity.class);
        }
        ++check_activity; **/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "OnDestroy here ");

       // mResponse = null;
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.e(TAG, "On Stop here");
      //  if(mResponse != null && phone == null) {
       //     verifyPhoneNumber(phone, mResponse.getToken());
       // }
       // mResponse = null;
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e(TAG, "OnPause here");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
           // mProgressDialog.setMessage("Uploading Picture ...");
           // mProgressDialog.show();
            Log.e(TAG, "Result upload 1");
            //add to gallery
            if (requestCode == CHOOSE_PHOTO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, "there was an error", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Result upload 2");
                    mMediaUri = data.getData();
                }
                int fileSize = 0;
                Log.e(TAG, "Result upload 3");
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(mMediaUri);
                    assert inputStream != null;
                    fileSize = inputStream.available();
                    Log.e(TAG, "Result upload 4");

                } catch (FileNotFoundException e) {
                    Toast.makeText(SignUpActivity.this, "Error opening image. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                } catch (IOException e) {
                    Toast.makeText(SignUpActivity.this, "Error opening image. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                } finally {
                    try {
                        if (inputStream != null)
                        inputStream.close();
                    } catch (IOException e) {/*Intentionally blank*/ }
                }

                if (fileSize >= FILE_SIZE_LIMIT) {
                    Toast.makeText(SignUpActivity.this, "The selected image is too large. Please choose another image.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(mMediaUri != null)
                  fileBytes = getByteArrayFromFile(SignUpActivity.this, mMediaUri);

                //create mediaFile
                Log.e(TAG, "creating media file to write chosen image into");
                if(mediaFile==null)
                mediaFile = createFile(MEDIA_TYPE_IMAGE);
                Log.e(TAG, "media file succesfully written");


                FileOutputStream fos = null;
                if(mediaFile != null) {
                try {
                    fos = new FileOutputStream(mediaFile);
                    fos.write(fileBytes);
                    fos.flush();
                    fos.close();
                    Log.e(TAG, "Result upload 5");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Result upload 6");
                } finally {
                    if (MyToolBox.isNetworkAvailable(this)) {
                        if (mediaFile != null) {
                            //TODO: do both here - upload file using mediaFile global variable ?
                            //uploadFile(mediaFile); ///////////////////////////////////////////////////////
                            Picasso.with(SignUpActivity.this).invalidate(mediaFile);
                            Picasso.with(SignUpActivity.this)
                                    .load(mediaFile)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .placeholder(R.drawable.wil_profile)
                                    .into(sigup_photo);
                        }
                        // mProgressDialog.dismiss();
                    } else {
                        MyToolBox.AlertMessage(this, "Oops", "Network Error. Please check your connection");
                    }
                }

            }

            }
            else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);

                /*
                 So it begins... ahhhhhhh!
                */
                try {
                    fileBytes = getByteArrayFromFile(this, mMediaUri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
                    } catch (OutOfMemoryError memoryError) {
                        Toast.makeText(SignUpActivity.this, memoryError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (bitmap != null) {
                        if(mediaFile != null) {
                        try {
                            bitmap = rotateImageIfRequired(bitmap, mMediaUri);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            FileOutputStream fos = new FileOutputStream(mediaFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                            Log.e(TAG, "media file succesfully written on Captured...");

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    }

                } finally {
                    if (MyToolBox.isNetworkAvailable(this)) {
//                    postProfilePhoto();
                        //TODO: do both here - upload file using mediaFile global variable ?
                        Log.e(TAG, "... Captured... Finally... ");
                       // uploadFile(mediaFile); ////////////////////////////////////////////
                        Picasso.with(SignUpActivity.this)
                                .load(mediaFile)
                                .into(sigup_photo);
                     //   mProgressDialog.dismiss();
                    } else {
                        MyToolBox.AlertMessage(this, "Oops", "Network Error. Please check your connection");
                    }
                }

            }

//            mProgressDialog.show();
            if (mediaFile != null ) { // captured image

            } else { //

            }

        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this,"There was an error saving your photo",Toast.LENGTH_LONG).show();
        }
       /** if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
               // goToMyLoggedInActivity();
                goToActivity(HomeActivity.class);
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();
        } **/

    }

    private File createFile(int mediaType) {
        if(isExternalStorageAvailable()){
            //storage dir
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.app_name));
            //subdir
            if(!mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdirs()){
                    return null;
                }
            }
            //file name and create the file
            //mediaFile;
            Date now = new Date();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path+"IMG_"+timestamp+".jpg");

            }
            else if (mediaType == MEDIA_TYPE_VIDEO){
                mediaFile = new File(path+"VID_"+timestamp+".mp4");
            }
            else{
                return null;
            }
            Toast.makeText(SignUpActivity.this,"File: "+Uri.fromFile(mediaFile),Toast.LENGTH_LONG).show();
            return mediaFile;
        }
        else{
            return null;
        }
    }

    private byte[] getByteArrayFromFile(Context context, Uri uri) {
        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        Log.e(TAG, uri.getScheme());

        if (uri.getScheme().equals("http")) {
//            new urlToBytes();
        }
        else if (uri.getScheme().equals("content")) {
            try {
                inStream = context.getContentResolver().openInputStream(uri);
                outStream = new ByteArrayOutputStream();

                byte[] bytesFromFile = new byte[1024 * 1024]; // buffer size (1 MB)
                assert inStream != null;
                int bytesRead = inStream.read(bytesFromFile);
                while (bytesRead != -1) {
                    outStream.write(bytesFromFile, 0, bytesRead);
                    bytesRead = inStream.read(bytesFromFile);
                }

                fileBytes = outStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    assert inStream != null;
                    inStream.close();
                    assert outStream != null;
                    outStream.close();
                } catch (IOException e) { /*( Intentionally blank */ }
            }
        }
        else {
            try {
                File file = new File(uri.getPath());
                FileInputStream fileInput = new FileInputStream(file);
                fileBytes = IOUtils.toByteArray(fileInput);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        Random r = new Random();
        int shortSide = r.nextInt(880 - 500) + 500;
        return reduceImageForUpload(fileBytes, shortSide);
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static byte[] reduceImageForUpload(byte[] imageData, int shortSide) {
        Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, shortSide);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
        byte[] reducedData = outputStream.toByteArray();
        try {
            outputStream.close();
        }
        catch (IOException e) {
            // Intentionally blank
        }

        return reducedData;
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void uploadFile(File photo) {
        mProgressDialog.setMessage("Uploading Picture ...");
         mProgressDialog.show();
        MultipartBody.Part body = null;
        try {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), photo);

            body =
                    MultipartBody.Part.createFormData("photo", photo.getName(), requestFile);
        }catch (NullPointerException e){
            Log.e(TAG, "Error Occured Here: "+e.getMessage());
        }

        final String fileName = photo.getName();
        RequestBody fileNameBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fileName);

        String token = mSharedPref.getToken();

        Call<ResponseBody> call = hollaNowApiInterface.uploadProfilePhoto(token, fileNameBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //TODO: set photo to imageview, toast success
                if (response.code() == 200) {
                    Picasso.with(SignUpActivity.this).invalidate(mediaFile);
                      Picasso.with(SignUpActivity.this)
                            .load(mediaFile)
                           .networkPolicy(NetworkPolicy.NO_CACHE)
                          .memoryPolicy(MemoryPolicy.NO_CACHE)
                          .placeholder(R.drawable.wil_profile)
                         .into(sigup_photo);
                    mProgressDialog.dismiss();
                    PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                    //Bundle args = new Bundle();
                    // args.putParcelable("data1", data);
                    //callNoteBottomSheet.setArguments(args);
                    FragmentManager fm = getSupportFragmentManager();
                    callNoteBottomSheet.show(fm,"REFS");

                    Toast.makeText(SignUpActivity.this, "Profile upload successful ", Toast.LENGTH_SHORT).show();
                    currentUser.setProfilePhoto(fileName);
                    mSharedPref.setCurrentUser(currentUser.toString());
                } else {
                    Toast.makeText(SignUpActivity.this, "Could not update profile photo", Toast.LENGTH_SHORT).show();
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Toast.makeText(SignUpActivity.this, "Error uploading photo. Try again", Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
            }
        });

    }





   /** public void phoneLogin( ) { //View view
        final Intent intent = new Intent(mContext, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


    private void showErrorActivity(final AccountKitError error) {
        final Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra(ErrorActivity.HELLO_TOKEN_ACTIVITY_ERROR_EXTRA, error);

        startActivity(intent);
    } **/



}
