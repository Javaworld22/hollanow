package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.NearbySearchActivity;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.model.User;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

/**
 * Created by Apple on 09/01/16.
 */
public class NearbyUserAdapter extends RecyclerView.Adapter<NearbyUserAdapter.ContactViewHolder> {

    private List<User> mUsers;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private float mLat;
    private float mLng;
    boolean fullWidth;

    public NearbyUserAdapter(List<User> users, float lat, float lng, Context context) {
        mUsers = users;
        mContext = context;
        mLat = lat;
        mLng = lng;
        fullWidth = false;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public NearbyUserAdapter(List<User> users, float lat, float lng, NearbySearchActivity nearbySearchActivity, boolean fullWidth) {
        mUsers = users;
        mContext = nearbySearchActivity;
        mLat = lat;
        mLng = lng;
        this.fullWidth = true;
        mLayoutInflater = LayoutInflater.from(nearbySearchActivity);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mDisplayName;
        TextView mPhoneNumber;
        TextView mLastSeen;
        TextView mDistanceAwayKm;
        ImageView mPhoto;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mDisplayName = (TextView) itemView.findViewById(R.id.displayname);
            mPhoneNumber = (TextView) itemView.findViewById(R.id.phone);
            mPhoto = (ImageView) itemView.findViewById(R.id.photo);
            mLastSeen = (TextView) itemView.findViewById(R.id.address);
            mDistanceAwayKm = (TextView) itemView.findViewById(R.id.distance_away);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = mUsers.get(getPosition());
                    if (user!=null) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(BetaCaller.USER_PROFILE, user);
                        Log.e("Nearby adapter", user.getName()+user.getEmail()+"");
                        mContext.startActivity(intent);
                    } else {
                        //NOTE: for view more item...
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
        User user = mUsers.get(position);
        mHolder = holder;

        if (user!=null) {
            holder.mDisplayName.setText(user.getName());
            holder.mPhoneNumber.setText(user.getPhone());
            String lastSeen = user.getLastSeen();
            if (lastSeen!=null) {
                lastSeen = lastSeen.replace("null,", "");
                holder.mLastSeen.setText(lastSeen);
            }

            if (user.getLat()!=0 & user.getLng() != 0) {
//                String kmAway = String.format(Locale.getDefault(), "%.2f", user.getDistance()) + " km away";
                holder.mDistanceAwayKm.setText(user.getDistance());
            }


            if (user.getProfilePhoto() != null) {
                Picasso.with(mContext)
                        .load(BetaCaller.PHOTO_URL+user.getProfilePhoto())
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
            holder.mDisplayName.setText("Search for people and \n businesses near you");
            holder.mPhoneNumber.setText("");
            holder.mLastSeen.setText("");
            holder.mDistanceAwayKm.setText("");
            Picasso.with(mContext)
                    .load(R.drawable.wil_profile)
                    .placeholder(R.drawable.wil_profile)
                    .error(R.drawable.wil_profile)
                    .into(holder.mPhoto);

        }

    }

    @Override
    public int getItemCount() {
        if (mUsers != null)
            return mUsers.size();
        else
            return 0;
    }




}
