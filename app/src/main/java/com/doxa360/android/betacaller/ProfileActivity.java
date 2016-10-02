package com.doxa360.android.betacaller;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.CallNote;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String FULL_NAME = "FULL_NAME";
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String PHOTO = "PHOTO";
    private static final String TAG = "Profile Activity";
    private String userIdIntent,usernameIntent,fullnameIntent,phoneIntent,emailIntent,photoIntent;

    private ImageView backdrop,photo;
    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        }
    };
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean isCurrentUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        photo = (ImageView) findViewById(R.id.photo);



        if (getIntent().getStringExtra(USER_ID)!=null) {
            if (!getIntent().getStringExtra(USER_ID).equalsIgnoreCase(ParseUser.getCurrentUser().getObjectId())) {
                isCurrentUser = false;
                userIdIntent = getIntent().getStringExtra(USER_ID);
                usernameIntent = getIntent().getStringExtra(USER_NAME);
                fullnameIntent = getIntent().getStringExtra(FULL_NAME);
                phoneIntent = getIntent().getStringExtra(PHONE);
                emailIntent = getIntent().getStringExtra(EMAIL);
                photoIntent = getIntent().getStringExtra(PHOTO);
//                fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                if (photoIntent != null) {
                    byte[] photoBytes = new byte[0];
                    Bitmap userPhoto = null;
                    if (MyToolBox.isNetworkAvailable(this)) {
                        new urlToBytes(Uri.parse(photoIntent));
                    } else {
                        Toast.makeText(ProfileActivity.this, "Network error. Please check your connection", Toast.LENGTH_SHORT).show();
                    }
                    Picasso.with(this)
                            .load(photoIntent)
                            .placeholder(R.drawable.wil_profile)
                            .error(R.drawable.wil_profile)
                            .into(photo);
                    Log.e(TAG, photoIntent);
                }
                fabClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //add favorites
                        addToFavorite();
                    }
                };
                fab.setVisibility(View.INVISIBLE);
            }
        } else {
            if (ParseUser.getCurrentUser().getParseFile("photo") != null) {
                byte[] photoBytes = new byte[0];
                Bitmap userPhoto = null;
                if (MyToolBox.isNetworkAvailable(this)) {
                    try {
                        photoBytes = ParseUser.getCurrentUser().getParseFile("photo").getData();
                        userPhoto = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
                        Bitmap blurred = blur(this, userPhoto);
                        backdrop.setImageBitmap(blurred);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Network error. Please check your connection", Toast.LENGTH_SHORT).show();
                }
                Picasso.with(this)
                        .load(ParseUser.getCurrentUser().getParseFile("photo").getUrl())
                        .placeholder(R.drawable.wil_profile)
                        .error(R.drawable.wil_profile)
                        .into(photo);
            }
        }

        photo.setBackground(getResources().getDrawable(R.drawable.circle));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(ParseUser.getCurrentUser().getUsername());


        FragmentManager fm = getSupportFragmentManager();
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        if (!isCurrentUser) {
            args.putString(USER_ID, userIdIntent);
            args.putString(USER_NAME, usernameIntent);
            args.putString(FULL_NAME, fullnameIntent);
            args.putString(PHONE, phoneIntent);
            args.putString(EMAIL, emailIntent);
            fragment.setArguments(args);
        }
        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.fragment, fragment, "PROFILE_FRAGMENT");
        ft.commit();


//
    }


    private void addToFavorite() {

    }

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    public static Bitmap blur(Context context, Bitmap image) {
        Bitmap outputBitmap;
        if (image!=null) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
        }
        else {
            return null;
        }

        return outputBitmap;
    }


    private byte[] getByteArrayFromFile(Context context, Uri uri) {
        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        Log.e(TAG, uri.getScheme());


        Random r = new Random();
        int shortSide = r.nextInt(880 - 500) + 500;
        return reduceImageForUpload(fileBytes, shortSide);
    }

    private class urlToBytes extends AsyncTask<Uri, String, byte[]> {

        byte[] fileBytes;
        Uri uri;
        Bitmap userPhoto;
        public urlToBytes(Uri uri) {
            this.uri = uri;
            Log.e(TAG, uri+" ... new url bytes");
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            try {
                URL aURL = new URL(uri.toString());
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream inStream = conn.getInputStream();
                fileBytes = IOUtils.toByteArray(inStream);

            } catch (IOException e) {
                Log.e("IMAGE", "Error getting bitmap", e);
            }

            return fileBytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            userPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (userPhoto!=null) {
                Bitmap blurred = blur(ProfileActivity.this, userPhoto);
                backdrop.setImageBitmap(blurred);
            }
        }
    }


    public static byte[] reduceImageForUpload(byte[] imageData, int shortSide) {
        Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, shortSide);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] reducedData = outputStream.toByteArray();
        try {
            outputStream.close();
        }
        catch (IOException e) {
            // Intentionally blank
        }

        return reducedData;
    }




}
