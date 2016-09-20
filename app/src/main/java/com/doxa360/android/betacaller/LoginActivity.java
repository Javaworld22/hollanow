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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();
    private EditText mUsername;
    private EditText mPassword;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;
    private Button mSignIn;
    private TextView mSignUp;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing in ...");
        mUsername = (EditText)  findViewById(R.id.email_address);
        mPassword = (EditText)  findViewById(R.id.password);
        mUsernameLayout = (TextInputLayout) findViewById(R.id.layout_email_address);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.layout_password);
        mSignIn = (Button)  findViewById(R.id.sign_in_button);
        mSignUp = (TextView)  findViewById(R.id.sign_up);
        TextView mForgotPassword = (TextView)  findViewById(R.id.forgot_password);

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm =  getSupportFragmentManager();
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                forgotPasswordFragment.show(fm, "Forgot Password");            }
        });



        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(LoginActivity.this)) {
                    mProgressDialog.show();
                    if (!MyToolBox.isMinimumCharacters(mUsername.getText().toString().trim(), 2)) {
                        mUsernameLayout.setError("Your username or email address");
                        mProgressDialog.dismiss();
                    }
//                    else if (!MyToolBox.isEmailValid(mUsername.getText().toString().trim())) {
//                        mUsernameLayout.setError("Invalid email address");
//                        mProgressDialog.dismiss();
//                    }
                    else if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString().trim(), 2)) {
                        mPasswordLayout.setError("Your password");
                        mProgressDialog.dismiss();
                    }
                    else {
                        mUsernameLayout.setErrorEnabled(false);
                        mPasswordLayout.setErrorEnabled(false);
                        loginUser();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(SignUpActivity.class);
            }
        });

    }

    private void loginUser() {
        String access = null;
        if (MyToolBox.isEmailValid(mUsername.getText().toString().trim().toLowerCase())) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", mUsername.getText().toString().trim().toLowerCase());
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        ParseUser.logInInBackground(object.getUsername(),
                                mPassword.getText().toString().trim(), new LogInCallback() {
                                    @Override
                                    public void done(ParseUser user, ParseException e) {
                                        if (e == null) {
                                            mProgressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else if (e.getCode() == 100) {
                                            Toast.makeText(LoginActivity.this, "Connection Error. Try Again", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();
                                            Log.e("Parse Error", e.getMessage());
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
        else {
            ParseUser.logInInBackground(mUsername.getText().toString().trim().toLowerCase(),
                    mPassword.getText().toString().trim(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (e.getCode() == 100) {
                                Toast.makeText(LoginActivity.this, "Connection Error. Try Again", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                                Log.e("Parse Error", e.getMessage());
                            }
                        }
                    });

        }
    }

    private void goToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
//        if (cls = HomeActivity.class) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        }
        startActivity(intent);
    }
}
