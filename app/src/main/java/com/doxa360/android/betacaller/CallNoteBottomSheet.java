package com.doxa360.android.betacaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.Contact;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Apple on 24/07/16.
 */
public class CallNoteBottomSheet extends DialogFragment {

    private EditText noteEditText;
    private Button finishBtn;
    private TextView dismissLbl;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String number;
    private ProfileFragment parentFragment;

    public CallNoteBottomSheet() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_note_bottomsheet, container, false);

        getDialog().setTitle("Add HollaNow Note");
        number = getArguments().getString(Contact.PHONE_NUMBER);

        noteEditText = (EditText) rootView.findViewById(R.id.call_note);
        finishBtn = (Button) rootView.findViewById(R.id.finish_button);
        dismissLbl = (TextView) rootView.findViewById(R.id.dismiss_label);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

//        parentFragment = (ProfileFragment) getTargetFragment();

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                //TODO: do char count etal check here...
                if (MyToolBox.isNetworkAvailable(mContext)) {
                    if (noteEditText.getText().toString().trim().isEmpty()) {
                        noteEditText.setError("empty note");
                    } else {
                        saveCallNote();
                    }
                } else {
                    Toast.makeText(mContext, "Network error. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dismissLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                makeCall();
            }
        });


        return rootView;
    }

    private void saveCallNote() {
        CallNote callNote = new CallNote();
        callNote.setCallNote(noteEditText.getText().toString().trim());
        callNote.setCallerId(ParseUser.getCurrentUser().getObjectId());
        callNote.setCallerName(ParseUser.getCurrentUser().getString("name"));
        callNote.setCallerNumber(ParseUser.getCurrentUser().getString("phoneNumber"));
        if (ParseUser.getCurrentUser().getParseFile("photo")!= null) {
            callNote.setCallerPhoto(ParseUser.getCurrentUser().getParseFile("photo").getUrl());
        }
        callNote.setUserNumber(number);
        callNote.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    Toast.makeText(mContext, "Call note saved", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    makeCall();
                } else {
                    Toast.makeText(mContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    @Override
    public void dismiss() {
        makeCall();
        super.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
