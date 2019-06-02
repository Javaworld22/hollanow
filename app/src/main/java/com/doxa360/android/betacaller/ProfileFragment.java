package com.doxa360.android.betacaller;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.adapter.SalesPictureAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.NotificationModel;
import com.doxa360.android.betacaller.model.SerializableUser;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.bimps;
import com.facebook.CallbackManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.models.Card;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import it.sephiroth.android.library.tooltip.Tooltip;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int ACTION_CALL = 1;
    private static final int ACTION_MESSAGE = 2;
    private static final int ACTION_EMAIL = 3;
    private static final int ACTION_WHATSAPP = 4;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView mProfilePhoto, mInviteFriendsIcon, mShareAppIcon;
    private TextView mFullName, mUserName, mPhone, mAbout, mTags, mInviteFriends, mShareApp, mAddress,
            mOccupation, mChangeNumber, mIndustry,mUpdateNumber;
    private Button mEditButton, mPostButton;
    private TextView textPost;
    private Context mContext;
    private String userIdIntent,usernameIntent,fullnameIntent,phoneIntent,emailIntent,photoIntent;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            InviteFragment inviteFragment = new InviteFragment();
            inviteFragment.show(fm, "INVITE_FRAGMENT");
        }
    };
    private LinearLayout mActionLabel;
    private ImageView mCallLabel, mMessageLabel, mWhatsappLabel, mEmailLabel;
    DigitsAuthButton digitsButton;

    private LinearLayout mShareLayout;
    private CardView mProCard;
    private HollaNowSharedPref mSharedPref;
    private CallbackManager callbackManager;
    private User currentUser;

    private String position;
    private String phoneNumber;

    private RecyclerView mRecyclerView;
    private SalesPictureAdapter mAdapter;
    private CardView salesCardView;
    private CardView postCardView;
    private TextView gallery;
    private NestedScrollView nView;

    private CardView salesCardView2;
    private TextView gallery2;
    private RecyclerView mRecyclerView2;
    private static boolean flag_profile;
    private ProgressBar recyclerBar, recyclerBar2;

    private Button saveContact;
    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath +"/HollaNow/";
    private String getDataPath;
   // private String rawDataPath = SDPath +"/HollaNow/contacts/raw/";
    private static int BUFFER_SIZE = 8192;
    private static String parentPath = "";
    private static User mUserProfile;

    private int no_of_upload = -1;

    private String checkUserName = "";

    private Set<String> picCount;
    private ProgressDialog mProgressDialog;
    private String android_id;

    private int STORAGE_PERMISSION_CODE = 23;
    private int WRITE_PERMISSION_CODE = 1;


    Random rand = new Random();
    int value = rand.nextInt(100);
    int neededValue = value % 3;



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(mContext);
//        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_profile, container, false);

        FragmentManager manager = getFragmentManager();
        int count = manager.getBackStackEntryCount();
        Log.e(TAG, "Test ProfileFragment backstack "+count  );
        if(count > 0)
            manager.popBackStackImmediate(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);

        picCount = new HashSet<String>();
      if(Build.VERSION.SDK_INT >= 23) {
          if (isWriteStorageAllowed()) {
              //Toast.makeText(mContext, "You already have the permission ", Toast.LENGTH_SHORT).show();
              Log.e(TAG, "You already have the permission  " );
          } else
              requestWritePermission();
          if (isReadStorageAllowed()) {
              //Toast.makeText(mContext, "You already have the permission to ", Toast.LENGTH_SHORT).show();
              Log.e(TAG, "You already have the permission  " );
          } else
              requestStoragePermission();
      }


//        mProfilePhoto = (ImageView) rootView.findViewById(R.id.profile_photo);
        mFullName = (TextView) rootView.findViewById(R.id.profile_name);
        mUserName = (TextView) rootView.findViewById(R.id.user_name);
        mPhone = (TextView) rootView.findViewById(R.id.phone);

        mAbout = (TextView) rootView.findViewById(R.id.pro_about);
        mAddress = (TextView) rootView.findViewById(R.id.pro_address);
        mOccupation = (TextView) rootView.findViewById(R.id.pro_occupation);
        mIndustry = (TextView) rootView.findViewById(R.id.pro_industry);

        mActionLabel = (LinearLayout) rootView.findViewById(R.id.action_label);
        mCallLabel = (ImageView) rootView.findViewById(R.id.call_label);
        mMessageLabel = (ImageView) rootView.findViewById(R.id.message_label);
        mWhatsappLabel = (ImageView) rootView.findViewById(R.id.whatsapp_label);
        mEmailLabel = (ImageView) rootView.findViewById(R.id.email_label) ;
        mChangeNumber = (TextView) rootView.findViewById(R.id.change_number_label);
        mUpdateNumber = (TextView) rootView.findViewById(R.id.update_number);

        mPostButton = (Button) rootView.findViewById(R.id.post_service);
        textPost = (TextView) rootView.findViewById(R.id.textview_post);
        Typeface face1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
        textPost.setTypeface(face1);
        mContext = getContext();

        mProCard = (CardView) rootView.findViewById(R.id.pro_card);
        mSharedPref = new HollaNowSharedPref(mContext);
        currentUser = mSharedPref.getCurrentUser();
        mSharedPref.setFlagPhoneAuth(true);
        getDataPath = SDPath +"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/zip/";

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.pictures);
        salesCardView = (CardView) rootView.findViewById(R.id.scrolling_pictures);
        nView = (NestedScrollView) rootView.findViewById(R.id.nested_view) ;

         salesCardView.setFocusable(true);
        salesCardView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                Log.e(TAG, "Check focus of Cardview " + hasFocus);
            }
        });

        salesCardView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int x){
                Log.e(TAG, "Check focus of Cardview 2 " + x);
            }
        });
        postCardView = (CardView) rootView.findViewById(R.id.cardview_post);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        gallery =(TextView) rootView.findViewById(R.id.recycler_textview);
        Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
        gallery.setTypeface(face);

        gallery2 = (TextView) rootView.findViewById(R.id.recycler_textview2);
        gallery2.setTypeface(face);
        salesCardView2 = (CardView) rootView.findViewById(R.id.layout_card_for_second_recycler);
        mRecyclerView2 = (RecyclerView) rootView.findViewById(R.id.pictures2);
        mRecyclerView2.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager1.setAutoMeasureEnabled(true);
        mRecyclerView2.setLayoutManager(gridLayoutManager1);

        recyclerBar = (ProgressBar) rootView.findViewById(R.id.progress_pictures);
        recyclerBar2 = (ProgressBar) rootView.findViewById(R.id.progress_pictures2);

        mProgressDialog = new ProgressDialog(getContext(),R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        mProgressDialog.setMessage("saving contact... ");
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        saveContact = (Button) rootView.findViewById(R.id.save_contacts);



//        ShareLinkContent sharedContent = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.doxa360.android.betacaller"))
//                .setContentDescription("Hello friends, I now use the HollaNow app. So whenever you want to talk to me, just holla @"+ParseUser.getCurrentUser().getUsername()+ ". It\'s cooler.")
////                .setImageUrl(Uri.parse("http://hollanow.com/facebook_share.png"))
//                .build();

        /**
         * START SERVICE OVERLAY TEST
         */

        /**
         * END
         */

        if (getArguments().getParcelable(BetaCaller.USER_PROFILE) != null ) {
            User userProfile = getArguments().getParcelable(BetaCaller.USER_PROFILE);
            if (userProfile != null) {
                mUserProfile = userProfile;
//                usernameIntent = getArguments().getString(userProfile.getUsername());
//                fullnameIntent = getArguments().getString(userProfile.getName());
//                phoneIntent = getArguments().getString(userProfile.getPhone());
//                emailIntent = getArguments().getString(userProfile.getEmail());
                flag_profile = false;

                usernameIntent = userProfile.getUsername();
                checkUserName = userProfile.getUsername();
                fullnameIntent = userProfile.getName();
                phoneIntent = userProfile.getPhone();
                emailIntent = userProfile.getEmail();

                mCallLabel.setOnClickListener(actionListener(ACTION_CALL));
                mMessageLabel.setOnClickListener(actionListener(ACTION_MESSAGE));
                mEmailLabel.setOnClickListener(actionListener(ACTION_EMAIL));
                mWhatsappLabel.setOnClickListener(actionListener(ACTION_WHATSAPP));


                mChangeNumber.setVisibility(View.GONE);
                mUpdateNumber.setVisibility(View.GONE);
                postCardView.setVisibility(View.GONE);
                salesCardView.setVisibility(View.GONE);
                //salesCardView.setVisibility(View.GONE);

                mAbout.setText((userProfile.getAbout()!=null)?userProfile.getAbout():"-");
                mAddress.setText((userProfile.getAddress()!=null)?userProfile.getAddress():"-");
                mOccupation.setText((userProfile.getOccupation()!=null)?userProfile.getOccupation():"-");
                mIndustry.setText((userProfile.getIndustry()!=null)?userProfile.getIndustry():"-");
                //currentUser.setOccupation(userProfile.getOccupation());
            }


        } else {
            flag_profile = true;
            usernameIntent = currentUser.getUsername();
            fullnameIntent = currentUser.getName();
            phoneIntent = currentUser.getPhone();
            emailIntent = currentUser.getEmail();

            checkUserName = currentUser.getUsername();


            mCallLabel.setVisibility(View.GONE); //hide action if owner's profile
            mMessageLabel.setVisibility(View.GONE); //hide action if owner's profile
            mWhatsappLabel.setVisibility(View.GONE); //hide action if owner's profile
            mEmailLabel.setVisibility(View.GONE); //hide action if owner's profile
            saveContact.setVisibility(View.GONE);
            mChangeNumber.setVisibility(View.VISIBLE);
            mUpdateNumber.setVisibility(View.VISIBLE);
            salesCardView2.setVisibility(View.GONE);
            mChangeNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ProfileActivity activity = (ProfileActivity) mContext;
//                    FragmentManager fm = activity.getSupportFragmentManager();
//                    ChangeNumberFragment fragment = new ChangeNumberFragment();
//                    Bundle args = new Bundle();
//                    args.putString(ProfileActivity.PHONE, phoneIntent+"");
//                    fragment.setArguments(args);
//                    fragment.show(fm, "CHANGE_FRAGMENT");
                    verifyPhoneNumber(phoneIntent);
                }
            });



//            toggleProShare();

            mAbout.setText((currentUser.getAbout()!=null)?currentUser.getAbout():"-");
            mAddress.setText((currentUser.getAddress()!=null)?currentUser.getAddress():"-");
            mOccupation.setText((currentUser.getOccupation()!=null)?currentUser.getOccupation():"-");
            mIndustry.setText((currentUser.getIndustry()!=null)?currentUser.getIndustry():"-");

            mPostButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Log.e(TAG , "PhoneIntent: "+phoneIntent   + " Occupation : " + currentUser.getOccupation());
                    if(phoneIntent != null && phoneIntent.length() > 5 && currentUser.getOccupation() != null) {
                        Intent intent = new Intent(getContext(), UploadPictureActivity.class);
                        intent.putExtra("count",no_of_upload);
                        startActivity(intent);
                    }else{

                          if(!(phoneIntent != null && phoneIntent.length() > 5 && currentUser.getOccupation() != null)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                            builder.setTitle("Alert");
                            builder.setMessage("Please, ensure your phone number and occupation are updated so clients can reach you." +
                                    " So update your Phone Number and Occupation ");
                            // builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                            //     public void onClick(DialogInterface dialog, int id){


                            //     }
                            //  });
                            // builder.setNegativeButton("Cancel", null);
                            builder.show();
                        }
                    }
                }
            });


        }


        saveContact.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.e(TAG, "Button is working  " );
                if(saveContact.getText().toString().contains("Add")) {
                    if(mUserProfile == null)
                        mUserProfile = mSharedPref.getCurrentUser();
                    zip(mUserProfile, dataPath, null, false);
                    Toast.makeText(mContext, "Contact Added", Toast.LENGTH_SHORT).show();
                    saveContact.setText("Remove Contact");
                    File file = new File(SDPath+"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/zip/".trim());
                    backUpContacts(file, android_id);
                    //if (getArguments().getParcelable(BetaCaller.USER_PROFILE) != null )
                    //   if (getArguments().getParcelable(BetaCaller.USER_PROFILE) != null)
                    //    receiveNotification(getArguments().getParcelable(BetaCaller.USER_PROFILE).);
                }
                else{
                    if(deleteFile(mUserProfile, getDataPath)) {
                        Log.e(TAG, "File delete here 2" + "True here ooo");
                        Toast.makeText(mContext, "Contact Removed", Toast.LENGTH_SHORT).show();
                        saveContact.setText("Add Contact");
                    }
                }
            }
        });



        mFullName.setText(fullnameIntent);
        mUserName.setText(usernameIntent);
        mPhone.setText(phoneIntent);
        if(phoneIntent == null || phoneIntent.length() < 5 || phoneIntent.equals(""))
            saveContact.setVisibility(View.GONE);

        retrieveSalesPictures(checkUserName);

        testContactSaved(mUserProfile, getDataPath);


        nView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener(){
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollY, int oldScrollX){
                Tooltip.removeAll(mContext);
                //Log.e(TAG, "Check focus of NestedView  " + scrollX);
                if(oldScrollX > 150 ||  scrollY > 150) {
                    Log.e(TAG, "Check focus of NestedView  " + scrollY);
                    if(saveContact != null)
                        if(neededValue == 0  && (saveContact.getText().equals("ADD CONTACT") ||  saveContact.getText().equals("Add Contact"))) {
                            Tooltip.make(mContext,
                                    new Tooltip.Builder(102).fitToScreen(true)
                                            .anchor(saveContact, Tooltip.Gravity.BOTTOM)
                                            .closePolicy(new Tooltip.ClosePolicy()
                                                    .insidePolicy(true, true)
                                                    .outsidePolicy(true, false), 0)
                                            .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                                            .activateDelay(800)
                                            .showDelay(300)
                                            .text(getString(R.string.tooltip_add_contacts))
                                            .maxWidth(500)
                                            .withArrow(true)
                                            .withOverlay(true)
                                            //    .typeface(mYourCustomFont)
                                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                            .build()
                            ).show();
                        }

                }
            }
        });


        return rootView;
    }

    private void verifyPhoneNumber(String phoneIntent) {


                position = getArguments().getString("checkposition");
                phoneNumber = getArguments().getString("number");
                Log.e(TAG + "user", position + " and " + phoneNumber);
                //  currentUser.setPhone(phoneNumber);
                //  mPhone.setText(phoneNumber);
            if(position == null && phoneNumber == null) {
                Log.e(TAG + "user", position + " and " + phoneNumber + " " );
                FirebaseAuth.getInstance().signOut();
                PhoneInputFragment callNoteBottomSheet = new PhoneInputFragment();
                Bundle args = new Bundle();
                args.putString("checkposition", "ProfileFragment");
                FragmentManager fm = getFragmentManager();
                callNoteBottomSheet.setArguments(args);
                callNoteBottomSheet.show(fm, "REFS");
            }

       // if (!(position.equals("ProfileFragmentRecturn")) || position == null){

      //  if(position != null)
   //  if((position.equals("ProfileFragmentRecturn")))
      //      updatePhoneNumber(phoneNumber);
      /**  AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(new AuthCallback() {
                    @Override
                    public void success(DigitsSession session, String phoneNumber) {
                        //TODO: check phone availablity?
                       // updatePhoneNumber(phoneNumber);

                    }

                    @Override
                    public void failure(DigitsException error) {
                        Toast.makeText(mContext, "Phone number not changed", Toast.LENGTH_SHORT).show();
                    }
                })
                .withPhoneNumber(phoneIntent);

        Digits.authenticate(authConfigBuilder.build()); **/
    }

    private void toggleProShare() {

    }

    View.OnClickListener serviceOverlayTest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNumber = "08037046543";
            Intent intent = new Intent(mContext.getApplicationContext(), CallNoteService.class);
            intent.putExtra(oldCallNote.CALLER_NUMBER, phoneNumber);
            Log.e("PRO: ", "profile activity test call note: "+ phoneNumber);
            mContext.startService(intent);
        }
    };

    private void updatePhoneNumber(final String phoneNumber) {
        mPhone.setText("updating...");
        currentUser.setPhone(phoneNumber);
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<User> call = hollaNowApiInterface.editUserPhone(currentUser, mSharedPref.getToken(), "Nigeria");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    currentUser.setPhone(response.body().getPhone());
                    mSharedPref.setCurrentUser(currentUser.toString());
                    mSharedPref.setPhoneEmoji(response.body().getPhone());
                    mPhone.setText(response.body().getPhone());
                    Toast.makeText(mContext, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    mPhone.setText(phoneIntent);
                    Toast.makeText(mContext, "Phone number not updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mPhone.setText(phoneIntent);
                Toast.makeText(mContext, "Network error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private View.OnClickListener actionListener(final int actionCall) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (actionCall) {
                    case 1:
                        if (phoneIntent!=null) {
                            //do call note here...
                            CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
                            FragmentManager fragmentManager = getFragmentManager();
                            Bundle args = new Bundle();
                            args.putString(Parse_Contact.PHONE_NUMBER, phoneIntent);
                            args.putString(Parse_Contact.USERNAME, usernameIntent);
                            callNoteBottomSheet.setArguments(args);
                            callNoteBottomSheet.setTargetFragment(ProfileFragment.this, 1001);
                            callNoteBottomSheet.show(fragmentManager,"CALL_NOTE");
//                            placeCall(phoneIntent);
                        } else {
                            Toast.makeText(mContext, "Sorry. This contact has no phone number", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2:
                        if (phoneIntent!=null) {
                            sendSMSIntent(phoneIntent,"");
                        } else {
                            Toast.makeText(mContext, "Sorry. This contact has no phone number", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        if (phoneIntent!=null) {
                            sendEMAILIntent(emailIntent);
                        } else {
                            Toast.makeText(mContext, "Sorry. This email is null", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 4:
                        if(phoneIntent != null){
                            sendWHATSAPPIntent(phoneIntent);
                        }else{
                            Toast.makeText(mContext, "Sorry. This number is null", Toast.LENGTH_LONG).show();
                        }
                }
            }
        };
    }

//    private void getProInfo(String userIdIntent) {
//        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        userQuery.getInBackground(userIdIntent, new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser object, ParseException e) {
//                if (e==null) {
//                    if (object.getString("bio")!=null) {
//                        mAbout.setText(object.getString("bio"));
//                    }
//                    if (object.getString("address")!=null) {
//                        mAddress.setText(object.getString("address"));
//                    }
//                    if (object.getString("occupation")!=null) {
//                        mOccupation.setText(object.getString("occupation"));
//                    }
//                } else {
//                    Toast.makeText(mContext, "Connection error...", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    private void sendSMSIntent(String number, String msg) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        startActivity(it);

    }

    private void sendWHATSAPPIntent(String number) {
       // String toNumber = number.replace("+","").replace(" ","").trim();

        try {
           // Uri uri = Uri.parse("smsto:"+ number.trim());
            String url = "https://api.whatsapp.com/send?phone="+number+"&text="+ URLEncoder.encode("Hi ","UTF-8");
            Intent waIntent = new Intent(Intent.ACTION_VIEW); //android.content.Intent.ACTION_SEND  Intent.ACTION_SEND
          //  waIntent.setAction(Intent.ACTION_SEND);
          //  waIntent.setType("text/plain");
            PackageManager pm = getActivity().getPackageManager() ;
           // List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(waIntent, 0);
            // String text = "My Text";
          //  if (!resInfo.isEmpty()) {
             //   for (ResolveInfo info : resInfo) {
                //    PackageInfo inf1 = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);  //GET_META_DATA
                  //  waIntent.setComponent(new ComponentName(info.activityInfo.packageName,info.activityInfo.packageName )); // "com.whatsapp.Conversation"
                    // "com.whatsapp"
           // waIntent.putExtra(Intent.EXTRA_TEXT, "Hi");
           // waIntent.putExtra("chat", true);
                    //waIntent.putExtra("jid", toNumber + "@s.whatsapp.net"); // PhoneNumberUtils.stripSeparators(number)
                    waIntent.setPackage("com.whatsapp");
                    waIntent.setData(Uri.parse(url));
                if(waIntent.resolveActivity(pm) != null)
                     startActivity(waIntent);
              //  }


               // startActivity(Intent.createChooser(waIntent, "Share with"));
               // startActivity(waIntent);
           // }


        }//catch(PackageManager.NameNotFoundException ee){
        catch(Exception e){


       }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.e(TAG, "Check for grantresults  "+grantResults);
        if(requestCode == WRITE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(mContext, "Permission Granted! ",Toast.LENGTH_LONG);
            else
                Toast.makeText(mContext, "Oops you just denied the permission! ",Toast.LENGTH_LONG);
        }
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(mContext, "Permission Granted! ",Toast.LENGTH_LONG);
            else
                Toast.makeText(mContext, "Oops you just denied the permission! ",Toast.LENGTH_LONG);
        }
    }

    private boolean isReadStorageAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }

    private boolean isWriteStorageAllowed(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //if permission is granted return true
        if(result == PackageManager.PERMISSION_GRANTED)
            return true;

        // else return false
        return  false;
    }

    private void requestWritePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(mContext, "Write external storage permission allows us to store contacts. " +
                    "Please allow this permission in the App Settings. ",Toast.LENGTH_LONG);
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
    }

    private void requestStoragePermission(){
         if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
        //This code reueirs permission for android version 6 and 7
          }
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

    }

    private void sendEMAILIntent(String email){
           String[] emailAddress = new String[] {email};
         Intent intent = new Intent(Intent.ACTION_SENDTO);
         intent.setData(Uri.parse("mailto:")); // only email apps should handle this
         intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
         if (intent.resolveActivity(mContext.getPackageManager()) != null) {
         startActivity(intent);
         }
    }

    private void placeCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "SaveContacts isShown: "+saveContact.isShown());
      //  if(saveContact != null)
      //  if(/**neededValue == 0  && **/(saveContact.getText().equals("ADD CONTACT") ||  saveContact.getText().equals("Add Contact"))) {
         /**   Tooltip.make(mContext,
                    new Tooltip.Builder(102).fitToScreen(true)
                            .anchor(saveContact, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, true)
                                    .outsidePolicy(true, false), 0)
                            .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                            .activateDelay(800)
                            .showDelay(300)
                            .text(getString(R.string.tooltip_add_contacts))
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            //    .typeface(mYourCustomFont)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
        } **/

    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
       // Log.e(TAG, "SaveContacts isShown: "+salesCardView.isShown());
        //if(salesCardView.isShown()) {

       // }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    public void retrieveSalesPictures(final String username){
        Log.e("EDITED_USER=> ", currentUser.toString());
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.receiveSalesPictures(username);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                   //  Log.e(TAG, "success "+ response.body().toString());
                   // Log.e(TAG, "success "+ response.body().toString());
                   // Log.e(TAG, "success "+ response.body().toString());
                   // Log.e(TAG, "success "+ response.body().toString()+" "+response.body().getAsString());
                    ListSalesMarket sales =  new GsonBuilder().create().fromJson(response.body().toString(),ListSalesMarket.class);
                    int number_of_pictures = sales.getSalesMarket().size();
                   // if(!(number_of_pictures > 0)) {
                    //    salesCardView.setVisibility(View.GONE);
                    //    salesCardView2.setVisibility(View.GONE);
                  //  }
                    if(flag_profile) {
                        no_of_upload = sales.getSalesMarket().size();
                        mAdapter = new SalesPictureAdapter(sales.getSalesMarket(), mContext, sales);
                        mRecyclerView.setAdapter(mAdapter);
                        recyclerBar.setVisibility(View.GONE);
                        salesCardView2.setVisibility(View.GONE);
                    }

                    if(!flag_profile){
                        no_of_upload = sales.getSalesMarket().size();
                        mAdapter = new SalesPictureAdapter(sales.getSalesMarket(),mContext,sales);
                        mRecyclerView2.setAdapter(mAdapter);
                        recyclerBar2.setVisibility(View.GONE);
                        salesCardView.setVisibility(View.GONE);
                    }
                        String count = username+no_of_upload;
                        picCount.add(count);
                    mSharedPref.setGalleryCount(picCount);
                   // Toast.makeText(mContext, "Sales updated", Toast.LENGTH_SHORT).show();

//                    onBackPressed();
                } else {
                    Log.e(TAG, "error: " + response.code() + response.message());
                   // Log.e(TAG, "success "+ response.body().toString());
                   // Log.e(TAG, "success "+ response.body().toString());
                  //  Log.e(TAG, "success "+ response.body().toString());
                    Toast.makeText(mContext, "Error updating Sales", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean zip(User user, String destinationPath, String destinationFileName, boolean includeParentsFolder){

        File doc = new File(destinationPath);
        if(!doc.exists())
              doc.mkdirs();
        //FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream = null;
        if(!destinationPath.endsWith("/"))
            destinationPath +="/";
        destinationPath =  destinationPath + mSharedPref.getCurrentUser().getUsername()+"/zip/";
        new File(destinationPath).mkdirs();

        String destination = destinationPath + user.getUsername().trim();
        new File(destination).mkdirs();
        //if(!destinationPath.endsWith("/"))
         //   destinationPath +="/";

        if(!destination.endsWith("/"))
            destination +="/";
        Log.e(TAG,"Path of the file "+ destination);
        File file = new File(destination);
        try {
            if (!file.exists())
                file.createNewFile();

           // fileOutputStream = new FileOutputStream(file);
           // zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
            zipFile(destination,user);
        }catch (IOException ex){
            Log.e(TAG,"EXception occured "+ ex.getMessage());
            return false;
        }finally {
            if(zipOutputStream != null)
                try{
                    zipOutputStream.close();
                }catch (IOException a){

                }

        }
        return true;
    }

    private void zipFile( String destination, User user){
    /**    java.io.File files = new java.io.File(sourcePath);
        File[] fileList = files.listFiles();
        String entryPath;
        BufferedInputStream input;
        for(File file: fileList) {
            if (file.isDirectory())
                zipFile(zipOutputStream, file.getPath(),user); **/
         //   else {
        /////////////////////////  For Username alone   //////////////////////////////
        String entryPath;
        BufferedInputStream input;
        FileOutputStream fileOutputStream;
        FileWriter writer;
        ZipOutputStream zipOutputStream = null;
        BufferedOutputStream buf = null;
        //File file = new File(destination+"/"+"name"+user.getUsername()+".txt");
       /** try {
            if (!file.exists()) {
                file.createNewFile();
                byte data[] = new byte[BUFFER_SIZE];
                // FileInputStream fileInputStream = new FileInputStream(file.getPath());
                // input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                writer = new FileWriter(file);

                writer.write(user.getName());
                writer.flush();
                writer.close();
            }  else{
                getActivity().runOnUiThread(new Runnable() {
                         @Override
                        public void run() {
                             saveContact.setText("Unsave Contact");
                        }
                      } );
            }


        }catch(IOException a){
            Log.e(TAG,"EXception occured 1 "+ a.getMessage());
        } **/

        /////////////////////////  For Phonenumber alone   //////////////////////////////

     /**   File number = new File(destination+"/"+"phone"+user.getPhone()+".txt");
        try {
            if (!number.exists()) {
                number.createNewFile();
                writer = new FileWriter(number);

                writer.write(user.getPhone());
                writer.flush();
                writer.close();
            } else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveContact.setText("Unsave Contact");
                    }
                } );
            }
        }catch(IOException z){
        Log.e(TAG,"EXception occured 2 "+ z.getMessage());
    } **/

        /////////////////////////  For Occupation alone   //////////////////////////////
    /**    try{
    if(user.getOccupation() != null) {
        File occupation = new File(destination + "/" +"occupat"+ user.getName() + ".txt".trim());
        if (!occupation.exists()) {
            occupation.createNewFile();
            writer = new FileWriter(occupation);
            writer.write(user.getOccupation());
            writer.flush();
            writer.close();
        }
    }
        }catch(IOException a){
            Log.e(TAG,"EXception occured 3 "+ a.getMessage());
        } **/

        /////////////////////////  For Last Seen alone   //////////////////////////////
      /**  try{
            if(user.getmUpdatedDate() != null) {
                File lastSeen = new File(destination + "/" +"last"+ user.getmUpdatedDate() + ".txt".trim());
                if (!lastSeen.exists()) {
                    lastSeen.createNewFile();
                    writer = new FileWriter(lastSeen);
                    writer.write(user.getmUpdatedDate());
                    writer.flush();
                    writer.close();
                }
            }
        }catch(IOException a){
            Log.e(TAG,"EXception occured 3 "+ a.getMessage());
        } **/


        /////////////////////////  For Image alone   //////////////////////////////
        File image = null;
        try{
       URL url =  new URL(BetaCaller.PHOTO_URL+user.getProfilePhoto().trim());
       if(user.getProfilePhoto() != null){
           if(!url.getPath().contains(".jpg"))
         image = new File(destination+"/"+user.getProfilePhoto()+".jpg".trim());
           else
               image = new File(destination+"/"+user.getProfilePhoto().trim());
        if (!image.exists()) {
            image.createNewFile();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input3 = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input3);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.toByteArray(input3);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(image);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        } /**else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveContact.setText("Unsave Contact");
                }
            } );
        } **/
        }catch(IOException a){
            Log.e(TAG,"EXception occured 4 "+ a.getMessage());
        }catch (Exception ex){
            Log.e(TAG,"EXception occured at image saving contact "+ ex.getMessage());
        }

        /////////////////////////  For Object alone   //////////////////////////////
        Parse_Contact contact = new Parse_Contact();
        SerializableUser seruser = new SerializableUser();
        seruser.setEmail(user.getEmail());
        seruser.setUserName(user.getUsername());
        seruser.setAbout(user.getAbout());
        seruser.setAddress(user.getAddress());
        seruser.setPhone(user.getPhone());
        seruser.setOccupation(user.getOccupation());
        seruser.setIndustry(user.getIndustry());
        seruser.setProfilePhoto(BetaCaller.PHOTO_URL+user.getProfilePhoto());
        seruser.setName(user.getName());
        seruser.setIndicateSaved(true);
        contact.setLastSeen(user.getmUpdatedDate());
        seruser.setUpdate(contact.getLastSeen());
        if(image != null)
            seruser.setProfilePhoto(image.getPath());
        try{
            File userFile = new File(destination+ user.getUsername() + ".ser".trim());
            if (!userFile.exists())
                  userFile.createNewFile();
            FileOutputStream fileOutputStream1 = new FileOutputStream(userFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream1);
            objectOutputStream.writeObject(seruser);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream1.close();
        }catch (IOException ex) {
            Log.e(TAG,"EXception occured 6 "+ ex.getMessage());
        }


    }



    private void testContactSaved(User user, String path) {
        File files = new File(path);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveContact.setText("Add Contact");
            }
        });
        File[] fileList = files.listFiles();
        if(fileList != null)
        for (File file : fileList) {
            if (file.isDirectory())
                if(file != null) {
                    Log.e(TAG,"Print File here "+file.getPath());
                    try {
                        if (file.getPath().contains(mSharedPref.getCurrentUser().getUsername().trim()))
                            if (file.getPath().contains(user.getUsername().trim()))
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveContact.setText("Remove Contact");
                                    }
                                });
                    }catch(NullPointerException ee){
                        Log.e(TAG,"Nullpointer Exception occurred "+ee.getMessage());
                    }
                }
        }
    }

    private boolean deleteFile(User user, String path){
        boolean saveFile = false;
        File files = new File(path);
        File[] fileList = files.listFiles();
        for (File file : fileList) {
            if (file.isDirectory())
                if(file != null) {
                    try {
                        if (file.getPath().contains(user.getUsername().trim()))
                            FileUtils.deleteDirectory(file);
                            //saveFile =  file.delete();
                            Log.e(TAG, "File delete here " + saveFile);
                            saveFile = true;

                    }catch (IOException ex){
                        Log.e(TAG, "Error Occured " + ex.getMessage());
                    }
                }
    }
            return saveFile;

  }


    private void backUpContacts(File contacts, String id) {


        String username = null;
        File mFile = null;
        File[] fileList = contacts.listFiles();
        if(fileList != null)
          for (File file : fileList) {
            if (file.isFile())
                if ((file.getPath().contains(".txt".trim()))||(file.getPath().contains(".ser") &&  file.getPath().contains(mSharedPref.getCurrentUser().getUsername()))){
                    if (mSharedPref.getCurrentUser() != null) {
                        username = mSharedPref.getCurrentUser().getUsername();
                    }





                    SerializableUser user ;
                    List<Parse_Contact> parseContact ;
                    if (file.getPath().contains(".ser")) {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(file);
                            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                            user =  (SerializableUser) objectInputStream.readObject();
                            Log.e(TAG, "List of files in readHollaFromExternal 3 " + file.getPath());
                            Log.e(TAG, "List of files in readHollaFromExternal  3 " + file.getPath());

                            parseContact = (List) user.getmUserArrayList();
                            //allHollaContacts = parseContact;

                            if(parseContact.size() >= 1)
                              Log.e(TAG, "GetItemCount 3 "+parseContact.get(0).getSerialUser().getName());
                            objectInputStream.close();
                            fileInputStream.close();

                            Log.e(TAG, "HollaContacts  3 " + file.length());

                            File file2 = new File(SDPath+"/HollaNow/"+mSharedPref.getCurrentUser().getUsername()+"/zip/"
                                    +mSharedPref.getCurrentUser().getUsername()+".txt".trim());
                            if (!file2.exists())
                                file2.createNewFile();
                            FileOutputStream outputStream = new FileOutputStream(file2);
                            for(int i=0;i<parseContact.size();i++){
                                outputStream.write("START".getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getName() != null) {
                                    outputStream.write(("HollaName" + parseContact.get(i).getSerialUser().getName()).trim().getBytes());
                                    outputStream.flush();
                                }
                                else if(parseContact.get(i).getSerialUser().getName() == null) {
                                    outputStream.write("HollaName".trim().getBytes());
                                    outputStream.flush();
                                }
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getPhone() != null) {
                                    outputStream.write(("HollaPhone" + parseContact.get(i).getSerialUser().getPhone()).trim().getBytes());
                                    outputStream.flush();
                                }
                                else if(parseContact.get(i).getSerialUser().getPhone() == null){
                                    outputStream.write("HollaPhone".trim().getBytes());
                                    outputStream.flush();
                                }
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getUsername() != null)
                                outputStream.write(("HollaUsername"+parseContact.get(i).getSerialUser().getUsername()).trim().getBytes());
                                else if(parseContact.get(i).getSerialUser().getUsername() == null)
                                    outputStream.write("HollaUsername".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getOccupation() != null)
                                outputStream.write(("HollaOccupation"+parseContact.get(i).getSerialUser().getOccupation()).trim().getBytes());
                                else
                                    outputStream.write("HollaOccupation".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getAbout() != null)
                                outputStream.write(("HollaAbout"+parseContact.get(i).getSerialUser().getAbout()).trim().getBytes());
                                else
                                    outputStream.write("HollaAbout".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getThumbnailUrl() != null)
                                outputStream.write(("HollaProfilePhoto"+parseContact.get(i).getThumbnailUrl()).trim().getBytes());
                                else
                                    outputStream.write("HollaProfilePhoto".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getIndustry() != null)
                                  outputStream.write(("HollaIndustry"+parseContact.get(i).getSerialUser().getIndustry()).trim().getBytes());
                                else
                                    outputStream.write("HollaIndustry".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getEmail() != null)
                                outputStream.write(("HollaEmail"+parseContact.get(i).getSerialUser().getEmail()).trim().getBytes());
                                else
                                    outputStream.write("HollaEmail".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getUpdate() != null)
                                    outputStream.write(("HollaUpdate"+parseContact.get(i).getSerialUser().getUpdate()).trim().getBytes());
                                else
                                    outputStream.write("HollaUpdate".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                if(parseContact.get(i).getSerialUser().getCountry() != null)
                                outputStream.write(("HollaCountry"+parseContact.get(i).getSerialUser().getCountry()).trim().getBytes());
                                else
                                    outputStream.write("HollaCountry".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                  outputStream.write("true".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                   outputStream.write("zip".trim().getBytes());
                                outputStream.write("\n".getBytes());
                                outputStream.write("END".getBytes());
                                outputStream.write("\n".getBytes());
                                outputStream.flush();

                            }

                            //countData++;
                        } catch (IOException z) {
                            Log.e(TAG, "IOException FileInputStream 3 " + z.getMessage());
                            Log.e(TAG, "IOException FileInputStream 3 " + file.length()/1024);
                        } catch (ClassNotFoundException zx) {
                            Log.e(TAG, " ClassNotFoundException 3 " + zx.getMessage());
                        }
                    }








                   // mProgressDialog.show();
        if(file.getPath().contains(".txt".trim())) {
            mFile = file;
           // Log.e(TAG, "Intro to contact management ");
            Log.e(TAG, "Intro to contact management " + mFile.getPath());
            Log.e(TAG, "Intro to contact management" + mFile.getPath());
            //Log.e(TAG, "Check some parameters " + username + "  " + id + " ");
            //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"),Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            //     + File.separator + "mikoko3.pdf");


            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), mFile);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("zip", mFile.getName(), requestFile);
            if (MyToolBox.isNetworkAvailable(mContext)) {
                HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
                //   if (token != null && id != null){
                Call<ResponseBody> call = hollaNowApiInterface.contactManagement(body, id, username);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            mProgressDialog.dismiss();
                            Log.e(TAG, "success of contacts " + response.code() + "");
                            Log.e(TAG, "success of contacts " + response.code() + "");
                           // Log.e(TAG, "success of contacts " + response.code() + "");
                           // Log.e(TAG, "success of contacts " + response.code() + "");
                           // Log.e(TAG, "success of contacts " + response.code() + "");
                            // Toast.makeText(mContext, "Contact Saved", Toast.LENGTH_SHORT).show();
                            try {
                                Log.e(TAG, "success of contacts " + response.body().string() + "");
                                // Log.e(TAG, "success of contacts " + response.body().string() + "");
                                Log.e(TAG, "success of contacts " + response.body() + "");

                            } catch (IOException e) {
                                Toast.makeText(mContext, "Error updating Device", Toast.LENGTH_SHORT).show();
                            }
//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "error from contacts: " + "error updating device id");
                            Log.e(TAG, "error from contacts: " + response.code());
                           // Log.e(TAG, "error from contacts: " + response.code());
                           // Log.e(TAG, "error from contacts: " + response.code());
                           // Log.e(TAG, "error from contacts: " + response.code());
                            //mProgressDialog.dismiss();


                            Toast.makeText(mContext, "Contact Not Saved", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Device id update failed: " + t.getMessage());
                       // Log.e(TAG, "Device id update failed: " + t.getMessage());
                       // Log.e(TAG, "Device id update failed: " + t.getMessage());
                       // mProgressDialog.dismiss();
                        Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

                    }
                });
                //   }
            } ////////////////////////////
        }
        }
    }

    }

    private void notifyUsers(String username, String userNotification){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <JsonElement> call = hollaNowApiInterface.notifyUserContact(username, userNotification);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {

                    Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                   // Post_Model model1 =   model.getNotification().get(0);
                    //Log.e(TAG, "Whats saved on the notification "+bip.getUsername());
                   // Log.e(TAG, "Whats saved on the notification "+bip.getUserNotification());
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Failure "+t);

                // bipList = helper.allNotification();

            }

        });
    }


    private void receiveNotification(String username, String userNotification){
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call <List<NotificationModel>> call = hollaNowApiInterface.receiveNotification(username);

        call.enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.code() == 200) {

                    Notification_ model = new GsonBuilder().create().fromJson(response.body().toString(), Notification_.class);
                      bimps bip =   model.getNotification().get(0);
                   // Log.e(TAG, "Whats received on the notification "+bip.getUsername());
                  //  Log.e(TAG, "Whats received on the notification "+bip.getUserNotification());
                   // notifyUsers(bip.getUsername(), bip.getUserNotification());
                }


            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e(TAG, "Failure "+t);
               // Log.e(TAG, "Failure "+t);
                // bipList = helper.allNotification();

            }

        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibileToUser){
        super.setUserVisibleHint(isVisibileToUser);
        Tooltip.removeAll(mContext);
       // if(isVisibileToUser){
            // Log.e(TAG, "This fragment is visible now "+isVisibileToUser);
            //  if(mSharedPref.getView() == 2)
            if(mSharedPref != null)
              //  if(mSharedPref.getListShout() == null) {
                    Tooltip.make(mContext,
                            new Tooltip.Builder(102).fitToScreen(true)
                                    .anchor(saveContact, Tooltip.Gravity.BOTTOM)
                                    .closePolicy(new Tooltip.ClosePolicy()
                                            .insidePolicy(true, true)
                                            .outsidePolicy(true, false), 0)
                                    .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                                    .activateDelay(800)
                                    .showDelay(300)
                                    .text(getString(R.string.tooltip_shoutout))
                                    .maxWidth(500)
                                    .withArrow(true)
                                    .withOverlay(true)
                                    //    .typeface(mYourCustomFont)
                                    .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                    .build()
                    ).show();
              //  }
       // }
    }


}
