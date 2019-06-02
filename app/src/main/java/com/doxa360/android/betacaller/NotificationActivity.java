package com.doxa360.android.betacaller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by user on 7/28/2017.
 */

public class NotificationActivity extends AppCompatActivity {

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        intent = new Intent(this, HomeActivity.class);

        MobileAds.initialize(this,"ca-app-pub-3940256099942544/2793859312");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                 startActivity(intent);
            }
        });



        Intent intent  = new Intent(this,NotificationService.class);
        startService(intent);

       // NotificationFragment callNoteBottomSheet = new NotificationFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment1,new NotificationFragment())
         //      .commit();
    }
}
