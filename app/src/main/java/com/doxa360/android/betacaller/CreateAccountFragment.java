package com.doxa360.android.betacaller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import io.fabric.sdk.android.services.network.HttpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private Button mSignIn;
    private TextView mSignUp;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mEmailLayout;
    private EditText mPassword;
    private ProgressDialog mProgressDialog;
    private String email;
    private boolean flag_login;
    private HollaNowApiInterface hollaNowApiInterface;
    private User user1,user;
    public static String mUserName1;
    private String userID;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        FacebookSdk.sdkInitialize(mContext);
        FacebookSdk.setApplicationId("1695593120473978");
        callbackManager = CallbackManager.Factory.create();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = (View) inflater.inflate(R.layout.fragment_create_account, container, false);

        //mFacebookLogin = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
      //  mLoginButton = (Button) rootView.findViewById(R.id.sign_in_button);
       // mSignUpButton = (Button) rootView.findViewById(R.id.sign_up);
        mEmailTextEdit = (EditText) rootView.findViewById(R.id.emailTextEdit);
        mPassword = (EditText)  rootView.findViewById(R.id.password);
        mPasswordLayout = (TextInputLayout) rootView.findViewById(R.id.layout_password);
        mEmailLayout = (TextInputLayout) rootView.findViewById(R.id.layout_emailText);
        mSignIn = (Button)  rootView.findViewById(R.id.sign_in_button);
        mSignUp = (TextView)  rootView.findViewById(R.id.sign_up);
        TextView mForgotPassword = (TextView)  rootView.findViewById(R.id.forgot_password);

        mForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        mProgressDialog = new ProgressDialog(getContext(),R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating ...");

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SignUpActivity.class );
                intent.putExtra("EMAIL", mEmailTextEdit.getText().toString());
                startActivity(intent);
            }
        });

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
            if(accountList != null)
            if (accountList.length > 0) {
                mEmailTextEdit.setText(accountList[0].name);
            }
        }


       /*** mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isMinimumCharacters(mEmailTextEdit.getText().toString(),6) &&   please remember here ////////////////////////
                        MyToolBox.isEmailValid(mEmailTextEdit.getText().toString()) ) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra("EMAIL", mEmailTextEdit.getText().toString());
                    startActivity(intent);
                } else {
                    mEmailTextEdit.setError("Please type in a valid email");
                }
            }
        }); **/



        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(getContext())) {
                    mProgressDialog.show();
                    flag_login = true;
                    if (!MyToolBox.isMinimumCharacters(mPassword.getText().toString(), 2)) {
                        mPasswordLayout.setError("Your password");
                        mProgressDialog.dismiss();
                        flag_login = false;
                    }
                    else {

                        if (!(MyToolBox.isMinimumCharacters(mEmailTextEdit.getText().toString(),6) &&
                                MyToolBox.isEmailValid(mEmailTextEdit.getText().toString())) ) {
                            // Intent intent = new Intent(mContext, SignUpActivity.class);
                            //intent.putExtra("EMAIL", mEmailTextEdit.getText().toString());
                            //  startActivity(intent);
                            mEmailLayout.setError("Your Email");
                            mProgressDialog.dismiss();
                            flag_login = false;
                        }
                        if(flag_login) {
                            email = mEmailTextEdit.getText().toString();
                            mPasswordLayout.setErrorEnabled(false);
                            mEmailLayout.setErrorEnabled(false);
                           // goToActivity(HomeActivity.class);
                             loginUser(email, mPassword.getText().toString());
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Network Error. Check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });


        String[] permissions = new String[]{"email","user_friends", "public_profile"};
     /**   mFacebookLogin.setReadPermissions(Arrays.asList(permissions));
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

                   //     }
                 //   }
              //  }); **/
          /**      Bundle parameters = new Bundle();
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
        });   **/


        Log.e(TAG+" ","Facebook Picture String is 2: ");
        Log.e(TAG+" ","Facebook Picture String is 2: ");
        Log.e(TAG+" ","Facebook Picture String is 2: ");

     /**   Log.e("CreateAccountFragment", "Just for  a start : "+userID);

       // callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("user_photos","public_profile","user_posts" ,"email"));// ,"public_profile","user_posts" ,"email"
      //  LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("publish_actions"));
        // LoginManager.getInstance().logInWithReadPermissions();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                      //  AccessToken tok = AccessToken.getCurrentAccessToken();
                        AccessToken tok = loginResult.getAccessToken();
                        userID = tok.getUserId();
                        Log.e("CreateAccountFragment", "UserID is : "+userID);
                        Log.e(TAG+" ","Facebook Picture String is 5: "+userID);
                        Log.e(TAG+" ","Facebook Picture String is 5: ");
                    }

                    @Override
                    public void onCancel() {
                        Log.e("CreateAccountFragment", "On Cancel : "+userID);
                        Log.e(TAG+" ","Facebook Picture String is 6: "+userID);
                        Log.e(TAG+" ","Facebook Picture String is 6: "+userID);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("CreateAccountFragment", "Error occured : "+error.getMessage());
                        Log.e(TAG+" ","Facebook Picture String is 7: "+error.getMessage());
                        Log.e(TAG+" ","Facebook Picture String is 7: "+error.getMessage());
                    }
                }); **/



    /**    Bundle params = new Bundle();
        params.putString("fields","id,name,picture"); //,gender ,cover ,picture.type(large) ,email
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/me",params, HttpMethod.GET, //me
                new  GraphRequest.Callback(){
                    @Override
                    public void onCompleted(GraphResponse response){
                        if(response != null){
                            try{
                                JSONObject data = null;
                                 data = response.getJSONObject();
                                Log.e(TAG+" ","Facebook Picture String is 4: "+response.toString());
                                Log.e(TAG+" ","Facebook Picture String is 4: "+data);
                                if(data != null)
                                    if(data.has("picture")){
                                        String profilePicUrl = data.getJSONObject("picture").getJSONObject("data")
                                                .getString("url");
                                        Log.e(TAG+" ","Facebook Picture String is 3: "+profilePicUrl);
                                        //    Bitmap profilePic = BitmapFactory.decodeStream(profilePicUrl.openConnection()
                                        //  .getInputStream());
                                    }
                            }catch (Exception e){
                                Log.e(TAG+" ","Facebook Picture String is 1: "+e.getMessage());
                                Log.e(TAG+" ","Facebook Picture String is 1: "+e.getMessage());
                                Log.e(TAG+" ","Facebook Picture String is 1: "+e.getMessage());
                                Log.e(TAG+" ","Facebook Picture String is 1: "+e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();          // ;  executeAndWait()  **/



        return rootView;
    }


    private void loginUser(String email,String password) {
        Log.e(TAG, "Check email: "  +email);
        Log.e(TAG, "Check password " + password);
        user = new User(email, password);
        user1 = new User(email, password);
        Call<User> call = hollaNowApiInterface.signInUser(user);
        //email login
        if (MyToolBox.isEmailValid(email)) {
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        mUserName1 = response.body().getUsername();
                        Log.e(TAG, "response: " + response.body().getToken() + " code: " + response.code());
//                        Toast.makeText(LoginActivity.this, response.body().getToken()+"", Toast.LENGTH_LONG).show();
                        getUserDetails(response.body().getToken());
                    } else {
                        Log.e(TAG, "response: " + response.body() + " code: " + response.code());
                        Toast.makeText(mContext, "Wrong Email/Password", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "failed: " + t.getMessage()+" "+t.getLocalizedMessage());
                    Toast.makeText(mContext, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    if(t instanceof TimeoutException)
                        Log.e(TAG, "Timeout Exception: " + t.getMessage());
                    if(t instanceof SocketTimeoutException)
                        Log.e(TAG, "Socket Timeout Exception: " + t.getMessage());
                    if(t instanceof UnknownHostException)
                        Log.e(TAG, "Unknown Host Exception: " + t.getMessage());
                    if(t instanceof HttpRequest.HttpRequestException)
                        Log.e(TAG, "Http Request Exception: " + t.getMessage());
                    if(t instanceof ConnectException)
                        Log.e(TAG, "Connection Exception: " + t.getMessage());
                    t.printStackTrace();
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
                    Toast.makeText(mContext, "Welcome " + response.body().getName(), Toast.LENGTH_SHORT).show();
                    user = response.body();
                    user.setToken(token);
                    Log.e("Logged in/saved user: ", user.getName()+"");
                    Log.e("Logged in/saved user "," Get token in getUserDetails: "+ user.getToken()+"");
                    saveCurrentUser(user, token);
                    goToActivity(HomeActivity.class);
                } else {
                    Toast.makeText(mContext, "Oops! Could not log in. Try again", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.e(TAG, "user details failed: " + t.getMessage());
                Toast.makeText(mContext, "Wrong Username/Password", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void saveCurrentUser(User user, String token) {
        HollaNowSharedPref sharedPref = new HollaNowSharedPref(mContext);
       // user.setPassword(mPassword.getText().toString());
        sharedPref.setCurrentUser(user.toString());
        sharedPref.setToken(token);
        sharedPref.setPhone(user.getPhone());
        sharedPref.setFlagContacts("Update");
        //parseSignup();
        Log.e(TAG, "Successful login: " + user.getUsername()+" "+user1.getPassword());
        sharedPref.setUsername(user.getUsername().toString().trim());

    }

    private void goToActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        if (cls == HomeActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
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

    public static Bitmap getFacebookProfilePicture(String userID){
        Bitmap bitmap = null;
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        }catch (MalformedURLException e){

        }catch (IOException a){

        }
        return bitmap;
    }

    public void fbLogin(View view){

    }



}
