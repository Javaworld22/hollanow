package com.doxa360.android.betacaller;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Contact;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int ACTION_CALL = 1;
    private static final int ACTION_MESSAGE = 2;
    private static final int ACTION_EMAIL = 3;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView mProfilePhoto, mInviteFriendsIcon, mShareAppIcon;
    private TextView mFullName, mUserName, mPhone, mAbout, mTags, mInviteFriends, mShareApp, mAddress, mOccupation, mChangeNumber;
    private Button mEditButton;
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
    private TextView mCallLabel, mMessageLabel, mEmailLabel;
    DigitsAuthButton digitsButton;

    private LinearLayout mShareLayout;
    private CardView mProCard;
    private HollaNowSharedPref mSharedPref;
    private CallbackManager callbackManager;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_profile, container, false);

        digitsButton = (DigitsAuthButton) rootView.findViewById(R.id.auth_button);
        digitsButton.setText("Change Phone Number");
//        digitsButton.setTypeface(null, Typeface.NORMAL);
        digitsButton.setBackground(getResources().getDrawable(R.drawable.custom_button));
//        digitsButton.setPadding(8,2,8,2);
        digitsButton.setAuthTheme(R.style.CustomDigitsTheme);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                updatePhoneNumber(phoneNumber);
                Toast.makeText(mContext, "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });

//        mProfilePhoto = (ImageView) rootView.findViewById(R.id.profile_photo);
        mFullName = (TextView) rootView.findViewById(R.id.profile_name);
        mUserName = (TextView) rootView.findViewById(R.id.user_name);
        mPhone = (TextView) rootView.findViewById(R.id.phone);

        mAbout = (TextView) rootView.findViewById(R.id.pro_about);
        mAddress = (TextView) rootView.findViewById(R.id.pro_address);
        mOccupation = (TextView) rootView.findViewById(R.id.pro_occupation);

        mActionLabel = (LinearLayout) rootView.findViewById(R.id.action_label);
        mCallLabel = (TextView) rootView.findViewById(R.id.call_label);
        mMessageLabel = (TextView) rootView.findViewById(R.id.message_label);
        mEmailLabel = (TextView) rootView.findViewById(R.id.email_label);
        mChangeNumber = (TextView) rootView.findViewById(R.id.change_number_label);

        mShareLayout = (LinearLayout) rootView.findViewById(R.id.shareLayout);
        mProCard = (CardView) rootView.findViewById(R.id.pro_card);
        mSharedPref = new HollaNowSharedPref(mContext);

        ShareLinkContent sharedContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://hollanow.com"))
                .setContentDescription("Hello friends, I now use the HollaNow app. So whenever you want to talk to me, just holla @"+ParseUser.getCurrentUser().getUsername()+ ". It\'s cooler.")
                .setImageUrl(Uri.parse("http://hollanow.com/facebook_share.png"))
                .build();


        ShareButton shareButton = (ShareButton) rootView.findViewById(R.id.facebook_share_button);
        shareButton.setShareContent(sharedContent);
        shareButton.setFragment(this);
        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                mSharedPref.setShared();
                toggleProShare();
                Log.e(TAG, "shared "+ result.toString() + " - " + result.getPostId());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


//        mTags = (TextView) rootView.findViewById(R.id.pro_tags);

//        mInviteFriends = (TextView) rootView.findViewById(R.id.inviteFriends);
//        mInviteFriendsIcon = (ImageView) rootView.findViewById(R.id.inviteFriendsImage);
//        mInviteFriends.setOnClickListener(listener);
//        mInviteFriendsIcon.setOnClickListener(listener);

//        if (ParseUser.getCurrentUser().getParseFile("photo") != null) {
//            Picasso.with(mContext)
//                    .load(ParseUser.getCurrentUser().getParseFile("photo").getUrl())
//                    .into(mProfilePhoto);
//        }

        /**
         * START SERVICE OVERLAY TEST
         */
//        mFullName.setOnClickListener(serviceOverlayTest);
        /**
         * END
         */

        if (getArguments()!=null) {
            userIdIntent = getArguments().getString(ProfileActivity.USER_ID);
            usernameIntent = getArguments().getString(ProfileActivity.USER_NAME);
            fullnameIntent = getArguments().getString(ProfileActivity.FULL_NAME);
            phoneIntent = getArguments().getString(ProfileActivity.PHONE);
            emailIntent = getArguments().getString(ProfileActivity.EMAIL);
            mCallLabel.setOnClickListener(actionListener(ACTION_CALL));
            mMessageLabel.setOnClickListener(actionListener(ACTION_MESSAGE));
            mEmailLabel.setOnClickListener(actionListener(ACTION_EMAIL));
            digitsButton.setVisibility(View.GONE);
            getProInfo(userIdIntent);

        } else {
            usernameIntent = ParseUser.getCurrentUser().getUsername();
            fullnameIntent = ParseUser.getCurrentUser().getString("name");
            phoneIntent = ParseUser.getCurrentUser().getString("phoneNumber");
            emailIntent = ParseUser.getCurrentUser().getEmail();

            Log.e("Phone", ParseUser.getCurrentUser().getString("phoneNumber") + "");
            mCallLabel.setVisibility(View.GONE); //hide action if owner's profile
            mMessageLabel.setVisibility(View.GONE); //hide action if owner's profile
            mEmailLabel.setVisibility(View.GONE); //hide action if owner's profile
            digitsButton.setVisibility(View.VISIBLE);
            mChangeNumber.setText("CHANGE NUMBER");
            mChangeNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileActivity activity = (ProfileActivity) mContext;
                    FragmentManager fm = activity.getSupportFragmentManager();
                    ChangeNumberFragment fragment = new ChangeNumberFragment();
                    Bundle args = new Bundle();
                    args.putString(ProfileActivity.PHONE, phoneIntent+"");
                    fragment.setArguments(args);
                    fragment.show(fm, "CHANGE_FRAGMENT");
                }
            });

            toggleProShare();

            if (ParseUser.getCurrentUser().getString("bio")!=null) {
                mAbout.setText(ParseUser.getCurrentUser().getString("bio"));
            }
//            else {
//                mAbout.setVisibility(View.GONE);
//            }
            if (ParseUser.getCurrentUser().getString("address")!=null) {
                mAddress.setText(ParseUser.getCurrentUser().getString("address"));
            }
//            else {
//                mAddress.setVisibility(View.GONE);
//            }
            if (ParseUser.getCurrentUser().getString("occupation")!=null) {
                mOccupation.setText(ParseUser.getCurrentUser().getString("occupation"));
            }
//            else {
//                mOccupation.setVisibility(View.GONE);
//            }

        }
        mFullName.setText(fullnameIntent);
        mUserName.setText(usernameIntent);
        mPhone.setText(phoneIntent);


//        ArrayList<String> tagList = new ArrayList<String>();
//        if (ParseUser.getCurrentUser().getList("tags")!=null) {
//            for (int i = 0; i < ParseUser.getCurrentUser().getList("tags").size(); i++) {
//                tagList.add((String) ParseUser.getCurrentUser().getList("tags").get(i));
//            }
//            mTags.setText(MyToolBox.listToTaggedString(tagList));
//        } else {
//            mTags.setVisibility(View.GONE);
//        }


        return rootView;
    }

    private void toggleProShare() {
        if (mSharedPref.isShared()) {
            mProCard.setVisibility(View.VISIBLE);
            mShareLayout.setVisibility(View.INVISIBLE);
        } else {
            mProCard.setVisibility(View.INVISIBLE);
            mShareLayout.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener serviceOverlayTest = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNumber = "08037046543";
            Intent intent = new Intent(mContext.getApplicationContext(), CallNoteService.class);
            intent.putExtra(CallNote.CALLER_NUMBER, phoneNumber);
            Log.e("PRO: ", "profile activity test call note: "+ phoneNumber);
            mContext.startService(intent);
        }
    };

    private void updatePhoneNumber(final String phoneNumber) {
        mPhone.setText("updating...");
        ParseUser.getCurrentUser().put("phoneNumber", phoneNumber);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(mContext, "Phone number successfully changed", Toast.LENGTH_SHORT).show();
                mPhone.setText(phoneNumber);
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
                            args.putString(Contact.PHONE_NUMBER, phoneIntent);
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
                        if (emailIntent!=null) {
                            sendEMAILIntent(emailIntent);
                        } else {
                            Toast.makeText(mContext, "Sorry. This contact has no email address", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };
    }

    private void getProInfo(String userIdIntent) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.getInBackground(userIdIntent, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e==null) {
                    if (object.getString("bio")!=null) {
                        mAbout.setText(object.getString("bio"));
                    }
                    if (object.getString("address")!=null) {
                        mAddress.setText(object.getString("address"));
                    }
                    if (object.getString("occupation")!=null) {
                        mOccupation.setText(object.getString("occupation"));
                    }
                } else {
                    Toast.makeText(mContext, "Connection error...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendSMSIntent(String number, String msg) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", msg);
        startActivity(it);

    }

    private void sendEMAILIntent(String email) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
