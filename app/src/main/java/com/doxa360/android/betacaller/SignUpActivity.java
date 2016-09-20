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

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail,mPhone;
    private EditText mName;
    private EditText mPassword;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mNameLayout;
    private ProgressDialog mProgressDialog;
    private Button mSignUp;
    private EditText mUserName;
    private TextInputLayout mEmailLayout, mPhoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing up ...");
        mPhone = (EditText)  findViewById(R.id.phone);
        mEmail = (EditText)  findViewById(R.id.email_address);
        mUserName = (EditText)  findViewById(R.id.user_name);
        mName = (EditText)  findViewById(R.id.full_name);
        mPassword = (EditText)  findViewById(R.id.password);
        mEmailLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
        mPhoneLayout = (TextInputLayout) findViewById(R.id.layout_phone);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_username);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mSignUp = (Button)  findViewById(R.id.sign_up_button);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(SignUpActivity.this)) {
                    mProgressDialog.show();
                    if (!MyToolBox.isMinimumCharacters(mEmail.getText().toString().trim(), 2)) {
                        mEmailLayout.setError("Your email address");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isEmailValid(mEmail.getText().toString().trim())) {
                        mEmailLayout.setError("Invalid email address");
                        mProgressDialog.dismiss();
                    } else if (!MyToolBox.isMinimumCharacters(mPhone.getText().toString().trim(), 2)) {
                        mPhoneLayout.setError("Your phone number");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString().trim(), 5)) {
                        mPasswordLayout.setError("Your password should have more characters");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mUserName.getText().toString().trim(), 5)) {
                        mUsernameLayout.setError("Your username should have more characters");
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
                        mEmailLayout.setErrorEnabled(false);
                        mPhoneLayout.setErrorEnabled(false);
                        verifyPhoneNumber(mPhone.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    private void verifyPhoneNumber(String phone) {
        Digits.authenticate(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                createUser(phoneNumber);
            }

            @Override
            public void failure(DigitsException error) {

            }
        }, phone);
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

    private void createUser(String verifiedPhone) {
        Log.e("SIGN UP", "clicked begin");
        ParseUser user = new ParseUser();
        user.setEmail(mEmail.getText().toString().trim().toLowerCase());
        user.setUsername(mUserName.getText().toString().trim().toLowerCase());
        user.setPassword(mPassword.getText().toString().trim());
        user.put("phoneNumber",verifiedPhone);
        user.put("name", mName.getText().toString().trim());
//                    user.put("photo", parseFile);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mProgressDialog.dismiss();
                    Log.e("SIGN UP", "clicked");
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    if (e.getCode() ==  ParseException.USERNAME_TAKEN ) {
                        Toast.makeText(SignUpActivity.this, "Oops! The username has already been taken. Please try a different one",
                                Toast.LENGTH_LONG).show();
                    } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                        Toast.makeText(SignUpActivity.this, "Oops! This email is already registered on HollaNow.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Oops! Something went wrong. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                    mProgressDialog.dismiss();
                }
            }
        });

    }

}
