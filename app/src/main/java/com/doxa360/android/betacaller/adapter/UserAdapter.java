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

import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.model.Category;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ContactViewHolder> {

    private List<ParseUser> mParseUserList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public UserAdapter(List<ParseUser> parseUserList, Context context) {
        mParseUserList = parseUserList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mDisplayName;
        TextView mUsername;
        TextView mPhoneNumber;
        ImageView mPhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mDisplayName = (TextView) itemView.findViewById(R.id.displayname);
//            mUsername = (TextView) itemView.findViewById(R.id.username);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.phone);
            mPhoto = (ImageView) itemView.findViewById(R.id.photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    ParseUser user = mParseUserList.get(getPosition());
                    intent.putExtra(ProfileActivity.USER_ID, user.getObjectId());
                    intent.putExtra(ProfileActivity.USER_NAME, user.getUsername());
                    intent.putExtra(ProfileActivity.FULL_NAME, user.getString("name"));
                    intent.putExtra(ProfileActivity.PHONE, user.getString("phoneNumber"));
                    intent.putExtra(ProfileActivity.EMAIL, user.getEmail());
                    if (user.getParseFile("photo")!=null) {
                        intent.putExtra(ProfileActivity.PHOTO, user.getParseFile("photo").getUrl());
                    }
                    mContext.startActivity(intent);
                }
            });


        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.user_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ParseUser user = mParseUserList.get(position);
        mHolder = holder;

        holder.mDisplayName.setText(user.getString("name"));
        holder.mPhoneNumber.setText(user.getString("phoneNumber"));
        if (user.getParseFile("photo")!=null) {
            Picasso.with(mContext)
                    .load(user.getParseFile("photo").getUrl())
                    .placeholder(R.drawable.wil_profile)
                    .error(R.drawable.wil_profile)
                    .into(holder.mPhoto);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.wil_profile)
                    .placeholder(R.drawable.wil_profile)
                    .error(R.drawable.wil_profile)
                    .into(holder.mPhoto);
        }

    }

    @Override
    public int getItemCount() {
        if (mParseUserList != null)
            return mParseUserList.size();
        else
            return 0;
    }




}
