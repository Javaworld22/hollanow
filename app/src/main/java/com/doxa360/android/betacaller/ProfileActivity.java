package com.doxa360.android.betacaller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.digits.sdk.android.internal.Beta;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.model.User;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

  public static final String USER_ID = "USER_ID";
  public static final String USER_NAME = "USER_NAME";
  public static final String FULL_NAME = "FULL_NAME";
  public static final String PHONE = "PHONE";
  public static final String EMAIL = "EMAIL";
  public static final String PHOTO = "PHOTO";
  private static final String TAG = "Profile Activity";
    private final String NUMBER = "number";
    private final String POSITION = "checkposition";
  private String userIdIntent,usernameIntent,fullnameIntent,phoneIntent,emailIntent,photoIntent;

  private ImageView backdrop;
  private View.OnClickListener fabClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
      startActivity(intent);
    }
  };
  CollapsingToolbarLayout mCollapsingToolbarLayout;
  private boolean isCurrentUser = true;
  private User userIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
//        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    HollaNowSharedPref sharedPref = new HollaNowSharedPref(ProfileActivity.this);
    User currentUser = sharedPref.getCurrentUser();

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(fabClickListener);
    backdrop = (ImageView) findViewById(R.id.backdrop);
//        photo = (ImageView) findViewById(R.id.photo);

    Intent intent = getIntent();
    if (intent != null) {

      userIntent = intent.getParcelableExtra(BetaCaller.USER_PROFILE);
      if (userIntent != null) {
        isCurrentUser = false;
        usernameIntent = userIntent.getUsername();
        Log.e(TAG+"user", userIntent.getUsername()+"");
        fullnameIntent = userIntent.getName();
        phoneIntent = userIntent.getPhone();
        emailIntent = userIntent.getEmail();
          if(phoneIntent != null)
              sharedPref.setPhoneEmoji(phoneIntent);
        photoIntent = userIntent.getProfilePhoto();
        if (photoIntent != null) {
          Picasso.with(this)
                  .load(BetaCaller.PHOTO_URL + photoIntent)
                  .placeholder(R.drawable.wil_profile)
                  .error(R.drawable.wil_profile)
                  .into(backdrop);
        }
        Log.e(TAG + " user:", usernameIntent+fullnameIntent+phoneIntent+emailIntent+photoIntent);
        fab.setVisibility(View.INVISIBLE);
        fab.setEnabled(false);
      } else {
        isCurrentUser = true;
        if (currentUser.getProfilePhoto() != null) {
          Picasso.with(this)
                  .load(BetaCaller.PHOTO_URL + currentUser.getProfilePhoto())
                  .placeholder(R.drawable.wil_profile)
                  .error(R.drawable.wil_profile)
                  .into(backdrop);
        }
      }
    } else {
      isCurrentUser = true;
      if (currentUser.getProfilePhoto() != null) {
        Picasso.with(this)
                .load(BetaCaller.PHOTO_URL + currentUser.getProfilePhoto())
                .placeholder(R.drawable.wil_profile)
                .error(R.drawable.wil_profile)
                .into(backdrop);
      }
    }

//        photo.setBackground(getResources().getDrawable(R.drawable.circle));

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle((isCurrentUser)?currentUser.getName():fullnameIntent);

      String number = intent.getStringExtra(NUMBER);
      String position = intent.getStringExtra(POSITION);

    FragmentManager fm = getSupportFragmentManager();
    ProfileFragment fragment = new ProfileFragment();
    Bundle args = new Bundle();
    if (!isCurrentUser) {
      args.putString(USER_ID, userIdIntent);
      args.putString(USER_NAME, usernameIntent);
      args.putString(FULL_NAME, fullnameIntent);
      args.putString(PHONE, phoneIntent);
      args.putString(EMAIL, emailIntent);
        args.putString(NUMBER, number);
        args.putString(POSITION,position);
      args.putParcelable(BetaCaller.USER_PROFILE, userIntent);
      fragment.setArguments(args);
    }
   else if(isCurrentUser){
        args.putString(NUMBER, number);
        args.putString(POSITION,position);
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

//    public static Bitmap blur(Context context, Bitmap image) {
//        Bitmap outputBitmap;
//        if (image!=null) {
//            int width = Math.round(image.getWidth() * BITMAP_SCALE);
//            int height = Math.round(image.getHeight() * BITMAP_SCALE);
//
//            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
//            outputBitmap = Bitmap.createBitmap(inputBitmap);
//
//            RenderScript rs = RenderScript.create(context);
//            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
//            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
//            theIntrinsic.setRadius(BLUR_RADIUS);
//            theIntrinsic.setInput(tmpIn);
//            theIntrinsic.forEach(tmpOut);
//            tmpOut.copyTo(outputBitmap);
//        }
//        else {
//            return null;
//        }
//
//        return outputBitmap;
//    }


//    private byte[] getByteArrayFromFile(Context context, Uri uri) {
//        byte[] fileBytes = null;
//        InputStream inStream = null;
//        ByteArrayOutputStream outStream = null;
//        Log.e(TAG, uri.getScheme());
//
//
//        Random r = new Random();
//        int shortSide = r.nextInt(880 - 500) + 500;
//        return reduceImageForUpload(fileBytes, shortSide);
//    }
//
//    private class urlToBytes extends AsyncTask<Uri, String, byte[]> {
//
//        byte[] fileBytes;
//        Uri uri;
//        Bitmap userPhoto;
//        public urlToBytes(Uri uri) {
//            this.uri = uri;
//            Log.e(TAG, uri+" ... new url bytes");
//        }
//
//        @Override
//        protected byte[] doInBackground(Uri... uris) {
//            try {
//                URL aURL = new URL(uri.toString());
//                URLConnection conn = aURL.openConnection();
//                conn.connect();
//                InputStream inStream = conn.getInputStream();
//                fileBytes = IOUtils.toByteArray(inStream);
//
//            } catch (IOException e) {
//                Log.e("IMAGE", "Error getting bitmap", e);
//            }
//
//            return fileBytes;
//        }
//
//        @Override
//        protected void onPostExecute(byte[] bytes) {
//            super.onPostExecute(bytes);
//            userPhoto = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            if (userPhoto!=null) {
//                Bitmap blurred = blur(ProfileActivity.this, userPhoto);
//                backdrop.setImageBitmap(blurred);
//            }
//        }
//    }
//
//
//    public static byte[] reduceImageForUpload(byte[] imageData, int shortSide) {
//        Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, shortSide);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
//        byte[] reducedData = outputStream.toByteArray();
//        try {
//            outputStream.close();
//        }
//        catch (IOException e) {
//            // Intentionally blank
//        }
//
//        return reducedData;
//    }




}
