package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.NearbySearchActivity;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Apple on 09/01/16.
 */
public class NearbyUserAdapter extends RecyclerView.Adapter<NearbyUserAdapter.ContactViewHolder> {

    private List<ParseUser> mParseUserList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ParseGeoPoint mCurrentGeoPoint;
    boolean fullWidth;

    public NearbyUserAdapter(List<ParseUser> parseUserList, ParseGeoPoint currentGeoPoint, Context context) {
        mParseUserList = parseUserList;
        mContext = context;
        fullWidth = false;
        mCurrentGeoPoint = currentGeoPoint;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public NearbyUserAdapter(List<ParseUser> nearbyUsersList, ParseGeoPoint currentParseGeopoint, NearbySearchActivity nearbySearchActivity, boolean fullWidth) {
        mParseUserList = nearbyUsersList;
        mContext = nearbySearchActivity;
        this.fullWidth = true;
        mCurrentGeoPoint = currentParseGeopoint;
        mLayoutInflater = LayoutInflater.from(nearbySearchActivity);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mDisplayName;
        TextView mUsername;
        TextView mPhoneNumber;
        TextView mAddress;
        TextView mMilesAway;
        ImageView mPhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mDisplayName = (TextView) itemView.findViewById(R.id.displayname);
//            mUsername = (TextView) itemView.findViewById(R.id.username);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.phone);
            mPhoto = (ImageView) itemView.findViewById(R.id.photo);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mMilesAway = (TextView) itemView.findViewById(R.id.distance_away);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser user = mParseUserList.get(getPosition());
                    if (user!=null) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(ProfileActivity.USER_ID, user.getObjectId());
                        intent.putExtra(ProfileActivity.USER_NAME, user.getUsername());
                        intent.putExtra(ProfileActivity.FULL_NAME, user.getString("name"));
                        intent.putExtra(ProfileActivity.PHONE, user.getString("phoneNumber"));
                        intent.putExtra(ProfileActivity.EMAIL, user.getEmail());
                        if (user.getParseFile("photo") != null) {
                            intent.putExtra(ProfileActivity.PHOTO, user.getParseFile("photo").getUrl());
                        }
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, NearbySearchActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });


        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (fullWidth) {
            view = mLayoutInflater.inflate(R.layout.nearby_user_full_width_layout, parent, false);
        } else {
            view = mLayoutInflater.inflate(R.layout.nearby_user_layout, parent, false);
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ParseUser user = mParseUserList.get(position);
        mHolder = holder;

        if (user!=null) {
            holder.mDisplayName.setText(user.getString("name"));
            holder.mPhoneNumber.setText(user.getString("phoneNumber"));
            String address = user.getString("lastSeenAddress");
            if (address!=null) {
                address = address.replace("null,", "");
                holder.mAddress.setText(address);
            }
            ParseGeoPoint geoPoint = user.getParseGeoPoint("lastSeen");
            if (geoPoint!=null) {
                String kmAway = String.format(Locale.getDefault(), "%.2f", geoPoint.distanceInKilometersTo(mCurrentGeoPoint)) + " km away";
                holder.mMilesAway.setText(kmAway);
            }

            if (user.getParseFile("photo") != null) {
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
        } else {
            holder.mDisplayName.setText("Search users near you");
            holder.mPhoneNumber.setText("");
            holder.mAddress.setText("");
            holder.mMilesAway.setText("");
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
