package com.doxa360.android.betacaller;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFacebook extends Fragment {

    ImageView mProfilePic;
    TextView mFullname;
    EditText mPhone, mEmail, mUsername, mPassword;
    String id, email, firstname, lastname, fullname, phoneNumber, profilePicUrl;
    private Context mContext;
    private Button mSignUp;
    private ProgressDialog mProgressDialog;

    public ConfirmFacebook() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String facebookJson = args.getString(CreateAccountFragment.FACEBOOK_JSON);
        try {
            JSONObject jsonObject = new JSONObject(facebookJson);
            id = jsonObject.getString("id");
            email = jsonObject.getString("email");
//            firstname = jsonObject.getString("first_name");
//            lastname = jsonObject.getString("last_name");
            fullname = jsonObject.getString("name");
//            phoneNumber = jsonObject.getString("phone");
            profilePicUrl = null;
            if (jsonObject.has("picture")) {
                profilePicUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                // set profile image to imageview using Picasso or Native methods
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_facebook, container, false);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Verifying phone number ...");

        mProfilePic = (ImageView) rootView.findViewById(R.id.profile_photo);
        mPhone = (EditText)  rootView.findViewById(R.id.phone);
        mEmail = (EditText)  rootView.findViewById(R.id.email_address);
        mUsername = (EditText)  rootView.findViewById(R.id.user_name);
        mPassword = (EditText)  rootView.findViewById(R.id.password);
        mFullname = (TextView) rootView.findViewById(R.id.full_name);
        mSignUp = (Button) rootView.findViewById(R.id.sign_up_button);

        TelephonyManager tMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        if (mPhoneNumber!=null) {
            mPhone.setText(mPhoneNumber);
        }

        mFullname.setText(fullname);
        mEmail.setText(email);
        if (profilePicUrl!=null) {
            Picasso.with(mContext)
                    .load(profilePicUrl)
                    .resize(75, 75)
                    .centerCrop()
                    .into(mProfilePic);
        }

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(mContext)) {
                    mProgressDialog.show();
                    if (!MyToolBox.isMinimumCharacters(mEmail.getText().toString().trim(), 2)) {
                        mEmail.setError("Your email address");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isEmailValid(mEmail.getText().toString().trim())) {
                        mEmail.setError("Invalid email address");
                        mProgressDialog.dismiss();
                    } else if (!MyToolBox.isMinimumCharacters(mPhone.getText().toString().trim(), 2)) {
                        mPhone.setError("Your phone number");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString().trim(), 5)) {
                        mPassword.setError("Your password should have more characters");
                        mProgressDialog.dismiss();
                    }
                    else if (!MyToolBox.isMinimumCharacters(mUsername.getText().toString().trim(), 5)) {
                        mUsername.setError("Your username should have more characters");
                        mProgressDialog.dismiss();
                    }
//                    else if (!MyToolBox.isMinimumCharacters(mFullname.getText().toString().trim(), 5)) {
//                        mFullname.setError("Your full name should have more characters");
//                        mProgressDialog.dismiss();
//                    }
                    else {
                        verifyPhoneNumber(mPhone.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(mContext, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

    private void verifyPhoneNumber(String phone) {
        if (Digits.getActiveSession()!=null) {
            //TODO: review the necessity of this session thingy.
            Digits.clearActiveSession();
        }

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(new AuthCallback() {
                    @Override
                    public void success(DigitsSession session, String phoneNumber) {
                        if (profilePicUrl!=null) {
                            new urlToBytes(Uri.parse(profilePicUrl), phoneNumber).execute();
                        } else {
                            createUser(phoneNumber, null);
                        }
                    }

                    @Override
                    public void failure(DigitsException error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .withPhoneNumber(phone);

        Digits.authenticate(authConfigBuilder.build());


    }

    private class urlToBytes extends AsyncTask<Uri, String, byte[]> {

        byte[] fileBytes;
        Uri uri;
        String phone;
        public urlToBytes(Uri uri, String phone) {
            this.uri = uri;
            this.phone = phone;

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
            Log.e("CONFIRM", phone+bytes.length);
            createUser(bytes, phone);
        }
    }

    private void createUser(byte[] bytes, final String phoneNumber) {
        final ParseFile parseProfile = new ParseFile(bytes);
        parseProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    createUser(phoneNumber, parseProfile);
                }
            }
        });
    }

    private void createUser(String verifiedPhone, ParseFile parseProfile) {
        Log.e("SIGN UP", "clicked begin");
        ParseUser user = new ParseUser();
        user.setEmail(mEmail.getText().toString().trim().toLowerCase());
        user.setUsername(mUsername.getText().toString().trim().toLowerCase());
        user.setPassword(mPassword.getText().toString().trim().toLowerCase());
        user.put("phoneNumber",verifiedPhone);
        user.put("name", mFullname.getText().toString().trim());
        if (parseProfile != null) {
            user.put("photo",parseProfile);
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mProgressDialog.dismiss();
                    Log.e("SIGN UP", "clicked");
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    if (e.getCode() ==  ParseException.USERNAME_TAKEN ) {
                        Toast.makeText(mContext, "Oops! The username has already been taken. Please try a different one",
                                Toast.LENGTH_LONG).show();
                    } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                        Toast.makeText(mContext, "Oops! This email is already registered on HollaNow.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("CONFIRM_FB", e.getMessage());
                        Toast.makeText(mContext, "Oops! Something went wrong. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                    mProgressDialog.dismiss();
                }
            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
