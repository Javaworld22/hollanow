package com.doxa360.android.betacaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by Apple on 24/07/16.
 */
public class CallNoteBottomSheet extends DialogFragment {

    public static final String TAG = "CallNoteBottomSheet";
    private static final String SHOWCASE_ID = "12345654321";
    private EditText noteEditText;
    private Button finishBtn;
    private TextView dismissLbl;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String number;
    private ProfileFragment parentFragment;
    private HollaNowApiInterface hollaNowApiInterface;
    private HollaNowSharedPref mSharedPref;
    private User currentUser;

    public CallNoteBottomSheet() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.call_note_bottomsheet, container, false);

        getDialog().setTitle("Add HollaNow Note");
        number = getArguments().getString(Parse_Contact.PHONE_NUMBER);

        noteEditText = (EditText) rootView.findViewById(R.id.call_note);
        finishBtn = (Button) rootView.findViewById(R.id.finish_button);
        dismissLbl = (TextView) rootView.findViewById(R.id.dismiss_label);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        mSharedPref = new HollaNowSharedPref(mContext);
        currentUser = mSharedPref.getCurrentUser();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);


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

        if (!mSharedPref.isTutorial2()) {
            startTutorialSequence();
        }


        return rootView;
    }

    private void startTutorialSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(noteEditText,
                getString(R.string.tooltip_three), "GOT IT");
//        sequence.addSequenceItem(tab2,
//                getString(R.string.tooltip_two), "GOT IT");
//        sequence.addSequenceItem(null,
//                getString(R.string.tooltip_three), "GOT IT");
        sequence.start();
        mSharedPref.setTutorial2(true);

    }

    private void saveCallNote() {
        finishBtn.setText("...");
        CallNote callNote = new CallNote(noteEditText.getText().toString().trim(),currentUser.getUsername(),
                currentUser.getName(),currentUser.getPhone(),currentUser.getIndustry(),currentUser.getProfilePhoto(),
                number); //note,username,name,from,industry,photo,to
        Call<CallNote> call = hollaNowApiInterface.saveCallNote(mSharedPref.getToken(), callNote);
        call.enqueue(new Callback<CallNote>() {
            @Override
            public void onResponse(Call<CallNote> call, Response<CallNote> response) {
                if (response.code() == 200) {
                    Toast.makeText(mContext, "Note saved", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    makeCall();
                } else {
                    Toast.makeText(mContext, "Note not saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CallNote> call, Throwable t) {
//                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(mContext, "Network error. Note not saved. Try again.", Toast.LENGTH_LONG).show();
            }
        });

        finishBtn.setText("Save &amp; Continue");

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
