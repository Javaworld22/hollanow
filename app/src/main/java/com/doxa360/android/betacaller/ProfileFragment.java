package com.doxa360.android.betacaller;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.doxa360.android.betacaller.model.User;
import com.facebook.CallbackManager;

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
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView mProfilePhoto, mInviteFriendsIcon, mShareAppIcon;
    private TextView mFullName, mUserName, mPhone, mAbout, mTags, mInviteFriends, mShareApp, mAddress, mOccupation, mChangeNumber, mIndustry;
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
    private User currentUser;


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
        mCallLabel = (TextView) rootView.findViewById(R.id.call_label);
        mMessageLabel = (TextView) rootView.findViewById(R.id.message_label);
        mEmailLabel = (TextView) rootView.findViewById(R.id.email_label);
        mChangeNumber = (TextView) rootView.findViewById(R.id.change_number_label);

        mProCard = (CardView) rootView.findViewById(R.id.pro_card);
        mSharedPref = new HollaNowSharedPref(mContext);
        currentUser = mSharedPref.getCurrentUser();

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

        if (getArguments()!=null) {
            User userProfile = getArguments().getParcelable(BetaCaller.USER_PROFILE);
            if (userProfile != null) {
//                usernameIntent = getArguments().getString(userProfile.getUsername());
//                fullnameIntent = getArguments().getString(userProfile.getName());
//                phoneIntent = getArguments().getString(userProfile.getPhone());
//                emailIntent = getArguments().getString(userProfile.getEmail());

                usernameIntent = userProfile.getUsername();
                fullnameIntent = userProfile.getName();
                phoneIntent = userProfile.getPhone();
                emailIntent = userProfile.getEmail();

                mCallLabel.setOnClickListener(actionListener(ACTION_CALL));
                mMessageLabel.setOnClickListener(actionListener(ACTION_MESSAGE));
                mEmailLabel.setOnClickListener(actionListener(ACTION_EMAIL));

                mChangeNumber.setVisibility(View.GONE);

                mAbout.setText((userProfile.getAbout()!=null)?userProfile.getAbout():"-");
                mAddress.setText((userProfile.getAddress()!=null)?userProfile.getAddress():"-");
                mOccupation.setText((userProfile.getOccupation()!=null)?userProfile.getOccupation():"-");
                mIndustry.setText((userProfile.getIndustry()!=null)?userProfile.getIndustry():"-");
            }


        } else {
            usernameIntent = currentUser.getUsername();
            fullnameIntent = currentUser.getName();
            phoneIntent = currentUser.getPhone();
            emailIntent = currentUser.getEmail();

            mCallLabel.setVisibility(View.GONE); //hide action if owner's profile
            mMessageLabel.setVisibility(View.GONE); //hide action if owner's profile
            mEmailLabel.setVisibility(View.GONE); //hide action if owner's profile
            mChangeNumber.setVisibility(View.VISIBLE);
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

        }

        mFullName.setText(fullnameIntent);
        mUserName.setText(usernameIntent);
        mPhone.setText(phoneIntent);

        return rootView;
    }

    private void verifyPhoneNumber(String phoneIntent) {
        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(new AuthCallback() {
                    @Override
                    public void success(DigitsSession session, String phoneNumber) {
                        //TODO: check phone availablity?
                        updatePhoneNumber(phoneNumber);
                    }

                    @Override
                    public void failure(DigitsException error) {
                        Toast.makeText(mContext, "Phone number not changed", Toast.LENGTH_SHORT).show();
                    }
                })
                .withPhoneNumber(phoneIntent);

        Digits.authenticate(authConfigBuilder.build());
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
        Call<User> call = hollaNowApiInterface.editUserPhone(currentUser, mSharedPref.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    currentUser.setPhone(response.body().getPhone());
                    mSharedPref.setCurrentUser(currentUser.toString());
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

}
