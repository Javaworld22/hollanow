package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
    private ProgressDialog mProgressDialog;
    private Button mSignUp;
    private EditText mUserName;
//    private TextInputLayout mEmailLayout, mPhoneLayout;
    private String email;
    private String phone;

    private HollaNowApiInterface hollaNowApiInterface;
    private User mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent!=null) email = intent.getStringExtra("EMAIL");

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing up ...");
//        mPhone = (EditText)  findViewById(R.id.phone);
//        mEmail = (EditText)  findViewById(R.id.email_address);
        mUserName = (EditText)  findViewById(R.id.user_name);
        mName = (EditText)  findViewById(R.id.full_name);
        mPassword = (EditText)  findViewById(R.id.password);
//        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
//        mPhoneLayout = (TextInputLayout) findViewById(R.id.layout_phone);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mSignUp = (Button)  findViewById(R.id.sign_up_button);
        mSignUp.setText("Sign up as " + email);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(SignUpActivity.this)) {
                    mProgressDialog.show();
//                    if (!MyToolBox.isMinimumCharacters(mEmail.getText().toString().trim(), 2)) {
//                        mEmailLayout.setError("Your email address");
//                        mProgressDialog.dismiss();
//                    }
//                    else if (!MyToolBox.isEmailValid(mEmail.getText().toString().trim())) {
//                        mEmailLayout.setError("Invalid email address");
//                        mProgressDialog.dismiss();
//                    } else if (!MyToolBox.isMinimumCharacters(mPhone.getText().toString().trim(), 2)) {
//                        mPhoneLayout.setError("Your phone number");
//                        mProgressDialog.dismiss();
//                    }
                    if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString().trim(), 5)) {
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
                    else {
                        mUsernameLayout.setErrorEnabled(false);
                        mPasswordLayout.setErrorEnabled(false);
                        mNameLayout.setErrorEnabled(false);
                        mSignUp.setEnabled(false);
//                        mEmailLayout.setErrorEnabled(false);
//                        mPhoneLayout.setErrorEnabled(false);
//                        verifyPhoneNumber(phone);
                        createUser(email, mPassword.getText().toString(),
                                mUserName.getText().toString(), mName.getText().toString());
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    private void verifyPhoneNumber(String phone, final String token) {
        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(new AuthCallback() {
                    @Override
                    public void success(DigitsSession session, String phoneNumber) {
                        //TODO: check phone availablity?
                        mProgressDialog.show();
                        getUserDetails(token, phoneNumber);
                    }

                    @Override
                    public void failure(DigitsException error) {
                        //still go to app;
//                        goToActivity(HomeActivity.class);
                    }
                })
                .withPhoneNumber(phone);

        Digits.authenticate(authConfigBuilder.build());

    }

    private void updateUser(User user, String phoneNumber, String token) {
        user.setPhone(phoneNumber);
        Log.e(TAG, "user update: " + user.getName() + " - " + phoneNumber);
        saveCurrentUser(user, token);
        Call<User> call = hollaNowApiInterface.editUserPhone(user, token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e(TAG, response.body().getPhone());
                Toast.makeText(SignUpActivity.this, "Welcome " + mResponse.getName() , Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                goToActivity(HomeActivity.class);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Your phone was not successfully verified", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                goToActivity(HomeActivity.class);
            }
        });

    }


    private class urlToBytes extends AsyncTask<Uri, String, byte[]> {

        byte[] fileBytes;
        Uri uri;
        public urlToBytes(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            try {
                URL aURL = new URL(uri.toString());
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream inStream = conn.getInputStream();
                fileBytes = IOUtils.toByteArray(inStream);

            } catch (IOException e) {
                Log.e("IMAGE", "Error getting bitmap", e);
            }

            return fileBytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
//            createUser(bytes);
        }
    }

    private void createUser(String email, String password, String username, String name) {
        Log.e(TAG, ""+email+password+username+name);
        User user = new User(email, password, username, name);
        Call<User> call = hollaNowApiInterface.signUpUser(user);
        Log.e(TAG, "signing up");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
                    mResponse = response.body(); // containing token
                    mProgressDialog.dismiss();
                    verifyPhoneNumber(phone, response.body().getToken());
//                    Toast.makeText(SignUpActivity.this, response.body().getToken()+"", Toast.LENGTH_LONG).show();
//                    getUserDetails(response.body().getToken());
//                        saveCurrentUser(response.body());
//                    goToActivity(HomeActivity.class);
                } else {
//                    Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                    Toast.makeText(SignUpActivity.this, "User with email/username exists - "+ response.body(), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "The email is already taken ", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
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

    private void getUserDetails(final String token, final String phone) {
        Call<User> call = hollaNowApiInterface.getUserDetails(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
//                    Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
//                    Toast.makeText(SignUpActivity.this, "Welcome" + response.body().getName(), Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    user.setToken(token);
                    saveCurrentUser(user, token); //shared pref
                    updateUser(user, phone, token);
                } else {
                    Toast.makeText(SignUpActivity.this, "Oops! Could not log in. Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.e(TAG, "user details failed: " + t.getMessage());
                Toast.makeText(SignUpActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
//                mProgressDialog.dismiss();
            }
        });
    }

    private void saveCurrentUser(User user, String token) {
        HollaNowSharedPref sharedPref = new HollaNowSharedPref(this);
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
    }


    private void goToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (cls == HomeActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }
}
