package com.doxa360.android.betacaller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doxa360.android.betacaller.adapter.SalesMarketAdapter;

/**
 * Created by user on 10/26/2017.
 */

public class SalesMarketFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ListSalesMarket salesMarket;

    private SalesMarketAdapter mAdapter;

    private Context mContext;

    public SalesMarketFragment(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(mContext);
//        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        salesMarket = (ListSalesMarket) getArguments().getParcelable("fragment_sales_market");
        View rootView = (View) inflater.inflate(R.layout.fragment_image_full_view, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sales_image_recyclerview);
        mAdapter = new SalesMarketAdapter(salesMarket.getSalesMarket(), mContext);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);
       // Log.e("SalesMarketFragment", "No of list found : "+salesMarket.getSalesMarket().size());
       // Log.e("SalesMarketFragment", "No of list found : "+salesMarket.getSalesMarket().size());
       // Log.e("SalesMarketFragment", "No of list found : "+salesMarket.getSalesMarket().size());
       // Log.e("SalesMarketFragment", "No of list found : "+salesMarket.getSalesMarket().size());
       // Log.e("SalesMarketFragment", "No of list found : "+salesMarket.getSalesMarket().size());

        return rootView;
    }
}
