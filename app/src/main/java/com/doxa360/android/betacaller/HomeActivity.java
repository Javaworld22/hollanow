package com.doxa360.android.betacaller;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

import com.digits.sdk.android.Digits;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MLRoundedImageView;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String SHOWCASE_ID = "123456789";
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
    private ViewPager mViewPager;
    HollaNowSharedPref mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_home);
        mSharedPref = new HollaNowSharedPref(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if(mSharedPref.getDeviceId().isEmpty()) {
            Log.e(TAG, "device id empty");
            sendDeviceIdToServer();
        }

        mSharedPref.setTutorial(false);

        View tab1,tab2,tab3;
        tab1 = ( (ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        tab2 = ( (ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        tab3 = ( (ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);

        if (tab1 != null && tab2 != null && tab3 != null) {
            if (!mSharedPref.isTutorial()) {
                startTutorialSequence(tab1, tab2, tab3);
            }
        }
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
        User currentUser = mSharedPref.getCurrentUser();
        if (MyToolBox.isNetworkAvailable(getApplicationContext())) {
            currentUser.setDeviceId(mSharedPref.getDeviceId());
            mSharedPref.setCurrentUser(currentUser.toString());
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
            Call<User> call = hollaNowApiInterface.editUserProfile(mSharedPref.getCurrentUser(), mSharedPref.getToken());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200) {
                        Log.e(TAG, "success " + response.body().getDeviceId()+"");
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
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
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
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Digits.clearActiveSession();
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
                    return "Home";
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


}
