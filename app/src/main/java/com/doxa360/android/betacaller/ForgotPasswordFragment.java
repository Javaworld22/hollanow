package com.doxa360.android.betacaller;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordFragment extends DialogFragment {


    private final String TAG = this.getClass().getSimpleName();
    private EditText mUsername;
    private EditText mPassword;
    private Button mResetButton;
    private TextView mSignUp;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forgotPasswordView = (View) inflater.inflate(R.layout.fragment_forgot_password, container, false);


        mUsername = (EditText) forgotPasswordView.findViewById(R.id.email_address);
        mResetButton = (Button) forgotPasswordView.findViewById(R.id.reset_button);

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyToolBox.isNetworkAvailable(getActivity())) {
                    ParseUser.requestPasswordResetInBackground(mUsername.getText().toString().trim(), new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "A password reset link has been sent to " + mUsername.getText().toString().trim(),
                                        Toast.LENGTH_LONG).show();
                                getDialog().dismiss();
                            } else {
                                Toast.makeText(getActivity(), "An error occurred. Please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {

                    Toast.makeText(getActivity(), "Network error. Please check you connection and try again",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return forgotPasswordView;
    }

}
