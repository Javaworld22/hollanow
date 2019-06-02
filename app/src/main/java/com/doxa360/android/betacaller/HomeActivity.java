package com.doxa360.android.betacaller;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;
import android.content.pm.PackageManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
//import com.android.billingclient.api.BillingClient;
//import com.android.vending.billing.IInAppBillingService;
import com.digits.sdk.android.Digits;
//import com.doxa360.android.betacaller.billing.BillingUpdatesListener;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.service.HollaAlarmService;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import java.io.File;


import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.NativeExpressAdView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.ads.MobileAds;

import com.hypertrack.hyperlog.HyperLog;
import com.rockerhieu.emojicon.EmojiconsFragment;

public class HomeActivity extends AppCompatActivity implements EmojiconsFragment.OnEmojiconBackspaceClickedListener{

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
  private TabLayout tabLayout;
  HollaNowSharedPref mSharedPref;
    public static boolean note1;
    TextView itemMessageBadgeTextview;
    private LinearLayout mTabsLinearLayout1,mTabsLinearLayout2,mTabsLinearLayout3;
    //private File file;

    private NativeExpressAdView adView;
    private FloatingActionButton fabs;
    private FloatingActionButton fabShout;
    private FloatingActionButton searchNumber;

    private Activity mActivity;
   // private BillingUpdatesListener mBillingUpdateListener;
   // private BillingClient mBilligClient;
    private Context mContext;
    private int tabIcon[] = {}; //R.drawable.ic_account_circle_white_24dp,R.drawable.personalidentity1,R.drawable.personalidentity1
    private String tabText[] = {"Holla","Search","Contacts"}; //"Holla","Search","Contacts"
    private String[] zoneIds = {"vzfae61b44bd9a496197","vzff95da0d36de425d9c"};
    private PagerSlidingTabStrip mTabStripLayout;

   // private WorkManager nWorkManager;

    private IntentFilter filter;
    private BroadcastReceiver mReceiver;

    //private static boolean pageTransit; // this provides the control needed to navigate between CallDiary and HomeFragment


   // public HomeActivity(Activity activity, final BillingUpdatesListener updateslistener){

    ///        mActivity = activity;
    //        mBillingUpdateListener = updateslistener;
   //        /** mBilligClient =**/ new BillingClient.Builder().newBuilder(mActivity)
  //  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FacebookSdk.sdkInitialize(this);
    // This api is to record log files offline;
      HyperLog.initialize(this);
      HyperLog.setLogLevel(Log.ERROR);
   // pageTransit = false;
      AdColonyAppOptions appOptions = new AdColonyAppOptions()
              .setUserID("Javaworld22");
      mContext = getApplicationContext();

      MobileAds.initialize(this,"ca-app-pub-4194808928301689/8593894567");   //ca-app-pub-4194808928301689/7724699860
      MobileAds.initialize(this,"ca-app-pub-4194808928301689/7724699860");
     // AdColony.configure(this,"app8941dd41be7e45cf86","vzfae61b44bd9a496197");
      AdColony.configure(this,"app8941dd41be7e45cf86",zoneIds);
      ComponentName receiver = new ComponentName(mContext, CallReceiver.class);
      PackageManager pkgManager = mContext.getPackageManager();
      pkgManager.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
              PackageManager.DONT_KILL_APP);

      ComponentName receiver1 = new ComponentName(mContext, HollaAlarmService.class);
      PackageManager pkgManager1 = mContext.getPackageManager();
      pkgManager1.setComponentEnabledSetting(receiver1,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
              PackageManager.DONT_KILL_APP);
      pkgManager1.setApplicationEnabledSetting("com.doxa360.android.betacaller",PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
              PackageManager.DONT_KILL_APP);

       String SDPath = "https://hollanow.com/assets/photos/merchant";
     // HyperLog.setURL(SDPath);
     // File file3 = HyperLog.getDeviceLogsInFile(this);
      //HyperLog.e(TAG, "Using Hyperlog is cool");
      //HyperLog.e(TAG, "What is this?");

    /**  HyperLog.pushLogs(this,false, new HLCallback(){
          @Override
          public void onSuccess(@NonNull Object response){
              Log.e(TAG, "Successful from HyperLog");

              Log.e(TAG, "Please view this "+response.toString());
          }
          @Override
          public void onError(@NonNull HLErrorResponse errorResponse){
              Log.e(TAG, "Error from HyperLog");
              Log.e(TAG, "view error message "+errorResponse.getErrorMessage());
          }
      });*/
     // File file3 = HyperLog.getDeviceLogsInFile(this,"MichaelLog.txt",true);

     // if(nWorkManager == null) {
      //    nWorkManager = WorkManager.getInstance();
        //  Log.e(TAG, "WorkManager is null. ");
     // }

     // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         // Log.e(TAG, "Telephony started here in android 7. ");
         // startWorkManager("07037304321");
          // phoneRinging = true;
        //  Log.e(TAG, "This is for only android 7. ");
        //  Log.e(TAG, "This is where startWorkManager begins. ");
    //  }

       String permission = Manifest.permission.CALL_PHONE;
       String permission1 = Manifest.permission.PROCESS_OUTGOING_CALLS;
    /**  if(ContextCompat.checkSelfPermission(HomeActivity.this,permission) != PackageManager.PERMISSION_GRANTED){

          // Please show an explanation

          if(ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,permission)){
              // This ia called up when user declines a permission before
              // in this case i am asking for another permission again

              ActivityCompat.requestPermissions(HomeActivity.this,new String[]{permission, permission1},2);
          }else{
              ActivityCompat.requestPermissions(HomeActivity.this,new String[]{permission, permission1},2);
          }
      }else{
          Log.e(TAG, "This permission is already granted for phone state");
      }**/


     /** if(ContextCompat.checkSelfPermission(HomeActivity.this,permission1) != PackageManager.PERMISSION_GRANTED){

          // Please show an explanation

          if(ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,permission1)){
              // This ia called up when user declines a permission before
              // in this case i am asking for another permission again

              ActivityCompat.requestPermissions(HomeActivity.this,new String[]{permission1},1);
          }else{
              ActivityCompat.requestPermissions(HomeActivity.this,new String[]{permission1},1);
          }
      }else{
          Log.e(TAG, "This permission is already granted for outgoing");
      } */



     // if(filter == null) {
      //    Log.e(TAG, "OnstartJob action ");
       //   filter = new IntentFilter();
       //   filter.addAction("android.intent.action.PHONE_STATE");
       //   filter.addAction("android.intent.action.READ_PHONE_STATE");
      //}

      //mReceiver = new BroadcastReceiver() {
        //  @Override
        //  public void onReceive(Context context, Intent intent) {
       //       Log.e(TAG, "Phone state receiver ");
       //   }
      //};
      //HomeActivity.this.registerReceiver(mReceiver, filter);

      MobileAds.initialize(this,"ca-app-pub-4194808928301689/5073664952");
     // MobileAds.initialize(this);
    setContentView(R.layout.activity_home);
    CoordinatorLayout mainLayout = (CoordinatorLayout) findViewById(R.id.home_coordinatorlayout);
      mViewPager = (ViewPager) findViewById(R.id.container);
        mTabStripLayout = new PagerSlidingTabStrip(mContext);  //(PagerSlidingTabStrip) findViewById(R.id.tabs1);
      tabLayout = (TabLayout) findViewById(R.id.tabs);
      tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
      tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
      tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));



      mTabStripLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 48/** 140 **/));
      mTabStripLayout.setShouldExpand(true);
      mTabStripLayout.setAllCaps(true);
      mTabStripLayout.setTextSize(30);
     // mTabStripLayout.setTextColor(mContext.getResources().getColor(R.color.textColor));
     // mTabStripLayout.setDividerColor(mContext.getResources().getColor(R.color.accent));
      mTabStripLayout.setDividerPadding(0);
     // mTabStripLayout.setIndicatorColor(mContext.getResources().getColor(R.color.holla_white));
      mTabStripLayout.setIndicatorHeight(3);
      mTabStripLayout.setUnderlineColor(Color.BLUE);
      mTabStripLayout.setTabBackground(10);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      fabs = (FloatingActionButton) findViewById(R.id.fab);
      fabShout = (FloatingActionButton) findViewById(R.id.fab_shout);
      searchNumber = (FloatingActionButton) findViewById(R.id.search_number);
      fabShout.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(mContext, ShoutOutActivity.class);
              startActivity(intent);
          }

      });

      fabs.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
             Intent intent = new Intent(mContext, HomeContactActivity.class);
             // intent.putExtra("","activity_shoutout");
              startActivity(intent);
          }
      });

      searchNumber.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
              Intent intent = new Intent(mContext, SearchActivity.class);
              intent.putExtra("Search","Numbers");
              startActivity(intent);
          }
      });

     // FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mainLayout.getLayoutParams();
     // lp.setBehavior(new AppBarLayout.Behavior());

//      mainLayout.addView(mViewPager);



      mSharedPref = new HollaNowSharedPref(this);


    /** creating a file object ****/
   // file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ File.separator + "mikoko.txt" try {

   // mSharedPref.setRefresh_flag(true);



    setSupportActionBar(toolbar);


    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
    // Set up the ViewPager with the sections adapter.




      AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
     // AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
      // lp.setScrollFlags(SCROLL_FLAG_ENTER_ALWAYS);
      //appBarLayout.addView(mTabStripLayout,lp);


   // TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    //tabLayout.setupWithViewPager(mViewPager);
    //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

      mViewPager.setAdapter(mSectionsPagerAdapter);
      tabLayout.setupWithViewPager(mViewPager);
      mViewPager.setPageTransformer(true, new RotateUpTransformer());
     // mTabStripLayout.setViewPager(mViewPager);

      mViewPager.setCurrentItem(1);

      showRightFab(mViewPager.getCurrentItem());

    if(mSharedPref.getDeviceId().isEmpty()) {
      Log.e(TAG, "device id empty");
      sendDeviceIdToServer();
    }
      //contactManagemet(file,android_id);
      //uploadFile(file);

    mSharedPref.setTutorial(false);

    View tab1,tab2,tab3;
    tab1 =  /**((ViewGroup)mTabStripLayout.getChildAt(0)).getChildAt(0);**/((ViewGroup)tabLayout.getChildAt(0)).getChildAt(0);
    tab2 = /**((ViewGroup)mTabStripLayout.getChildAt(0)).getChildAt(1);**/ ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(1);
    /**((ViewGroup)mTabStripLayout.getChildAt(0)).getChildAt(2);**/ //( (ViewGroup) tabLayout.getChildAt(0)).getChildAt(2)
      tab3 =    ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(2);

    if (tab1 != null && tab2 != null && tab3 != null) {
      if (!mSharedPref.isTutorial()) {
        startTutorialSequence(tab1, tab2, tab3);
      }
    }
     // mTabsLinearLayout1 = (LinearLayout)mViewPager.getChildAt(0);
     // TextView tv1 = (TextView) mViewPager.getChildAt(1);
     // tv1.setTextColor(Color.WHITE);

      mTabStripLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
           // mTabsLinearLayout1 = (LinearLayout)mViewPager.getChildAt(0);
          //  mTabsLinearLayout2 = (LinearLayout)mViewPager.getChildAt(1);
          //  mTabsLinearLayout3 = (LinearLayout)mViewPager.getChildAt(2);
            Log.e(TAG, "Gray is showing here 2 "+ 44);
            Log.e(TAG, "Gray is showing here 4  "+55);
            TextView []tv = new TextView[3];
           for(int i=0; i < 3; i++) {
               tv[i] = (TextView) mViewPager.getChildAt(i);
               tv[i].setTextColor(Color.GRAY);
               Log.e(TAG, "Gray is showing here "+tv[position].getText());
               Log.e(TAG, "Gray is showing here "+tv[position].getText());
           }

               // if(i == position){
                    tv[position].setTextColor(Color.WHITE);
            Log.e(TAG, "White is showing here "+tv[position].getText());
            Log.e(TAG, "White is showing here "+tv[position].getText());
             //   }

        //    }
            showRightFab(position);

        }
        public void onPageScrollStateChanged(int state) {

        }
    });
      //tabLayout.addOnTabSelectedListener();

    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Log.e(TAG, "White is showing here 2 "+tab.getPosition() );
            Log.e(TAG, "White is showing here 4 "+tab.getPosition());
            if(tab.getPosition() == 1 || tab.getPosition() == 2)
                mSharedPref.setPageTransit(false); // transition between CallDiary and HomePage
            mSharedPref.setView(tab.getPosition());
           // mViewPager.getAdapter().notifyDataSetChanged();
            showRightFab(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    });

     // FirebaseCrash.report(new Exception("Android Error For Test"));

        //createToken("4084084084084081",1,2020,"408");
      //createToken(String cardNumber, int expiryMonth, int expiryYear, String cvv){
      Log.e(TAG, "Launch service intent here  1");
// if(getIntent() == null || getIntent().getStringExtra("shout_out") == null ||
  //       !getIntent().getStringExtra("shout_out").equals("activity_shoutout") ) {
  //   Log.e(TAG, "Launch service intent here  2");
    // Intent intent = new Intent(this, NotificationService.class);
    // startService(intent);
 //}

       if(getIntent() != null){
          // Log.e(TAG, "GetIntent() must show something  "+getIntent().getStringExtra("return_notification"));
          // Log.e(TAG, "GetIntent() must show something  "+getIntent().getStringExtra("return_shoutout"));
          // Log.e(TAG, "GetIntent() must show something  "+getIntent().getStringExtra("shout_out"));
          if (getIntent().getStringExtra("return_notification") != null) {
              if (getIntent().getStringExtra("return_notification").equals("note")) {
                  Log.e(TAG, "HollaContact page to show ");
                  mViewPager.setCurrentItem(0);
              }


          }else if (getIntent().getStringExtra("return_shoutout") != null) {
              if (getIntent().getStringExtra("return_shoutout").equals("shout")) {
                  Log.e(TAG, "Shoutout page  ");
                  mViewPager.setCurrentItem(2);
              }


          }else if (getIntent().getStringExtra("shout_out") != null) {
              if (getIntent().getStringExtra("shout_out").equals("activity_shoutout")) {
                  Log.e(TAG, "Launch service intent here  ");
                  Intent intent = new Intent(this, NotificationService.class);
                  intent.putExtra("service_start", "for_shouout");
                  startService(intent);
                  mViewPager.setCurrentItem(2);
              }
          }}else {
           Log.e(TAG, "GetIntent() is Empty  here");
           mViewPager.setCurrentItem(1);
       }

      //Log.e(TAG, "Facebook Profile "+Profile.getCurrentProfile().getId());
     // Log.e(TAG, "Facebook Profile "+Profile.getCurrentProfile().getFirstName());
     // Log.e(TAG, "Facebook Profile "+Profile.getCurrentProfile().getName());
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

      if(mSharedPref.getNotification_flag() > 0) {
          int value = mSharedPref.getNotification_flag();
          itemMessageBadgeTextview.setText(Integer.toString(value));
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
          mSharedPref.setNotification_flag(0);

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
  public class SectionsPagerAdapter extends FragmentPagerAdapter /**implements PagerSlidingTabStrip.IconTabProvider**/{

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment = null;
      switch (position) {
        case 0:
            fragment = new CallDiaryFragment();
            Log.e(TAG, "CallDiaryFragrament "+mSharedPref.isTransit());
           // fabs.setVisibility(View.VISIBLE);
          break;
        case 1:
            fragment =  new SearchFragment();
            Log.e(TAG, "SearchFragrament "+position);
          //  fabs.setVisibility(View.INVISIBLE);
          break;
        case 2:
            fragment = new FragmentShoutOut();
            Log.e(TAG, "FragmentFragrament "+position);
          //  fabs.setVisibility(View.VISIBLE);
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

  /**     @Override
      public int getPageIconResId(int position){
      return tabIcon[position];
      } **/

    @Override
    public CharSequence getPageTitle(int position) {
     /**   Drawable image = getResources().getDrawable(tabIcon[position]);
        image.setBounds(5,5,image.getIntrinsicWidth(),image.getIntrinsicHeight());
        SpannableStringBuilder sb = new SpannableStringBuilder(tabText[position]);
        ImageSpan imageSpan = new ImageSpan(image,ImageSpan.ALIGN_BASELINE);
        sb.setSpan(imageSpan,0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;**/
      switch (position) {
        case 0:
          return "Holla";
        case 1:
          return "Search";
        case 2:
          return "Shout-out";
//                case 3:
//                    return "Profile";
      }
      return null;
    }

   // @Override
    //  public int getItemPosition(Object object){
     //   return POSITION_NONE;
   // }


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
            //HomeActivity.this.unregisterReceiver(mReceiver);

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

  // @Override
  // public void onEmojiconClicked(Emojicon emojicon) {

   //    EmojiconsFragment.input(mEditEmojicon, emojicon);
   //}

    /**
     * It called, when backspace button of Emoticons pressed
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

       // EmojiconsFragment.backspace(mEditEmojicon);
    }

   private void showRightFab(int tab){
        switch (tab){
            case 0:
                //searchNumber.hide();
                fabShout.show();
                fabShout.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);
                        fabs.show();
                    }
                });

                searchNumber.show();
                searchNumber.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);
                        fabs.show();
                    }
                });


                break;

            case 1:
                searchNumber.show();
                fabs.show();
                fabs.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onHidden(fab);
                        searchNumber.show();
                    }
                });
               fabShout.show();
                fabShout.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onHidden(fab);
                        searchNumber.show();
                    }
                });


                break;

            case 2:
                fabShout.show();
                fabs.show();
                fabs.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onHidden(fab);
                        fabShout.show();
                    }
                });
                searchNumber.show();
                searchNumber.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onHidden(fab);
                        fabShout.show();
                    }
                });

                break;

                default:
                    fabs.hide();
                    fabShout.hide();
                    searchNumber.hide();
        }
   }



    /**@Override
    public void  onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(ActivityCompat.checkSelfPermission(HomeActivity.this,permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch(requestCode) {
                case 1:
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS) !=
                            PackageManager.PERMISSION_GRANTED)
                        Log.e(TAG, "Outgoing permission not granted ");
                    break;

                case 2:
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED)
                        Log.e(TAG, "Phone state permission not granted ");
                    break;

            }
        }
    }**/



}
