package com.doxa360.android.betacaller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    public static final int SHORT_SIDE_TARGET = 600;//1280;
    private FloatingActionButton fab;
    private Button mEdit;
    private EditText mFullName, mAbout, mOccupation, mAddress;
    private TextView mEmail;
    private ImageView mPhoto;
    private Button mFinish;
    private ProgressDialog mProgressDialog;
    private TextInputLayout mFullNameLayout;
    private TextInputLayout mBioLayout;
    private List<String> selectedTags;

    View.OnClickListener editPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cameraChoices();
        }
    };

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public  static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB
    protected Uri mMediaUri;
    private byte[] fileBytes;
    String fileName;


    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            //Capture Image
                            Intent captureVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null){
                                Toast.makeText(EditProfileActivity.this, "There was a problem", Toast.LENGTH_LONG).show();
                            }
                            else{
                                captureVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(captureVideoIntent, TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1:
                            //Choose Image
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVideoIntent.setType("image/*");
                            //Toast.makeText(getActivity(), "The size of your video must be less than 10MB", Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, CHOOSE_PHOTO_REQUEST);
                            break;
                    }
                }

                private Uri getOutputMediaFileUri(int mediaType) {
                    if(isExternalStorageAvailable()){
                        //storage dir
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                getString(R.string.app_name));
                        //subdir
                        if(!mediaStorageDir.exists()){
                            if(!mediaStorageDir.mkdirs()){
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
                        Toast.makeText(EditProfileActivity.this,"File: "+Uri.fromFile(mediaFile),Toast.LENGTH_LONG).show();
                        return Uri.fromFile(mediaFile);
                    }
                    else{
                        return null;
                    }
                }

                private boolean isExternalStorageAvailable(){
                    String state = Environment.getExternalStorageState();

                    if (state.equals(Environment.MEDIA_MOUNTED)){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            };
    private EditText mIndustry;
    private List<String> allTags;
    private LinearLayout mShareLayout;
    private CardView mProCard;
    private HollaNowSharedPref mSharedPref;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        fab = (FloatingActionButton) findViewById(R.id.fab);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");

        getSupportActionBar().setTitle("Edit Profile");


        mEmail = (TextView) findViewById(R.id.email);
        mFullName = (EditText) findViewById(R.id.full_name);
        mFullNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mAbout = (EditText) findViewById(R.id.about);
        mOccupation = (EditText) findViewById(R.id.occupation);
        mAddress = (EditText) findViewById(R.id.address);
        mIndustry = (EditText) findViewById(R.id.industry);
        mEdit = (Button) findViewById(R.id.edit);
        mPhoto = (ImageView) findViewById(R.id.photo);

        mShareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        mProCard = (CardView) findViewById(R.id.pro_card);
        mSharedPref = new HollaNowSharedPref(this);

        ShareLinkContent sharedContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.doxa360.android.betacaller"))
                .setContentDescription("Hello friends, I now use the HollaNow app. So whenever you want to talk to me, just holla @"+ParseUser.getCurrentUser().getUsername()+ ". It\'s cooler.")
//                .setImageUrl(Uri.parse("http://hollanow.com/facebook_share.png"))
                .build();


        ShareButton shareButton = (ShareButton)findViewById(R.id.facebook_share_button);
        shareButton.setShareContent(sharedContent);

        shareButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                mSharedPref.setShared();
                toggleProShare();
                Log.e(TAG, "shared "+ result.toString() + " - " + result.getPostId());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        toggleProShare();


        mEmail.setText(ParseUser.getCurrentUser().getEmail());
        mFullName.setText(ParseUser.getCurrentUser().getString("name"));
//        mPhone.setText(ParseUser.getCurrentUser().getString("phoneNumber"));

        if (ParseUser.getCurrentUser().getString("bio")!=null) {
            mAbout.setText(ParseUser.getCurrentUser().getString("bio"));
        } else {
        }
        if (ParseUser.getCurrentUser().getString("occupation")!=null) {
            mOccupation.setText(ParseUser.getCurrentUser().getString("occupation"));
        } else {
        }
        if (ParseUser.getCurrentUser().getString("address")!=null) {
            mAddress.setText(ParseUser.getCurrentUser().getString("address"));
        } else {
        }
        if (ParseUser.getCurrentUser().getString("industry")!=null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<ParseUser.getCurrentUser().getList("industry").size();i++){
                String string = (String) ParseUser.getCurrentUser().getList("industry").get(i);
                stringBuilder.append(string);
                if (i != (ParseUser.getCurrentUser().getList("industry").size() - 1)) {
                    stringBuilder.append(",");
                }
            }
            mIndustry.setText(stringBuilder.toString());
        } else {
        }
//        ArrayList<String> tagList = new ArrayList<String>();
//        if (ParseUser.getCurrentUser().getList("tags")!=null) {
//            for (int i = 0; i < ParseUser.getCurrentUser().getList("tags").size(); i++) {
//                tagList.add((String) ParseUser.getCurrentUser().getList("tags").get(i));
//            }
//            mOccupation.setText(MyToolBox.listToString(tagList));
//        } else {
//        }

        if (ParseUser.getCurrentUser().getParseFile("photo") != null) {
            Picasso.with(EditProfileActivity.this)
                    .load(ParseUser.getCurrentUser().getParseFile("photo").getUrl())
                    .into(mPhoto);
        }

        mEdit.setOnClickListener(editPhoto);
        mPhoto.setOnClickListener(editPhoto);
        mIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                AddIndustryFragment fragment = new AddIndustryFragment();

            }
        });


    }

    private void toggleProShare() {
        if (mSharedPref.isShared()) {
            mProCard.setVisibility(View.VISIBLE);
            mShareLayout.setVisibility(View.INVISIBLE);
        } else {
            mProCard.setVisibility(View.INVISIBLE);
            mShareLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void cameraChoices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editProfile() {
        mProgressDialog.show();
        if (!mFullName.getText().toString().trim().isEmpty()) {
            ParseUser.getCurrentUser().put("name", mFullName.getText().toString().trim());
        }
        if (!mAbout.getText().toString().trim().isEmpty()) {
            ParseUser.getCurrentUser().put("bio", mAbout.getText().toString().trim());
        }
        if (!mAddress.getText().toString().trim().isEmpty()) {
            ParseUser.getCurrentUser().put("address", mAddress.getText().toString().trim());
        }
        if (!mOccupation.getText().toString().trim().isEmpty()) {
            ParseUser.getCurrentUser().put("occupation", mOccupation.getText().toString().trim());
        }
        if (allTags!=null) {
            ParseUser.getCurrentUser().addAllUnique("industry", allTags);
        }
//        ParseUser.getCurrentUser().addAll("tags", Arrays.asList(mFullName.getText().toString().trim().split("\\s*,\\s*")));
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(EditProfileActivity.this, "Your profile has been successfully updated", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
//                    NavUtils.navigateUpFromSameTask(EditProfileActivity.this);
                    finish();
                } else {
                    Log.e("What went wrong: ", e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Oops! Something went wrong. Please check your connection and try again",
                            Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            //add to gallery
            if (requestCode == CHOOSE_PHOTO_REQUEST){
                if (data == null){
                    Toast.makeText(this,"there was an error",Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri = data.getData();
                }
                int fileSize = 0;
                InputStream inputStream = null;
                try{
                    inputStream = getContentResolver().openInputStream(mMediaUri);
                    assert inputStream != null;
                    fileSize = inputStream.available();
                }
                catch (FileNotFoundException e){
                    Toast.makeText(EditProfileActivity.this,"Error opening image. Please try again.",Toast.LENGTH_LONG).show();
                    return;
                }
                catch (IOException e){
                    Toast.makeText(EditProfileActivity.this,"Error opening image. Please try again.",Toast.LENGTH_LONG).show();
                    return;
                }
                finally {
                    try{
                        assert inputStream != null;
                        inputStream.close();
                    } catch (IOException e){/*Intentionally blank*/ }
                }

                if (fileSize >= FILE_SIZE_LIMIT){
                    Toast.makeText(EditProfileActivity.this,"The selected image is too large. Please choose another image.",Toast.LENGTH_LONG).show();
                    return;
                }

            }
            else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            mProgressDialog.show();
            try {
                fileBytes = getByteArrayFromFile(this, mMediaUri);
            }
            finally{
                if (MyToolBox.isNetworkAvailable(this)) {
                    postProfilePhoto();
                } else {
                    MyToolBox.AlertMessage(this,"Oops", "Network Error. Please check your connection");
                }
            }

        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this,"There was an error saving your video",Toast.LENGTH_LONG).show();
        }

    }

    private void postProfilePhoto() {
        final ParseFile parseFile = new ParseFile("photo", fileBytes);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.getCurrentUser().put("photo", parseFile);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Picasso.with(EditProfileActivity.this)
                                        .load(parseFile.getUrl())
                                        .into(mPhoto);
                                Toast.makeText(EditProfileActivity.this, "Your profile photo has been successfully updated", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            } else {
                                Log.e("What went wrong: ", e.getMessage());
                                Toast.makeText(EditProfileActivity.this, "Oops! Something went wrong. Please check your connection and try again",
                                        Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Log.e("What went wrong: ", e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Oops! Something went wrong. Please check your connection and try again",
                            Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private byte[] getByteArrayFromFile(Context context, Uri uri) {
        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        Log.e(TAG, uri.getScheme());

        if (uri.getScheme().equals("http")) {
//            new urlToBytes();
        }
        else if (uri.getScheme().equals("content")) {
            try {
                inStream = context.getContentResolver().openInputStream(uri);
                outStream = new ByteArrayOutputStream();

                byte[] bytesFromFile = new byte[1024 * 1024]; // buffer size (1 MB)
                assert inStream != null;
                int bytesRead = inStream.read(bytesFromFile);
                while (bytesRead != -1) {
                    outStream.write(bytesFromFile, 0, bytesRead);
                    bytesRead = inStream.read(bytesFromFile);
                }

                fileBytes = outStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    assert inStream != null;
                    inStream.close();
                    assert outStream != null;
                    outStream.close();
                } catch (IOException e) { /*( Intentionally blank */ }
            }
        }
        else {
            try {
                File file = new File(uri.getPath());
                FileInputStream fileInput = new FileInputStream(file);
                fileBytes = IOUtils.toByteArray(fileInput);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        Random r = new Random();
        int shortSide = r.nextInt(880 - 500) + 500;
        return reduceImageForUpload(fileBytes, shortSide);
    }

    public static byte[] reduceImageForUpload(byte[] imageData, int shortSide) {
        Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, shortSide);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] reducedData = outputStream.toByteArray();
        try {
            outputStream.close();
        }
        catch (IOException e) {
            // Intentionally blank
        }

        return reducedData;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("VIEW_PAGER", 3);
        setResult(RESULT_OK, intent);
        finishActivity(2016);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            Intent intent = new Intent(this, HomeActivity.class);
//            Log.e(TAG, "hello.");
//            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
//            intent.putExtra("VIEW_PAGER", 3);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);

            Intent intent = new Intent();
            intent.putExtra("VIEW_PAGER", 3);
            setResult(RESULT_OK, intent);
            finishActivity(2017);
//            NavUtils.navigateUpTo(this, intent);
        }

        if (item.getItemId() == R.id.action_finish) {
            if (MyToolBox.isNetworkAvailable(EditProfileActivity.this)) {
                if (!MyToolBox.isMinimumCharacters(mFullName.getText().toString().trim(), 3)) {
                    mFullNameLayout.setError("Type your full name");
                }
                else {
                    mFullNameLayout.setErrorEnabled(false);
                    hideKeyboard();
                    editProfile();
                }
            } else {
                Toast.makeText(EditProfileActivity.this, "Network Error. Check your connection", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTags(List<String> selectedTags) {
        allTags = selectedTags;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<selectedTags.size();i++){
            String string = selectedTags.get(i);
            stringBuilder.append(string);
            if (i != (selectedTags.size() - 1)) {
                stringBuilder.append(",");
            }
        }
        mIndustry.setText(stringBuilder.toString());
    }
}
