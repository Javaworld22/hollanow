package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.CallNoteBottomSheet;
import com.doxa360.android.betacaller.ContactDetailActivity;
import com.doxa360.android.betacaller.HomeActivity;
import com.doxa360.android.betacaller.R;
//import com.doxa360.android.betacaller.helpers.BubbleTextGetter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> /**implements  BubbleTextGetter **/{

  private static final String TAG = ContactAdapter.class.getSimpleName();
  private List<Parse_Contact> mContactList;
  ContactViewHolder mHolder;
  private Context mContext;
  private LayoutInflater mLayoutInflater;
  HollaNowSharedPref sharedPref;

  public ContactAdapter(List<Parse_Contact> contactList, Context context) {
    mContactList = contactList;
    mContext = context;
    sharedPref = new HollaNowSharedPref(context);
    mLayoutInflater = LayoutInflater.from(context);
  }

  public class ContactViewHolder extends RecyclerView.ViewHolder {

    ImageView mContactPhoto;
    TextView mContactName;
    TextView mContactPhone;
    TextView mContactInvite;
    ImageView mContactGeo;
    ImageView mLocationBeep;
    ImageView mPlaceCall;

    public ContactViewHolder(View itemView) {
      super(itemView);

      mContactPhoto = (ImageView) itemView.findViewById(R.id.contact_photo);
      mContactName = (TextView) itemView.findViewById(R.id.contact_name);
      mContactPhone = (TextView) itemView.findViewById(R.id.contact_phone);
      mContactInvite = null;//(TextView) itemView.findViewById(R.id.contact_invite);
      mContactGeo = null;//(TextView) itemView.findViewById(R.id.contact_geo);
      mLocationBeep = (ImageView) itemView.findViewById(R.id.location_beep);
      mPlaceCall = (ImageView) itemView.findViewById(R.id.place_call);


      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(mContext, ContactDetailActivity.class);
          intent.putExtra(BetaCaller.CONTACT_ID, mContactList.get(getPosition()).getId());
          intent.putExtra(BetaCaller.CONTACT_NAME, mContactList.get(getPosition()).getDisplayName());
          intent.putExtra(BetaCaller.CONTACT_PHONE, mContactList.get(getPosition()).getPhoneNumber());
          intent.putExtra(BetaCaller.CONTACT_PHOTO, mContactList.get(getPosition()).getThumbnailUrl());
          mContext.startActivity(intent);
        }
      });

      mPlaceCall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mPlaceCallListener(mContactList.get(getPosition()).getPhoneNumber());
        }
      });
      mContactPhone.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          mPlaceCallListener(mContactList.get(getPosition()).getPhoneNumber());
        }
      });

//            mContactInvite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });


    }
  }

  private void mPlaceCallListener(String phoneNumber) {
    //do call note here...
    CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
    FragmentManager fragmentManager = ((HomeActivity) mContext).getSupportFragmentManager();
    Bundle args = new Bundle();
    args.putString(Parse_Contact.PHONE_NUMBER, phoneNumber);
    callNoteBottomSheet.setArguments(args);
    callNoteBottomSheet.show(fragmentManager,"CALL_NOTE");
  }

  @Override
  public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mLayoutInflater.inflate(R.layout.contact_layout, parent, false);
    return new ContactViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ContactViewHolder holder, int position) {
    Parse_Contact contact = mContactList.get(position);
    mHolder = holder;
      double distanceApart =0.0;

    holder.mContactName.setText(contact.getDisplayName());
    holder.mContactPhone.setText(contact.getPhoneNumber());

    if (contact.getThumbnailUrl()!=null) {
      Picasso.with(mContext)
              .load(contact.getThumbnailUrl())
              .into(holder.mContactPhoto);
    } else {
      Picasso.with(mContext)
              .load(R.drawable.wil_profile)
              .into(holder.mContactPhoto);
    }
    if (contact.getLatitude()!=0 && contact.getLongitude()!=0) {
      Log.e(TAG, contact.getDisplayName() +" has location "+ contact.getLatitude() + "," + contact.getLongitude());
      if (sharedPref.getLattitude()!=0 && sharedPref.getLongtitude()!=0) {
        holder.mLocationBeep.setVisibility(View.VISIBLE);
         distanceApart = getDistanceApartKm(contact.getLatitude(), contact.getLongitude(), sharedPref.getLattitude(), sharedPref.getLongtitude());
        if (distanceApart <= 4.0) {
          holder.mLocationBeep.setColorFilter(Color.GREEN);
        } else if (distanceApart > 4.0){
          holder.mLocationBeep.setColorFilter(Color.RED);
            Log.e(TAG,"Distance apart "+contact.getDisplayName()+" "+distanceApart);
        }
      } else {
        holder.mLocationBeep.setColorFilter(Color.GRAY);
      }

    } else {
      holder.mLocationBeep.setVisibility(View.INVISIBLE);
        Log.e(TAG,"Distance apart "+contact.getDisplayName()+" "+distanceApart);
    }

  }

  @Override
  public int getItemCount() {
    if (mContactList != null)
      return mContactList.size();
    else
      return 0;
  }

  // @Override
  public String getTextToShowInBubble(final int pos) {
    return Character.toString(mContactList.get(pos).getDisplayName().charAt(0));
  }

  private double getDistanceApartKm(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515;
    return (dist);
  }

  private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
  }
  private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
  }







}
