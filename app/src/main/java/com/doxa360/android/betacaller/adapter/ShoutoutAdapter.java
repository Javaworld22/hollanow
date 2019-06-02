package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/10/2018.
 */

public class ShoutoutAdapter extends RecyclerView.Adapter<ShoutoutAdapter.ContactViewHolder>{

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private List<ShoutOutModel> mBips;
    private LayoutInflater mLayoutInflater;
    private HollaNowSharedPref mSharedPref;
     ContactViewHolder mHolder;

    public ShoutoutAdapter(Context context, List<ShoutOutModel> bip) {
        mContext = context;
        mBips = bip;
        mLayoutInflater = LayoutInflater.from(context);
        mSharedPref = new HollaNowSharedPref(mContext);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView likeButton;
        TextView message;
        TextView number_of_likes;
        TextView sender_username;
        TextView receiver_username;
        ImageView photo;
        TextView date;
        CardView cardView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            likeButton = (ImageView) itemView.findViewById(R.id.like_button);
           // likeButton.setColorFilter(mContext.getResources().getColor(R.color.materialcolorpicker__lightgrey));
            message = (TextView) itemView.findViewById(R.id.shoutout_msg);
            sender_username = (TextView) itemView.findViewById(R.id.sender_shoutout);
            number_of_likes = (TextView)itemView.findViewById(R.id.no_of_likes);
            receiver_username = (TextView)itemView.findViewById(R.id.reciever_shoutout);
            photo = (ImageView) itemView.findViewById(R.id.shoutout_photo);
            date = (TextView) itemView.findViewById(R.id.time_sent);
            cardView = (CardView) itemView.findViewById(R.id.cardview_hollaShout);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    int no_of_likes =  mBips.get(pos).getNoOfLikes();
                    boolean toggle_like = mBips.get(pos).getCurrent_user_like();
                       toggle_like = !toggle_like;
                     //  mBips.get(pos).setCurrent_user_like(toggle_like);
                     //  mSharedPref.setListShout(mBips.toString());
                   // Log.e(TAG, "Pls print Toggle here "+toggle_like);
                        if (!toggle_like) {
                            if(no_of_likes > 0) {
                                no_of_likes = no_of_likes - 1;
                                ((ImageView) v).setColorFilter(mContext.getResources().getColor(R.color.materialcolorpicker__lightgrey));
                                number_of_likes.setText(String.valueOf(no_of_likes));
                            }
                        }else if (toggle_like) {
                            no_of_likes = no_of_likes + 1;
                            number_of_likes.setText(String.valueOf(no_of_likes));
                            ((ImageView) v).setColorFilter(mContext.getResources().getColor(R.color.like_color)); //.setCol.set.setBackgroundColor(mContext.getResources().getColor(R.color.like_color));
                        }
                        mBips.get(pos).setNoOfLikkes(no_of_likes);
                        mBips.get(pos).setCurrent_user_like(toggle_like);
                        mSharedPref.setListShout(mBips.toString());

                   // ShoutOutModel model = mBips.get(pos);
                  //  mHolder.likeButton.setBackgroundColor(mContext.getResources().getColor(R.color.holla_white));
                   // receiveShoutOutMessages(model.getUsername(),model.getSender() , model.getMsg(),mHolder);
                }
            });
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_shoutout_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ShoutOutModel model = null;
        mHolder = holder;
           if(mBips != null){
              model = (ShoutOutModel) mBips.get(position);
              // Log.e(TAG, "See what is printing at  " +model.toString());
               SimpleDateFormat format  = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
               Calendar cal = Calendar.getInstance();
               final String strTimeFormate = "h:mm aa";
               final String strDateFormate = "dd/MM/yyyy  h:mm aa";
               Date date1 = null;
               try {
                   date1 = ((Date) format.parse(model.getDate()));
               }catch (ParseException ex){
                   Log.e(TAG, "ParseException Occurred " +ex.getMessage());
               }

               Calendar now = Calendar.getInstance();
              // Log.e(TAG, "Print date here  1 " +model.getDate());
              // Log.e(TAG, "Print date here  2 " +date1.toString());
               long msgTimeMills ;
               if(date1 == null){
                   Calendar c = Calendar.getInstance();
                    msgTimeMills =  c.getTimeInMillis();
               }
                else
                    msgTimeMills = date1.getTime();
               cal.setTimeInMillis(msgTimeMills);
               if((now.get(Calendar.DATE) - cal.get(Calendar.DATE)) == 1
                       && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                       && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))) {
                 //  Log.e(TAG, "Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
                   holder.date.setText("Yestarday at " + android.text.format.DateFormat.format(strTimeFormate, cal));
               }
               else if((now.get(Calendar.DATE) == cal.get(Calendar.DATE))
                       && ((now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)))
                       && ((now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)))){
                  // Log.e(TAG,  "today at "+ android.text.format.DateFormat.format(strTimeFormate, cal));
                   holder.date.setText("today at " + android.text.format.DateFormat.format(strTimeFormate, cal));
               }

              holder.number_of_likes.setText(String.valueOf(model.getNoOfLikes()));
              holder.receiver_username.setText(model.getReceiver());
              holder.sender_username.setText(model.getSender());
                 if(model.getCurrent_user_like())
                      holder.likeButton.setColorFilter(mContext.getResources().getColor(R.color.like_color));
               if(!model.getCurrent_user_like())
                   holder.likeButton.setColorFilter(mContext.getResources().getColor(R.color.materialcolorpicker__lightgrey));
            if(model.getSender().equals(mSharedPref.getCurrentUser().getUsername())) {
                holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.shout_out));
                holder.sender_username.setText("From me");
                holder.receiver_username.setText("To everyone");
            }



              if(model.getMsg() != null)
                holder.message.setText(model.getMsg());

           }
    }

    @Override
    public int getItemCount() {
       if(mBips != null) {
          // Log.e(TAG, "bShoutOutActivity here is " + mBips.size());
           return mBips.size();
       }
       else
           return 0;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }




}
