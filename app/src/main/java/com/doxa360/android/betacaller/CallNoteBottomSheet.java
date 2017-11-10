package com.doxa360.android.betacaller;


import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Editable;
import android.text.TextWatcher;


import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.User;
import com.doxa360.android.betacaller.model.oldCallNote;
import com.doxa360.android.betacaller.model.Parse_Contact;
import com.google.gson.JsonElement;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;


/**
 * Created by Apple on 24/07/16.
 */
public class CallNoteBottomSheet extends DialogFragment {

    public static final String TAG = "CallNoteBottomSheet";
    private static final String SHOWCASE_ID = "12345654321";
    protected EmojiconEditText noteEditText;

    private Button finishBtn;
    private TextView dismissLbl, emojis;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String number;
    private ProfileFragment parentFragment;
    private HollaNowApiInterface hollaNowApiInterface;
    private HollaNowSharedPref mSharedPref;
    private User currentUser;
    private  String changeByte;
    private User userIntent;



    public CallNoteBottomSheet() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.call_note_bottomsheet, container, false);

        getDialog().setTitle("Add HollaNow Note");


        noteEditText = (EmojiconEditText) rootView.findViewById(R.id.call_note);
        int unicode = 0x1f601;
        String emoji = new String(Character.toChars(unicode));
        noteEditText.setEnabled(true);
        finishBtn = (Button) rootView.findViewById(R.id.finish_button);
        dismissLbl = (TextView) rootView.findViewById(R.id.dismiss_label);
        //emojis = (TextView) rootView.findViewById(R.id.emojis);
        Spannable span=  new SpannableString(emoji);
        span.setSpan(new RelativeSizeSpan(1.5f), 0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //emojis.setText(span);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        number = getArguments().getString("PHONE_NUMBER");
        mSharedPref = new HollaNowSharedPref(mContext);
        currentUser = mSharedPref.getCurrentUser();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
       // Intent i = null;
       // try {
       //      i = getActivity().getIntent(); //Intent.getIntentOld("Emoji");
       // }catch(Exception e){}
      //  if(i != null)
      //  noteEditText.setText(i.getStringExtra("Emoji"));
        if(getArguments().getString("Emojis") != null)
        noteEditText.setText(getArguments().getString("Emojis"));

//try {
  //  Thread.sleep(500);
    //setEmojiconFragment(false);
//}catch(Exception e){Log.e(TAG, "Exception occured while waiting: "+e.getMessage());}


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
                if(number == null)
                number = getArguments().getString("EmojiNumber1");
                if(number == null)
                number = getArguments().getString("PHONE_NUMBER");
                getDialog().dismiss();
                makeCall();
                //saveCallNote();
            }
        });
        noteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getDialog().dismiss();
                noteEditText.setEnabled(false);
               Intent i = new Intent(getActivity(), EmojiActivity.class);
                i.putExtra("Emoji1",noteEditText.getText().toString().trim());
                i.putExtra("EmojiNumber",number);
                startActivity(i);
                getDialog().dismiss();
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
        //TelephonyManager tmr;
        String mPhone;
        String token = null;
        if(mSharedPref.getCurrentUser() != null) {
            token = mSharedPref.getCurrentUser().getToken();
        }

        number = getArguments().getString("EmojiNumber1");
        int unicode = 0x1f60e; ///// remove 0x1f60e
        String emoji = new String(Character.toChars(unicode));  //remove
        //noteEditText.setText("I want a man to be by my side, somebody that will show me love");

        Log.e(TAG, "Check emoji in CallNoteBottomSheet " +changeByte);
       // byte[] cc2 = {73,32,119,105,115,104,32,85,32,103,111,111,100,32,-16,-97,-104,-115};
       // String getByte = new String(cc2);
        CallNote callNote = new CallNote(changeByte,currentUser.getUsername(),
                currentUser.getName(),currentUser.getPhone(), currentUser.getIndustry(),currentUser.getProfilePhoto(),number); //note,username,name,from,industry,photo,to
        Log.e(TAG, "Current User Printout: "+noteEditText.getText().toString().trim()+" "+currentUser.getIndustry()
        +" "+currentUser.getName()+" "+mSharedPref.getPhoneEmoji()+" "+currentUser.getProfilePhoto()
        +" "+ number + " " + currentUser.getUsername()+" "+mSharedPref.getToken());

     //  String chars = noteEditText.getText().toString().trim();
       // byte[] cv = chars.getBytes();
       /** for(int i=0;i<cv.length;i++){
            char c = chars.charAt(i);

            Log.e(TAG, "Test the emoji of these here 11111111111 " +c);
          //  Log.e(TAG, "Test the emoji of these here 11111111111 " +Integer.decode(chars));
            Log.e(TAG, "Test the emoji of these here 11111111111 " +(int)c);
            Log.e(TAG, "Test the emoji of these here 11111111111 " +cv[i]);
            Log.e(TAG, "Test the emoji of these here 11111111111 " +~cv[i]);
        }**/
      //  byte[] cc1 = {73,32,119,105,115,104,32,85,32,103,111,111,100,32,-16,-97,-104,-115};
      //   String ss1 = new String(cc1);
      //  Log.e(TAG,"Check emoji out "+ss1);
        // noteEditText.setText(ss1);
        String chars = noteEditText.getEditableText().toString().trim();
        byte[] cv = chars.getBytes();
        int c;
        String ss = "";
        for(int i=0;i<cv.length;i++) {
             c = cv[i];
           //  Log.e(TAG, "Test the emoji of these here 11111111111 " +c);
              ss = ss+String.valueOf(c)+";";
             Log.e(TAG, "Test the emoji of these here 11111111111 " +ss);
         }






        //byte[] cc1 = {73,32,119,105,115,104,32,85,32,103,111,111,100,32,-16,-97,-104,-115};
       // String ss1 = new String(cc1);
        //Log.e(TAG,"Check emoji out "+ss1);
       // noteEditText.setText(ss1);
       // Log.e(TAG,"Check emoji out "+noteEditText.getText().toString().trim());
    /**    Call<CallNote> call = hollaNowApiInterface.saveCallNote(mSharedPref.getToken(), callNote);
        call.enqueue(new Callback<CallNote>() {
            @Override
            public void onResponse(Call<CallNote> call, Response<CallNote> response) {
                Log.e(TAG,"response.Code"+response.code());
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
        });  **/


            if(number.startsWith("+234")) {
                String p = null;
                p = number.substring(4);
               // "0".concat(p);
                number = "0"+p;
                // mSharedPref.setPhoneEmoji(phone);
                Log.e(TAG, "phone number u are calling is:" + number);
            }
        String phone = mSharedPref.getPhoneEmoji();
        if(phone.startsWith("+234")) {
            String p = null;
            p = phone.substring(4);
           // "0".concat(p);
            phone = "0"+p;
            Log.e(TAG, "Your phone number :" + phone);
        }


        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class); //token,from,to,note
            Call<JsonElement> call = hollaNowApiInterface.callnotePhone(token,/**"08057270466"**/phone,/**"08154965498"**/number,ss/**noteEditText.getText().toString().trim()**/,currentUser.getUsername(),currentUser.getName(),currentUser.getProfilePhoto());

            Log.e(TAG, "Current User Printout1111: "+token+" "+phone+" "+number+" "+noteEditText.getText().toString().trim()+" "+currentUser.getUsername()+" "+currentUser.getName()+" "+currentUser.getProfilePhoto());

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.code() == 200) {
                       // mProgressDialog.dismiss();
                        Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.code() + "");
                        Log.e(TAG, "success of contacts " + response.code() + "");

                        Log.e(TAG, "success of contacts " + response.body() + "");
                        // Log.e(TAG, "success of contacts " + response.body().string() + "");
                        Log.e(TAG, "success of contacts " + response.body() + "");
                        Toast.makeText(mContext, "Note saved", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                        makeCall();
                        //Log.e(TAG, "success of contacts " + response.body().string() + "");
                        // Log.e(TAG, "success of contacts " + response.body().string() + "");
                        //  Log.e(TAG, "success of contacts " + response.body().string() + "");
                        //  Log.e(TAG, "success of contacts " + response.body().string() + "");

//                        Toast.makeText(getApplicationContext(), "Device id successfully updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "error from contacts: " + "error updating device id");
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.errorBody());
                        Log.e(TAG, "error from contacts: " + response.errorBody());
                        Log.e(TAG, "error from contacts: " + response.errorBody());
                        Log.e(TAG, "error from contacts: " + response.errorBody());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.errorBody().toString());
                        Log.e(TAG, "error from contacts: " + response.code());
                        Log.e(TAG, "error from contacts: " + response.errorBody().toString());

                            Toast.makeText(mContext, "Note not saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
                    Log.e(TAG, "Device id update failed: " + t.getMessage());
//                    Toast.makeText(getApplicationContext(), "Network error. Try again", Toast.LENGTH_LONG).show();

                }
            });
        }



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
