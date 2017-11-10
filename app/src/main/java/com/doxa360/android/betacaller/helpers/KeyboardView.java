package com.doxa360.android.betacaller.helpers;

/**
 * Created by HollaNow on 9/2/2017.
 */

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.EditText;
import android.view.View;

import com.doxa360.android.betacaller.PhoneVerificationActivity;
import com.doxa360.android.betacaller.R;
import android.util.AttributeSet;
import android.widget.TextView;
import android.text.Editable;

public class KeyboardView extends FrameLayout implements View.OnClickListener  {
    private static final String TAG = KeyboardView.class.getSimpleName();
   // private EditText mPasswordField;
    private String text;
    private PhoneVerificationActivity verifyActivity;
    private String sendText;
    private Context mContext;
    private TextView firstText, secondText,thirdText,fourthText,fifthText,sixText;
    private static int countNumber;
    private View view;

    public KeyboardView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
     view =    inflate(getContext(), R.layout.keyboard, this);
//firstView = $(R.id.t9_key_0);
       // verifyActivity = new PhoneVerificationActivity();
        initViews();
    }



    private void initViews() {
       // mPasswordField = $(R.id.password_field);
       // firstView = $(R.id.verify1);

        firstText = (TextView) findViewById(R.id.verify1);
        secondText = (TextView) findViewById(R.id.verify2);
        thirdText = (TextView) findViewById(R.id.verify3);
        fourthText = (TextView) findViewById(R.id.verify4);
        fifthText = (TextView) findViewById(R.id.verify5);
        sixText = (TextView) findViewById(R.id.verify6);
        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_clear).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      ///  firstView = (TextView) v.findViewById(R.id.verify1);
        // handle number button click
        Log.e(TAG, "Keyboardview inner  one clicked here "+v.toString());
       // verifyActivity.inputString(((TextView) v).getText().toString().trim());
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
          //  mPasswordField.append(((TextView) v).getText());
            text = ((TextView) v).getText().toString().trim();
            Log.e(TAG, "Keyboardview clicked here "+countNumber);
            Log.e(TAG, "Keyboardview clicked here "+text);

            if(!(v.getId() == R.id.t9_key_clear || v.getId() == R.id.t9_key_backspace || countNumber >= 6))
                                    countNumber++;

            if(countNumber == 1)
                firstText.setText(text);
            else if(countNumber == 2)
                secondText.setText(text);
            else if(countNumber == 3)
                thirdText.setText(text);
            else if(countNumber == 4)
                fourthText.setText(text);
            else if(countNumber == 5)
                fifthText.setText(text);
            else if(countNumber == 6)
                sixText.setText(text);


           // firstView.setText(text);
            return;
        }
        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
              //  mPasswordField.setText(null);
                firstText.setText(" ");
                secondText.setText(" ");
                thirdText.setText(" ");
                fourthText.setText(" ");
                fifthText.setText(" ");
                sixText.setText(" ");
                countNumber = 0;
                break;
            }

            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
              //  Editable editable = mPasswordField.getText();
               // int charCount = editable.length();
              //  if (charCount > 0) {
              //      editable.delete(charCount - 1, charCount);
              //  }
                if(countNumber == 1)
                    firstText.setText(" ");
                else if(countNumber == 2)
                    secondText.setText(" ");
                else if(countNumber == 3)
                    thirdText.setText(" ");
                else if(countNumber == 4)
                    fourthText.setText(" ");
                else if(countNumber == 5)
                    fifthText.setText(" ");
                else if(countNumber == 6)
                    sixText.setText(" ");
                if(countNumber > 0)
                countNumber--;
                break;
            }
          //
        }
    }

    public String getInputText() {
        //return mPasswordField.getText().toString();
        sendText = firstText.getText().toString().trim()+secondText.getText().toString().trim()+
                thirdText.getText().toString().trim()+fourthText.getText().toString().trim()+
                fifthText.getText().toString().trim()+sixText.getText().toString().trim();
        return sendText;
    }

    public void setInputText(String code, Context context) {
        Log.e(TAG, "Keyboardview setInputText  here "+code);
       // new KeyboardView(context);
     //   init();
        if(code != null) {
           // view.
            ((TextView) view).setId(R.id.verify1);
        ((TextView) view).setText(code.charAt(1));
            ((TextView) view).setId(R.id.verify2);
        ((TextView) view).setText(code.charAt(2));
            ((TextView) view).setId(R.id.verify3);
        ((TextView) view).setText(code.charAt(3));
            ((TextView) view).setId(R.id.verify4);
        ((TextView) view).setText(code.charAt(4));
            ((TextView) view).setId(R.id.verify5);
        ((TextView) view).setText(code.charAt(5));
            ((TextView) view).setId(R.id.verify6);
        ((TextView) view).setText(code.charAt(6));


          //  firstText.setText(code.charAt(1));
          //  secondText.setText(code.charAt(2));
          //  thirdText.setText(code.charAt(3));
          //  fourthText.setText(code.charAt(4));
          //  fifthText.setText(code.charAt(5));
          //  sixText.setText(code.charAt(6));
        }
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}