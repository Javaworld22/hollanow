package com.doxa360.android.betacaller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;


public class CreateAccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CreateAccountFragment.class.getSimpleName();
    public static String FACEBOOK_JSON;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private LoginButton mFacebookLogin;
    private CallbackManager callbackManager;
    private Button mSignUpButton, mLoginButton;
    private EditText mEmailTextEdit;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = (View) inflater.inflate(R.layout.fragment_create_account, container, false);

        mFacebookLogin = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        mLoginButton = (Button) rootView.findViewById(R.id.loginButton);
        mSignUpButton = (Button) rootView.findViewById(R.id.signUpButton);
        mEmailTextEdit = (EditText) rootView.findViewById(R.id.emailTextEdit);

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        } else {
            Account[] accountList = AccountManager.get(mContext).getAccountsByType("com.google");
            if (accountList.length > 0) {
                mEmailTextEdit.setText(accountList[0].name);
            }
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isMinimumCharacters(mEmailTextEdit.getText().toString(),6) &&
                        MyToolBox.isEmailValid(mEmailTextEdit.getText().toString()) ) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("EMAIL", mEmailTextEdit.getText().toString());
                    startActivity(intent);
                } else {
                    mEmailTextEdit.setError("Please type in a valid email");
                }
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isMinimumCharacters(mEmailTextEdit.getText().toString(),6) &&
                        MyToolBox.isEmailValid(mEmailTextEdit.getText().toString()) ) {
                    Intent intent = new Intent(mContext, SignUpActivity.class);
                    intent.putExtra("EMAIL", mEmailTextEdit.getText().toString());
                    startActivity(intent);
                } else {
                    mEmailTextEdit.setError("Please type in a valid email");
                }
            }
        });

        String[] permissions = new String[]{"email","user_friends", "public_profile"};
        mFacebookLogin.setReadPermissions(Arrays.asList(permissions));
        mFacebookLogin.setFragment(this);

        mFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "success login");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError()!=null) {
                            //handle error
                        }
                        else {

                            String jsonResult = String.valueOf(json);
                            Log.e(TAG, response.getRawResponse());

//                            try {
//                                Log.e(TAG+" FB: ", json.getString("id")+json.getString("email")+json.getString("name")+json.getJSONObject("picture").getJSONObject("data").getString("url"));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            id = jsonObject.getString("id");
//                            email = jsonObject.getString("email");
//                            firstname = jsonObject.getString("first_name");
//                            lastname = jsonObject.getString("last_name");
//                            fullname = jsonObject.getString("name");
////            phoneNumber = jsonObject.getString("phone");
//                            profilePicUrl = null;
//                            if (jsonObject.has("picture")) {
//                                profilePicUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
//                                // set profile image to imageview using Picasso or Native methods
//                            }

                            Log.e(TAG+" FB LOGIN:", response.getJSONObject().toString());
                            ConfirmFacebook fragment = new ConfirmFacebook();
                            Bundle args = new Bundle();
                            args.putString(FACEBOOK_JSON, jsonResult);
                            fragment.setArguments(args);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.relative_layout, fragment)
                                    .commit();

                            /**
                             * ShareLinkContent content = new ShareLinkContent.Builder()
                             .setContentUrl(Uri.parse("https://developers.facebook.com"))
                             .build();
                             ShareLinkContent content = new ShareLinkContent.Builder()
                             .setContentUrl(Uri.parse("https://developers.facebook.com"))
                             .setShareHashtag(new ShareHashtag.Builder()
                             .setHashtag("#ConnectTheWorld")
                             .build());
                             .build();

                             */

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
