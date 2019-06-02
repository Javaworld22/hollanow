package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.HollaNowApiClient;
import com.doxa360.android.betacaller.HollaNowApiInterface;
import com.doxa360.android.betacaller.ListSalesMarket;
import com.doxa360.android.betacaller.NearbySearchActivity;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.android.gms.ads.NativeExpressAdView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    List <String>mLastPicture;
    final String TAG = "NearbyUserAdapter";
    HollaNowSharedPref mSharedPref;
    String market;
    static  int counter = 0;
    int color;
    Set<String> no_of_upload ;
    Iterator<String> it ;
    CardView cardView;
    ViewGroup.LayoutParams params;
    int mHeight;
   // NativeExpressAdView mAdView ;


    public NearbyUserAdapter(List<User> users, float lat, float lng, Context context) {//List <String>lastPicture
        mUsers = users;
        mContext = context;
        mLat = lat;
        mLng = lng;
        fullWidth = false;
        mLayoutInflater = LayoutInflater.from(context);
       // mLastPicture = lastPicture;
        mSharedPref = new HollaNowSharedPref(mContext);
        no_of_upload =  new HashSet<String>();
        no_of_upload.addAll(mSharedPref.getGalleryCount());
        it = no_of_upload.iterator();
       // market = new ArrayList<String>();
      //  for(int i = 0; i < mLastPicture.size();i++){
        //    retrieveSalesPictures(mLastPicture.get(i), i);
        //    Log.e(TAG, "Iteration :  "+ mLastPicture.get(i)+" "+market+"  "+i);
      //  }

     /**   for(int i= 0; i < users.size()-1;i++){

                Log.e(TAG, "From NearbyUserAdapter " + i);
                Log.e(TAG, "From NearbyUserAdapter " + users.get(i).getProfilePhoto());
                Log.e(TAG, "From NearbyUserAdapter " + users.get(i).getProfilePhoto2());
                Log.e(TAG, "From NearbyUserAdapter " + users.get(i).getTextPhoto());
                Log.e(TAG, "From NearbyUserAdapter " + users.get(i).getUsername());
            Log.e(TAG, "From NearbyUserAdapter " + users.get(i).getOccupation());

        } **/


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
        //TextView mPhoneNumber;
       // TextView mLastSeen;
        TextView mDistanceAwayKm;
        ImageView mPhoto;
        TextView mOccupation;
        TextView mTextPhotot;
        TextView noGallery;
        CardView mCardView;

       // NativeExpressAdView adView ;



        public ContactViewHolder(View itemView) {
            super(itemView);

            mDisplayName = (TextView) itemView.findViewById(R.id.displayname);
           // mPhoneNumber = (TextView) itemView.findViewById(R.id.phone);
            mPhoto = (ImageView) itemView.findViewById(R.id.photo);
           // mLastSeen = (TextView) itemView.findViewById(R.id.address);
            mDistanceAwayKm = (TextView) itemView.findViewById(R.id.distance_away);
            mOccupation = (TextView) itemView.findViewById(R.id.occupation);
            mTextPhotot = (TextView) itemView.findViewById(R.id.text_image_search);
            noGallery = (TextView) itemView.findViewById(R.id.no_of_gallery);
            mCardView = (CardView) itemView.findViewById(R.id.cardview_home);
            cardView = mCardView;


            //adView = (NativeExpressAdView) itemView.findViewById(R.id.adView2);
           // adView.setAdUnitId("ca-app-pub-3940256099942544/2793859312");
           // adView.setAdSize(new AdSize(300,300));
          //  mAdView = adView;



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = mUsers.get(getPosition());
                    if (user!=null) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(BetaCaller.USER_PROFILE, user);  //////////////////////////
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
        String textPhoto = "";
        mHolder = holder;
        int c = 0;
        String check = null;
        String check1 = null;
        String username = null;


        //textPhoto = retrieveSalesPictures(user.getUsername(),position);
       // if(textPhoto == null)
        //    textPhoto = "";

        try{
            username = user.getUsername();
        }catch(NullPointerException ex){
            username = "";
        }
        if(it != null)
        while (it.hasNext()){
            String pic_count = it.next();
            if(pic_count.contains(username)){
                try {
                    c = Integer.parseInt(pic_count);
                    user.setCountPic(c);
                }catch (NumberFormatException a) {
                    Log.e(TAG, "From NearbyUserAdapter " + pic_count);
                    check = pic_count.substring(pic_count.length()-3,pic_count.length());
                    try{
                        c = Integer.parseInt(check);
                        user.setCountPic(c);
                    }catch (NumberFormatException ex) {
                        Log.e(TAG, "From NearbyUserAdapter " + check);
                        check1 = check.substring(1,check.length());
                        try{
                            c = Integer.parseInt(check1);
                            user.setCountPic(c);
                        }catch (NumberFormatException e) {
                            Log.e(TAG, "From NearbyUserAdapter " + check1);
                        }
                    }
                }
               // Log.e(TAG, "From NearbyUserAdapter " + user.getUsername());
               // Log.e(TAG, "From NearbyUserAdapter " + c);
                return;
            }
        }

        if (user!=null) {
            StringBuilder displayName = new StringBuilder("");
            int j = 0;
            holder.mDisplayName.setVisibility(View.VISIBLE);
            holder.mTextPhotot.setVisibility(View.VISIBLE);
            holder.noGallery.setVisibility(View.VISIBLE);
            if(user.getName().length() > 18){
                for(int i = 0; i<user.getName().length();i++){
                    j++;
                 if(displayName.length() == 18  && j > 18 && displayName.equals(" ")) {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 1 " );
                     j = 0;
                 }else if(displayName.length() == 19  && j > 18 && displayName.equals(" ")) {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 2 " );
                     j = 0;
                 } else if(displayName.length() == 20  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 3 " );
                     j = 0;
                 }else if(displayName.length() == 21  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     j = 0;
                 }else if(displayName.length() == 22  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 4 " );
                     j = 0;
                 }
                 else if(displayName.length() == 23  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 5 " );
                     j = 0;
                 }
                 else if(displayName.length() == 24  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     j = 0;
                 }
                 else if(displayName.length() == 25  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 7 " );
                     j = 0;
                 }
                 else if(displayName.length() == 26  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 7 " );
                     j = 0;
                 }
                 else if(displayName.length() == 27  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 7 " );
                     j = 0;
                 }
                 else if(displayName.length() == 28  && j > 18 && displayName.charAt(i-1) == ' ') {
                     displayName.append("\n");
                     displayName.append(user.getName().charAt(i));
                     Log.e(TAG, "Noted 7 " );
                     j = 0;
                 }
                 else{
                     displayName.append(user.getName().charAt(i));
                 }
                }

            }else{
                displayName.append(user.getName());
            }
            holder.mDisplayName.setText(displayName);
            holder.mTextPhotot.setText(user.getTextPhoto());
            //String text1 = user.getCountPic()+ "  content(s) on Gallery";
            holder.noGallery.setTextColor(Color.parseColor("#0d4800"));
            holder.noGallery.setText(user.getCountPic()+ "  content(s) on Gallery");
             params = cardView.getLayoutParams();
             mHeight = params.height;
            //RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              //      mHeight);
           // ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              //      mHeight);

            cardView.setLayoutParams(params);
            try {
                 color = Integer.parseInt(user.getColor());
            }catch(NumberFormatException ex){
                Log.e(TAG, "NumberFormat Exception in getColor "+ ex.getMessage());
            }
            holder.mTextPhotot.setTextColor(color);
           // holder.mPhoneNumber.setText(user.getPhone());
            holder.mOccupation.setVisibility(View.VISIBLE);
            holder.mOccupation.setText(user.getOccupation());
            String lastSeen = user.getLastSeen();
            if (lastSeen!=null) {
                lastSeen = lastSeen.replace("null,", "");
                //holder.mLastSeen.setText(lastSeen);
            }

            if (user.getLat()!=0 & user.getLng() != 0) {
               // String kmAway = String.format(Locale.getDefault(), "%.2f", user.getDistance()) + " km away";
                holder.mDistanceAwayKm.setVisibility(View.VISIBLE);
                holder.mDistanceAwayKm.setText(user.getDistance());
            }


            if(user.getProfilePhoto2() != null) {
                Picasso.with(mContext)
                        .load(BetaCaller.SALES_URL+user.getProfilePhoto2())
                        .placeholder(R.drawable.wil_profile)
                        .error(R.drawable.wil_profile)
                        .into(holder.mPhoto);
               // Log.e(TAG, "From NearbyUserAdapter "+ position);
               // Log.e(TAG, "From NearbyUserAdapter "+ user.getTextPhoto());
               // Log.e(TAG, "From NearbyUserAdapter "+ user.getProfilePhoto2());
                //textPhoto = "";
            }

            else if(user.getProfilePhoto() != null) {
                Picasso.with(mContext)
                        .load(BetaCaller.PHOTO_URL+user.getProfilePhoto())
                        .placeholder(R.drawable.wil_profile)
                        .error(R.drawable.wil_profile)
                        .into(holder.mPhoto);
              //  Log.e(TAG, "From NearbyUserAdapter "+ position);
              //  Log.e(TAG, "From NearbyUserAdapter "+ user.getTextPhoto());
              //  Log.e(TAG, "From NearbyUserAdapter "+ user.getProfilePhoto());
            } else {
                Picasso.with(mContext)
                        .load(R.drawable.wil_profile)
                        .placeholder(R.drawable.wil_profile)
                        .error(R.drawable.wil_profile)
                        .into(holder.mPhoto);
            }
        } else {
           // ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            //        mHeight);
           // RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            //        mHeight);
          //  cardView.setLayoutParams(params);
            holder.mDisplayName.setText("Search for people and \n businesses near you. \n Its strictly for those \n who" +
                    " filled occupation\n or industry. ");
            holder.mTextPhotot.setVisibility(View.GONE);
            holder.noGallery.setVisibility(View.GONE);
            holder.mDistanceAwayKm.setVisibility(View.GONE);
            holder.mOccupation.setVisibility(View.GONE);
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


    public String retrieveSalesPictures(String username, final int count){
      //  Log.e("EDITED_USER=> ", currentUser.toString());
        HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        Call<JsonElement> call = hollaNowApiInterface.receiveSalesPictures(username);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "success "+ response.body().toString());
                  //  Log.e(TAG, "success "+ response.body().toString());
                  //  Log.e(TAG, "success "+ response.body().toString());
                    // Log.e(TAG, "success "+ response.body().toString()+" "+response.body().getAsString());
                    ListSalesMarket sales =  new GsonBuilder().create().fromJson(response.body().toString(),ListSalesMarket.class);
                    int number_of_pictures = sales.getSalesMarket().size();
                    int lastIndex = number_of_pictures - 1;
                    // market.add(sales.getSalesMarket().get(lastIndex).getPhoto());
                    if(lastIndex != -1)
                     market = sales.getSalesMarket().get(lastIndex).getPhoto();
                    counter = count;
                    mUsers.get(count).setmProfilePhoto2(market);
                    Log.e(TAG, "success "+ market);
                   // Log.e(TAG, "success "+ market);
                    //return market;
//                    onBackPressed();
                } else {
                    Log.e(TAG, "error: " + response.code() + response.message());
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
                    Log.e(TAG, "success "+ response.body().toString());
                    Toast.makeText(mContext, "Error updating Sales", Toast.LENGTH_SHORT).show();
                    market = "";
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(mContext, "Network error. Try again", Toast.LENGTH_LONG).show();
                market = "";
            }
        });
            return market;
    }






}
