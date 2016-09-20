package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.ContactDetailActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ContactViewHolder> {

    private List<Category> mCategoryList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        mCategoryList = categoryList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mCategory;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mCategory = (TextView) itemView.findViewById(R.id.category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, ContactDetailActivity.class);
//                    intent.putExtra(BetaCaller.CONTACT_ID, mCategoryList.get(getPosition()).getId());
//                    intent.putExtra(BetaCaller.CONTACT_NAME, mCategoryList.get(getPosition()).getDisplayName());
//                    intent.putExtra(BetaCaller.CONTACT_PHONE, mCategoryList.get(getPosition()).getPhoneNumber());
//                    intent.putExtra(BetaCaller.CONTACT_PHOTO, mCategoryList.get(getPosition()).getThumbnailUrl());
//                    mContext.startActivity(intent);
                    Toast.makeText(mContext, "There are currently no users in this category", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.category_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Category category = mCategoryList.get(position);
        mHolder = holder;

        holder.mCategory.setText(category.getCategory());

    }

    @Override
    public int getItemCount() {
        if (mCategoryList != null)
            return mCategoryList.size();
        else
            return 0;
    }




}
