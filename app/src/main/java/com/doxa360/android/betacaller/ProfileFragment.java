package com.doxa360.android.betacaller;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
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
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.facebook.CallbackManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.models.Card;

import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    private CardView salesCardView2;
    private TextView gallery2;
    private RecyclerView mRecyclerView2;
    private static boolean flag_profile;
    private ProgressBar recyclerBar, recyclerBar2;

    private int no_of_upload = -1;

    private String checkUserName = "";


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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.pictures);
        salesCardView = (CardView) rootView.findViewById(R.id.scrolling_pictures);
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

        mFullName.setText(fullnameIntent);
        mUserName.setText(usernameIntent);
        mPhone.setText(phoneIntent);

        retrieveSalesPictures(checkUserName);


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
//        if (!getUserVisibleHint())
//        {
//            return;
//        }

    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    public void retrieveSalesPictures(String username){
        Log.e("EDITED_USER=> ", currentUser.toString());
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.receiveSalesPictures(username);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
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
                   // Toast.makeText(mContext, "Sales updated", Toast.LENGTH_SHORT).show();

//                    onBackPressed();
                } else {
                    Log.e(TAG, "error: " + response.code() + response.message());
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
                    Toast.makeText(mContext, "Error updating Sales", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();

            }
        });

    }

}
