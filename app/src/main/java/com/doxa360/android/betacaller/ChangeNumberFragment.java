package com.doxa360.android.betacaller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.MyToolBox;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeNumberFragment extends DialogFragment {


  private Context mContext;
  EditText phoneEditText;

  public ChangeNumberFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = (View) inflater.inflate(R.layout.fragment_change_number, container, false);

    String phoneNumber = getArguments().getString(ProfileActivity.PHONE);

    phoneEditText = (EditText) rootView.findViewById(R.id.phone);
    phoneEditText.setText(phoneNumber);

    Button changeButton = (Button) rootView.findViewById(R.id.change_button);

    changeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (MyToolBox.isNetworkAvailable(mContext)) {
          if (MyToolBox.isValidMobile(phoneEditText.getText().toString().trim())) {
            //TODO: verify phone number here
//                        Intent intent = new Intent(mContext, VerifyPhoneNumber)
          } else {
            Toast.makeText(mContext, "Invalid Phone Number", Toast.LENGTH_LONG).show();
            phoneEditText.setError("Invalid Phone Number");
          }
        } else {
          Toast.makeText(mContext, "Network error. Please check your connection and try again", Toast.LENGTH_LONG).show();
        }
      }
    });

    return rootView;
  }

  @Override
  public void onAttach(Activity activity) {
    mContext = activity;
    super.onAttach(activity);
  }
}
