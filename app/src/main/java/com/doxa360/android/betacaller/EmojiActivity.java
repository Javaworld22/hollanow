package com.doxa360.android.betacaller;

/**
 * Created by user on 5/30/2017.
 */

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

import com.doxa360.android.betacaller.model.Parse_Contact;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class EmojiActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    EmojiconEditText mEditEmojicon;
    TextView mTxtEmojicon;
    String contactPhone;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);


        // initialize EmojicobEditText and EmojiconTextView.
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);
        mTxtEmojicon = (TextView) findViewById(R.id.txtEmojicon);
        String retrive_text = getIntent().getStringExtra("Emoji1");
        if(retrive_text != null)
        mEditEmojicon.setText(retrive_text);
         view = this.getCurrentFocus();
        contactPhone = getIntent().getStringExtra("EmojiNumber");
        if(view != null) {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromInputMethod(view.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        mTxtEmojicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
               //Intent emojiIntent = new Intent(getApplicationContext(), CallNoteBottomSheet.class);
               // emojiIntent.putExtra("Emoji",mEditEmojicon.getText().toString().trim());
               // startActivity(emojiIntent);
                CallNoteBottomSheet callNoteBottomSheet = new CallNoteBottomSheet();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle args = new Bundle();
                // callNoteBottomSheet1 = callNoteBottomSheet;
                args.putString("Emojis", mEditEmojicon.getText().toString().trim());
                args.putString("EmojiNumber1", contactPhone);
                callNoteBottomSheet.setArguments(args);
                callNoteBottomSheet.show(fragmentManager, "CALL_NOTE");
                if(view != null)
                view.setVisibility(View.INVISIBLE);
            }

            });


if(mEditEmojicon != null)
        mEditEmojicon.addTextChangedListener(new TextWatcher() {

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
            public void afterTextChanged(Editable s) {}

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

               // mTxtEmojicon.setText(s);
                Log.e("EmojoActivity", "Character emoji "+mEditEmojicon.getText().toString().trim());
            }
        });

        setEmojiconFragment(false);

    }

    /**
     * Set the Emoticons in Fragment.
     * @param useSystemDefault
     */
    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    /**
     * It called, when click on icon of Emoticons.
     * @param emojicon
     */
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    /**
     * It called, when backspace button of Emoticons pressed
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

        EmojiconsFragment.backspace(mEditEmojicon);
    }
}

