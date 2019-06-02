package com.doxa360.android.betacaller.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.doxa360.android.betacaller.ActivityVideoAds;
import com.doxa360.android.betacaller.R;

import java.io.File;

/**
 * Created by user on 11/29/2017.
 */

public class ViewDialog {
    private File mFile;
    private String mTextPicture;
    private int mColor;
    private String mPic_name;

    public void showDialog(final Activity activity, String msg, File file, String textPicture,int color,String pic_name){
        mFile = file;
        mColor = color;
        mTextPicture = textPicture;
        mPic_name = pic_name;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialogbox_otp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button dialogBtn_gopro = (Button) dialog.findViewById(R.id.btn_gopro);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(activity.getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        dialogBtn_gopro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              //  Intent intent = new Intent(activity.getApplicationContext(), ActivityVideoAds.class);
              //  intent.putExtra("color", mColor);

               // intent.putExtra("content", mTextPicture);
               // intent.putExtra("pic",mFile);
               // intent.putExtra("pic_name",mPic_name);
               // activity.startActivity(intent);
               // dialog.dismiss();
            }
        });
        dialog.show();
    }
}
