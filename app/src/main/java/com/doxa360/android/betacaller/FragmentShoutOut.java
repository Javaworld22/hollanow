package com.doxa360.android.betacaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doxa360.android.betacaller.adapter.ShoutoutAdapter;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.model.ShoutOutModel;
import com.doxa360.android.betacaller.model.bimps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 5/7/2018.
 */

public class FragmentShoutOut extends Fragment {

    private static final String TAG = FragmentShoutOut.class.getSimpleName();
    private Context mContext;
    private CardView mSearchLayout;
    private RecyclerView mRecyclerView;
    private ShoutoutAdapter adapter;
    private List<bimps> bip;
    private List<ShoutOutModel> shout;
    private BroadcastReceiver mReceiver;
    public static final String BROADCAST_ACTION = "com.doxa360.android";

   //
    private HollaNowSharedPref mSharedPref;
    private  List<String> usernames;


   public FragmentShoutOut(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mSharedPref = new HollaNowSharedPref(mContext);
        IntentFilter filter = new IntentFilter();
       // mSharedPref.setListShout(new ArrayList<ShoutOutModel>().toString());
        filter.addAction(BROADCAST_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // Log.e(TAG, "Broadcast Received  ");
               // Log.e(TAG, "Broadcast Received  "+intent.getStringExtra("broadcast_intent"));
                shout = mSharedPref.getListShout();
                if(adapter != null) {
                   // Log.e(TAG, "Broadcast Received 2 "+shout);
                    adapter = new ShoutoutAdapter(mContext, shout );
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                  //  Log.e(TAG, "Broadcast Received 2 ");
                }
                if(adapter == null){
                    adapter = new ShoutoutAdapter(mContext, shout );
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                   // Log.e(TAG, "Broadcast Received  2 "+intent.getStringExtra("broadcast_intent"));
                }
            }
        };
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_shout_out, container, false);
        mSearchLayout = (CardView) rootView.findViewById(R.id.shout_out_card);
        mSearchLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoutOutActivity.class);
                startActivity(intent);
            }
        });
        shout = new ArrayList<ShoutOutModel>();

       // mSharedPref.setListShout(null);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.shoutout_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
       return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibileToUser){
       super.setUserVisibleHint(isVisibileToUser);
        Tooltip.removeAll(mContext);
       if(isVisibileToUser){

          // Log.e(TAG, "This fragment is visible now "+isVisibileToUser);
         //  if(mSharedPref.getView() == 2)
           if(mSharedPref != null)
    if(mSharedPref.getListShout() == null) {
        Tooltip.make(mContext,
                new Tooltip.Builder(102).fitToScreen(true)
                        .anchor(mSearchLayout, Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, true)
                                .outsidePolicy(true, false), 0)
                        .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                        .activateDelay(800)
                        .showDelay(300)
                        .text(getString(R.string.tooltip_shoutout))
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        //    .typeface(mYourCustomFont)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }else if(mSharedPref.getListShout().size() <= 0){
        Tooltip.make(mContext,
                new Tooltip.Builder(102).fitToScreen(true)
                        .anchor(mSearchLayout, Tooltip.Gravity.BOTTOM)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, true)
                                .outsidePolicy(true, false), 0)
                        .closePolicy(Tooltip.ClosePolicy.TOUCH_INSIDE_CONSUME, 0)
                        .activateDelay(800)
                        .showDelay(300)
                        .text(getString(R.string.tooltip_shoutout))
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        //    .typeface(mYourCustomFont)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }
       }
    }

    @Override
    public void onResume() {
        super.onResume();

       // shoutOutData = new ArrayList<bimps>();
        if( mSharedPref.getListShout() != null)
            shout = mSharedPref.getListShout();
        else
            shout = new ArrayList<ShoutOutModel>();
        adapter = new ShoutoutAdapter(mContext, shout );

        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


       //

       /** new EasyDialog(getActivity())
                .setLayoutresourceId(R.layout.fragment_shout_out)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(getActivity().getResources().getColor(R.color.background_color_black))
                .setLocationByAttachedView(mSearchLayout)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_Y,1000,800,-100,-50,50,0)
                .setAnimationAlphaDismiss(EasyDialog.DIRECTION_Y,500,0,800)
                .setAnimationAlphaShow(1000,0.3f,1.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(24,24)
                .setOutsideColor(getActivity().getResources().getColor(R.color.outside_color_gray))
                .show(); **/

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }





}
