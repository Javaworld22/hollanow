package com.doxa360.android.betacaller;

import android.app.Notification;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import  android.widget.LinearLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.view.ViewGroup;
import android.widget.Toast;
import android.graphics.Color;

import com.digits.sdk.android.Digits;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import android.widget.ImageView;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Token;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.ads.MobileAds;

public class HomeActivity extends AppCompatActivity {

  private static final String TAG = "HomeActivity";
  private static final String SHOWCASE_ID = "123456789";
    private File file;
    private String android_id;
  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */

  private Animation rotation;

  private ViewPager mViewPager;
  HollaNowSharedPref mSharedPref;
    public static boolean note1;
    TextView itemMessageBadgeTextview;
    private LinearLayout mTabsLinearLayout1,mTabsLinearLayout2,mTabsLinearLayout3;
    //private File file;

    private NativeExpressAdView adView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FacebookSdk.sdkInitialize(this);

      MobileAds.initialize(this,"ca-app-pub-4194808928301689/8593894567");   //ca-app-pub-4194808928301689/7724699860
      MobileAds.initialize(this,"ca-app-pub-4194808928301689/7724699860");
     // MobileAds.initialize(this);
    setContentView(R.layout.activity_home);
    mSharedPref = new HollaNowSharedPref(this);

    /** creating a file object ****/
   // file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "mikoko.txt" try {

   // mSharedPref.setRefresh_flag(true);


    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);


   // TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    //tabLayout.setupWithViewPager(mViewPager);
    //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

      final PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs1);
      tabsStrip.setViewPager(mViewPager);

    if(mSharedPref.getDeviceId().isEmpty()) {
      Log.e(TAG, "device id empty");
      sendDeviceIdToServer();
    }
      //contactManagemet(file,android_id);
      //uploadFile(file);

    mSharedPref.setTutorial(false);

    View tab1,tab2,tab3;
    tab1 =  ((ViewGroup)tabsStrip.getChildAt(0)).getChildAt(0);
    tab2 = ((ViewGroup)tabsStrip.getChildAt(0)).getChildAt(1);
    tab3 = ((ViewGroup)tabsStrip.getChildAt(0)).getChildAt(2); //( (ViewGroup) tabLayout.getChildAt(0)).getChildAt(2)

    if (tab1 != null && tab2 != null && tab3 != null) {
      if (!mSharedPref.isTutorial()) {
        startTutorialSequence(tab1, tab2, tab3);
      }
    }

    tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mTabsLinearLayout1 = (LinearLayout)tabsStrip.getChildAt(0);
            mTabsLinearLayout2 = (LinearLayout)tabsStrip.getChildAt(1);
            mTabsLinearLayout3 = (LinearLayout)tabsStrip.getChildAt(2);
            for(int i=0; i < 3; i++) {
                TextView tv1 = (TextView) mTabsLinearLayout1.getChildAt(i);

                if(i == position){
                    tv1.setTextColor(Color.WHITE);
                } else {
                    tv1.setTextColor(Color.GRAY);
                }

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    });

     // FirebaseCrash.report(new Exception("Android Error For Test"));

        //createToken("4084084084084081",1,2020,"408");
      //createToken(String cardNumber, int expiryMonth, int expiryYear, String cvv){

        Intent intent  = new Intent(this,NotificationService.class);
      startService(intent);
  }

  private void startTutorialSequence(View tab1, View tab2, View tab3) {

    ShowcaseConfig config = new ShowcaseConfig();
    config.setDelay(500); // half second between each showcase view
    MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
    sequence.setConfig(config);
    sequence.addSequenceItem(tab1,
            getString(R.string.tooltip_one), "GOT IT");
    sequence.addSequenceItem(tab2,
            getString(R.string.tooltip_two), "GOT IT");
    sequence.addSequenceItem(tab3,
            getString(R.string.tooltip_four), "GOT IT");
    sequence.start();

    mSharedPref.setTutorial(true);

  }

  private void sendDeviceIdToServer() {
      // try{
      User currentUser = mSharedPref.getCurrentUser();

      if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
              try{
          currentUser.setDeviceId(mSharedPref.getDeviceId());
          mSharedPref.setCurrentUser(currentUser.toString());

          HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
          Call<User> call = hollaNowApiInterface.editUserProfile(mSharedPref.getCurrentUser(), mSharedPref.getToken());
          call.enqueue(new Callback<User>() {
              @Override
              public void onResponse(Call<User> call, Response<User> response) {
                  if (response.code() == 200) {
                      Log.e(TAG, "success " + response.body().getDeviceId() + "");
//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                  } else {
                      Log.e(TAG, "error: " + "error updating device id");
                      Toast.makeText(getApplicationContext(), "Error updating Device id", Toast.LENGTH_SHORT).show();
                  }

              }

              @Override
              public void onFailure(Call<User> call, Throwable t) {
//                    Log.e(TAG, "Device id update failed: " + t.getMessage());
//                    Toast.makeText(getApplicationContext(), "Network error. Try again", Toast.LENGTH_LONG).show();

              }
          });
           }catch(NullPointerException e){

                   Toast.makeText(getApplicationContext(), "Error. Current User not set", Toast.LENGTH_LONG).show();

              }
      }

  }



//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem menuItem = menu.findItem(R.id.action_profile);
//
//
//
//        MLRoundedImageView imageView = new MLRoundedImageView(HomeActivity.this);
//
//        if (ParseUser.getCurrentUser().getParseFile("photo")!=null){
//            Picasso.with(this)
//                    .load(ParseUser.getCurrentUser().getParseFile("photo").getUrl())
//                    .placeholder(R.drawable.ic_account_circle_black_24dp)
//                    .error(R.drawable.ic_account_circle_black_24dp)
//                    .centerCrop()
//                    .resize(18, 18)
//                    .into(imageView);
//        } else {
//            Picasso.with(this)
//                    .load(R.drawable.wil_profile)
//                    .placeholder(R.drawable.ic_account_circle_black_24dp)
//                    .error(R.drawable.ic_account_circle_black_24dp)
//                    .centerCrop()
//                    .resize(18, 18)
//                    .into(imageView);
//            imageView.setMaxHeight(18);
//            imageView.setMaxWidth(18);
//        }
//
//        imageView.setBackground(getResources().getDrawable(R.drawable.circle));
//
//        menuItem.setActionView(imageView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                if (ParseUser.getCurrentUser().getParseFile("photo")!=null) {
//                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
//                    startActivity(intent);
////                } else {
////                    MyToolBox.AlertMessage(HomeActivity.this, "Network error. Please check your connection.");
////                }
//            }
//        });
//        return super.onPrepareOptionsMenu(menu);
//    }
//

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_home, menu);
      LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      MenuItem itemMessage = menu.findItem(R.id.action_notification);
      MenuItem refresh = menu.findItem(R.id.action_refresh);
      refresh.setVisible(false);
      ImageView itemRefresh = (ImageView) inflater.inflate(R.layout.refresh_action_view,null);
      RelativeLayout badgeLayout = (RelativeLayout)itemMessage.getActionView();
      itemMessageBadgeTextview = (TextView)badgeLayout.findViewById(R.id.notificationTextview);
      itemMessageBadgeTextview.setVisibility(View.INVISIBLE);

      if(!mSharedPref.getNotification_flag()) {
          itemMessageBadgeTextview.setText("1");
          itemMessageBadgeTextview.setVisibility(View.VISIBLE);
      }

      ImageView iconButttonMessage = (ImageView)badgeLayout.findViewById(R.id.notificationButton);
      iconButttonMessage.setOnClickListener(new View.OnClickListener(){
      @Override
            public void onClick(View view) {
          Log.e(TAG, "Login NotificationActivity.class");
          itemMessageBadgeTextview.setVisibility(View.INVISIBLE);
          Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
          startActivity(intent);
          note1 = true;
          mSharedPref.setNotification_flag(note1);

          //Bundle args = new Bundle();
          //callNoteBottomSheet.setArguments(args);
          //callNoteBottomSheet..show(fragmentManager, "CALL_NOTE");
      }
       });
    //  MenuItem itemMessage1 = menu.findItem(R.id.action_refresh);
      String re =  mSharedPref.getFlagContacts();

      rotation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.clockwise_refresh);
      rotation.setRepeatCount(Animation.INFINITE);
      if(mSharedPref.getrefresh_flag()) {
          refresh.setVisible(true);
          itemRefresh.startAnimation(rotation);
          refresh.setActionView(itemRefresh);
      }
     // if(re.equals("Update"))
          //if(mSharedPref.getrefresh_flag())
         // itemMessage1.setVisible(true);
     // else if(!(re.equals("Update")))
      //    itemMessage1.setVisible(false);

     // for(int i =0; i<menu.size();i++){
          //Drawable drawable = menu.getItem(0).getIcon();
         // if(drawable != null){
         //     drawable.mutate();
         //     drawable.setColorFilter(getResources().getColor(R.color.hollabackground), PorterDuff.Mode.SRC_ATOP);
       //   }
      //}
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

//        if (id == R.id.action_search) {
//            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//            startActivity(intent);
//        }

    if (id == R.id.action_profile) {
      Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
       // Intent intent = new Intent(HomeActivity.this, PaystackActivity.class);
      startActivity(intent);
    }

   // if(id)

    if (id == R.id.action_settings) {
      Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
      startActivity(intent);
      return true;
    }

   // if(id == R.id.action_refresh){
   //     Intent intent = new Intent(this, HomeActivity.class);
   //     startActivity(intent);
       // FragmentManager fm = getSupportFragmentManager();
       // HomeFragment fragment = new HomeFragment();

       // fragment.closefragment();
       // FragmentTransaction ft = fm.beginTransaction();
       // ft.detach(fragment);
       // ft.attach(fragment);
              // ft.replace(R.id.fragment_container_home, fragment, "PROFILE2");
       // ft.commit();

   // }
    if (id == R.id.action_invite) {
      String appLinkUrl, previewImageUrl;

      appLinkUrl = "https://play.google.com/store/apps/details?id=com.doxa360.android.betacaller";
      previewImageUrl = "http://hollanow.com/img/bclogo.png";

      if (AppInviteDialog.canShow()) {
        AppInviteContent content = new AppInviteContent.Builder()
                .setApplinkUrl(appLinkUrl)
//                        .setPreviewImageUrl(previewImageUrl)
                .build();
        AppInviteDialog.show(this, content);
      } else {
        Toast.makeText(HomeActivity.this, "You do not have the Facebook app installed on this device", Toast.LENGTH_SHORT).show();
      }
    }

    if (id == R.id.action_logout) {
      HollaNowSharedPref sharedPref = new HollaNowSharedPref(HomeActivity.this);
      if (sharedPref.clearCurrentUser()) {
          sharedPref.setAnotherDevice_flag(false);
          sharedPref.setRefresh_flag(false);
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Digits.clearActiveSession();
          FirebaseAuth.getInstance().signOut();
      }
    }

    return super.onOptionsItemSelected(item);
  }


  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment = null;
      switch (position) {
        case 0:
          fragment = new HomeFragment();
          break;
        case 1:
          fragment = new SearchFragment();
          break;
        case 2:
          fragment = new CallDiaryFragment();
//                    tabToolTip(2, "Welcome. Access your phone contacts here");
          break;
//                case 3:
//                    fragment = new ProfileFragment();
//                    break;
      }
      return fragment;
    }

    @Override
    public int getCount() {
      // Show 3 total pages.
      return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return "Contacts";
        case 1:
          return "Search";
        case 2:
          return "Call Diary";
//                case 3:
//                    return "Profile";
      }
      return null;
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    //bbb
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();

    } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
      String uri = intent.getDataString();
      Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
    }
  }
    @Override
        public void onDestroy(){
            super.onDestroy();

    }

  //  @Override
  //  public void onBackPressed(){
    //    if(getFragmentManager().getBackStackEntryCount() == 0){
    //        super.onBackPressed();
     //   }else
     //       getFragmentManager().popBackStack();
   // }

   /** private void createToken(String cardNumber, int expiryMonth, int expiryYear, String cvv){
        Card card = new Card.Builder(cardNumber,expiryMonth,expiryYear,cvv).build();
        PaystackSdk.createToken(card,new Paystack.TokenCallback(){
            @Override
            public void onCreate(Token token){
                Log.e(TAG, "Token for the Card: "+token.token);
                Log.e(TAG, "Token for the Card: "+token.token);

                Log.e(TAG, "Token for the Card: "+token.token);
                Log.e(TAG, "Token for the Card: "+token.token);
                Log.e(TAG, "Token for the Card: "+token.token);
                Log.e(TAG, "Token for the Card: "+token.token);
            }
            @Override
            public void onError(Exception e){
                Log.e(TAG, "Error Occured at Card Number: "+e.getMessage());
                Log.e(TAG, "Error Occured at Card Number: "+e.getMessage());
                Log.e(TAG, "Error Occured at Card Number: "+e.getMessage());

            }
        });
    } **/


}
