package com.doxa360.android.betacaller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFragment extends BottomSheetDialogFragment {

    private HollaNowSharedPref mSharedPref;
    private Spannable wordSpan;

  private String[] moreOptions = new String[] {
          "Facebook",
          "Messenger",
          "Twitter",
          "Google Plus",
          "WhatsApp",
          "Message",
          "Email"
  };
  int[] optionsThumbnail = new int[]{
          R.drawable.facebooklogo,
          android.R.drawable.ic_dialog_email,
          R.drawable.twitter_logo,
          R.drawable.google_logo,
          R.drawable.whaissapp,
          R.drawable.ic_account_circle_black_24dp,
          R.drawable.ic_account_circle_black_24dp
  };
  private Context mContext;

  public InviteFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = (View) inflater.inflate(R.layout.fragment_invite, container, false);
      mSharedPref = new HollaNowSharedPref(this.getContext());

    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    for (int i=0;i<5;i++){
      HashMap<String,String> hm = new HashMap<String,String>();
      hm.put("optionsTitle",moreOptions[i]);
      hm.put("optionsThumbnail", String.valueOf(optionsThumbnail[i]));
      aList.add(hm);
    }
    String[] from = {"optionsThumbnail","optionsTitle"};
    int[] to = {R.id.optionsThumbnail,R.id.optionsTitle};
    final SimpleAdapter adapter = new SimpleAdapter(mContext,aList,R.layout.more_options_grid_layout,from,to);
    ListView optionsList = (ListView) rootView.findViewById(R.id.moreListView);
    optionsList.setAdapter(adapter);
    optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
          case 0:
            initShareIntent("facebook.katana");
            break;
          case 1:
            initShareIntent("facebook.orca");
            break;
          case 2:
            initShareIntent("twitter");
            break;
          case 3:
            initShareIntent("google.android.apps.plus");
            break;
          case 4:
            initShareIntent("whatsapp");
            break;
          case 5:
            initShareIntent("message");
            break;
          case 6:
            initShareIntent("mail");
            break;
        }
      }
    });

    return rootView;
  }


  public void initShareIntent(String type) {
    boolean found = false;
    Intent share = new Intent(android.content.Intent.ACTION_SEND);
    share.setType("text/plain");

    // gets the list of intents that can be loaded.
    List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(share, 0);
      wordSpan = new SpannableString("Get HollaNow On Your Android Device");
      wordSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), 5,8+5 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    if (!resInfo.isEmpty()){
      for (ResolveInfo info : resInfo) {
        if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                info.activityInfo.name.toLowerCase().contains(type) ) {
          share.putExtra(Intent.EXTRA_SUBJECT,  wordSpan);
          share.putExtra(Intent.EXTRA_TEXT,     "Hi let's make smart calls by exchanging expression whenever we call each other with HollaNow. " +
                  "Install link http://bit.ly/2rDEq84. Holla @"+/**username**/mSharedPref.getUsername() );
//                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("http://whatilyke.com"));
//                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(myPath)) ); // Optional, just if you wanna share an image.
          share.setPackage(info.activityInfo.packageName);
          found = true;
          break;
        }
      }
      if (!found)
        return;

      startActivity(Intent.createChooser(share, "Select"));
        getDialog().dismiss();

    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mContext = activity;
  }
}
