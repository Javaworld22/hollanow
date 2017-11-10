package com.doxa360.android.betacaller;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.rockerhieu.emojicon.EmojiconEditText;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import android.graphics.Color;

/**
 * Created by user on 6/14/2017.
 */

public class FaceBookAdvertFragment extends DialogFragment {
    public static final String TAG = "FaceBookAdvertFragment";
    private static final String SHOWCASE_ID = "12345654321";
    Animation animScale;
    //protected EmojiconEditText noteEditText;

    private Button finishBtn;
    private TextView note1, emojis;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String number;
    private ProfileFragment parentFragment;
    private HollaNowApiInterface hollaNowApiInterface;
    private HollaNowSharedPref mSharedPref;
    private String noteString;
    private String htmlPage;
    private Spannable wordSpan;
    //private HollaNowSharedPref mSharedPref;

    public FaceBookAdvertFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSharedPref = new HollaNowSharedPref(this.getContext());
        animScale = AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_buttonscale);
        View rootView = inflater.inflate(R.layout.facebook_share_category, container, false);
        getDialog().setTitle("Congrats! ");
        note1 = (TextView) rootView.findViewById(R.id.sharefacebook);
        finishBtn = (Button) rootView.findViewById(R.id.share_label);
        //Typeface font1 = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),"Pacifico.ttf");
        //wordSpan = new SpannableString(mSharedPref.getUsername());
        //wordSpan.setSpan(font1, 1,7,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan = new SpannableString("Clients can now holla @"+ /**Username**/  mSharedPref.getUsername()+" whenever they need your services. ");
        wordSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), 23,mSharedPref.getUsername().length()+23 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        note1.setText(wordSpan);
        finishBtn.startAnimation(animScale);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteFragment faceBookAdvertFragment = new InviteFragment();
                FragmentManager fragmentManager = getFragmentManager();
                Bundle args = new Bundle();
                faceBookAdvertFragment.setArguments(args);
                //finishActivity(12345);
                faceBookAdvertFragment.show(fragmentManager, "SHARE");
               // getView().setVisibility(View.GONE);
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
