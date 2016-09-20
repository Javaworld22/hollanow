package com.doxa360.android.betacaller;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    private  final String TAG = getClass().getSimpleName() ;
    EditText mSearchEditText;
    RecyclerView mSearchResults;
    UserAdapter mAdapter;
    //    SearchUserAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmpty;
    private ArrayList<String> mSelectedMembers = new ArrayList<String>();
    private List<String> mSelectedMembersName = new ArrayList<String>();


    public  List<String> memberIds = new ArrayList<String>();
    public  List<String> memberNames = new ArrayList<String>();
    private List<ParseUser> searchedUsers;
    Toolbar mToolBar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);
//        mEmpty = (TextView) findViewById(R.id.empty_text);
//        mSearchEditText = (EditText) findViewById(R.id.search_edittext);
        mSearchResults = (RecyclerView) findViewById(R.id.search_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        mSearchResults.setLayoutManager(layoutManager);

//        mSearchResults.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        mSearchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (!charSequence.toString().trim().isEmpty()) {
//                    getSearch(charSequence.toString().trim());
//                    if (mAdapter!=null)
//                        mAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//




//        Button mFinish = (Button) findViewById(R.id.finish_button);
//        mFinish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addMembers();
//            }
//        });




    }

    private void getSearch(final CharSequence charSequence) {
//        if (charSequence.toString().isEmpty() || charSequence.toString().length()<=3) {
//
//        } else {
            mProgressBar.setVisibility(View.VISIBLE);
//            ParseQuery<ParseUser> queryName = ParseUser.getQuery();
//            if (charSequence.toString().length()>=2) {
//                queryName.whereEqualTo("name", charSequence.toString().substring(0, 1).toUpperCase() + charSequence.toString().substring(1).toLowerCase());
//
//                queryName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
//                queryName.whereNotEqualTo("searchVisible", false);
//            }


//            ParseQuery<ParseUser> queryLowerCaseName = ParseUser.getQuery();
//            queryLowerCaseName.whereEqualTo("name", charSequence.toString().toLowerCase());
//            queryLowerCaseName.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
//        queryLowerCaseName.whereNotEqualTo("searchVisible", false);

        if (charSequence.toString().length()>=3) {
            mSearchResults.setVisibility(View.VISIBLE);

            ParseQuery<ParseUser> realQuery = ParseUser.getQuery();
            if (charSequence.toString().startsWith("0")) {
                String phoneNumber = charSequence.toString().replaceFirst("[0]", "+234");
                realQuery.whereContains("phoneNumber", phoneNumber);
                realQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                realQuery.whereNotEqualTo("searchVisible", false);
            } else if (charSequence.toString().startsWith("+")) {
                realQuery.whereContains("phoneNumber", charSequence.toString());
                realQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                realQuery.whereNotEqualTo("searchVisible", false);
            } else {
                realQuery.whereContains("username", charSequence.toString().toLowerCase());
                realQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                realQuery.whereNotEqualTo("searchVisible", false);
            }


//        ParseQuery<ParseUser> queryEmail = ParseUser.getQuery();
//            if (charSequence.toString().startsWith("0")) {
//                String phoneNumber = charSequence.toString().replaceFirst("[0]", "+234");
//                queryEmail.whereContains("phoneNumber", phoneNumber);
//                queryEmail.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
//                queryEmail.whereNotEqualTo("searchVisible", false);
//            }
//
//            ParseQuery<ParseUser> queryUsername = ParseUser.getQuery();
//            queryUsername.whereEqualTo("username", charSequence.toString().toLowerCase());
//            queryUsername.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
//            queryUsername.whereNotEqualTo("searchVisible", false);


//        ParseQuery<ParseUser> queryAddress = ParseUser.getQuery();
//        queryAddress.whereContains("address", charSequence.toString().toLowerCase());
//        queryAddress.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

//            ParseQuery<ParseUser> queryLastSeenAddress = ParseUser.getQuery();
//            queryLastSeenAddress.whereContains("lastSeenAddress", charSequence.toString().toLowerCase());
//            queryLastSeenAddress.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

//            List<ParseQuery<ParseUser>> queryUser = new ArrayList<ParseQuery<ParseUser>>();
//            queryUser.add(queryName);
//        queryUser.add(queryEmail);
//            queryUser.add(queryLowerCaseName);
//            queryUser.add(queryUsername);
//        queryUser.add(queryAddress);
//            queryUser.add(queryLastSeenAddress);


//            ParseQuery<ParseUser> mainQuery = ParseQuery.or(queryUser);
            realQuery.setLimit(25);
            realQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        Log.e(TAG, objects.size() + " count");
                        searchedUsers = new ArrayList<ParseUser>();
                        searchedUsers.addAll(objects);
                        mAdapter = new UserAdapter(searchedUsers, SearchActivity.this);
                        mSearchResults.setAdapter(mAdapter);
                        mProgressBar.setVisibility(View.INVISIBLE);
//                    mEmpty.setText("\'" + mSearchEditText.getText().toString().trim() + "\' not found on HollaNow");
//                    Toast.makeText(SearchActivity.this, charSequence +" not found on HollaNow", LEN).show
                        if (objects.size() == 0) {
                            Log.e(TAG, "User not found on HollaNow.");
//                        mEmpty.setText("Sorry. User not found on HollaNow.");
                        } else {
//                        mEmpty.setText("");
                        }
                        if (charSequence.toString().length() > 5) {
                            SearchRecentSuggestions suggestions =
                                    new SearchRecentSuggestions(SearchActivity.this,
                                            MySuggestionProvider.AUTHORITY,
                                            MySuggestionProvider.MODE);
                            suggestions.saveRecentQuery(charSequence.toString(), null);
                        }
                    }
                }
            });
//        }

        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mSearchResults.setVisibility(View.INVISIBLE);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchActivity.class)));

//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_query_hint));
        searchView.setIconifiedByDefault(false);
//        searchView.setIconified(false);
//        searchView.setActivated(true);
//        searchView.setFocusable(true);
        searchView.requestFocus();


        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }
        searchTextView.setDropDownBackgroundResource(R.drawable.rectangle);
        searchTextView.setDropDownHeight(300);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        if (TextUtils.isEmpty(searchView.getQuery())) {
////            searchView.setQuery(null, true);
//            mSearchResults.setVisibility(View.INVISIBLE);
//
//        }
            if (newText!= null&& newText.length()>=3) {
            getSearch(newText);
        } else {
            Log.e(TAG, "empty");

            mSearchResults.setVisibility(View.INVISIBLE);
            getSearch("");
//            if (searchedUsers!=null && mAdapter!=null) {
//                searchedUsers.clear();
////            searchedUsers.add(null);
//                mAdapter.notifyDataSetChanged();
//            }
        }
        return false;
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//
////            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();
//
//        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            String uri = intent.getDataString();
//            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        handlesearch(intent);
    }

    public void handlesearch( Intent intent)
    {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

//            searchView.setQuery(query, true);
//            getSearch(query);


            SearchRecentSuggestions searchRecentSuggestions=new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY,MySuggestionProvider.MODE);

            searchRecentSuggestions.saveRecentQuery(query,null);
        }
    }
}
