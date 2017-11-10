package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.KeyboardView;
import com.doxa360.android.betacaller.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.CountDownTimer;

/**
 * Created by user on 9/2/2017.
 */

public class PhoneVerificationActivity extends AppCompatActivity {

    private TextView firstText, secondText, thirdText, fourthText, fifthText, sixText;
    private TextView infoText;
    private KeyboardView keyboardView;
    private static int countNumber;
    private Button buttonSubmit;
    private Button buttonResend;
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;
    private Intent intent;
    private String phoneNumber;
    private static final String TAG = PhoneVerificationActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String number;
    private String mVerificationId;
    private String mCountry;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private PhoneAuthCredential credential;
    private static int count;
    private static int counter = 0;
    private static int counter_for_clk;
    private PhoneAuthCredential mCredential;

    private String checkPosition;

    private Context mContext;

    private HollaNowApiInterface hollaNowApiInterface;

    private User mResponse;
    private String verifiedNumber;
    private String hollaToken;

    private HollaNowSharedPref sharedPref;

    private FirebaseUser firebaseUser;

    private final String NUMBER = "number";
    private final String POSITION = "checkposition";

    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_phone_verification);
        //  mContext = getApplicationContext();
        setSupportActionBar(toolbar);
        sharedPref = new HollaNowSharedPref(this);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        currentUser = sharedPref.getCurrentUser();

        intent = getIntent();
        mProgressDialog = new ProgressDialog(this, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        mProgressDialog.setMessage("Signing up ...");
       // final String countrycode = intent.getStringExtra("countrycode");
        number = intent.getStringExtra("number");
        checkPosition =   intent.getStringExtra("checkposition");
        mCountry = intent.getStringExtra("country");
        sharedPref.setCountry(mCountry);
        if(checkPosition == null)
            checkPosition = " ";
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "Firebase Authentication Success is here 000000000000");
                mCredential = phoneAuthCredential;
             //   mProgressBar.setVisibility(View.GONE);
               // buttonResend.setVisibility(View.VISIBLE);
                Log.e(TAG, "Firebase Authentication Success is here for Firebase " + mCredential.getSmsCode() + " " + mCredential.getProvider());
                // if(mCredential.getSmsCode() != null)
                //  keyboardView.setInputText(mCredential.getSmsCode(), mContext);
                if (mCredential.getSmsCode() == null) {
                    //   keyboardView = new KeyboardView(mContext);

                    signInWithPhoneAuthCredential(mCredential);

                }
                Log.e(TAG, "Firebase Authentication " + mCredential.getSmsCode());
                //  AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                //  builder.setTitle("Alert!");
                // builder.setMessage("Please, kindly check your phone SMS. If no 6-digit code sent. Hence, input this "+mCredential.getSmsCode());
                // builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                //     public void onClick(DialogInterface dialog, int id){

                //     }
                //  });
                //  builder.setNegativeButton("Cancel", null);
                //   builder.show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "Firebase Authentication Failure is here 11111111111111111111");
                Log.e(TAG, "Error Firebase " + e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.e(TAG, "Firebase Authentication onCodeSent is here 222222222222222222 ");
                mVerificationId = verificationId;
                mToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                Log.e(TAG, "Firebase Authentication onCodeAutoRetrievalTimeOut is here 99999999999999999999 " + verificationId);
                mVerificationId = verificationId;
                mProgressBar.setVisibility(View.GONE);
                buttonResend.setVisibility(View.VISIBLE);
            }
        };


        firstText = (TextView) findViewById(R.id.verify1);
        secondText = (TextView) findViewById(R.id.verify2);
        thirdText = (TextView) findViewById(R.id.verify3);
        fourthText = (TextView) findViewById(R.id.verify4);
        fifthText = (TextView) findViewById(R.id.verify5);
        sixText = (TextView) findViewById(R.id.verify6);
        buttonSubmit = (Button) findViewById(R.id.submit_phone_verify);
        buttonResend = (Button) findViewById(R.id.resend_verification);
        keyboardView = (KeyboardView) findViewById(R.id.keyboardtext);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_verification);
        infoText = (TextView) findViewById(R.id.info1);
        //keyboardView = new KeyboardView(this);
        firstText.setText(" ");
        secondText.setText(" ");
        thirdText.setText(" ");
        fourthText.setText(" ");
        fifthText.setText(" ");
        sixText.setText(" ");

        /**   if(number.trim().startsWith("0")){
         phoneNumber = number.substring(1);
         Log.e(TAG, "Change to +234 rr: "+phoneNumber);
         phoneNumber = countrycode.concat(phoneNumber);
         Log.e(TAG, "Change to +234: "+phoneNumber);
         } **/


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // count = 2;
                String code = keyboardView.getInputText().toString().trim();
                mProgressDialog.show();
                // authenticateNumber(code);
               // if (mVerificationId == null && mCredential.getSmsCode() == null)
              //      signInWithPhoneAuthCredential(credential);
              //  else if (mVerificationId != null)
                    authenticateNumber(code);
                // FirebaseAuth.getInstance().signOut();
                /** if(number.trim().startsWith("0")){
                 phoneNumber = number.substring(1);
                 Log.e(TAG, "Change to +234 rr: "+phoneNumber);
                 phoneNumber = countrycode.concat(phoneNumber);
                 Log.e(TAG, "Change to +234: "+phoneNumber);
                 } **/
                Log.e(TAG, "Keyboardview in buttonSubmit one clicked here " + code);
            }
        });

        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // count = 1;
                mProgressBar.setVisibility(View.VISIBLE);
                buttonResend.setVisibility(View.GONE);
                resendCode(mToken, number);

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "OnStart from PhoneVerificationActivity   " + number);
        if(sharedPref.getFlagPhoneAuth())
                  phoneVerify(number);
        sharedPref.setFlagPhoneAuth(false);

    }

    private void resendCode(PhoneAuthProvider.ForceResendingToken token, String phone) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone, 10, TimeUnit.SECONDS,
                    this, mCallbacks, token
            );
        }catch(IllegalArgumentException exc){
            Log.e(TAG, "IllegalArgument Exception  " + phone + " ");
        }
       // removeUser();
        Log.e(TAG, "Null MToken not mVerification  " + phone + " ");


      //  final String body = SmsListener.body;
       // final String no = SmsListener.mobile;
        mProgressBar.setVisibility(View.VISIBLE);
        buttonResend.setVisibility(View.GONE);
        new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinishes) {
                counter_for_clk++;
                final String body = SmsListener.body;
                final String no = SmsListener.mobile;
                if( body != null && no != null){
                    mProgressBar.setVisibility(View.GONE);
                    buttonResend.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Message ReSent  " +body+" "+no );
                }
                // infoText.setText(verifiedNumber + " is a valid number. " + counter);
            }

            public void onFinish() {
                //counter = 0;
                final String body = SmsListener.body;
                final String no = SmsListener.mobile;
                mProgressBar.setVisibility(View.GONE);
                buttonResend.setVisibility(View.VISIBLE);
                // body = SmsListener.body;
              //  final String no = SmsListener.mobile;
                Log.e(TAG, "Message ReSent  @ the onFinish" +body+" "+no );
            }
        }.start();

        proceedAfter10Munites();


    }

    private void authenticateNumber(String mCode) {
        try {
            credential = PhoneAuthProvider.getCredential(mVerificationId, mCode);
            signInWithPhoneAuthCredential(credential);
        }catch(Exception ee){
            mProgressDialog.dismiss();
            Toast.makeText(PhoneVerificationActivity.this, "Invalid code ", Toast.LENGTH_SHORT).show();
        }
    }

    private void phoneVerify(String phoneNumber) {
        //  count++;
        // int count1 = count % 2;
        Log.e(TAG, "Phone Authentication start  " + phoneNumber);
        // if(mToken == null || mVerificationId == null){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2348154965498", 7, TimeUnit.SECONDS,
                this, mCallbacks
        );
        Log.e(TAG, "Null MToken and mVerification  " + phoneNumber);
        //  }

        mProgressBar.setVisibility(View.VISIBLE);
        buttonResend.setVisibility(View.GONE);
        new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinishes) {
                final String body = SmsListener.body;
                final String no = SmsListener.mobile;
                counter_for_clk++;
                if( body != null && no != null){
                    mProgressBar.setVisibility(View.GONE);
                    buttonResend.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Message Sent  " +body+" "+no );
                }
               // infoText.setText(verifiedNumber + " is a valid number. " + counter);
            }

            public void onFinish() {
                //counter = 0;
                final String body = SmsListener.body;
                final String no = SmsListener.mobile;
                mProgressBar.setVisibility(View.GONE);
                buttonResend.setVisibility(View.VISIBLE);
                Log.e(TAG, "Message Sent @ onFinish  " +body+" "+no );

            }
        }.start();

    }


    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                verifiedNumber = task.getResult().getUser().getPhoneNumber();
                                Log.e(TAG, "successful PhoneVerificationActivity1111111   " + verifiedNumber);
                                if (verifiedNumber.equals(number)) {
                                    infoText.setText(verifiedNumber + " is a valid number. ");
                                    firebaseUser = task.getResult().getUser();
                                    Log.e(TAG, "successful PhoneVerificationActivity44444444777   " + verifiedNumber);
                                    Toast.makeText(PhoneVerificationActivity.this, verifiedNumber + " valid number", Toast.LENGTH_SHORT).show();
                                    /**           new CountDownTimer(9000, 1000) {
                                     public void onTick(long millisUntilFinishes) {
                                     counter--;
                                     infoText.setText(verifiedNumber + " is a valid number. " + counter);
                                     }

                                     public void onFinish() {
                                     counter = 0;
                                     sharedPref.setPhoneEmoji(verifiedNumber);
                                     getUserDetails(sharedPref.getToken());
                                     //  updateUser(mResponse, verifiedNumber, hollaToken);
                                     }
                                     }.start(); **/
                                    // firebaseUser = task.getResult().getUser();
                                    // removeUser();
                                    // updatePhone();
                                    sharedPref.setPhoneEmoji(verifiedNumber);

                                    if (!(checkPosition.equals("ProfileFragment")))
                                        getUserDetails(sharedPref.getToken());

                                    else if (checkPosition.equals("ProfileFragment")) {
                                        mProgressDialog.setMessage("Updating...");
                                        mProgressDialog.show();
                                        //   Bundle args = new Bundle();
                                        //  args.putString("checkposition", "ProfileFragmentRecturn");
                                        //  args.putString("phoneposition", verifiedNumber);
                                        //  profileFragment.setArguments(args);
                                        //  FragmentTransaction ft = fm.beginTransaction()
                                        //          .replace(R.id.fragment, profileFragment, "PROFILE_FRAGMENT1");
                                        //  ft.commit();
                                        updatePhoneNumber(verifiedNumber, mCountry);

                                        //  Intent intent = new Intent(PhoneVerificationActivity.this, ProfileActivity.class);
                                        // intent.putExtra(NUMBER,verifiedNumber);
                                        // intent.putExtra(POSITION,"ProfileFragmentRecturn");
                                        //startActivity(intent);
                                    }

                                }
                                Log.e(TAG, "successful PhoneVerificationActivity2222222222   " + verifiedNumber);
                                mProgressDialog.dismiss();
                                if (count == 2) {
                                    mProgressDialog.show();
                                    sharedPref.setPhoneEmoji(verifiedNumber);
                                    getUserDetails(sharedPref.getToken());
                                    // updateUser(mResponse, verifiedNumber, hollaToken);
                                }
                                //  updateUser( User user, String phoneNumber, final String token)
                                //  goToActivity(HomeActivity.class);

                            } else {
                                Log.e(TAG, "Failure PhoneVerificationActivity   " + task.getException());
                                Toast.makeText(PhoneVerificationActivity.this, "Invalid Phone number. Try with another valid number.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private void goToActivity(Class<?> cls) {

      //  mAuth.signOut();
        Intent intent = new Intent(this, cls);
        if (cls == HomeActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }

    private void getUserDetails(final String token) {
       // mProgressDialog.show();
        Call<User> call = hollaNowApiInterface.getUserDetails(token);
        Log.e(TAG, "Response 10000016 " + token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
//                    Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
//                    Toast.makeText(SignUpActivity.this, "Welcome" + response.body().getName(), Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    user.setToken(token);
                    Log.e(TAG, "Response 1008 ");
                    // if(user.getToken() != null) {
                    saveCurrentUser(user, token); //shared pref
                    hollaToken = token;
                    mResponse = user;
                    updateUser(user, verifiedNumber, user.getToken(), mCountry);
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
                    Toast.makeText(PhoneVerificationActivity.this, "Oops! Could not log in. Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "user details failed: " + t.getMessage());
                Toast.makeText(PhoneVerificationActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
//                mProgressDialog.dismiss();
            }
        });
    }

    private void saveCurrentUser(User user, String token) {
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
        //  sharedPref.setPhone(phone);
        sharedPref.setPhoneEmoji(user.getPhone());
        sharedPref.setFlagContacts("BackUp");
        Log.e(TAG, "Response 1009 ");
    }

    private void updateUser(User user, String phoneNumber, final String token, String country) {
        final User user1 = user;
        Log.e(TAG, "Response 10000015 " + token);
        //String token1 = token;
        user.setPhone(phoneNumber);
        Log.e(TAG, "user update: " + user.getName() + " - " + phoneNumber);
        //saveCurrentUser(user, token);
        Call<User> call = hollaNowApiInterface.editUserPhone(user, token, country);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // presentActivity = 3;
                Log.e(TAG, "Response 1010 ");
                User user = response.body();
                // flag_updateUser = true;
                // user.setToken(token);
                saveCurrentUser(user, token);
                //  Log.e(TAG, response.body().getPhone());
                Toast.makeText(PhoneVerificationActivity.this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                goToActivity(HomeActivity.class);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PhoneVerificationActivity.this, "Your phone was not successfully verified", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                goToActivity(HomeActivity.class);
            }
        });

    }


    private void updatePhoneNumber(final String phoneNumber, String country) {
       // mPhone.setText("updating...");
        currentUser.setPhone(phoneNumber);
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<User> call = hollaNowApiInterface.editUserPhone(currentUser, sharedPref.getToken(),country);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    currentUser.setPhone(response.body().getPhone());
                    sharedPref.setCurrentUser(currentUser.toString());
                    sharedPref.setPhoneEmoji(response.body().getPhone());
                   // mPhone.setText(response.body().getPhone());
                    Toast.makeText(PhoneVerificationActivity.this, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhoneVerificationActivity.this, ProfileActivity.class);
                    intent.putExtra(NUMBER,verifiedNumber);
                    intent.putExtra(POSITION,"ProfileFragmentReturn");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                } else {
                  //  mPhone.setText(phoneIntent);
                    mProgressDialog.dismiss();
                    infoText.setText(phoneNumber+" not updated. please, re-login again. ");
                    Toast.makeText(PhoneVerificationActivity.this, "Phone number not updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressDialog.dismiss();
              //  mPhone.setText(phoneIntent);
                infoText.setText(phoneNumber+" not updated. Network error. ");
                Toast.makeText(PhoneVerificationActivity.this, "Network error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeUser() {
        if (firebaseUser != null) {
            firebaseUser.reauthenticate(mCredential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.e(TAG, "Deleting account is successful. Delete is complete ffffffffffff  ");
                            updatePhone();
                           // FirebaseAuth.getInstance().signOut();
                         /**   firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "Deleting account is successful. Delete is complete ggggggggggggggg  ");
                                    }
                                }
                            }); **/
                        }
                    });
        }

//firebaseUser.reload();
    }

    private  void updatePhone(){
        firebaseUser.updatePhoneNumber(mCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Update account is successful. Update is complete wwwwwwwwwwwwww  ");
                        }else{
                            Log.e(TAG, "Failure Updating   " + task.getException());
                        }
                    }


                });
        firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Reload account is successful. Reload is complete sssssssssssssss  ");
                }else {
                    Log.e(TAG, "Failure reloading   " + task.getException());
                }
            }
        });


    }

    private void proceedAfter10Munites(){
        if (count == 0){
            new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinishes) {
                   // counter_for_clk++;
                    // final String body = SmsListener.body;

                }

                public void onFinish() {
                    verifiedNumber = number;
                    counter = 5;
                    count = 0;
                    sharedPref.setPhoneEmoji(verifiedNumber);

                    if (!(checkPosition.equals("ProfileFragment"))) {
                       // mProgressDialog.show();;
                        getUserDetails(sharedPref.getToken());
                    }

                    else if (checkPosition.equals("ProfileFragment")) {
                        mProgressDialog.setMessage("Updating...");
                        mProgressDialog.show();
                        updatePhoneNumber(verifiedNumber,mCountry);
                    }

                }
            }.start();
        }

        ++count;


    }



}