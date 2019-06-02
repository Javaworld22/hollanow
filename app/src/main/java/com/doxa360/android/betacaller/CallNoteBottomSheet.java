package com.doxa360.android.betacaller;


import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Editable;
import android.text.TextWatcher;


import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.doxa360.android.betacaller.model.User;
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
public class CallNoteBottomSheet extends DialogFragment implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    public static final String TAG = "CallNoteBottomSheet";
    private static final String SHOWCASE_ID = "12345654321";
    protected EmojiconEditText noteEditText;

    private Button finishBtn;
    private TextView dismissLbl, emojis;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String number;
    private String name;
    private String username;
    private String photo;
    private ProfileFragment parentFragment;
    private HollaNowApiInterface hollaNowApiInterface;
    private HollaNowSharedPref mSharedPref;
    private User currentUser;
    private  String changeByte;
    private User userIntent;
    private Editable editable;
    private View view;
    private boolean isReached = false;



    public CallNoteBottomSheet() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.call_note_bottomsheet, container, false);

        getDialog().setTitle("Add HollaNow Note");
        mSharedPref = new HollaNowSharedPref(mContext);
        currentUser = mSharedPref.getCurrentUser();
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

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
        Bundle bundle =   getArguments();
        if(bundle != null) {
            number = bundle.getString(Parse_Contact.PHONE_NUMBER);
            if(mSharedPref.getCurrentUser() != null)
               name = mSharedPref.getCurrentUser().getName();
            username = bundle.getString(Parse_Contact.USERNAME);
            if(mSharedPref.getCurrentUser() != null)
                photo = mSharedPref.getCurrentUser().getProfilePhoto();
        }

       // Intent i = null;
       // try {
       //      i = getActivity().getIntent(); //Intent.getIntentOld("Emoji");
       // }catch(Exception e){}
      //  if(i != null)
      //  noteEditText.setText(i.getStringExtra("Emoji"));
        //if(getArguments().getString("Emojis") != null)
       // noteEditText.setText(getArguments().getString("Emojis"));

//try {
  //  Thread.sleep(500);
    //setEmojiconFragment(false);
//}catch(Exception e){Log.e(TAG, "Exception occured while waiting: "+e.getMessage());}


        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                Log.e("EmojoActivity", "Check for visibility of the progressbar");
                //TODO: do char count etal check here...
                if (MyToolBox.isNetworkAvailable(mContext)) {
                    if (noteEditText.getText().toString().trim().isEmpty()) {
                        noteEditText.setError("empty note");
                        Log.e("EmojoActivity", "Check for Network availability");
                    } else {
                        Log.e("EmojoActivity", "Check for the emoji character");
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
      /**  noteEditText.setOnClickListener(new View.OnClickListener() {
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
        }); **/

        if (!mSharedPref.isTutorial2()) {
            startTutorialSequence();
        }


        noteEditText.addTextChangedListener(new TextWatcher() {

            /**
             * This notify that, within s,
             * the count characters beginning at start are about to be replaced by new text with length
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * This notify that, somewhere within s, the text has been changed.
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
             //   int charCount = s.length();
              //    if (charCount > 0) {
                //      s.delete(charCount - 1, charCount);
              //    }
                editable = s;
                if(noteEditText.getText().length() == 15 && !isReached){
                    noteEditText.append("\n");
                    isReached = true;
                }
                else if(noteEditText.getText().length() == 30  && !isReached){
                    noteEditText.append("\n");
                    isReached = true;
                }
                else if(noteEditText.getText().length() == 45  && !isReached){
                    noteEditText.append("\n");
                    isReached = true;
                }
                if(noteEditText.getText().length() < 10  &&  isReached)
                    isReached = false;
            }

            /**
             * This notify that, within s, the count characters beginning at start have just
             * replaced old text that had length
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               // noteEditText.setText(s);
                Log.e("EmojoActivity", "Character emoji "+noteEditText.getText().toString().trim());
            }
        });

        setEmojiconFragment(false);
        view = getActivity().getCurrentFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //if(in != null)
       // in.hideSoftInputFromInputMethod(view.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

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

        //number = getArguments().getString("EmojiNumber1");

        if(number == null)
            number = getArguments().getString("EmojiNumber1");
        if(number == null)
            number = getArguments().getString("PHONE_NUMBER");

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
        String phone = mSharedPref.getCurrentUser().getPhone();
        if(phone.startsWith("+234")) {
            String p = null;
            p = phone.substring(4);
           // "0".concat(p);
            phone = "0"+p;
            Log.e(TAG, "Your phone number :" + phone);
        }


        if (MyToolBox.isNetworkAvailable(mContext)) {
            HollaNowApiInterface hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class); //token,from,to,note
            Call<JsonElement> call = hollaNowApiInterface.callnotePhone(token,/**"08057270466"**/phone,/**"08154965498"**/number,ss/**noteEditText.getText().toString().trim()**/,username,name,photo);

            Log.e(TAG, "Current User Printout111: "+token+" "+phone+" "+number+" "+noteEditText.getText().toString().trim()+" "+username+" "+name+" "+photo );

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.code() == 200) {
                       // mProgressDialog.dismiss();
                        Log.e(TAG, "success of contacts " + response.code() + "");
                       // Log.e(TAG, "success of contacts " + response.code() + "");
                        //Log.e(TAG, "success of contacts " + response.body() + "");
                        //Log.e(TAG, "success of contacts " + response.body() + "");


                        Toast.makeText(mContext, "Note sent", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(mContext, "Note not saved", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
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

    private void setEmojiconFragment(boolean useSystemDefault) {

       // getSupportFragmentManager()
                getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        Log.e(TAG, "EmojiconClicked is here ooo");

        EmojiconsFragment.input(noteEditText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        int charCount = 0;
         if(editable != null)
         charCount = editable.length();
            if (charCount > 0) {
              editable.delete(charCount - 1, charCount);
            }
        Log.e(TAG, "BackSpace is here ooooo");
        //Log.e(TAG, "BackSpace is here ooooo");
       // Log.e(TAG, "BackSpace is here ooooo");

        EmojiconsFragment.backspace(noteEditText);
    }



}
