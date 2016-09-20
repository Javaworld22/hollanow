package com.doxa360.android.betacaller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doxa360.android.betacaller.adapter.UserAdapter;
import com.doxa360.android.betacaller.model.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

//    public static final String CATEGORY_ID = "CATEGORY_ID";
//    public static final String CATEGORY = "CATEGORY";
    private ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    UserAdapter mAdapter;

    String category, categoryId;
    private TextView mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryId = getIntent().getStringExtra(Category.ID);
        category = getIntent().getStringExtra(Category.CATEGORY);

        toolbar.setTitle(category);
        getSupportActionBar().setTitle(category);
        mEmpty = (TextView) findViewById(R.id.empty_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.category_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        getUsersByCategory();


    }

    private void getUsersByCategory() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("industry", Arrays.asList(category.toLowerCase()));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e==null){
                    if (users.size()==0) {
                        mEmpty.setText("There are currently no users in this category");
                    } else {
                        mEmpty.setText("");
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mAdapter = new UserAdapter(users, CategoryActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

}
