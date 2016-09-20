package com.doxa360.android.betacaller;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doxa360.android.betacaller.model.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.thinkincode.utils.views.HorizontalFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class AddIndustryFragment extends DialogFragment {

    private static final String TAG = "AddIndustryFragment";
    private EditProfileActivity mActivity;
    private EditText mTagsEditText;
    private HorizontalFlowLayout mCategoryLayout;
    private ProgressBar mProgressBar;
    private List<String> selectedTags;

    public AddIndustryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_add_industry, container, false);

        getDialog().setTitle("Select your industry");
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mCategoryLayout = (HorizontalFlowLayout) rootView.findViewById(R.id.category_horizontal_layout);
        Button mFinishBtn = (Button) rootView.findViewById(R.id.finish_button);

        selectedTags = new ArrayList<String>();

        getCategories();

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.updateTags(selectedTags);
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    private void getCategories() {
        ParseQuery<Category> query = Category.getQuery();
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
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
                categoryChips = new TextView(mActivity);
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
                if (isSelected(category.getCategory().toLowerCase())) {
                    view.setBackgroundResource(R.drawable.chips);
                    selectedTags.remove(category.getCategory().toLowerCase());
                } else {
                    view.setBackgroundResource(R.drawable.selected_chips);
                    selectedTags.add(category.getCategory().toLowerCase());
                }
            }
        };
    }

    private boolean isSelected(String s) {
        for (String category:selectedTags) {
            if (category.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = (EditProfileActivity) activity;
        super.onAttach(activity);
    }
}
