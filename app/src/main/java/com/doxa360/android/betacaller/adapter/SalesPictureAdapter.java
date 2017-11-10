package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.BetaCaller;
import com.doxa360.android.betacaller.ListSalesMarket;
import com.doxa360.android.betacaller.ProfileActivity;
import com.doxa360.android.betacaller.R;
import com.doxa360.android.betacaller.SalesMarketActivity;
import com.doxa360.android.betacaller.model.SalesMarket;
import com.doxa360.android.betacaller.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 10/22/2017.
 */

public class SalesPictureAdapter extends RecyclerView.Adapter<SalesPictureAdapter.ContactViewHolder> {

    private List<SalesMarket> mSalesList;
    ContactViewHolder mHolder;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ListSalesMarket mMarket;

    public SalesPictureAdapter(List<SalesMarket> salesList, Context context, ListSalesMarket market) {
        mSalesList = salesList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMarket = market;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView mPhoto;
        TextView photoText;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.image_scroll);
            photoText = (TextView) itemView.findViewById(R.id.text_for_picture);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SalesMarketActivity.class);
                    SalesMarket market = mSalesList.get(getPosition());
                    intent.putExtra("sales_market", mMarket);
                    mContext.startActivity(intent);
                }});
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.layout_sales_market, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
       SalesMarket sMarket =  mSalesList.get(position);
        mHolder = holder;
        int color = -1;
        try {
          color =   Integer.parseInt(sMarket.getColor());
        }catch(NumberFormatException ex){

        }
        holder.photoText.setText(sMarket.getContent());
        holder.photoText.setTextColor(color);
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
