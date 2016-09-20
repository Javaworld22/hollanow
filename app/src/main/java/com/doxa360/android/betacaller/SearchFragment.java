package com.doxa360.android.betacaller;


import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.Category;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.thinkincode.utils.views.HorizontalFlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123450;
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
    List<ParseUser> nearbyUsersList;
    NearbyUserAdapter nearbyUsersAdapter;
    HollaNowSharedPref mSharedPref;
    private boolean isConnected = false;
    private Status status;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPref = new HollaNowSharedPref(mContext);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 5 second, in milliseconds


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_search, container, false);


        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mCategoryLayout = (HorizontalFlowLayout) rootView.findViewById(R.id.category_horizontal_layout);
        mSearchLayout = (CardView) rootView.findViewById(R.id.search_card);
        mTurnLocation = (TextView) rootView.findViewById(R.id.turn_on_location_label);
        mSearchNearby = (ImageView) rootView.findViewById(R.id.nearby_search_icon);
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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.nearby_contacts_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false);
        gridLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);


        getCategories();

        return rootView;
    }

    private void turnOnLocation() {
        try {
            // Show the dialog by calling startResolutionForResult(), and check the result
            // in onActivityResult().
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "PendingIntent unable to execute request.");
        }
        Log.e(TAG, "turn location on clicked");
    }

    private void findNearbyUsers() {
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
                    nearbyUsersList = new ArrayList<ParseUser>();
                    nearbyUsersList.addAll(nearbyUsers);
                    nearbyUsersList.add(null);
                    nearbyUsersAdapter = new NearbyUserAdapter(nearbyUsersList, mCurrentParseGeopoint, mContext);
                    mRecyclerView.setAdapter(nearbyUsersAdapter);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void getCategories() {
        ParseQuery<Category> query = Category.getQuery();
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e==null) {
                    displayCategories(categories);
                }
            }
        });
    }


    private void displayCategories(List<Category> categories) {
        TextView categoryChips;
        if (categories!=null) {
            Log.e(TAG, categories.size() + "");
            for (Category category : categories) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 16, 32);
                Log.e(TAG, category.getCategory());
                categoryChips = new TextView(mContext);
                categoryChips.setLayoutParams(lp);
                categoryChips.setPadding(16, 16, 16, 16);
                categoryChips.setBackgroundResource(R.drawable.chips);
//                categoryChips.setSingleLine();
//                categoryChips.setTextAppearance(mContext, android.R.style.TextAppearance_DeviceDefault_Medium);
                categoryChips.setText(category.getCategory());
                categoryChips.setOnClickListener(categoryClickListener(category));
                mCategoryLayout.addView(categoryChips);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }

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


    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Toast.makeText(mContext, "Something went wrong with your device\'s location services", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Something went wrong with your device\'s location services");
        }
        else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.e(TAG, "Location services connected. ");
            isConnected = true;
            handleNewLocation(mCurrentLocation);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        checkLocationSettings();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location services requested. ");
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        mSharedPref.setLattitude(location.getLatitude());
        mSharedPref.setLongtitude(location.getLongitude());
        findNearbyUsers();
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
                    mTurnLocation.setVisibility(View.INVISIBLE);
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
                break;
        }
    }

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
                        break;
                }
                break;
        }
    }

    private void startUpdates(){
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds
    }

}
