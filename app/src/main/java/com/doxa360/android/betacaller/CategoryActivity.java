package com.doxa360.android.betacaller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.UserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

  //    public static final String CATEGORY_ID = "CATEGORY_ID";
//    public static final String CATEGORY = "CATEGORY";

    private static final String TAG = "CategoryActivity";

  private ProgressBar mProgressBar;
  RecyclerView mRecyclerView;
  UserAdapter mAdapter;

  String industry, categoryId;
  private TextView mEmpty;
  private HollaNowSharedPref mSharedPref;
  private HollaNowApiInterface hollaNowApiInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_category);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    industry = getIntent().getStringExtra("INDUSTRY");
    mSharedPref = new HollaNowSharedPref(this);
    hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

    toolbar.setTitle(industry);
    getSupportActionBar().setTitle(industry);
    mEmpty = (TextView) findViewById(R.id.empty_text);
    mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    mRecyclerView = (RecyclerView) findViewById(R.id.category_recyclerview);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(layoutManager);

    if (MyToolBox.isNetworkAvailable(this)) {
      getUsersByIndustry(industry);
    } else {
      MyToolBox.AlertMessage(this, "Network error. Check your connection");
    }


  }

  private void getUsersByIndustry(String industry) {
    Call<List<User>> call = hollaNowApiInterface.getUserByIndustry(mSharedPref.getToken(), industry, "Kenya"); // Nigeria
    call.enqueue(new Callback<List<User>>() {
      @Override
      public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.code() == 200) {
            for(int i = 0; i < response.body().size();i++) {
                User user = response.body().get(i);
                Log.e(TAG, "print from get user by Industry1: "+user.getName());
            }
            Log.e(TAG, "print from get user by Industry1: "+response.code());
          if (response.body().size()==0) {
            mEmpty.setText("There are currently no users in this industry");
          } else {
            mEmpty.setText("");
          }
          mProgressBar.setVisibility(View.INVISIBLE);
          mAdapter = new UserAdapter(response.body(), CategoryActivity.this);
          mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.e(TAG, "print from get user by Industry2: "+response.code());
          Toast.makeText(CategoryActivity.this, "Error retrieving users in this industry", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<List<User>> call, Throwable t) {
          Log.e(TAG, "print from get user by Industry3: "+t.getMessage());
        Toast.makeText(CategoryActivity.this, "Network error. Check your connection", Toast.LENGTH_SHORT).show();
      }

    });
  }

}
