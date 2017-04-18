package com.doxa360.android.betacaller;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.Industry;
import com.doxa360.android.betacaller.model.User;
import com.thinkincode.utils.views.HorizontalFlowLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIndustryFragment extends DialogFragment {

    private static final String TAG = "AddIndustryFragment";
    private EditProfileActivity mActivity;
    private EditText mTagsEditText;
    private HorizontalFlowLayout mCategoryLayout;
    private ProgressBar mProgressBar;
    private String selectedIndustry;
    private HollaNowApiInterface hollaNowApiInterface;
    HollaNowSharedPref mSharedPref;
    private User currentUser;

    public AddIndustryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_add_industry, container, false);

        getDialog().setTitle("Choose your industry");

        mSharedPref = new HollaNowSharedPref(mActivity);
        currentUser = mSharedPref.getCurrentUser();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mCategoryLayout = (HorizontalFlowLayout) rootView.findViewById(R.id.industry_layout);

        if (MyToolBox.isNetworkAvailable(mActivity)) {
            getIndustries();
        } else {
            MyToolBox.AlertMessage(mActivity, "Network error. Please check your connection");
        }


        return rootView;
    }

    private void getIndustries() {
        Call<List<Industry>> call = hollaNowApiInterface.
                getIndustry((currentUser.getToken()!="")?currentUser.getToken():mSharedPref.getToken());
        call.enqueue(new Callback<List<Industry>>() {
            @Override
            public void onResponse(Call<List<Industry>> call, Response<List<Industry>> response) {
                if (response.code()==200) {
                    displayIndustries(response.body());
                } else {
                    Toast.makeText(mActivity, "Error loading industries", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Industry>> call, Throwable t) {
//                Log.e(TAG, t.getMessage());
                MyToolBox.AlertMessage(mActivity, "Network error. Please check your connection");
            }
        });
    }


    private void displayIndustries(List<Industry> industries) {
        TextView industryChips;
        if (industries!=null) {
            for (Industry industry : industries) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 16, 32);
                industryChips = new TextView(mActivity);
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
                mActivity.updateIndustry(industry);
                getDialog().dismiss();
            }
        };
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = (EditProfileActivity) activity;
        super.onAttach(activity);
    }
}
