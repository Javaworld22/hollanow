package com.doxa360.android.betacaller;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.CountryCodePicker;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;

import com.doxa360.android.betacaller.model.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 8/31/2017.
 */

public class PhoneInputFragment extends BottomSheetDialogFragment { //BottomSheetDialogFragment

    private static final String TAG = PhoneInputFragment.class.getSimpleName();
private  Context mContext;
    private CountryCodePicker codePicker;
    private  EditText phone;
    private Button next;
    private final String NUMBER = "number";
    private final String COUNTRYCODE = "countrycode";
    private final String FROMPROFILEFRAGMENT = "checkposition";
    private final String COUNTRY = "country";
    private TextInputLayout mNumberLayout;
    private String phoneNumber;
    private String position;
    private String mEmail;
    private String mPassword;
    private String mUserName;
    private String mName;
    private HollaNowSharedPref mSharedPref;
    private HollaNowApiInterface hollaNowApiInterface;
    private ProgressBar mProgressBar;
    private static int countData;
    private static int countData2;
    private  User user;
    private  String photo;
    //private File mPhoto;



    public PhoneInputFragment(){
        mContext = getContext();
       // mPhoto = photo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mContext = getContext();
        mSharedPref = new HollaNowSharedPref(mContext);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        if(getArguments() != null)
       position =  getArguments().getString(FROMPROFILEFRAGMENT);
        View rootView = (View) inflater.inflate(R.layout.activity_phone_input, container, false);
        codePicker = (CountryCodePicker) rootView.findViewById(R.id.ccp_loadFullNumber);
        phone = (EditText) rootView.findViewById(R.id.editText_loadCarrierNumber);
        next = (Button) rootView.findViewById(R.id.button_next);
        mNumberLayout = (TextInputLayout) rootView.findViewById(R.id.layout_number_input);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_verify_phone) ;


        if(getArguments() != null)
            if(getArguments().getParcelable("data1") != null)
                 user = getArguments().getParcelable("data1");

         photo = null;
        if(getArguments() != null)
            if(getArguments().getString("url") != null)
                photo = getArguments().getString("url");

       // Log.e(TAG, " From SignupActivity to PhoneInputFragment : "+user.toString());
       // Log.e(TAG, " From SignupActivity to PhoneInputFragment : "+photo);
       // codePicker.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
      //      }
     //   });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               String countryCode =  codePicker.getSelectedCountryCode();
                String country = codePicker.getSelectedCountryName();
                String number = phone.getText().toString().trim();
               // Log.e(TAG, "onCreateView is here "+countryCode+" "+"555555555555555555555555");
                Log.e(TAG, "onCreateView is here "+number+" "+"00000000000000000000");
               // if (MyToolBox.isNetworkAvailable(mContext)) {
                    if ((number == null)  || number.equals(" ")  || number.length() < 7) {
                        mNumberLayout.setError("Enter valid phone number");
                    }else{
                        mNumberLayout.setErrorEnabled(false);
                        if(number.trim().startsWith("0")){
                            phoneNumber = number.substring(1);
                            Log.e(TAG, "Change to +234 in PhoneInputFragment: "+phoneNumber);
                            phoneNumber = countryCode.concat(phoneNumber);
                           // Log.e(TAG, "Change to +234: in PhoneInputfragment "+phoneNumber);
                            if(!(phoneNumber.startsWith("+")))
                                phoneNumber = "+".concat(phoneNumber);
                           // Log.e(TAG, "Change to +234: in PhoneInputfragment "+phoneNumber);
                        }

                        mSharedPref.setCountry(country);
                        mSharedPref.setPhone(phoneNumber);

                       // Intent intent = new Intent(getActivity(),PhoneVerificationActivity.class);
                       // intent.putExtra(COUNTRYCODE,countryCode);
                       // intent.putExtra(NUMBER,phoneNumber);
                       // intent.putExtra(FROMPROFILEFRAGMENT, position);
                        //intent.putExtra(COUNTRY,country);
                        //startActivity(intent);
                      //  Log.e(TAG, " Fragment : "+user.toString());
                        if(user != null) {
                            user.setPhone( phoneNumber);
                         Background_downloadContact bGround =    new Background_downloadContact(country, phoneNumber, user);
                            if(!bGround.isAlive())
                                bGround.start();
                           // else if(bGround.isInterrupted())
                             //   bGround.start();
                        } else {
                            User mUser = mSharedPref.getCurrentUser();
                            String token = mSharedPref.getToken();
                            update(mUser, phoneNumber, token, country);
                        }

                    }
               // }
            }
        });

        return rootView;

    }


    private void getUserDetails(final String token, final String mCountry, final String verifiedNumber) {
        Log.e(TAG, "Test Registration here  At getUser " );
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
        while (countData2 == 0);
        countData2 = 0;
        Call<User> call = hollaNowApiInterface.getUserDetails(token);
       // Log.e(TAG, "Response 10000016 " + token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG, "Response code getuserdatails "+response.code());
                if (response.code() == 200) {
                    countData2 = 5;
//                    Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
//                    Toast.makeText(SignUpActivity.this, "Welcome" + response.body().getName(), Toast.LENGTH_SHORT).show();
                    User user = response.body();
                   // user.setToken(token);
                    Log.e(TAG, "Response 1008 ");
                   // Log.e(TAG, "Test Registration here  At getUser  1" );
                   // Log.e(TAG, "Test Registration here  At getUser  1 "+user.getPhone() );
                   // Log.e(TAG, "Test Registration here  At getUser  1 "+user.getToken() );
                   // Log.e(TAG, "Test Registration here  At getUser  2 "+token );
                    // if(user.getToken() != null) {
                    if(user != null)
                      saveCurrentUser(user, token); //shared pref
                   // hollaToken = token;
                   // mResponse = user;
                   // if(MyToolBox.isNetworkAvailable(mContext))
                   //    updateUser(user, verifiedNumber, token, mCountry);
                  //  else {
                        countData = 3;
                     //   Toast.makeText(mContext, "Network Error. Try again ", Toast.LENGTH_SHORT).show();
                   // }
                    //  }

                    //  updateUser(user, phone, token);
                    //   goToActivity(HomeActivity.class);
                    //flag_getUserdetails = true;
                    //  return;
                    //else{
                    //   goToActivity(HomeActivity.class);
                    // }
                } else {
                    Log.e(TAG, "Response 1011 " + response.code());
                    Toast.makeText(mContext, "Oops! Could not log in. Try again", Toast.LENGTH_SHORT).show();
                   // countData = 3;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "user details failed: " + t.getMessage());
                Toast.makeText(mContext, "Network Error. Try again", Toast.LENGTH_LONG).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                return;
                //countData = 3;
            }
        });
    }

    private void saveCurrentUser(User user, String token) {
        mSharedPref.setCurrentUser(user.toString());
        mSharedPref.setToken(token);
        //  sharedPref.setPhone(phone);
        mSharedPref.setPhoneEmoji(user.getPhone());
        mSharedPref.setFlagContacts("BackUp");
        Log.e(TAG, "Response 1009 ");
    }

    private void updateUser(User user, String phoneNumber, final String token, String country) {
        final User user1 = user;
        Log.e(TAG, "Response 10000015 " + token);
        while (countData2 == 0);
        countData2 = 0;
        user.setPhone(phoneNumber);
        //Log.e(TAG, "user update: " + user.getName() + " - " + phoneNumber);
        //saveCurrentUser(user, token);
        Call<User> call = hollaNowApiInterface.editUserPhone(user, token, country);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG, "Test Registration here  At UpdateUser 1 " );
                // presentActivity = 3;
                Log.e(TAG, "Response 1010 ");
                Log.e(TAG, "Response code update user "+response.code());
                User user = response.body();
                // flag_updateUser = true;
                // user.setToken(token);
                if(user != null)
                  saveCurrentUser(user, token);
                File mediaFile = null;
                if(photo != null) {
                    mediaFile = new File(photo);
                    uploadFile(mediaFile);
                }else{
                    countData = 3;
                }
                //countData = 5;
                //  Log.e(TAG, response.body().getPhone());
                Toast.makeText(mContext, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
               // mProgressBar.dismiss();
               // goToActivity(HomeActivity.class);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(mContext, "Network Error. Try again", Toast.LENGTH_LONG).show();
                getActivity().runOnUiThread(new Runnable() {
                        @Override
                       public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                     }
                    });
                 return;
                //countData = 3;
                //goToActivity(HomeActivity.class);
            }
        });

    }



    private void goToActivity(Class<?> cls, Context ctx) {

        //  mAuth.signOut();
        Intent intent = new Intent(mContext, cls);
        if (cls == HomeActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        ctx.startActivity(intent);
    }

    // while(count < i);

    private class Background_downloadContact extends Thread {
        private String mCountry;
        private String mPhoneNumber;
        private User mUser;
        //private String mPhoto;

        public Background_downloadContact(String country, String phoneNumber, User user){
            mCountry = country;
            mPhoneNumber = phoneNumber;
            mUser = user;
           // mPhoto = photo;
        }

        @Override
        public void run() {
            Log.e(TAG, "Test Registration here  1 " );
            countData = 0;
            if(MyToolBox.isNetworkAvailable(mContext))
                createUser(mUser,mCountry,mPhoneNumber);
               //getUserDetails(mSharedPref.getToken(), mCountry, mPhoneNumber);
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Network Error ", Toast.LENGTH_SHORT).show();
                    }
                });
               // countData = 3;
            }
            Log.e(TAG, "Test Registration here  At doinginBackground 4  " );
            while (countData == 0);
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }catch(NullPointerException ex){
                Log.e(TAG, "Oops!  Nullpointer exception just occurred" );
            }
            if(countData == 3) {
                goToActivity(HomeActivity.class, mContext);
                return ;
            }else  if(countData == 1)
                return ;
           // return "Finish";
        }



    }

    private void createUser(final User user, final String mCountry, final String verifiedNumber) {
       // Log.e(TAG, ""+email+password+username+name);
        mSharedPref.setCurrentUser(user.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
        Call<User> call = hollaNowApiInterface.signUpUser(user);
        Log.e(TAG, "signing up");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    countData2 = 3;
                    Log.e(TAG, "Test Registration here  CreateUser  " );
                    // Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
                    getUserDetails(response.body().getToken(), mCountry, verifiedNumber);
                   // Log.e(TAG, "response: " + response.body());
                   // mResponse = response.body(); // containing token
                    // mResponse.setName(name);
                    //  flag_createuser = true;
                   // Log.e(TAG, "Response 1001");

                   // User user = response.body();
                    //if(user != null)
                     //   saveCurrentUser(user, user.getToken());
                  //  File mediaFile = null;
                   // if(photo != null) {
                    //    mediaFile = new File(photo);
                    //    uploadFile(mediaFile);
                  //  }else{
                  //      countData = 3;
                  //  }

                     //   PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                        //Bundle args = new Bundle();
                        // args.putParcelable("data1", data);
                        //callNoteBottomSheet.setArguments(args);
                       // FragmentManager fm = getSupportFragmentManager();
                      //  callNoteBottomSheet.show(fm,"REFS");
                  //  }


                    Log.e(TAG, "Response 1000043");

                    // if(phone != null) {

                    // }
                } else {
//                    Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                    Log.e(TAG, "Response 1002");
                    Toast.makeText(mContext, "User with email/username exists - "+ response.body(), Toast.LENGTH_LONG).show();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (t instanceof IOException) {
                    Log.e(TAG, "Response 1002 " + t.getMessage());
                    Toast.makeText(mContext, "Data Error ", Toast.LENGTH_LONG).show();
                   // countData2 = 1;
                    // mProgressDialog.dismiss();
                } else if (t instanceof SocketTimeoutException) {
                    Log.e(TAG, "Response 1003 " + t.getMessage());
                    Toast.makeText(mContext, "Timeout!!", Toast.LENGTH_LONG).show();
                    //countData2 = 1;
                    //  mProgressDialog.dismiss();
                } else if (t instanceof NetworkErrorException) {
                    Log.e(TAG, "Response 1003 " + t.getMessage());
                    Toast.makeText(mContext, "Oops Network Error!", Toast.LENGTH_LONG).show();
                    //countData2 = 1;
                    // mProgressDialog.dismiss();
                }else if(t instanceof  IllegalStateException){
                    Log.e(TAG, "Response 1004 " + t.getMessage());
                    Toast.makeText(mContext, "User with email/username already been used", Toast.LENGTH_LONG).show();
                }

                else {
                    Toast.makeText(mContext, "Wrong username format!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response 1005 " + t.getMessage());
                   // countData2 = 1;
                    //   mProgressDialog.dismiss();}
                    // mSignUp.setEnabled(true);
                }
                if(getActivity() != null)
                  getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                countData = 1;
                return;
            }
        });


    }


    private void uploadFile(File photo) {
       // mProgressDialog.setMessage("Uploading Picture ...");
       // mProgressDialog.show();
        while (countData2 == 0);
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
                   // Picasso.with(mContext).invalidate(mediaFile);
                   // Picasso.with(mContext)
                   //         .load(mediaFile)
                   //         .networkPolicy(NetworkPolicy.NO_CACHE)
                   //         .memoryPolicy(MemoryPolicy.NO_CACHE)
                    //        .placeholder(R.drawable.wil_profile)
                    //        .into(sigup_photo);
                    countData = 3;
                   // mProgressDialog.dismiss();
                   // PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                    //Bundle args = new Bundle();
                    // args.putParcelable("data1", data);
                    //callNoteBottomSheet.setArguments(args);
                   // FragmentManager fm = getSupportFragmentManager();
                    //callNoteBottomSheet.show(fm,"REFS");

                   // Toast.makeText(SignUpActivity.this, "Profile upload successful ", Toast.LENGTH_SHORT).show();
                    User currentUser = mSharedPref.getCurrentUser();
                    currentUser.setProfilePhoto(fileName);
                    mSharedPref.setCurrentUser(currentUser.toString());
                } //else {
                  //  Toast.makeText(SignUpActivity.this, "Could not update profile photo", Toast.LENGTH_SHORT).show();
               // }

              //  mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Toast.makeText(mContext, "Error uploading photo. Try again", Toast.LENGTH_LONG).show();
                 countData = 3;
               // mProgressDialog.dismiss();
            }
        });

    }


    private void update(User user, String phoneNumber, final String token, String country) {
        final User user1 = user;
        user.setPhone(phoneNumber);
        Log.e(TAG, "user update: " + user.getName() + " - " + phoneNumber);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
        //saveCurrentUser(user, token);
        Call<User> call = hollaNowApiInterface.editUserPhone(user, token, country);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG, "Test Registration here  At UpdateUser 1 " );
                //Log.e(TAG, "Response code update "+response.code());
                User user = response.body();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                if(user != null)
                 saveCurrentUser(user, token);
                File mediaFile = null;

               // Log.e(TAG, "Test Registration here  At updateUser  1 "+response.code()  );
               // Log.e(TAG, "Test Registration here  At updateUser  1 "+phoneNumber );
               // Log.e(TAG, "Test Registration here  At updateUser  1 "+user.getToken() );
               // Log.e(TAG, "Test Registration here  At updateUser  2 "+token );

                //Toast.makeText(mContext, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
               // Intent intent = new Intent(mContext, ProfileActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Intent intent = new Intent(HomeActivity.this, PaystackActivity.class);
               // startActivity(intent);

                // Refresh fragment

                dismiss();
                FragmentManager manager = getFragmentManager();
                Fragment frg = manager.findFragmentByTag("PROFILE_FRAGMENT");
                FragmentTransaction ft = manager.beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                //int count = manager.getBackStackEntryCount();
                //if(count > 0)
                 //   manager.popBackStackImmediate("REFS",FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(mContext, "Network Error. Try again", Toast.LENGTH_LONG).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                return;

            }
        });

    }

    @Override
    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
    }




}
