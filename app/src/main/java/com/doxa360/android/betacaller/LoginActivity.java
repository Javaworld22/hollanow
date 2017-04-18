package com.doxa360.android.betacaller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();
    private EditText mUsername;
    private EditText mPassword;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private Button mSignIn;
    private TextView mSignUp;
    private ProgressDialog mProgressDialog;
    private String email;
    private HollaNowApiInterface hollaNowApiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent!=null) email = intent.getStringExtra("EMAIL");

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Logging in ...");
//        mUsername = (EditText)  findViewById(R.id.email_address);
        mPassword = (EditText)  findViewById(R.id.password);
//        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mSignIn = (Button)  findViewById(R.id.sign_in_button);
        mSignUp = (TextView)  findViewById(R.id.sign_up);
        TextView mForgotPassword = (TextView)  findViewById(R.id.forgot_password);

        mForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        mSignIn.setText("Login as \'" + email +"\'");

//        mForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fm =  getSupportFragmentManager();
//                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
//                forgotPasswordFragment.show(fm, "Forgot Password");            }
//        });



        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(LoginActivity.this)) {
                    mProgressDialog.show();
                    if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString(), 2)) {
                        mPasswordLayout.setError("Your password");
                        mProgressDialog.dismiss();
                    }
                    else {
                        mPasswordLayout.setErrorEnabled(false);
                        loginUser(email, mPassword.getText().toString());
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(CreateAccountActivity.class);
            }
        });

    }


    private void loginUser(String email,String password) {
        User user = new User(email, password);
        Call<User> call = hollaNowApiInterface.signInUser(user);
        //email login
        if (MyToolBox.isEmailValid(email)) {
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
//                        Toast.makeText(LoginActivity.this, response.body().getToken()+"", Toast.LENGTH_LONG).show();
                        getUserDetails(response.body().getToken());
                    } else {
                        Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                        Toast.makeText(LoginActivity.this, "Wrong Email/Password", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
//                    Log.e(TAG, "failed: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });

        }
        //username
//        else {
//            ParseUser.logInInBackground(email,
//                    mPassword.getText().toString().trim(), new LogInCallback() {
//                        @Override
//                        public void done(ParseUser user, ParseException e) {
//                            if (e == null) {
//                                mProgressDialog.dismiss();
//                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            } else if (e.getCode() == 100) {
//                                Toast.makeText(LoginActivity.this, "Connection Error. Try Again", Toast.LENGTH_LONG).show();
//                                mProgressDialog.dismiss();
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
//                                mProgressDialog.dismiss();
//                                Log.e("Parse Error", e.getMessage());
//                            }
//                        }
//                    });
//
//        }
    }

    private void getUserDetails(final String token) {
        Call<User> call = hollaNowApiInterface.getUserDetails(token);
        Log.e("TOKEN: ", token+"");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "response: " + response.body().getEmail() + " code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Welcome " + response.body().getName(), Toast.LENGTH_SHORT).show();
                    User user = response.body();
                    user.setToken(token);
                    Log.e("Logged in/saved user: ", user.getName()+"");
                    saveCurrentUser(user, token);
                    goToActivity(HomeActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "Oops! Could not log in. Try again", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.e(TAG, "user details failed: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void saveCurrentUser(User user, String token) {
        HollaNowSharedPref sharedPref = new HollaNowSharedPref(this);
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
        Log.e(TAG, "Saved current user: " + user.toString());
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
