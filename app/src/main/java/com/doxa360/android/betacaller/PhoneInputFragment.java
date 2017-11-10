package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import com.doxa360.android.betacaller.helpers.CountryCodePicker;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.security.PrivateKey;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 8/31/2017.
 */

public class PhoneInputFragment extends BottomSheetDialogFragment { //BottomSheetDialogFragment

    private static final String TAG = PhoneInputFragment.class.getSimpleName();
private  Context mContext;
    private CountryCodePicker codePicker;
    private  EditText phone;
    private Button next;
    private final String NUMBER = "number";
    private final String COUNTRYCODE = "countrycode";
    private final String FROMPROFILEFRAGMENT = "checkposition";
    private final String COUNTRY = "country";
    private TextInputLayout mNumberLayout;
    private String phoneNumber;
    private String position;
    private String mEmail;
    private String mPassword;
    private String mUserName;
    private String mName;
    private HollaNowSharedPref mSharedPref;



    public PhoneInputFragment(){
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mContext = getContext();
        mSharedPref = new HollaNowSharedPref(mContext);
        if(getArguments() != null)
       position =  getArguments().getString(FROMPROFILEFRAGMENT);
        View rootView = (View) inflater.inflate(R.layout.activity_phone_input, container, false);
        codePicker = (CountryCodePicker) rootView.findViewById(R.id.ccp_loadFullNumber);
        phone = (EditText) rootView.findViewById(R.id.editText_loadCarrierNumber);
        next = (Button) rootView.findViewById(R.id.button_next);
        mNumberLayout = (TextInputLayout) rootView.findViewById(R.id.layout_number_input);

       // codePicker.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View v) {
      //      }
     //   });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               String countryCode =  codePicker.getSelectedCountryCode();
                String country = codePicker.getSelectedCountryName();
                String number = phone.getText().toString().trim();
               // Log.e(TAG, "onCreateView is here "+countryCode+" "+"555555555555555555555555");
                Log.e(TAG, "onCreateView is here "+number+" "+"00000000000000000000");
               // if (MyToolBox.isNetworkAvailable(mContext)) {
                    if ((number == null)  ||  (number.isEmpty()) || number.equals(" ")) {
                        mNumberLayout.setError("Enter phone number");
                    }else{
                        mNumberLayout.setErrorEnabled(false);
                        if(number.trim().startsWith("0")){
                            phoneNumber = number.substring(1);
                            Log.e(TAG, "Change to +234 in PhoneInputFragment: "+phoneNumber);
                            phoneNumber = countryCode.concat(phoneNumber);
                            Log.e(TAG, "Change to +234: in PhoneInputfragment "+phoneNumber);
                            if(!(phoneNumber.startsWith("+")))
                                phoneNumber = "+".concat(phoneNumber);
                            Log.e(TAG, "Change to +234: in PhoneInputfragment "+phoneNumber);
                        }

                        mSharedPref.setCountry(country);

                        Intent intent = new Intent(getActivity(),PhoneVerificationActivity.class);
                        intent.putExtra(COUNTRYCODE,countryCode);
                        intent.putExtra(NUMBER,phoneNumber);
                        intent.putExtra(FROMPROFILEFRAGMENT, position);
                        intent.putExtra(COUNTRY,country);
                        startActivity(intent);
                    }
               // }
            }
        });

        return rootView;

    }


}
