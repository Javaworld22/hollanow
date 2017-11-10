package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.HomeActivity;
import com.doxa360.android.betacaller.NotificationActivity;
import com.doxa360.android.betacaller.NotificationFragment;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowDbHelper;
import com.doxa360.android.betacaller.model.bimps;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;
import java.util.Set;

/**
 * Created by user on 7/28/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ContactViewHolder>{

    private LayoutInflater mLayoutInflater;
    private List<bimps> mNotificationList;
    private bimps mModel;
    private bimps mModel1;
    private Context mContext;
    ContactViewHolder mHolder;
    private HollaNowDbHelper helper;
    //private Set<String> mSetHead,mSetBody,mSetDate;
    private List<bimps> mBip;
    private NativeExpressAdView mAdView;


    public NotificationAdapter(bimps model, Context context){
        mModel = model;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public NotificationAdapter(List<bimps> bip, Context context){
        mBip = bip;
        mContext = context;
        helper = new HollaNowDbHelper(context);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.message_notification, parent, false);
        return new ContactViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        // NotificationModel model = mNotificationList.get(position);
        try {
            mModel = mBip.get(position);
        }catch (IndexOutOfBoundsException ex){
            if(mModel1 != null) {
                holder.messageHead.setText(mModel1.getHeadNotification());
                Log.e("NotificationAdapter", "Notification response5: " + position);
                // }
                Log.e("NotificationAdapter", "Notification response1: " + mModel1.getHeadNotification());
                holder.messageBody.setText(mModel1.getNotification());
                holder.mCallDate.setText(mModel1.getCreatedDate());
                return;
            }else if(mModel1 == null){
                mModel1 = mModel;
                holder.mContactPhoto.setVisibility(View.GONE);
                holder.closeButton.setVisibility(View.GONE);
                //  android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                AdRequest request = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  //  bbb0eb0fce7bf3a8
                        .addTestDevice("E0A7012BF382436CB461659B1F229E03")    // "E0A7012BF382436CB461659B1F229E03"
                        .build();
                holder.adView.loadAd(request);
                // Log.v("Ads", "Notification response5: " + position);
                return;
            }
        }catch (NullPointerException zz){
            if(mModel1 != null){
                holder.messageHead.setText(mModel1.getHeadNotification());
                Log.e("NotificationAdapter", "Notification response5: " + position);
                // }
                Log.e("NotificationAdapter", "Notification response1: " + mModel1.getHeadNotification());
                holder.messageBody.setText(mModel1.getNotification());
                holder.mCallDate.setText(mModel1.getCreatedDate());
                return;
            }else if(mModel1 == null){
                mModel1 = mModel;
                holder.mContactPhoto.setVisibility(View.GONE);
                holder.closeButton.setVisibility(View.GONE);
                //  android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                AdRequest request = new AdRequest.Builder()
                         //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  //  bbb0eb0fce7bf3a8
                         //    .addTestDevice("E0A7012BF382436CB461659B1F229E03")    // "E0A7012BF382436CB461659B1F229E03"
                        .build();
                holder.adView.loadAd(request);
                // Log.v("Ads", "Notification response5: " + position);
                return;
            }
        }
        mHolder = holder;
        try {
            if (mModel.getNotification() != null && position != 1) {
                if (position == 0) {
                    holder.messageHead.setText(mModel.getHeadNotification());
                    Log.e("NotificationAdapter", "Notification response5: " + position);
                    // }
                    Log.e("NotificationAdapter", "Notification response1: " + mModel.getHeadNotification());
                    holder.messageBody.setText(mModel.getNotification());
                    holder.mCallDate.setText(mModel.getCreatedDate());

                    Log.e("NotificationAdapter", "Notification response2: " + mModel.getNotification());
                    Log.e("NotificationAdapter", "Notification response3: " + mModel.getCreatedDate());
                } else if (position >= 2) {
                    holder.messageHead.setText(mModel1.getHeadNotification());
                    Log.e("NotificationAdapter", "Notification response5: " + position);
                    // }
                    Log.e("NotificationAdapter", "Notification response1: " + mModel1.getHeadNotification());
                    holder.messageBody.setText(mModel1.getNotification());
                    holder.mCallDate.setText(mModel1.getCreatedDate());
                    holder.mContactPhoto.setVisibility(View.VISIBLE);
                    holder.closeButton.setVisibility(View.VISIBLE);
                    mModel1 = mModel;

                }

            } else if (position == 1) {
                mModel1 = mModel;
                holder.mContactPhoto.setVisibility(View.GONE);
                holder.closeButton.setVisibility(View.GONE);
                //  android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                AdRequest request = new AdRequest.Builder()
                         //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  //  bbb0eb0fce7bf3a8
                         //  .addTestDevice("E0A7012BF382436CB461659B1F229E03")    // "E0A7012BF382436CB461659B1F229E03"
                        .build();
                holder.adView.loadAd(request);
                // Log.v("Ads", "Notification response5: " + position);
            } else {
                holder.messageBody.setVisibility(View.GONE);
                holder.messageHead.setVisibility(View.GONE);
                holder.mContactPhoto.setVisibility(View.GONE);
                //  AdRequest request = new AdRequest.Builder().build();
                //.addTestDevice();
                //  holder.adView.loadAd(request);

                // holder.itemView.setTag(mModel);
            }
        }catch (NullPointerException aa){

        }
    }



    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView mContactPhoto;
        //ImageView mCallType;
        TextView messageHead;
        TextView messageBody;
        TextView mCallDate;
        ImageView closeButton;
        NativeExpressAdView adView;


        public ContactViewHolder(View itemView) {
            super(itemView);

            mContactPhoto = (ImageView) itemView.findViewById(R.id.notification_icon);
           // mCallType = (ImageView) itemView.findViewById(R.id.call_type);
            messageHead = (TextView) itemView.findViewById(R.id.message_head);
            messageBody = (TextView) itemView.findViewById(R.id.message_body);
            mCallDate = (TextView) itemView.findViewById(R.id.notification_date);
            closeButton = (ImageView) itemView.findViewById(R.id.notification_closebutton);
            adView = (NativeExpressAdView) itemView.findViewById(R.id.adView4);
            mAdView = adView;
            closeButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    bimps bips = new bimps(messageHead.getText().toString(),messageBody.getText().toString(),
                            7,mCallDate.getText().toString());
                    helper.removeSingleNotification(bips);
                   // new NotificationFragment();
                    //notifyDataSetChanged();
                    Intent intent = new Intent(mContext, NotificationActivity.class);
                    mContext.startActivity(intent);
                    Toast.makeText(mContext, "Notification Deleted", Toast.LENGTH_LONG).show();

                }
            });

           // notifyDataSetChanged();


        }
    }



    @Override
    public int getItemCount() {

        if (mBip != null) {
            Log.e("NotificationAdapter", "Notification response4: " +mBip.size() );
            return mBip.size() + 1;
        }
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }
}
