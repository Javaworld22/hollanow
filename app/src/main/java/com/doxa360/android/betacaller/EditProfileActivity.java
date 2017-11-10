package com.doxa360.android.betacaller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
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

import com.crashlytics.android.beta.Beta;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.doxa360.android.betacaller.model.User;
import com.facebook.CallbackManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.doxa360.android.betacaller.helpers.ImageResizer.calculateInSampleSize;
import static com.doxa360.android.betacaller.helpers.MyToolBox.isExternalStorageAvailable;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    public static final int SHORT_SIDE_TARGET = 600;//1280;
    private FloatingActionButton fab;
//    private Button mEdit;
    private EditText mFullName, mAbout, mOccupation, mAddress;
    private TextView mUsername;
    private ImageView mPhoto;
    private ImageView mChangePhoto;
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


    private File mediaFile = null;

    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            //Capture Image
                            Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //cos of the broadcast...
                            if (mMediaUri == null){
                                Toast.makeText(EditProfileActivity.this, "There was a problem capturing your photo", Toast.LENGTH_LONG).show();
                            }
                            else{
                                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(captureImageIntent, TAKE_PHOTO_REQUEST);
                             }
                            break;
                        case 1:
                            //Choose Image
                            Intent chooseImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseImageIntent.setType("image/*");
                            //Toast.makeText(getActivity(), "The size of your video must be less than 10MB", Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseImageIntent, CHOOSE_PHOTO_REQUEST);
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
                        //mediaFile;
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
    private User currentUser;
    private HollaNowApiInterface hollaNowApiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        fab = (FloatingActionButton) findViewById(R.id.fab);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");

        getSupportActionBar().setTitle("Edit Profile");


        mUsername = (TextView) findViewById(R.id.user_name);
        mFullName = (EditText) findViewById(R.id.full_name);
        mFullNameLayout = (TextInputLayout) findViewById(R.id.layout_full_name);
        mAbout = (EditText) findViewById(R.id.about);
        mOccupation = (EditText) findViewById(R.id.occupation);
        mAddress = (EditText) findViewById(R.id.address);
       // mIndustry = (EditText) findViewById(R.id.industry);
//        mEdit = (Button) findViewById(R.id.edit);
        mChangePhoto = (ImageView) findViewById(R.id.change_photo);
        mPhoto = (ImageView) findViewById(R.id.photo);

        mShareLayout = (LinearLayout) findViewById(R.id.shareLayout);
        mProCard = (CardView) findViewById(R.id.pro_card);

        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);

        mSharedPref = new HollaNowSharedPref(this);
        currentUser = mSharedPref.getCurrentUser();


        mUsername.setText(currentUser.getEmail());
        mFullName.setText(currentUser.getName());


//        mAbout.setText((!currentUser.getAbout().isEmpty())?currentUser.getAbout():"-");
//        mAddress.setText((!currentUser.getAddress().isEmpty())?currentUser.getAddress():"-");
//        mOccupation.setText((!currentUser.getOccupation().isEmpty())?currentUser.getOccupation():"-");
//        mIndustry.setText((!currentUser.getIndustry().isEmpty())?currentUser.getIndustry():"-");

        mAbout.setText((currentUser.getAbout() != null)?currentUser.getAbout():"-");
        mAddress.setText((currentUser.getAddress() != null)?currentUser.getAddress():"-");
        mOccupation.setText((currentUser.getOccupation() != null)?currentUser.getOccupation():"-");
       // mIndustry.setText((currentUser.getIndustry() != null)?currentUser.getIndustry():"-");//needs to uncomment


        if (currentUser.getProfilePhoto() != null) {
            Picasso.with(EditProfileActivity.this)
                    .load(BetaCaller.PHOTO_URL + currentUser.getProfilePhoto())
                    .into(mPhoto);
        }

//        mEdit.setOnClickListener(editPhoto);
        mPhoto.setOnClickListener(editPhoto);
//        mIndustry should not accept user keyboard
      //  mIndustry.setOnClickListener(new View.OnClickListener() {  //needs to uncomment/////////////////////
       //     @Override
       //     public void onClick(View view) {
       //        FragmentManager fm = getSupportFragmentManager();
        //        AddIndustryFragment fragment = new AddIndustryFragment();
      //          fragment.show(fm, "INDUSTRY");
      //      }
     //   });


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
            currentUser.setName(mFullName.getText().toString());
        }
        if (!mAbout.getText().toString().trim().isEmpty()) {
            currentUser.setAbout(mAbout.getText().toString());
        }
        if (!mAddress.getText().toString().trim().isEmpty()) {
            currentUser.setAddress(mAddress.getText().toString());
        }
        if (!mOccupation.getText().toString().trim().isEmpty()) {
            currentUser.setOccupation(mOccupation.getText().toString());
        }
       // if (!mIndustry.getText().toString().trim().isEmpty()) {
        //    currentUser.setIndustry(mIndustry.getText().toString());  //needs to uncomment
       // }


        mSharedPref.setCurrentUser(currentUser.toString());
        Log.e("EDITED_USER=> ", currentUser.toString());
        Call<User> call = hollaNowApiInterface.editUserProfile(currentUser, mSharedPref.getToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    Log.e(TAG, "success "+ response.body().toString());
                    Toast.makeText(EditProfileActivity.this, "Profile successfully updated", Toast.LENGTH_SHORT).show();
                    FaceBookAdvertFragment faceBookAdvertFragment = new FaceBookAdvertFragment();
                    //returnHome();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Bundle args = new Bundle();
                    faceBookAdvertFragment.setArguments(args);
                   // finishActivity(12345);
                    faceBookAdvertFragment.show(fragmentManager, "FACEBOOK_NOTE");
                    finishActivity(12345);
                    returnHome();
//                    finish();
//                    onBackPressed();
                } else {
                    Log.e(TAG, "error: " + response.code() + response.message());
                    Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                Log.e(TAG, "failed: "+t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Network error. Try again", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            mProgressDialog.show();
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


                fileBytes = getByteArrayFromFile(EditProfileActivity.this, mMediaUri);
                if(mediaFile==null) {
                    //create mediaFile
                    Log.e(TAG, "creating media file to write chosen image into");
                    mediaFile = createFile(MEDIA_TYPE_IMAGE);
                    Log.e(TAG, "media file succesfully written");


                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mediaFile);
                    fos.write(fileBytes);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (MyToolBox.isNetworkAvailable(this)) {
                        //TODO: do both here - upload file using mediaFile global variable ?
                        uploadFile(mediaFile);
                    } else {
                        MyToolBox.AlertMessage(this, "Oops", "Network Error. Please check your connection");
                    }
                }



            }
            else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);

                /*
                 So it begins... ahhhhhhh!
                */
                try {
                    fileBytes = getByteArrayFromFile(this, mMediaUri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
                    } catch (OutOfMemoryError memoryError) {
                        Toast.makeText(EditProfileActivity.this, memoryError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (bitmap != null) {
                        try {
                            bitmap = rotateImageIfRequired(bitmap, mMediaUri);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            FileOutputStream fos = new FileOutputStream(mediaFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                            Log.e(TAG, "media file succesfully written on Captured...");

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } finally {
                    if (MyToolBox.isNetworkAvailable(this)) {
//                    postProfilePhoto();
                        //TODO: do both here - upload file using mediaFile global variable ?
                        Log.e(TAG, "... Captured... Finally... ");
                        uploadFile(mediaFile);
                    } else {
                        MyToolBox.AlertMessage(this, "Oops", "Network Error. Please check your connection");
                    }
                }

            }

//            mProgressDialog.show();
            if (mediaFile != null ) { // captured image

            } else { //

            }

        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this,"There was an error saving your photo",Toast.LENGTH_LONG).show();
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
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
//        Intent intent = new Intent();
//        intent.putExtra("VIEW_PAGER", 3);
//        setResult(RESULT_OK, intent);
//        finishActivity(2016);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent();
            intent.putExtra("VIEW_PAGER", 3);
            setResult(RESULT_OK, intent);
            finishActivity(2017);
        }

        if (item.getItemId() == R.id.action_finish) {
            if (MyToolBox.isNetworkAvailable(EditProfileActivity.this)) {
                if (!MyToolBox.isMinimumCharacters(mFullName.getText().toString().trim(), 5)) {
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

    private void returnHome(){
        Intent intent = new Intent();
        intent.putExtra("VIEW_PAGER", 3);
        setResult(RESULT_OK, intent);
        finishActivity(2017);
    }

    public void updateIndustry(String selectedIndustry) {
        mIndustry.setText(selectedIndustry);
    }

    //Captured mage manipulation - scale and rotation

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 320;
        int MAX_WIDTH = 320;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        if (imageStream != null) {
            imageStream.close();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);

        return img;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void uploadFile(File photo) {
        MultipartBody.Part body = null;
        try {
    RequestBody requestFile =
            RequestBody.create(MediaType.parse("multipart/form-data"), photo);

     body =
            MultipartBody.Part.createFormData("photo", photo.getName(), requestFile);
}catch (NullPointerException e){
            Log.e(TAG, "Error Occured Here: "+e.getMessage());
}

        final String fileName = photo.getName();
        RequestBody fileNameBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fileName);

        String token = mSharedPref.getToken();

        Call<ResponseBody> call = hollaNowApiInterface.uploadProfilePhoto(token, fileNameBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //TODO: set photo to imageview, toast success
                if (response.code() == 200) {
                    Picasso.with(EditProfileActivity.this)
                            .load(mediaFile)
                            .into(mPhoto);
                    Toast.makeText(EditProfileActivity.this, "Profile photo successfully updated ", Toast.LENGTH_SHORT).show();
                    currentUser.setProfilePhoto(fileName);
                    mSharedPref.setCurrentUser(currentUser.toString());
                } else {
                    Toast.makeText(EditProfileActivity.this, "Could not update profile photo", Toast.LENGTH_SHORT).show();
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e(TAG, "Retrofit: "+ t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Error uploading photo. Try again", Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
            }
        });

    }

    private File createFile(int mediaType) {
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
            //mediaFile;
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
            return mediaFile;
        }
        else{
            return null;
        }
    }


}
