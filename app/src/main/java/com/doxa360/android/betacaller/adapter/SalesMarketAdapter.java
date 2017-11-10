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
import com.doxa360.android.betacaller.SalesMarketActivity;
import com.doxa360.android.betacaller.model.SalesMarket;
import com.squareup.picasso.Picasso;

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

    public SalesMarketAdapter(List<SalesMarket> salesList, Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mSalesList = salesList;
       // mMarket = market;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView mPhoto;
        TextView photoText;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.image_market_adapter);
            photoText = (TextView) itemView.findViewById(R.id.textview_market_adapter);
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
       //      color = Integer.parseInt(sMarket.getColor());
     //   }catch(NumberFormatException ex){

      //  }
      //  holder.photoText.setTextColor(color);
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
