package com.doxa360.android.betacaller;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
//import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.adapter.UserAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener  {

  private  final String TAG = getClass().getSimpleName() ;
  RecyclerView mSearchResults;
  UserAdapter mAdapter;
  //    SearchUserAdapter mAdapter;
  private ProgressBar mProgressBar;
  private TextView mEmpty;
  Toolbar mToolBar;
  SearchView searchView;
  private HollaNowApiInterface hollaNowApiInterface;
  HollaNowSharedPref mSharedPref;
  private User currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    mToolBar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolBar);

    hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
    mSharedPref = new HollaNowSharedPref(this);
    currentUser = mSharedPref.getCurrentUser();

    mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    mProgressBar.setVisibility(View.INVISIBLE);
    mEmpty = (TextView) findViewById(R.id.empty_text);
    mSearchResults = (RecyclerView) findViewById(R.id.search_results);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setAutoMeasureEnabled(true);
    mSearchResults.setLayoutManager(layoutManager);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);



  }

  public void getSearch(final CharSequence charSequence) {
    mProgressBar.setVisibility(View.VISIBLE);
    if (charSequence.toString().length()>=3) {
      mSearchResults.setVisibility(View.VISIBLE);

      Call<List<User>> call = hollaNowApiInterface.search(charSequence.toString(), currentUser.getToken());
      call.enqueue(new Callback<List<User>>() {
        @Override
        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
          if (response.code() == 200) {
            Log.e(TAG, "size: " + response.body().size());
            List<User> arrayList = new ArrayList<User>();
            List<User> allUsers = response.body();
            for(int i = 0;i<response.body().size();i++){
                if(allUsers.get(i).isSearchVisible().equals("Unhidden")){
                    arrayList.add(allUsers.get(i));
                }
            }
            mAdapter = new UserAdapter(arrayList, SearchActivity.this);
            mSearchResults.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.INVISIBLE);

            if(arrayList != null)
              if (arrayList.size() == 0) {
              Log.e(TAG, "User not found on HollaNow.");
              mEmpty.setText("No user matched the search results.");
            } else {
              mEmpty.setText("");
            }
            if (charSequence.toString().length() > 5) {
              SearchRecentSuggestions suggestions =
                      new SearchRecentSuggestions(SearchActivity.this,
                              MySuggestionProvider.AUTHORITY,
                              MySuggestionProvider.MODE);
              suggestions.saveRecentQuery(charSequence.toString(), null);
            }

          } else {
            Log.e(TAG, response.code()+"");
            Toast.makeText(SearchActivity.this, "Network error. Try again", Toast.LENGTH_SHORT).show();
          }
        }

        @Override
        public void onFailure(Call<List<User>> call, Throwable t) {
//                    Log.e(TAG, t.getMessage()+"");
          Toast.makeText(SearchActivity.this, "Network error. Try again", Toast.LENGTH_SHORT).show();
        }
      });



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
    String search = getIntent().getStringExtra("Search");
    if(search != null)
      if(search.equals("Numbers"))
          searchTextView.setInputType(InputType.TYPE_CLASS_PHONE);

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
    if (newText!= null&& newText.length()>=3) {
      getSearch(newText);
    } else {
      Log.e(TAG, "empty");

      mSearchResults.setVisibility(View.INVISIBLE);
      getSearch("");

    }
    return false;
  }


  @Override
  protected void onNewIntent(Intent intent) {
    setIntent(intent);

    handlesearch(intent);
  }

  public void handlesearch( Intent intent)
  {
    if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      SearchRecentSuggestions searchRecentSuggestions=new SearchRecentSuggestions(this,
              MySuggestionProvider.AUTHORITY,MySuggestionProvider.MODE);
      searchRecentSuggestions.saveRecentQuery(query,null);
    }
  }
}
