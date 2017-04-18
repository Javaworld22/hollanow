package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ContactViewHolder> {

    private List<User> mUserList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public UserAdapter(List<User> userList, Context context) {
        mUserList = userList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mDisplayName;
        TextView mUsername;
        TextView mPhoneNumber;
        ImageView mPhoto;
        TextView mOccupation;

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
                    User user = mUserList.get(getPosition());
                    intent.putExtra(BetaCaller.USER_PROFILE, user);
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
        User user = mUserList.get(position);
        mHolder = holder;

        holder.mDisplayName.setText(user.getName());
        holder.mPhoneNumber.setText(user.getPhone());
        if (user.getProfilePhoto()!=null) {
            Picasso.with(mContext)
                    .load(BetaCaller.PHOTO_URL + user.getProfilePhoto())
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
        if (mUserList != null)
            return mUserList.size();
        else
            return 0;
    }




}
