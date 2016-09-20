/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doxa360.android.betacaller.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyToolBox {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int MEDIA_TYPE_IMAGE = 4;
    private static final int MEDIA_TYPE_VIDEO = 5;



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
            Log.e("NETWORK","network avail...");
        }
        else{
            Log.e("NETWORK", "no network avail...");
        }

        return isAvailable;
    }

    public static void SnackBarMessage(CoordinatorLayout coordinatorLayout, String message, int duration){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, duration);

        // Changing message text color
        snackbar.setActionTextColor(Color.BLUE);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void AlertMessage(Context context, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String mTitle = "Oops!";
        if(alertMessage.contains("An email containing a password reset link has been sent to")){
            mTitle = "Yay!";
        }
        builder.setTitle(mTitle)
                .setMessage(alertMessage)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void AlertMessage(Context context, String alertTitle, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String mTitle = alertTitle;
        if(alertMessage.contains("An email containing a password reset link has been sent to")){
            mTitle = "Yay!";
        }
        builder.setTitle(mTitle)
                .setMessage(alertMessage)
                .setPositiveButton(android.R.string.ok, null);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String listToString(List<String> list) {
        if (list != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                stringBuilder.append(string);
                if (i != (list.size() - 1)) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
        else {
            return null;
        }
    }

    public static String[] listToStringArray(List<String> list) {
        if (list != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                stringBuilder.append("\"");
                stringBuilder.append(string);
                stringBuilder.append("\"");
                if (i != (list.size() - 1)) {
                    stringBuilder.append(",");
                }
            }
            String builder =  stringBuilder.toString();
            return new String[] {builder};
        }
        else {
            return null;
        }
    }

    public static String listToTaggedString(List<String> list) {
        if (list != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                stringBuilder.append("#");
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }
        else {
            return null;
        }
    }

    public static void LoadingMessage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String mTitle = "Loading...";

        builder.setTitle(mTitle)
                .setMessage("Just a sec...")
                .setIcon(R.mipmap.ic_launcher);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isMinimumCharacters(String field, int min) {
        return field.length() > min;
    }

    public static boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

// TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    //TimeAgo Truncated
    public static String getShortTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

// TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "1s";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1m";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1h";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "h";
        } else {
            return diff / DAY_MILLIS + "d";
        }
    }

    public static String[] getCurrentWeek(/*String[] args*/){
        Calendar now = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        String[] days = new String[7];
        int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 1; //add 2 if your week start on monday
        now.add(Calendar.DAY_OF_MONTH, delta );
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        System.out.println(Arrays.toString(days));
        Log.e("week",days+"");
        return days;

    }

    // Truncate ellipsis
    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    public static String encodeString(String string0){
        String string1 = string0.replaceAll("^\"|\"$", "");//URLEncoder.encode(result);
        String string2 = string1.replace("\\", "");
        return string2;
    }

    //Media related stuff

    public static boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        else{
            return false;
        }
    }

    public static Uri getOutputMediaFolderUri(int mediaType, Context context) {

        if(isExternalStorageAvailable()){
            //storage dir
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    context.getString(R.string.app_name));
            //subdir
            if(!mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdirs()){
                    //failed
                    return null;
                }
            }
            //file name and create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path+"IMG_"+timestamp+".jpg");
            }
            else if (mediaType == MEDIA_TYPE_VIDEO){
                mediaFile = new File(path+"VID_"+timestamp+".mp4");
            }
            else{
                return null;
            }
            //return file's URI
            Toast.makeText(context, "File: " + Uri.fromFile(mediaFile), Toast.LENGTH_LONG).show();
            return Uri.fromFile(mediaFile);
        }
        else{
            return null;
        }
        //return null;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void copyImageToDevice(Context context,String fileName) {
        String dataFolder = context.getFilesDir().getAbsolutePath()+"/"+context.getResources().getString(R.string.app_name);
        try
        {
            String path = dataFolder + "/images/"+fileName;
            File file = new File(path);
            if (file.exists()) return;
            else if(!file.exists()) {
                file.getParentFile().mkdirs();
                //targetFile.createNewFile();
            }
            InputStream localInputStream = context.getAssets().open("images/"+fileName);
            FileOutputStream localFileOutputStream = new FileOutputStream(dataFolder+ "/images/"+fileName);

            byte[] arrayOfByte = new byte[1024];
            int offset;
            while ((offset = localInputStream.read(arrayOfByte))>0)
            {
                localFileOutputStream.write(arrayOfByte, 0, offset);
            }
            localFileOutputStream.close();
            localInputStream.close();
            Log.d(context.getClass().getSimpleName(), fileName + " copied to phone");
        }
        catch (IOException localIOException)
        {
            //localIOException.printStackTrace();
            //Log.d(TAG, "failed to copy");
            Log.e("YEMI COPYIMAGE", localIOException.getMessage());
            return;
        }
    }



}
