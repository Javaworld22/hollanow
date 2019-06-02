package com.doxa360.android.betacaller.adapter;

//import android.support.v4.a;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
        import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
        import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
        import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.CallNoteBottomSheet;
        import com.doxa360.android.betacaller.InviteFragment;
import com.doxa360.android.betacaller.NearbySearchActivity;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
        import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
        import com.doxa360.android.betacaller.model.Parse_Contact;
        import com.doxa360.android.betacaller.model.SerializableUser;
import com.doxa360.android.betacaller.model.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

        import java.io.ByteArrayOutputStream;
import java.io.File;
        import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
public class HollaContactAdapter extends  RecyclerView.Adapter<HollaContactAdapter.ContactViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    ContactViewHolder mHolder;
    private final Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Parse_Contact> mCallLogList;
    private boolean isFragment;
    private HollaNowSharedPref sharedPref;

    LayoutInflater inflater;
    View rowView;
    File mediaFile;
   // User mUser;

    public HollaContactAdapter(Context context, List<Parse_Contact> callLogList) {
        //super(context,R.layout.call_log_layout, itemName);
        mCallLogList = callLogList;
      //  mUser = user;
        //mItemName = itemName;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        sharedPref = new HollaNowSharedPref(mContext);
        //mImgid = imgid;
        // mediaFile = mMedia;
    }

    public HollaContactAdapter(List<Parse_Contact> callLogList, Context context, boolean isFragment) {
        mCallLogList = callLogList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.isFragment = isFragment;
    }

    public void setAdapterList(List<Parse_Contact> callLogList){
        mCallLogList = callLogList;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView mContactPhoto;
        ImageView mCallType;
        ImageView mCallIcon;
        TextView mContactName;
        TextView mCallDuration;
        TextView lastSeen;
        CardView cardView;
        TextView occupation;
        private AlertDialog.Builder builder;
        ImageView location;


        public ContactViewHolder(View itemView) {
            super(itemView);
            // inflater = /**mContext.getLayoutInflater();**/ (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //  rowView = inflater.inflate(R.layout.call_log_layout,null,true);
            mContactPhoto = (ImageView) itemView.findViewById(R.id.contact_photo);
           // mCallType = (ImageView) itemView.findViewById(R.id.call_type);
            mContactName = (TextView) itemView.findViewById(R.id.contact_name);
            mCallDuration = (TextView) itemView.findViewById(R.id.call_duration);
            cardView = (CardView) itemView.findViewById(R.id.cardview_hollacontacts);
            mCallIcon = (ImageView) itemView.findViewById(R.id.call_icon);
            builder = new AlertDialog.Builder(mContext);
            occupation = (TextView) itemView.findViewById(R.id.occup);
            location = (ImageView) itemView.findViewById(R.id.location_beep1);
            lastSeen = (TextView) itemView.findViewById(R.id.seen_last);
            //  mContactPhoto.setImageResource(mImgid[position]);

            // mContactName.setText(mItemName[position]);
            // mCallDuration.setText(" ");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        Parse_Contact dataClicked = mCallLogList.get(pos);
                        if (dataClicked == null) {
                            InviteFragment faceBookAdvertFragment = new InviteFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                            Bundle args = new Bundle();
                            faceBookAdvertFragment.setArguments(args);
                            //finishActivity(12345);
                            faceBookAdvertFragment.show(fragmentManager, "SHARE");
                        }else if(dataClicked != null){
                             SerializableUser user =  dataClicked.getSerialUser();
                            mPlaceCallListener(user.getPhone(),user.getName(),user.getUsername(),user.getProfilePhoto());
                        }
                    }

                }
            });

           mContactName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    User user = new User();
                    int pos = getAdapterPosition();
                    if(mCallLogList.get(pos) == null){
                        if(pos != RecyclerView.NO_POSITION) {
                            Parse_Contact dataClicked = mCallLogList.get(pos);
                          //  if (dataClicked == null) {
                                InviteFragment faceBookAdvertFragment = new InviteFragment();
                                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                                Bundle args = new Bundle();
                                faceBookAdvertFragment.setArguments(args);
                                //finishActivity(12345);
                                faceBookAdvertFragment.show(fragmentManager, "SHARE");
                           // }//else if(dataClicked != null){
                             //   mPlaceCallListener(dataClicked.getPhoneNumber());
                           // }
                        }
                    }else if(pos != RecyclerView.NO_POSITION && mCallLogList.get(pos) != null) {
                        SerializableUser serial = mCallLogList.get(pos).getSerialUser();
                        String url = mCallLogList.get(pos).getThumbnailUrl();
                        if (serial != null) {
                            user.setIndustry(serial.getIndustry());
                            user.setUsername(serial.getUsername());
                            user.setName(serial.getName());
                            user.setOccupation(serial.getOccupation());
                            user.setAbout(serial.getAbout());
                            user.setPhone(serial.getPhone());
                            if(mCallLogList.get(pos).getThumbnailUrl() != null)
                            user.setProfilePhoto(mCallLogList.get(pos).getThumbnailUrl());

                            Log.e("HollaContactAdapter","ProfilePhoto 3"+mCallLogList.get(pos).getThumbnailUrl() );
                        } else {
                            user = null;
                        }
                        //user.setProfilePhoto(serial.getProfilePhoto());
                        Log.e("Holla ContactAdapter", "");
                        // User user = mUsers.get(getPosition());
                        if (user != null) {
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(BetaCaller.USER_PROFILE, user);  //////////////////////////
                            Log.e("Nearby adapter", user.getName() + user.getEmail() + "");
                            mContext.startActivity(intent);
                        } else {
                            //NOTE: for view more item...
                            Intent intent = new Intent(mContext, NearbySearchActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            mContactPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // View rowView = null;
                    try {
                        int pos = getAdapterPosition();
                        Parse_Contact dataClicked = mCallLogList.get(pos);
                       // LayoutInflater inflater = mContext.getLayoutInflater();
                        View dialoglayout = mLayoutInflater.inflate(R.layout.custom_dialog, null);
                        ImageView mImage = (ImageView) dialoglayout.findViewById(R.id.signup_image_view);
                        Picasso.with(mContext).invalidate(BetaCaller.PHOTO_URL+dataClicked.getThumbnailUrl());
                        Picasso.with(mContext)
                                .load(BetaCaller.PHOTO_URL+dataClicked.getThumbnailUrl())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.wil_profile)
                                .into(mImage);
                        builder.setView(dialoglayout);
                        builder.show();
                    }catch(NullPointerException ex){

                    }catch(Exception a){

                    }
                }
            });

            /**  itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
            if (isFragment) {
            Intent intent = new Intent(mContext, ContactDetailActivity.class);
            intent.putExtra(BetaCaller.CONTACT_ID, mCallLogList.get(getPosition()).getId());
            intent.putExtra(BetaCaller.CONTACT_NAME, mCallLogList.get(getPosition()).getDisplayName());
            intent.putExtra(BetaCaller.CONTACT_PHONE, mCallLogList.get(getPosition()).getPhoneNumber());
            intent.putExtra(BetaCaller.CONTACT_PHOTO, mCallLogList.get(getPosition()).getThumbnailUrl());
            mContext.startActivity(intent);
            }
            }
            }); **/
            // }
            // return rowView;
        }
    }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.call_log_layout, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            Parse_Contact callLog = mCallLogList.get(position);
            mHolder = holder;

            if (callLog != null && callLog.getSerialUser() != null) {
                //Log.e(TAG, "from HollaContactAdapter 2 " + callLog.getSerialUser().getName());
               // Log.e(TAG, "from HollaContactAdapter 2 " +mCallLogList.size());
              /**  if (callLog.getDisplayName() != null) {
                    holder.mContactName.setTypeface(null,Typeface.BOLD_ITALIC);
                    holder.mContactName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimensionPixelSize(R.dimen.default_text));
                   // Log.e(TAG, "Any update available "+callLog.);
                   // holder.mContactName.sst
                    holder.mContactName.setText(callLog.getDisplayName());
                    holder.occupation.setText(callLog.getOccupation());
                    holder.lastSeen.setText(callLog.getLastSeen());
                    holder.mCallDuration.setText(callLog.getPhoneNumber() );
                    Log.e(TAG, "Date is   "+callLog.getLastSeen());
                    Log.e(TAG, "Date is   "+callLog.getLastSeen());
                    Log.e(TAG, "Date is   "+callLog.getLastSeen());
                    Log.e(TAG, "Date is   "+callLog.getLastSeen());
                   // try {
                   //     Date date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).parse(callLog.getLastSeen());
                   //     Log.e(TAG, "Date is   "+date);
                     //   Log.e(TAG, "Date is   "+date);
                      //  Log.e(TAG, "Date is   "+date);
                   // }catch (ParseException s){
                  //  }
                    Log.e(TAG, "Check for the text size"+holder.mContactName.getTextSize());
                    holder.mCallDuration.setVisibility(View.VISIBLE);
                    holder.mContactPhoto.setVisibility(View.VISIBLE);
                    holder.mCallIcon.setVisibility(View.VISIBLE);
                    holder.location.setVisibility(View.VISIBLE);
                    holder.occupation.setVisibility(View.VISIBLE);
                    holder.lastSeen.setVisibility(View.VISIBLE);
                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.holla_white));
                } else **/if(callLog.getSerialUser() != null && callLog.getSerialUser().getName() != null &&
                        callLog.getSerialUser().getPhone() != null){
                   // Log.e(TAG, "Name is   "+callLog.getSerialUser().getName());
                   // Log.e(TAG, "Phone is   "+callLog.getSerialUser().getPhone());
                    holder.mCallDuration.setVisibility(View.VISIBLE);
                    holder.mContactPhoto.setVisibility(View.VISIBLE);
                    holder.location.setVisibility(View.VISIBLE);
                    holder.lastSeen.setVisibility(View.VISIBLE);
                    holder.mContactName.setTypeface(null,Typeface.BOLD_ITALIC);
                    holder.mContactName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            mContext.getResources().getDimensionPixelSize(R.dimen.default_text));
                    holder.mContactName.setText(callLog.getSerialUser().getName());
                    if(callLog.getSerialUser().getName() == null)
                        holder.mContactName.setText(callLog.getSerialUser().getPhone());
                    if(callLog.getSerialUser().getOccupation() != null)
                         holder.occupation.setText(callLog.getSerialUser().getOccupation());
                    else
                        holder.occupation.setVisibility(View.INVISIBLE);

                    if(callLog.getSerialUser().getUpdate() != null)
                    holder.lastSeen.setText(callLog.getSerialUser().getUpdate());

                    if(callLog.getSerialUser().getPhone() != null)
                    holder.mCallDuration.setText(callLog.getSerialUser().getPhone());
                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.holla_white));
                    if(callLog.getSerialUser().getIndicateSaved())
                        holder.location.setColorFilter(mContext.getResources().getColor(android.R.color.holo_green_light));
                    else
                        holder.location.setColorFilter(mContext.getResources().getColor(android.R.color.holo_orange_light));
                    //else
                      //  holder.location.setBackgroundColor(mContext.getResources().getColor(R.color.materialcolorpicker__blue));

                    //Log.e(TAG, "Print user here " + callLog.getSerialUser().getPhone());
                }
              //  else {
              //      holder.mContactName.setText(callLog.getPhoneNumber());
               //     holder.mCallDuration.setVisibility(View.VISIBLE);
               //     holder.mContactPhoto.setVisibility(View.VISIBLE);
               // }
                //TODO: convert seconds to more readable format
                // holder.mCallDate.setText(MyToolBox.getShortTimeAgo(callLog.getDate(), mContext)+ " ago");

              /**  if (callLog.getThumbnailUrl() != null) {
                    try {
                                Uri tempUri = getImageUri(mContext, callLog.getThumbnail(), callLog.getThumbnailUrl());
                                File filePhoto = new File(getRealPathFromURI(tempUri, mContext));
                                Log.e(TAG, callLog.getThumbnailUrl() + "");

                                Picasso.with(mContext).invalidate(filePhoto); // BetaCaller.PHOTO_URL+callLog.getThumbnailUrl()
                                Picasso.with(mContext)
                                        .load(filePhoto)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .placeholder(R.drawable.wil_profile)
                                        .into(holder.mContactPhoto);

                    }catch (NullPointerException x){
                        Log.e(TAG, "An Error Occured 3"+x.getMessage());
                    }
                }else**/ if(callLog.getThumbnailUrl() != null){
                     // if(callLog.getSerialUser().getProfilePhoto() != null)
                  //  Log.e(TAG, "Print user image path " + callLog.getThumbnailUrl());
                  //  Log.e(TAG, "Print user image path " +  callLog.getThumbnailUrl());
                  //  Log.e(TAG, "Print user image path " +  callLog.getThumbnailUrl());
                  //  Log.e(TAG, "Print user image path " + callLog.getThumbnailUrl());
                   // Log.e(TAG, "Print user image path " +  callLog.getThumbnailUrl());
                    File file = new File(callLog.getThumbnailUrl());
                    Picasso.with(mContext).invalidate(file); // BetaCaller.PHOTO_URL+callLog.getThumbnailUrl()
                    Picasso.with(mContext)
                            .load(file)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.wil_profile)
                            .into(holder.mContactPhoto);
                }
                else {
                    Picasso.with(mContext)
                            .load(R.drawable.wil_profile) // ic_share_icon
                            .into(holder.mContactPhoto);
                }
            }
            else if(callLog == null){
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/BLOCKHEAD.ttf");
                holder.mContactName.setTypeface(face);
                holder.mContactName.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimensionPixelSize(R.dimen.Holla_text_view));
                holder.mContactName.setText("INVITE FRIENDS");
                holder.mCallDuration.setVisibility(View.GONE);
                holder.mContactPhoto.setVisibility(View.GONE);
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.primary_light));
                holder.mCallIcon.setVisibility(View.GONE);
                holder.location.setVisibility(View.INVISIBLE);
                holder.lastSeen.setVisibility(View.INVISIBLE);
                holder.occupation.setVisibility(View.INVISIBLE);
              //  holder.mContactName.setText("Invite friends");
           //     Picasso.with(mContext)
            //            .load(R.drawable.ic_share_icon)
            //            .into(holder.mContactPhoto);
            }

            /**  switch (callLog.getType()) {

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
             } **/

            // }


        }

    @Override
    public int getItemCount() {
        if (mCallLogList != null) {
           // Log.e(TAG, "GetItemCount "+mCallLogList.size());
           // Log.e(TAG, "GetItemCount "+mCallLogList.size());
            return mCallLogList.size();
        }
        else
            return 0;
    }

    private Uri getImageUri(Context context, Bitmap bitmap, String title){
        Uri route = null;
        File file = null;
        String pathPhoto = sharedPref.getPhotoPath();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Log.e(TAG, "Path of the photo "+pathPhoto);
          //  Log.e(TAG, "Path of the photo "+pathPhoto);
           // Log.e(TAG, "Path of the photo "+pathPhoto);
           // Log.e(TAG, "Path of the photo "+pathPhoto);

            file = new File(pathPhoto);
            deleteFiles(pathPhoto);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,title,null);
            route = Uri.parse(path);
            sharedPref.setPhotoPath(route.getPath());
            Log.e(TAG, "Path of the photo 3 "+file.getName());
            Log.e(TAG, "Path of the photo 3 "+file.getPath());
          //  Log.e(TAG, "Path of the photo 3 "+file.getAbsolutePath());
           // Log.e(TAG, "Path of the photo 3 "+file.getParentFile());
           // Log.e(TAG, "Path of the photo 3 "+file.getAbsolutePath());
        }catch (NullPointerException e){
            Log.e(TAG, "Exception occurred "+e.getMessage());
        }
        return  route;
    }

    private String getRealPathFromURI(Uri uri, Context context){
        Cursor cursor = null;
        int idx = 0;
        String routeURI = null;
        try {
             cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
             idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            routeURI = cursor.getString(idx);
            Log.e(TAG, "Path of the photo 2 "+routeURI);
            //Log.e(TAG, "Path of the photo 2 "+routeURI);
           // Log.e(TAG, "Path of the photo 2 "+routeURI);
           // Log.e(TAG, "Path of the photo 2 "+routeURI);

            sharedPref.setPhotoPath(routeURI);
        }catch (NullPointerException e){
            Log.e(TAG, "Exception occured 1 "+e.getMessage());
        }
        return routeURI;
    }

    private void mPlaceCallListener(String phoneNumber, String name, String username, String photo) {
        //do call note here...
        CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString(Parse_Contact.PHONE_NUMBER, phoneNumber);
        args.putString(Parse_Contact.NAME, name);
        args.putString(Parse_Contact.USERNAME, username);
        args.putString(Parse_Contact.PHOTO, photo);
        callNoteBottomSheet.setArguments(args);
        callNoteBottomSheet.show(fragmentManager,"CALL_NOTE");
    }

    private void deleteFiles( final String path){
       // String path = sharedPref.getPhotoPath();
        if(!path.equals("")) {
            Log.e(TAG, "File to delete "+path);
           // Log.e(TAG, "File to delete "+path);
            //Log.e(TAG, "File to delete "+path);
            //Log.e(TAG, "File to delete "+path);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    ContentResolver contentResolver = mContext.getContentResolver();
                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?",
                            new String[]{path});
                }
            });
        }
      /**  if(file.isDirectory()){
            for(File f: file.listFiles()) {
                Log.e(TAG, "Folder to delete "+f);
                Log.e(TAG, "Folder to delete "+f);
                Log.e(TAG, "Folder to delete "+f);
                deleteFiles(f);
            }
        }else {
            Log.e(TAG, "File to delete "+file);
            Log.e(TAG, "File to delete "+file);
            Log.e(TAG, "File to delete "+file);
            while (!file.delete());
        } **/
    }

}

