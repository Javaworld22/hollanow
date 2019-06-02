package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.ListSalesMarket;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.SalesMarket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 10/27/2017.
 */

public class SalesMarketAdapter extends RecyclerView.Adapter<SalesMarketAdapter.ContactViewHolder> {

   // SalesPictureAdapter.ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<SalesMarket> mSalesList;
    private ListSalesMarket mMarket;
    ContactViewHolder mHolder;
    String mColor;
    private static int check_for_hyphen;
    private HollaNowSharedPref mShareRef;

    public SalesMarketAdapter(List<SalesMarket> salesList, Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mSalesList = salesList;
        mShareRef = new HollaNowSharedPref(mContext);
       // mMarket = market;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView mPhoto;
        TextView photoText;
        TextView date;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.image_market_adapter);
            photoText = (TextView) itemView.findViewById(R.id.textview_market_adapter);
            date = (TextView) itemView.findViewById(R.id.date_market_adapter);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.layout_adapter_sales_image, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        SalesMarket sMarket =  mSalesList.get(position);
        mHolder = holder;
        Log.e("SalesMarketAdapter", "Display Views : "+sMarket.getPhoto());
       // Log.e("SalesMarketAdapter", "Display Views : "+sMarket.getPhoto());
       // Log.e("SalesMarketAdapter", "Display Views : "+sMarket.getPhoto());
       // Log.e("SalesMarketAdapter", "Display Views : "+sMarket.getPhoto());
      //  int color = -1;
      //  try {
             mColor = sMarket.getColor();
     //   }catch(NumberFormatException ex){

      //  }
      //  holder.photoText.setTextColor(color);
        List<Character> dateColor = new ArrayList<Character>(); // for the color
        List<Character> dateColor1 = new ArrayList<Character>(); // for the date
        List<Character> rewardsVideo = new ArrayList<Character>();
        boolean flag_count = false;
        if(mColor.length() != 0){
            for(int i = 0; i < mColor.length();i++) {
                char c = mColor.charAt(i);
                if(c != '_'){
                    if(!flag_count)
                        dateColor.add(c);
                    if(flag_count)
                        dateColor1.add(c);
                    if(check_for_hyphen == 2)
                        rewardsVideo.add(c);
                }else if(c == '_'){
                    check_for_hyphen++;
                    flag_count = true;
                }
            }
        }

        Iterator<Character> it = dateColor.iterator();
        Iterator<Character> it1 = dateColor1.iterator();
        Iterator<Character> it2 = rewardsVideo.iterator();
        String col = "";
        String col1 = "";
        String string_credit_rewards = "";

        while(it.hasNext()){
            col = col+it.next();
        }
        while (it1.hasNext()){
            col1 = col1+it1.next();
        }

        while(it2.hasNext()){
            string_credit_rewards = string_credit_rewards+it2.next();
        }
    try {
        int credit_rewards = Integer.parseInt(string_credit_rewards);
        if(credit_rewards != 0)
            mShareRef.setCreditCount(credit_rewards);
    }catch(NumberFormatException ex){

    }

        String date = col1.trim();
        String year = "".trim();
        String month= "".trim();
        String days= "".trim();
        String calendar = "".trim();
        mShareRef.setDateOfUpload(date);

        for (int i = 0;i<date.length();i++) {

            if (i <= 3)
                year = year + date.charAt(i);
            else if (i <= 5 && i >= 4)
                month = month + date.charAt(i);
            else if (i <= 7 && i >= 6)
                days = days + date.charAt(i);
        }
        if(days.equals("01"))
            calendar = calendar + "1st";
        else if(days.equals("02"))
            calendar = calendar + "2nd";
        else if(days.equals("03"))
            calendar = calendar + "3rd";
        else if(days.equals("04"))
            calendar = calendar + "4th";
        else if(days.equals("05"))
            calendar = calendar + "5th";
        else if(days.equals("06"))
            calendar = calendar + "6th";
        else if(days.equals("07"))
            calendar = calendar + "7th";
        else if(days.equals("08"))
            calendar = calendar + "8th";
        else if(days.equals("09"))
            calendar = calendar + "9th";
        else if(days.equals("10"))
            calendar = calendar + "10th";
        else if(days.equals("11"))
            calendar = calendar + "11th";
        else if(days.equals("12"))
            calendar = calendar + "12th";
        else if(days.equals("13"))
            calendar = calendar + "13th";
        else if(days.equals("14"))
            calendar = calendar + "14th";
        else if(days.equals("15"))
            calendar = calendar + "15th";
        else if(days.equals("16"))
            calendar = calendar + "16th";
        else if(days.equals("17"))
            calendar = calendar + "17th";
        else if(days.equals("18"))
            calendar = calendar + "18th";
        else if(days.equals("19"))
            calendar = calendar + "19th";
        else if(days.equals("20"))
            calendar = calendar + "20th";
        else if(days.equals("21"))
            calendar = calendar + "21th";
        else if(days.equals("22"))
            calendar = calendar + "22th";
        else if(days.equals("23"))
            calendar = calendar + "23th";
        else if(days.equals("24"))
            calendar = calendar + "24th";
        else if(days.equals("25"))
            calendar = calendar + "25th";
        else if(days.equals("26"))
            calendar = calendar + "26th";
        else if(days.equals("27"))
            calendar = calendar + "27th";
        else if(days.equals("28"))
            calendar = calendar + "28th";
        else if(days.equals("29"))
            calendar = calendar + "29th";
        else if(days.equals("30"))
            calendar = calendar + "30th";
        else if(days.equals("31"))
            calendar = calendar + "31th";

        calendar = calendar + " ";

        if(month.equals("01"))
            calendar = calendar + "January";
        else if(month.equals("02"))
            calendar = calendar + "Febuary";
        else  if(month.equals("03"))
            calendar = calendar + "March";
        else  if(month.equals("04"))
            calendar = calendar + "April";
        else if(month.equals("05"))
            calendar = calendar + "May";
        else if(month.equals("06"))
            calendar = calendar + "June";
        else if(month.equals("07"))
            calendar = calendar + "July";
        else if(month.equals("08"))
            calendar = calendar + "August";
        else if(month.equals("09"))
            calendar = calendar + "September";
        else if(month.equals("10"))
            calendar = calendar + "October";
        else if(month.equals("11"))
            calendar = calendar + "November";
        else if(month.equals("12"))
            calendar = calendar + "December";

        calendar = calendar + ", ";
        calendar = calendar + year;

        holder.date.setText(calendar);
        holder.photoText.setText(sMarket.getContent());
        Picasso.with(mContext)
                .load(BetaCaller.SALES_URL + sMarket.getPhoto())
                .placeholder(R.drawable.image_view_clip)
                .error(R.drawable.image_view_clip)
                .into(holder.mPhoto);
    }

    @Override
    public int getItemCount() {
        if (mSalesList != null)
            return mSalesList.size();
        else
            return 0;
    }


}
