package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.ContactDetailActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.PhoneCallLog;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class PhoneCallLogAdapter extends RecyclerView.Adapter<PhoneCallLogAdapter.ContactViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    private List<PhoneCallLog> mCallLogList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private boolean isFragment;

    public PhoneCallLogAdapter(List<PhoneCallLog> callLogList, Context context) {
        mCallLogList = callLogList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.isFragment = true;
    }

    public PhoneCallLogAdapter(List<PhoneCallLog> callLogList, Context context, boolean isFragment) {
        mCallLogList = callLogList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.isFragment = isFragment;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView mContactPhoto;
        ImageView mCallType;
        TextView mContactName;
        TextView mCallDuration;
        TextView mCallDate;

        public ContactViewHolder(View itemView) {
            super(itemView);

            mContactPhoto = (ImageView) itemView.findViewById(R.id.contact_photo);
            mCallType = (ImageView) itemView.findViewById(R.id.call_type);
            mContactName = (TextView) itemView.findViewById(R.id.contact_name);
            mCallDuration = (TextView) itemView.findViewById(R.id.call_duration);
            mCallDate = (TextView) itemView.findViewById(R.id.call_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFragment) {
                        Intent intent = new Intent(mContext, ContactDetailActivity.class);
                        intent.putExtra(BetaCaller.CONTACT_ID, mCallLogList.get(getPosition()).getId());
                        intent.putExtra(BetaCaller.CONTACT_NAME, mCallLogList.get(getPosition()).getDisplayName());
                        intent.putExtra(BetaCaller.CONTACT_PHONE, mCallLogList.get(getPosition()).getPhoneNumber());
                        intent.putExtra(BetaCaller.CONTACT_PHOTO, mCallLogList.get(getPosition()).getThumbnailUrl());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.call_log_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        PhoneCallLog callLog = mCallLogList.get(position);
        mHolder = holder;

        if (callLog.getDisplayName()!=null) {
            holder.mContactName.setText(callLog.getDisplayName());
        } else {
            holder.mContactName.setText(callLog.getPhoneNumber());
        }
        holder.mCallDuration.setText(callLog.getDuration()+ "s"); //TODO: convert seconds to more readable format
        holder.mCallDate.setText(MyToolBox.getShortTimeAgo(callLog.getDate(), mContext)+ " ago");

        if (callLog.getThumbnailUrl()!=null) {
            Log.e(TAG, callLog.getThumbnailUrl() + "");
            Picasso.with(mContext)
                    .load(callLog.getThumbnailUrl())
                    .into(holder.mContactPhoto);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.wil_profile)
                    .into(holder.mContactPhoto);
        }

        switch (callLog.getType()) {

            case CallLog.Calls.INCOMING_TYPE:
                holder.mCallType.setImageResource(R.drawable.ic_call_received_white_18dp);
                holder.mCallType.setColorFilter(mContext.getResources().getColor(android.R.color.holo_green_light));
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.mCallType.setImageResource(R.drawable.ic_call_missed_white_18dp);
                holder.mCallType.setColorFilter(mContext.getResources().getColor(android.R.color.holo_red_light));
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.mCallType.setImageResource(R.drawable.ic_call_made_white_18dp);
                holder.mCallType.setColorFilter(mContext.getResources().getColor(android.R.color.holo_green_light));
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mCallLogList != null)
            return mCallLogList.size();
        else
            return 0;
    }




}
