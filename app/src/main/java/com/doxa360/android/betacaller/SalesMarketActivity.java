package com.doxa360.android.betacaller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 10/26/2017.
 */

public class SalesMarketActivity extends AppCompatActivity {

    private static final String TAG = "SalesMarketActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_view);
        FragmentManager fm = getSupportFragmentManager();
        ListSalesMarket salesMarket = (ListSalesMarket) getIntent().getParcelableExtra("sales_market");
        SalesMarketFragment fragment = new SalesMarketFragment();
        Bundle args = new Bundle();

        args.putParcelable("fragment_sales_market", salesMarket);
        fragment.setArguments(args);
        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.full_view_image, fragment, "SALESMARKET");
        ft.commit();
    }
}
