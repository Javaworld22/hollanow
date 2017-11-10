package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.*;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import android.support.v4.app.FragmentManager;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    //    private EditText mEmail,mPhone;
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
    //    private TextInputLayout mEmailLayout, mPhoneLayout;
    private String email;

    private static String token1;

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





        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing up ...");
//        mPhone = (EditText)  findViewById(R.id.phone);
//        mEmail = (EditText)  findViewById(R.id.email_address);
        mEmail = (EditText)  findViewById(R.id.email_signup);
        mUserName = (EditText)  findViewById(R.id.user_name);
        mName = (EditText)  findViewById(R.id.full_name);
        mPassword = (EditText)  findViewById(R.id.password);
//        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
//        mPhoneLayout = (TextInputLayout) findViewById(R.id.layout_phone);
        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_signup);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mSignUp = (Button)  findViewById(R.id.sign_up_button);
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
                        mSignUp.setEnabled(false);
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
                        FirebaseAuth.getInstance().signOut();
                            createUser(mEmail.getText().toString(), mPassword.getText().toString(),
                                    mUserName.getText().toString(), mName.getText().toString());
                     //   PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                       // FragmentManager fm = getSupportFragmentManager();
                     //   Bundle args = new Bundle();
                      //  args.putString("EMAIL", mEmail.getText().toString());
                       // args.putString("PASSWORD", mPassword.getText().toString());
                       // args.putString("USERNAME", mUserName.getText().toString());
                       // args.putString("NAME", mName.getText().toString());
                      //  callNoteBottomSheet.setArguments(args);
                       // callNoteBottomSheet.show(fm,"REFS");


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
                    Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
                    mResponse = response.body(); // containing token
                    mResponse.setName(name);
                    //  flag_createuser = true;
                    Log.e(TAG, "Response 1001");
                    mProgressDialog.dismiss();
                    saveCurrentUser(mResponse, mResponse.getToken());


                     PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                     FragmentManager fm = getSupportFragmentManager();
                     callNoteBottomSheet.show(fm,"REFS");


//                    Toast.makeText(SignUpActivity.this, response.body().getToken()+"", Toast.LENGTH_LONG).show();
//                    getUserDetails(response.body().getToken());
//                        saveCurrentUser(response.body());
                    Log.e(TAG, "Response 1000043");

                    // if(phone != null) {

                    // }
                } else {
//                    Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                    Log.e(TAG, "Response 1002");
                    Toast.makeText(SignUpActivity.this, "User with email/username exists - "+ response.body(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if(t instanceof IOException){
                    Log.e(TAG, "Response 1002 "+t.getMessage());
                    Toast.makeText(SignUpActivity.this, "Oops Network Error ", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
                else if(t instanceof SocketTimeoutException){
                    Log.e(TAG, "Response 1003 "+t.getMessage() );
                    Toast.makeText(SignUpActivity.this, "Timeout!!", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
                else{ Toast.makeText(SignUpActivity.this, "Oops Network Error ", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response 1004 "+t.getMessage());
                    mProgressDialog.dismiss();}
            }
        });



//        user.signUpInBackground(new SignUpCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    mProgressDialog.dismiss();
//                    Log.e("SIGN UP", "clicked");
//                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } else {
//                    if (e.getCode() ==  ParseException.USERNAME_TAKEN ) {
//                        Toast.makeText(SignUpActivity.this, "Oops! The username has already been taken. Please try a different one",
//                                Toast.LENGTH_LONG).show();
//                    } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
//                        Toast.makeText(SignUpActivity.this, "Oops! This email is already registered on HollaNow.",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(SignUpActivity.this, "Oops! Something went wrong. Please try again",
//                                Toast.LENGTH_LONG).show();
//                    }
//                    mProgressDialog.dismiss();
//                }
//            }
//        });

    }








    private void saveCurrentUser(User user, String token) {
        HollaNowSharedPref sharedPref = new HollaNowSharedPref(this);
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
        sharedPref.setPhone(phone);
        sharedPref.setPhoneEmoji(user.getPhone());
        sharedPref.setFlagContacts("BackUp");
        Log.e(TAG, "Response 1009 ");
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
}
