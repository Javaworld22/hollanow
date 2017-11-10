package com.doxa360.android.betacaller.helpers;

/**
 * Created by HollaNow on 9/2/2017.
 */

        import android.app.Dialog;
        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

        import java.util.ArrayList;
        import java.util.List;

        import com.doxa360.android.betacaller.R;

/**
 * Created by hbb20 on 11/1/16.
 */
class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.CountryCodeViewHolder> implements SectionTitleProvider {
    List<Country> filteredCountries = null, masterCountries = null;
    TextView textView_noResult;
    CountryCodePicker codePicker;
    LayoutInflater inflater;
    EditText editText_search;
    Dialog dialog;
    Context context;
    RelativeLayout rlQueryHolder;
    ImageView imgClearQuery;
    int preferredCountriesCount = 0;
    private final String TAG = "CountryCodeAdapter";

    CountryCodeAdapter(Context context, List<Country> countries, CountryCodePicker codePicker, RelativeLayout rlQueryHolder, final EditText editText_search, TextView textView_noResult, Dialog dialog, ImageView imgClearQuery) {
        this.context = context;
        this.masterCountries = countries;
        this.codePicker = codePicker;
        this.dialog = dialog;
        this.textView_noResult = textView_noResult;
        this.editText_search = editText_search;
        this.rlQueryHolder = rlQueryHolder;
        this.imgClearQuery = imgClearQuery;
        this.inflater = LayoutInflater.from(context);
        this.filteredCountries = getFilteredCountries("");
        setSearchBar();
    }

    private void setSearchBar() {
        if (codePicker.isSearchAllowed()) {
            imgClearQuery.setVisibility(View.GONE);
            setTextWatcher();
            setQueryClearListener();
        } else {
            rlQueryHolder.setVisibility(View.GONE);
        }
    }

    private void setQueryClearListener() {
        imgClearQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_search.setText("");
            }
        });
    }

    /**
     * add textChangeListener, to apply new query each time editText get text changed.
     */
    private void setTextWatcher() {
        if (this.editText_search != null) {
            this.editText_search.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyQuery(s.toString());
                    if (s.toString().trim().equals("")) {
                        imgClearQuery.setVisibility(View.GONE);
                    } else {
                        imgClearQuery.setVisibility(View.VISIBLE);
                    }
                }
            });

            /*if (codePicker.isDialogKeyboardAutoPopup()) {
                try {
                    if(editText_search.getVisibility()==View.VISIBLE) {
                        editText_search.requestFocus();
                        editText_search.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                        editText_search.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                        editText_search.setSelection(editText_search.getText().toString().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
*/
            this.editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    /**
     * Filter country list for given keyWord / query.
     * Lists all countries that contains @param query in country's name, name code or phone code.
     *
     * @param query : text to match against country name, name code or phone code
     */
    private void applyQuery(String query) {


        textView_noResult.setVisibility(View.GONE);
        query = query.toLowerCase();

        //if query started from "+" ignore it
        if (query.length() > 0 && query.charAt(0) == '+') {
            query=query.substring(1);
        }

        filteredCountries= getFilteredCountries(query);

        if (filteredCountries.size() == 0) {
            textView_noResult.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    private List<Country> getFilteredCountries(String query) {
        List<Country> tempCountryList = new ArrayList<Country>();
        preferredCountriesCount = 0;
        if(codePicker.preferredCountries!=null && codePicker.preferredCountries.size()>0) {
            for (Country country : codePicker.preferredCountries) {
             //   Log.e(TAG, "Name of Country is : "+country+" "+country.getName()+" "+country.getNameCode()+
             //   " "+country.getPhoneCode());
              //  country.setName(country.getName());
                country.setNameCode(country.getNameCode());
                country.setPhoneCode(country.getPhoneCode());
                if (country.isEligibleForQuery(query)) {
                    tempCountryList.add(country);
                    preferredCountriesCount++;
                }
            }

            if (tempCountryList.size() > 0) { //means at least one preferred country is added.
                Country divider = null;
                tempCountryList.add(divider);
                preferredCountriesCount++;
            }
        }

        for (Country country : masterCountries) {
            if (country.isEligibleForQuery(query)) {
                tempCountryList.add(country);
            }
        }
        return tempCountryList;
    }

    @Override
    public CountryCodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = inflater.inflate(R.layout.layout_recycler_country_title, viewGroup, false);
        CountryCodeViewHolder viewHolder = new CountryCodeViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CountryCodeViewHolder countryCodeViewHolder, final int i) {
        countryCodeViewHolder.setCountry(filteredCountries.get(i));
        countryCodeViewHolder.getMainView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filteredCountries != null && filteredCountries.size() > i) {
                    codePicker.setSelectedCountry(filteredCountries.get(i));
                }
                if (view != null && filteredCountries.get(i)!=null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredCountries.size();
    }

    @Override
    public String getSectionTitle(int position) {
        Country country = filteredCountries.get(position);
        if (preferredCountriesCount > position) {
            return "★";
        } else if (country != null) {
            return country.getName().substring(0, 1);
        } else {
            return "☺"; //this should never be the case

        }
    }

    class CountryCodeViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout_main;
        TextView textView_name, textView_code;
        ImageView imageViewFlag;
        LinearLayout linearFlagHolder;
        View divider;

        public CountryCodeViewHolder(View itemView) {
            super(itemView);
            relativeLayout_main = (RelativeLayout) itemView;
            textView_name = (TextView) relativeLayout_main.findViewById(R.id.textView_countryName);
            textView_code = (TextView) relativeLayout_main.findViewById(R.id.textView_code);
            imageViewFlag = (ImageView) relativeLayout_main.findViewById(R.id.image_flag);
            linearFlagHolder = (LinearLayout) relativeLayout_main.findViewById(R.id.linear_flag_holder);
            divider = relativeLayout_main.findViewById(R.id.preferenceDivider);

            if (codePicker.getDialogTextColor() != 0) {
                textView_name.setTextColor(codePicker.getDialogTextColor());
                textView_code.setTextColor(codePicker.getDialogTextColor());
                divider.setBackgroundColor(codePicker.getDialogTextColor());
            }
        }

        public void setCountry(Country country) {
            if(country!=null) {
                divider.setVisibility(View.GONE);
                textView_name.setVisibility(View.VISIBLE);
                textView_code.setVisibility(View.VISIBLE);
                if (codePicker.isCcpDialogShowPhoneCode()) {
                    textView_code.setVisibility(View.VISIBLE);
                } else {
                    textView_code.setVisibility(View.GONE);
                }
                linearFlagHolder.setVisibility(View.VISIBLE);
                textView_name.setText(country.getName() + " (" + country.getNameCode().toUpperCase() + ")");
                textView_code.setText("+" + country.getPhoneCode());
                imageViewFlag.setImageResource(country.getFlagID());
            }else{
                divider.setVisibility(View.VISIBLE);
                textView_name.setVisibility(View.GONE);
                textView_code.setVisibility(View.GONE);
                linearFlagHolder.setVisibility(View.GONE);
            }
        }

        public RelativeLayout getMainView() {
            return relativeLayout_main;
        }
    }
}
