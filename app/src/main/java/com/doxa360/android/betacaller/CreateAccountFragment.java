package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
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
    private LoginButton mLoginButton;
    private CallbackManager callbackManager;
    private TextView mEmailSignUp, mLoginEmail;

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

        mLoginButton = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        mLoginEmail = (TextView) rootView.findViewById(R.id.login_email);
        mEmailSignUp = (TextView) rootView.findViewById(R.id.sign_up_email);

        mLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            }
        });
        mEmailSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        });
        String[] permissions = new String[]{"email","user_friends", "public_profile"};
        mLoginButton.setReadPermissions(Arrays.asList(permissions));
        mLoginButton.setFragment(this);

        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
