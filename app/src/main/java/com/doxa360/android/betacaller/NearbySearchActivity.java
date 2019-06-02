package com.doxa360.android.betacaller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.NearbyUserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbySearchActivity extends AppCompatActivity {

    private static final String TAG = NearbySearchActivity.class.getSimpleName();
    SeekBar nearbySeekbar;
    private String currentCar;
    private ParseGeoPoint mCurrentParseGeopoint;
    private RecyclerView mRecyclerView;
    private HollaNowSharedPref mSharedPref;
    private ArrayList<User> nearbyUsersList;
    private NearbyUserAdapter nearbyUsersAdapter;
    private ProgressBar mProgressBar;
    private TextView mTurnLocation;
    private boolean locationOn = false;
    private TextView mEmpty;
    private HollaNowApiInterface hollaNowApiInterface;

    private NativeExpressAdView adView;


    int mDistKm = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nearbyUsersList = new ArrayList<User>();
        mSharedPref = new HollaNowSharedPref(this);
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmpty = (TextView) findViewById(R.id.empty_text);

        mTurnLocation = (TextView) findViewById(R.id.turn_on_location_label);

        mRecyclerView = (RecyclerView) findViewById(R.id.nearby_contacts_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);


        nearbyUsersAdapter = new NearbyUserAdapter(nearbyUsersList, mSharedPref.getLattitude(), mSharedPref.getLongtitude(), NearbySearchActivity.this, true);
        mRecyclerView.setAdapter(nearbyUsersAdapter);

        nearbySeekbar = (SeekBar) findViewById(R.id.nearby_seekbar);
        nearbySeekbar.setProgress(30);
        nearbySeekbar.setMax(100);
        findNearbyUsers(mSharedPref.getLattitude(), mSharedPref.getLongtitude(),
                BetaCaller.LOCATION_DISTANCE_CLOSE, mSharedPref.getToken());

        nearbySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                if (progress >= 0 && progress <= 30) {
                    seekBar.setProgress(30);
                    Log.e(TAG, "default");
                    mDistKm = BetaCaller.LOCATION_DISTANCE_CLOSE;
                } else if (progress >= 30 && progress <= 70) {
                    seekBar.setProgress(60);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDistKm = BetaCaller.LOCATION_DISTANCE_MEDIUM;
                } else {
                    seekBar.setProgress(100);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDistKm = BetaCaller.LOCATION_DISTANCE_FARTHER;
                }

                findNearbyUsers(mSharedPref.getLattitude(), mSharedPref.getLongtitude(),
                        mDistKm, mSharedPref.getToken());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void findNearbyUsers(float lat, float lng, int distKm, String token) {
        if (MyToolBox.isNetworkAvailable(NearbySearchActivity.this)) {

            Call<List<User>> call = hollaNowApiInterface.getNearbyUsers(lat, lng, distKm, token);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.code() == 200) {
                        if (response.body().size() == 0) {
                            mEmpty.setText("No users nearby.");
                        } else {
                            mEmpty.setText("");
                        }
                        nearbyUsersList.clear();
                        nearbyUsersList.addAll(response.body());
                        mProgressBar.setVisibility(View.INVISIBLE);
                        nearbyUsersAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NearbySearchActivity.this, "Error retrieving users", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(NearbySearchActivity.this, "Network error. Please check your connection", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            MyToolBox.AlertMessage(this, "Network error. Please check your connection");
        }

    }

}
