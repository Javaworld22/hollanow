package com.doxa360.android.betacaller;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.CategoryAdapter;
import com.doxa360.android.betacaller.adapter.NearbyUserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Category;
import com.doxa360.android.betacaller.model.Industry;
import com.doxa360.android.betacaller.model.PhoneCallLog;
import com.doxa360.android.betacaller.model.SalesMarket;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.service.HollaAlarmService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.intentfilter.androidpermissions.PermissionManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.thinkincode.utils.views.HorizontalFlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1044;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;

    private static final String TAG = "Search Fragment";
    private CardView mSearchLayout;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private TextView mTurnLocation;
    private ImageView mSearchNearby;
    private CategoryAdapter mAdapter;
    private ProgressBar mProgressBar;
    private HorizontalFlowLayout mCategoryLayout;
    private SearchView mSearchView;
    private ParseGeoPoint mCurrentParseGeopoint;
    List<User> nearbyUsersList, nearbyUsersList1;
    NearbyUserAdapter nearbyUsersAdapter;
    HollaNowSharedPref mSharedPref;
    private boolean isConnected = false;
    private Status status;
    private HollaNowApiInterface hollaNowApiInterface;
    private TextView mEmptyIndustry;

    private int marketCount = 0;
    private static int mCount = 0;
    private String marketFlow;
    private SalesMarket market;
    private static boolean flag_state;
    private SearchFragment search;

    private AdView mAdView ;

    private String android_id;

    private Geocoder gcd;
    private String country;
    private HollaNowDbHelper hollaNowDbHelper;
    private static final int MY_PERMISSION_REQUEST_CALL_PHONE = 1;
    private SearchFragment fragment;
   // private FloatingActionButton fabs;
    private static final int OVERLAY_PERMISSION_CODE = 2525;



    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          fragment = this;
        if(mContext == null)
           mContext = getContext();
       // BetaCaller.startJob(getContext());
       // Log.e(TAG, " Itel 1516 androi device id is 1");
        mSharedPref = new HollaNowSharedPref(mContext);
        gcd = new Geocoder(getContext(),Locale.getDefault());
        search = this;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(fragment)
                .addOnConnectionFailedListener(fragment)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)// 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 5 second, in milliseconds

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(false);
        mLocationSettingsRequest = builder.build();

        saveLocation();

        hollaNowDbHelper = new HollaNowDbHelper(mContext);
        addOverlay();
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_search, container, false);
        Log.e(TAG, " Itel 1516 androi device id is2 ");
        if(mContext != null) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
               // BetaCaller.startJob(mContext);
                HollaAlarmService.setAlarm(true, mContext);
                BetaCaller.startJobPeroidic(mContext);
                BetaCaller.startJobPeroidic2(mContext);
                BetaCaller.startJobPeroidic3(mContext);
            }
        }

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mCategoryLayout = (HorizontalFlowLayout) rootView.findViewById(R.id.category_horizontal_layout);
        mEmptyIndustry = (TextView) rootView.findViewById(R.id.empty_industry);
        mSearchLayout = (CardView) rootView.findViewById(R.id.search_card);
        mTurnLocation = (TextView) rootView.findViewById(R.id.turn_on_location_label);
        mSearchNearby = (ImageView) rootView.findViewById(R.id.nearby_search_icon);
        //fabs.setVisibility(View.VISIBLE);
        mEmptyIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                getIndustries();
            }
        });
        mSearchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NearbySearchActivity.class);
                mContext.startActivity(intent);
            }
        });
        mTurnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                turnOnLocation();
            }
        });
        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.nearby_contacts_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);


        if (MyToolBox.isNetworkAvailable(mContext)) {
            getIndustries();
        } else {
            MyToolBox.AlertMessage(mContext, "Network error. Check your connection");
        }
        mAdView =  (AdView) rootView.findViewById(R.id.adView5);
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        AdRequest request = new AdRequest.Builder()
               // .addTestDevice("bbb0eb0fce7bf3a8")  //  bbb0eb0fce7bf3a8
                //.addTestDevice("62E45C4B92D0D341956DA7147CACA43D")    //
                .build();  
        mAdView.loadAd(request);
       // Log.e(TAG, " Itel 1516 androi device id is "+android_id);
        saveLocation();

        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
         //   startNotification1(mContext);
       // }


       //  PermissionManager permissionManager = PermissionManager.getInstance(mContext);
        // permissionManager.checkPermissions(Collections.singleton(Manifest.permission.CALL_PHONE),
           //      new PermissionManager.PermissionRequestListener(){
           //  @Override
           //          public void onPermissionGranted(){
           //      Log.e(TAG, " Access granted for call state ");
           //  }
           //  @Override
            //         public void onPermissionDenied(){
                // Log.e(TAG, " Access denied for call state  ");
            // }
             //    });







        return rootView;
    }

    private void turnOnLocation() {
        try {
            // Show the dialog by calling startResolutionForResult(), and check the result
            // in onActivityResult().
            if(status != null)
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "PendingIntent unable to execute request.");
        }
        Log.e(TAG, "turn location on clicked");
    }
     /**   private void findNearbyUsers() {
        mCurrentParseGeopoint = new ParseGeoPoint(mSharedPref.getLattitude(), mSharedPref.getLongtitude());
        ParseQuery<ParseUser> nearbyQuery = ParseUser.getQuery();
        nearbyQuery.whereNear("lastSeen", mCurrentParseGeopoint);
        nearbyQuery.whereWithinMiles("lastSeen", mCurrentParseGeopoint, 25.0); //25 miles
        nearbyQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        nearbyQuery.whereNotEqualTo("searchVisible", false);
        nearbyQuery.setLimit(6);
        nearbyQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> nearbyUsers, ParseException e) {
                if (e==null) {
                    nearbyUsersList = new ArrayList<User>();
                    nearbyUsersList.addAll(nearbyUsers);
                    nearbyUsersList.add(null);
                    nearbyUsersAdapter = new NearbyUserAdapter(nearbyUsersList, mCurrentParseGeopoint, mContext);
                    mRecyclerView.setAdapter(nearbyUsersAdapter);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
   }**/




    private void findNearbyUsers() {
        if (MyToolBox.isNetworkAvailable(mContext)) {

            //  findNearbyUsers(mSharedPref.getLattitude(), mSharedPref.getLongtitude(),
               //       BetaCaller.LOCATION_DISTANCE_CLOSE, mSharedPref.getToken());

            Call<List<User>> call = hollaNowApiInterface.getNearbyUsers(mSharedPref.getLattitude(), mSharedPref.getLongtitude(),
                    BetaCaller.LOCATION_DISTANCE_FARTHER, mSharedPref.getToken()); // 4km

            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.code() == 200) {
                        nearbyUsersList = new ArrayList<User>();
                        nearbyUsersList1 = new ArrayList<User>();
                        if (response.body().size() == 0) {
//                            mEmpty.setText("No users nearby.");
                        } else {
//                            mEmpty.setText("");
                            nearbyUsersList.addAll(response.body());
                           // nearbyUsersList.add(null);
                           // nearbyUsersList1.addAll(nearbyUsersList);

                        }

                        Iterator<User> it = nearbyUsersList.iterator();
                        while (it.hasNext()){
                           User user =  it.next();
                            String occupation =  user.getOccupation();
                            PhoneCallLog phoneCallLog = new PhoneCallLog(null, user.getName(), user.getPhone(), null, 0, 0);
                           // hollaNowDbHelper.cachePhoneLog(phoneCallLog);
                            try {
                                if ((occupation.equals("-") || occupation.equals("") || occupation == null))
                                    it.remove();
                            }catch (NullPointerException ex){
                                it.remove();
                            }
                        }
                       /** for(int i = 0; i < nearbyUsersList.size();i++){
                            try {
                           String occupation =  nearbyUsersList.get(i).getOccupation();
                                Log.e(TAG, "success is here in occupation "+ nearbyUsersList.get(i).getOccupation());
                                if (!(occupation.equals("-") || occupation.equals("") || occupation == null)) {
                                    nearbyUsersList1
                                            .add(nearbyUsersList.get(i));
                                    Log.e(TAG, "success is here in occupation if statement"+ nearbyUsersList.get(i).getOccupation());
                                    Log.e(TAG, "success is here in occupation if statement"+ i);
                                }
                            }catch(NullPointerException ex){
                                nearbyUsersList1
                                        .add(nearbyUsersList.get(i));
                                Log.e(TAG, "success is here in occupation error"+ nearbyUsersList.get(i).getOccupation());
                                Log.e(TAG, "success is here in occupation error "+ i);
                            }

                        } **/
                       // nearbyUsersList.clear();
                       // nearbyUsersList.addAll(nearbyUsersList1);
                      //  nearbyUsersList1.clear();
                        new  ReadSalesMarket().execute();

                        nearbyUsersList.add(null);
                   // if(mCount >= count) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        nearbyUsersAdapter = new NearbyUserAdapter(nearbyUsersList, mSharedPref.getLattitude(),
                                mSharedPref.getLongtitude(), mContext);
                        mRecyclerView.setAdapter(nearbyUsersAdapter);
                                     mCount = 0;
                   // }


                    } else {
//                        Toast.makeText(mContext, "Error retrieving nearby users", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error retrieving nearby users "+ response.code()+response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                   // Toast.makeText(mContext, "Network error. Please check your connection", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            MyToolBox.AlertMessage(mContext, "Network error. Please check your connection");
        }

    }

    private class ReadSalesMarket extends AsyncTask<Void, Integer,String> {
        Iterator<User> it2;

        @Override
        protected  String doInBackground(Void... argo) {


            List <String>lastPicture = new ArrayList<String>();
            if(nearbyUsersList != null){
            it2 = nearbyUsersList.iterator();
            while(it2.hasNext()){
                try {
                    lastPicture.add(it2.next().getUsername());
                }catch(NullPointerException ex){

                }
            }}
            int count = lastPicture.size() - 1;

            do{
                if(!flag_state) {
                    try {
                        retrieveSalesPictures(lastPicture.get(mCount));
                    }catch (IndexOutOfBoundsException ex){

                    }
                    Log.e(TAG, "Findnearbyusers is here " + mCount);
                    Log.e(TAG, "Findnearbyusers is here " + mCount);
                    //mCount++;
                }
            }while (mCount <= count);
            return "man";
        }

        @Override
        public   void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("man"))
            if(nearbyUsersAdapter != null)
                nearbyUsersAdapter.notifyDataSetChanged();
            mCount = 0;

        }
    }


    public String retrieveSalesPictures(String username){
        //  Log.e("EDITED_USER=> ", currentUser.toString());
        // mCount = count;
         //String market = null;
        Log.e(TAG, "Findnearbyusers is here in retrieveSalepicture " );
        flag_state = true;
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.receiveSalesPictures(username);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "success "+ response.body().toString());
                    //  Log.e(TAG, "success "+ response.body().toString());
                    //  Log.e(TAG, "success "+ response.body().toString());
                    // Log.e(TAG, "success "+ response.body().toString()+" "+response.body().getAsString());
                    ListSalesMarket sales =  new GsonBuilder().create().fromJson(response.body().toString(),ListSalesMarket.class);
                    int number_of_pictures = sales.getSalesMarket().size();
                    int lastIndex = number_of_pictures - 1;
                    Log.e(TAG, "Findnearbyusers is here in retrieveSalepicture1 " );
                    // market.add(sales.getSalesMarket().get(lastIndex).getPhoto());
                    if(lastIndex != -1) {
                        market =  sales.getSalesMarket().get(lastIndex);
                        Log.e(TAG, "Findnearbyusers is here in retrieveSalepicture2 " );
                        //  market = sales.getSalesMarket().get(lastIndex).getPhoto();
                        marketFlow = market != null ? market.getPhoto() : null;
                        nearbyUsersList.get(mCount).setmProfilePhoto2(market.getPhoto());
                        nearbyUsersList.get(mCount).setTextPhoto(market.getContent());
                        nearbyUsersList.get(mCount).setColor(market.getColor());
                        Log.e(TAG, "success "+ market);
                        Log.e(TAG, "success is here "+ mCount);
                        Log.e(TAG, "Last Index is here "+ lastIndex);
                        Log.e(TAG, "success is here "+ nearbyUsersList.get(mCount).getUsername());
                        Log.e(TAG, "success is here "+ nearbyUsersList.get(mCount).getProfilePhoto2());
                        market = null;
                        mCount++;
                        flag_state = false;
                        return ;
                    }

                    Log.e(TAG, "success is here "+ mCount);
                    Log.e(TAG, "success is here "+ mCount);
                    mCount++;
                    flag_state = false;
                    return;

                    //return market;
//                    onBackPressed();
                } else {
                    Log.e(TAG, "error: " + response.code() + response.message());
                  //  Log.e(TAG, "success "+ response.body().toString());
                  //  Log.e(TAG, "success "+ response.body().toString());
                  //  Log.e(TAG, "success "+ response.body().toString());
                    Toast.makeText(mContext, "Error updating Sales", Toast.LENGTH_SHORT).show();
                    marketFlow = "";
                    return;
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();
                marketFlow = "";
                return;
            }
        });
        return marketFlow;
    }





    private void getIndustries() {
        Call<List<Industry>> call = hollaNowApiInterface.getIndustry(mSharedPref.getToken());
        Log.e("TOKEN search frag", mSharedPref.getToken()+"");
        call.enqueue(new Callback<List<Industry>>() {
            @Override
            public void onResponse(Call<List<Industry>> call, Response<List<Industry>> response) {
                if (response.code()==200) {
                    displayIndustries(response.body());
                } else {
                    Log.e(TAG, "Error code id "+response.code());
                    Toast.makeText(mContext, "Error loading services", Toast.LENGTH_SHORT).show();
                    mEmptyIndustry.setText("Session expired. Re-Login if you want to use this services.  " );
                }
            }

            @Override
            public void onFailure(Call<List<Industry>> call, Throwable t) {
//                Log.e(TAG, t.getMessage());
                mEmptyIndustry.setText("Network error. Check your connection");
               // Toast.makeText(mContext, "Network error. Please check your connection", Toast.LENGTH_LONG).show();
            }
        });
    }


     private void displayIndustries(List<Industry> industries) {
        TextView industryChips;
        if (industries!=null) {
            for (Industry industry : industries) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 16, 32);
                industryChips = new TextView(mContext);
                industryChips.setLayoutParams(lp);
                industryChips.setPadding(16, 16, 16, 16);
                industryChips.setBackgroundResource(R.drawable.chips);
                industryChips.setText(industry.getIndustry());
                industryChips.setOnClickListener(industryClickListener(industry.getIndustry()));
                mCategoryLayout.addView(industryChips);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener industryClickListener(final String industry) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra("INDUSTRY", industry);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener categoryClickListener(final Category category) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra(Category.ID, category.getObjectId());
                intent.putExtra(Category.CATEGORY, category.getCategory());
                //Todo
                startActivity(intent);
            }
        };
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //@Nullable
    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result  = null;
        try {
            if (LocationServices.SettingsApi != null)
                result =
                        LocationServices.SettingsApi.checkLocationSettings(
                                mGoogleApiClient,
                                mLocationSettingsRequest
                        );
            result.setResultCallback(this);
        }catch (NullPointerException ex){
            Log.e(TAG, "Nullpointer Exception at checkLocationSettings. ");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
       // if(mSharedPref != null)
        //    mSharedPref.setlocation(mCurrentLocation);
        Log.e(TAG, "Location services connected. ");
        isConnected = true;
        if (mCurrentLocation!=null) {
            handleNewLocation(mCurrentLocation);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e(TAG, connectionResult.getErrorMessage());
        if (!connectionResult.isSuccess()) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                Toast.makeText(mContext, "Something went wrong with your device\'s location services", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Something went wrong with your device\'s location services");
            }
        }
        else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mSharedPref != null)
                mSharedPref.setlocation(mCurrentLocation);
            Log.e(TAG, "Location services connected. ");
            isConnected = true;
            handleNewLocation(mCurrentLocation);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(nearbyUsersAdapter != null)
            nearbyUsersAdapter.notifyDataSetChanged();
        if(mGoogleApiClient != null)
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.resume();
        if(nearbyUsersAdapter != null)
            nearbyUsersAdapter.notifyDataSetChanged();
        if(mGoogleApiClient != null)
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        checkLocationSettings();
        if (getUserVisibleHint()) {
            if (mSharedPref == null) {
                mSharedPref = new HollaNowSharedPref(mContext);
            }
        /**    if (!mSharedPref.isTutorial2()) {


                Tooltip.make(mContext,
                        new Tooltip.Builder(102)
                                .anchor(mSearchLayout, Tooltip.Gravity.BOTTOM)
//                        .closePolicy(new Tooltip.ClosePolicy()
//                                .insidePolicy(true, true)
//                                .outsidePolicy(true, false), 0)
                                .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
//                        .activateDelay(800)
//                        .showDelay(300)
                                .text(getString(R.string.tooltip_two))
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
//                        .typeface(mYourCustomFont)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();

                mSharedPref.setTutorial2(true);
            }**/

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        mAdView.pause();
        if(mGoogleApiClient != null)
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location services requested. ");
        if(mSharedPref != null)
            mSharedPref.setlocation(location);
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {

        if (location!=null) {
            mSharedPref.setLattitude((float) location.getLatitude());
            mSharedPref.setLongtitude((float) location.getLongitude());
            saveLocation();
            findNearbyUsers();
        } else {
            startUpdates();
        }
    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e(TAG, "All location settings are satisfied.");
                isConnected = true;
//                startLocationUpdates();
                if (mTurnLocation!=null) {
                    mTurnLocation.setVisibility(View.GONE);
                }
                findNearbyUsers();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                if (mTurnLocation!=null) {
                    mTurnLocation.setVisibility(View.VISIBLE);
                }

                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                if (mTurnLocation!=null)
                    mTurnLocation.setVisibility(View.VISIBLE);
                break;
            case LocationSettingsStatusCodes.CANCELED:
                Log.e(TAG, "Location settings cancelled.");
                if (mTurnLocation!=null)
                    mTurnLocation.setVisibility(View.VISIBLE);
                break;
        }
    }
    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        startUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        if (mTurnLocation!=null)
                            mTurnLocation.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case OVERLAY_PERMISSION_CODE:
           // if(reqestCode == OVERLAY_PERMISSION_CODE){
                if(!Settings.canDrawOverlays(mContext)){
                    Log.e(TAG, "System alert window permission  not granted...");
                }
                break;
           // }
        }
    }

    private void startUpdates(){
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds
    }

    private void saveLocation(){
        if(mSharedPref.getLongtitude() != 0  &&  mSharedPref.getLattitude() != 0) {
            try {
                List<Address> address = gcd.getFromLocation(mSharedPref.getLattitude(), mSharedPref.getLongtitude(), 1);
                Log.e(TAG, "Country is :" + address.get(0).getCountryName());
                country = address.get(0).getCountryName();
            } catch (IOException ex) {
                Log.e(TAG, "IOException Occured :" + ex.getMessage());
            }
            if (country != null)
                mSharedPref.setCountry(country);
        }
    }

    // @TargetApi(23)
     @SuppressLint("NewApi")
    public void addOverlay() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
    if (!Settings.canDrawOverlays(mContext)) {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    Uri.parse("package:" + mContext.getPackageName()));
      startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
        }
      }
    }


}
