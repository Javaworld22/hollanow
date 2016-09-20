package com.doxa360.android.betacaller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.NearbyUserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NearbySearchActivity extends AppCompatActivity {

    private static final String TAG = NearbySearchActivity.class.getSimpleName();
    SeekBar nearbySeekbar;
    private String currentCar;
    private ParseGeoPoint mCurrentParseGeopoint;
    private RecyclerView mRecyclerView;
    private HollaNowSharedPref mSharedPref;
    private ArrayList<ParseUser> nearbyUsersList;
    private NearbyUserAdapter nearbyUsersAdapter;
    private ProgressBar mProgressBar;
    private TextView mTurnLocation;
    private boolean locationOn = false;
    private TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSharedPref = new HollaNowSharedPref(this);
        nearbyUsersList = new ArrayList<ParseUser>();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmpty = (TextView) findViewById(R.id.empty_text);

        mTurnLocation = (TextView) findViewById(R.id.turn_on_location_label);

        mRecyclerView = (RecyclerView) findViewById(R.id.nearby_contacts_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);

//        if (locationOn) {
            mCurrentParseGeopoint = new ParseGeoPoint(mSharedPref.getLattitude(), mSharedPref.getLongtitude());
//        }
        nearbyUsersAdapter = new NearbyUserAdapter(nearbyUsersList, mCurrentParseGeopoint, NearbySearchActivity.this, true);
        mRecyclerView.setAdapter(nearbyUsersAdapter);

        nearbySeekbar = (SeekBar) findViewById(R.id.nearby_seekbar);
        nearbySeekbar.setProgress(30);
        nearbySeekbar.setMax(100);
        findNearbyUsers(BetaCaller.LOCATION_DISTANCE_CLOSE);

        nearbySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                if (progress >= 0 && progress <= 30) {
                    seekBar.setProgress(30);
                    Log.e(TAG, "default");
                    findNearbyUsers(BetaCaller.LOCATION_DISTANCE_CLOSE);
                } else if (progress >= 30 && progress <= 70) {
                    seekBar.setProgress(60);
                    mProgressBar.setVisibility(View.VISIBLE);
                    findNearbyUsers(BetaCaller.LOCATION_DISTANCE_MEDIUM);
                } else {
                    seekBar.setProgress(100);
                    mProgressBar.setVisibility(View.VISIBLE);
                    findNearbyUsers(BetaCaller.LOCATION_DISTANCE_FARTHER);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                int mProgress = seekBar.getProgress();
//                if (mProgress > 0 & mProgress < 21) {
//                    nearbySeekbar.setProgress(10);
//                    sendStateResponse("TODO");
//                    setBoldText(0);
//                } else if (mProgress > 20 & mProgress < 41) {
//                    nearbySeekbar.setProgress(30);
//                    sendStateResponse("START");
//                    setBoldText(1);
//                } else if (mProgress > 40 & mProgress < 61) {
//                    nearbySeekbar.setProgress(50);
//                    sendStateResponse("STOP");
//                    setBoldText(2);
//                } else if (mProgress > 60 & mProgress < 81) {
//                    nearbySeekbar.setProgress(70);
//                    sendStateResponse("CONTINUE");
//                    setBoldText(3);
//                } else {
//                    nearbySeekbar.setProgress(90);
//                    sendStateResponse("FINISHED");
//                    setBoldText(4);
//                }
            }
        });

    }

    private void findNearbyUsers(int distanceAway) {
        if (MyToolBox.isNetworkAvailable(NearbySearchActivity.this)) {
            ParseQuery<ParseUser> nearbyQuery = ParseUser.getQuery();
            nearbyQuery.whereNear("lastSeen", mCurrentParseGeopoint);
            nearbyQuery.whereWithinKilometers("lastSeen", mCurrentParseGeopoint, (double) distanceAway); //25 miles
            nearbyQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            nearbyQuery.whereNotEqualTo("searchVisible", false);
            nearbyQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> nearbyUsers, ParseException e) {
                    if (e == null) {
                        if (nearbyUsers.size()==0) {
                            mEmpty.setText("No users nearby.");
                        } else {
                            mEmpty.setText("");
                        }
                        nearbyUsersList.clear();
                        nearbyUsersList.addAll(nearbyUsers);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        nearbyUsersAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(NearbySearchActivity.this, "Network error. Please check your connection", Toast.LENGTH_LONG).show();
        }
    }

}
