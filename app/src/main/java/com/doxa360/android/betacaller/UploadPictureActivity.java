package com.doxa360.android.betacaller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.billingclient.api.Purchase;
//import com.android.vending.billing.IInAppBillingService;
//import com.doxa360.android.betacaller.billing.IabHelper;
import com.doxa360.android.betacaller.helpers.HollaNowSharedPref;
import com.doxa360.android.betacaller.helpers.ImageResizer;
import com.doxa360.android.betacaller.helpers.MyToolBox;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.doxa360.android.betacaller.helpers.MyToolBox.isExternalStorageAvailable;

/**
 * Created by user on 10/17/2017.
 */

public class UploadPictureActivity extends AppCompatActivity {
    private Button upload;
    private RadioButton radioButton;
    private static  ColorPicker cp;
    private Button okColor;
    private EditText caption;
    private TextView imagetext, imageText_post3;
    private ImageView mPhoto;
    protected Uri mMediaUri;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int CHOOSE_PHOTO_REQUEST = 1;
    private File mediaFile = null;
    public static final int MEDIA_TYPE_VIDEO = 5;
    private HollaNowApiInterface hollaNowApiInterface;
    private static final int BILLING_RESPONSE_RESULT_OK = 0;



    private HollaNowSharedPref mSharedPref;

    private int selectedColorR, selectedColorG, selectedColorB, selectColorRGB;

    public  static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB

    private static final String TAG = "UploadPictureActivity";
    private int mColor;

    private int no_of_upload;

    private static boolean flag_google_intent = false;


    private  ClickableSpan goPro;


    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           // Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
           // startActivity(intent);
                    cameraChoices();

        }
    };

    private ProgressDialog mProgressDialog;
    private byte[] fileBytes;

    //IInAppBillingService mService;
    ServiceConnection mServiceConn;
    private static final char[] symbols = new char[36];
    static{
        for(int idx = 0; idx<10; ++idx)
            symbols[idx] = (char) ('0' + idx);
        for(int idx = 10; idx< 36; ++idx)
            symbols[idx] = (char) ('a' + idx - 10);
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_service);
        upload = (Button) findViewById(R.id.upload_post);
        radioButton = (RadioButton) findViewById(R.id.radio_text);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(fabClickListener);
        final Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        final Intent charge = getIntent();
         String eCharge = charge.getStringExtra("billing");
        flag_google_intent = false;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing...");

        mPhoto = (ImageView) findViewById(R.id.post_imageview) ;
        hollaNowApiInterface = HollaNowApiClient.getClient().create(HollaNowApiInterface.class);
        mSharedPref = new HollaNowSharedPref(this);


        caption = (EditText) findViewById(R.id.EditText_caption_post);
        imagetext = (TextView) findViewById(R.id.image_text);
        imageText_post3 = (TextView) findViewById(R.id.text_upload_post3);
        if(eCharge != null)
            if(eCharge.equals("gopro")){
                eCharge = null;
                mProgressDialog.show();
                mServiceConn = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                       // mService = IInAppBillingService.Stub.asInterface(service);
                        flag_google_intent = true;
                       // new  CheckForBilling().execute();
                        Log.e("UploadPictureActivity", "Binding services here ok: "+name.getPackageName() +" ");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                      //  mService = null;
                        Log.e("UploadPictureActivity", "Binding services here disconnected: "+name.getPackageName());
                    }
                };

                serviceIntent.setPackage("com.android.vending");
                bindService(serviceIntent,mServiceConn,Context.BIND_AUTO_CREATE);
            }

         goPro = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mProgressDialog.show();
                 mServiceConn = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                       // mService = IInAppBillingService.Stub.asInterface(service);
                        flag_google_intent = true;
                       // new  CheckForBilling().execute();
                        Log.e("UploadPictureActivity", "Binding services here ok: "+name.getPackageName() +" ");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                      //  mService = null;
                        Log.e("UploadPictureActivity", "Binding services here disconnected: "+name.getPackageName());
                    }
                };

                serviceIntent.setPackage("com.android.vending");
                bindService(serviceIntent,mServiceConn,Context.BIND_AUTO_CREATE);

                //Toast.makeText(UploadPictureActivity.this, "Update to use this service.", Toast.LENGTH_LONG).show();
            }
            @Override
             public void updateDrawState(TextPaint ds){
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

      //  makeLinks(imageText_post3, new String("GoPro"),goPro);

        imagetext.setText("Enter Text...");
        cp = new ColorPicker(UploadPictureActivity.this, 0, 0, 0);
        cp.show();
        okColor = (Button) cp.findViewById(R.id.okColorButton);
        cp.dismiss();

        caption.addTextChangedListener(new TextWatcher() {

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

                imagetext.setText(caption.getText().toString());
              //  Log.e("EmojoActivity", "Character emoji "+mEditEmojicon.getText().toString().trim());
            }
        });


    /**    caption.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.FLAG_FALLBACK)
                    imagetext.setText(caption.getText().toString());
                if(keyCode == KeyEvent.FLAG_CANCELED)
                    imagetext.setText(caption.getText().toString());
                if(keyCode == KeyEvent.KEYCODE_ENTER)
                    imagetext.setText(caption.getText().toString());
                if(keyCode == KeyEvent.KEYCODE_BACK)
                    imagetext.setText(caption.getText().toString());
                if(keyCode == KeyEvent.KEYCODE_DEL)
                    imagetext.setText(caption.getText().toString());

               // else if(keyCode == )
                return false;
            }
        }); **/

        try {
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  cp.show();
                    selectedColorR = cp.getRed();
                    selectedColorB = cp.getBlue();
                    selectedColorG = cp.getGreen();
                    selectColorRGB = cp.getColor();
                    radioButton.setTextColor(selectColorRGB);
                    caption.setTextColor(selectColorRGB);
                    imagetext.setTextColor(selectColorRGB);
                    mColor = selectColorRGB;

                    cp.dismiss();
                }
            });
        }catch(Exception e){
            Log.e("UploadPictureActivity", "Error just occored here : "+e.getMessage());
        }

        radioButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if(cp == null) {
                                                   cp = new ColorPicker(UploadPictureActivity.this, 0, 0, 0);
                                                   cp.show();
                                               }else
                                                   cp.show();;
                                              // okColor = (Button) cp.findViewById(R.id.okColorButton);
                                           }
                                       });

        // no_of_upload = getIntent().getExtras().getInt("count");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String color = String.valueOf(mColor);
              //  if(no_of_upload != -1 && no_of_upload >= 0) {
                try {
                    Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                    intent.putExtra("picture", mediaFile);
                    intent.putExtra("picture_name", mediaFile.getName());
                    intent.putExtra("color", selectColorRGB);
                    intent.putExtra("content", imagetext.getText());
                    intent.putExtra("count2", no_of_upload);
                    startActivity(intent);
                }catch(NullPointerException ex){
                    Toast.makeText(UploadPictureActivity.this, "Upload picture", Toast.LENGTH_LONG).show();
                }
               // }else
               //     Toast.makeText(UploadPictureActivity.this, "Check your network", Toast.LENGTH_LONG).show();
               // Log.e("UploadPictureActivity", "No of uploads : "+no_of_upload);
               // Log.e("UploadPictureActivity", "No of uploads : "+no_of_upload);
               // Log.e("UploadPictureActivity", "No of uploads : "+no_of_upload);

            }
        });

    }


    private void cameraChoices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            //Capture Image
                            Log.e("UploadPictureActivity", "For the case 0 : "+which);
                            Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); //cos of the broadcast...
                            if (mMediaUri == null){
                                Toast.makeText(UploadPictureActivity.this, "There was a problem capturing your photo", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Log.e("UploadPictureActivity", "For the case 0 : "+which);
                                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(captureImageIntent, TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1:
                            //Choose Image
                            Log.e("UploadPictureActivity", "For the case 1 : "+which);
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
                        Toast.makeText(UploadPictureActivity.this,"File: "+Uri.fromFile(mediaFile),Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int responseCode = 0;
        if(resultCode == RESULT_OK){
            if(flag_google_intent)
                 responseCode = data.getIntExtra("RESPONSE_CODE",0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            Log.e("UploadPictureActivity", "response List size >>>>>>>: "+responseCode +" ");
            Log.e("UploadPictureActivity", "response List size >>>>>>>: "+purchaseData +" ");
            Log.e("UploadPictureActivity", "response List size >>>>>>>: "+dataSignature +" ");
        }
        if (resultCode == RESULT_OK){
            mProgressDialog.show();
            //add to gallery
            Log.e(TAG, "Check the Request Code: "+requestCode);
            if (requestCode == CHOOSE_PHOTO_REQUEST){
                if (data == null){
                    Toast.makeText(this,"there was an error",Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri = data.getData();
                    Log.e(TAG, "Is media file null 1: "+mMediaUri.getPath());
                }
                int fileSize = 0;
                InputStream inputStream = null;
                try{
                    inputStream = getContentResolver().openInputStream(mMediaUri);
                    assert inputStream != null;
                    fileSize = inputStream.available();

                }
                catch (FileNotFoundException e){
                    Toast.makeText(UploadPictureActivity.this,"Error opening image. Please try again.",Toast.LENGTH_LONG).show();
                    return;
                }
                catch (IOException e){
                    Toast.makeText(UploadPictureActivity.this,"Error opening image. Please try again.",Toast.LENGTH_LONG).show();
                    return;
                }
                finally {
                    try{
                        assert inputStream != null;
                        inputStream.close();
                    } catch (IOException e){/*Intentionally blank*/ }
                }

                if (fileSize >= FILE_SIZE_LIMIT){
                    Toast.makeText(UploadPictureActivity.this,"The selected image is too large. Please choose another image.",Toast.LENGTH_LONG).show();
                    return;
                }


                fileBytes = getByteArrayFromFile(UploadPictureActivity.this, mMediaUri);
                if(mediaFile==null) {
                    //create mediaFile
                    Log.e(TAG, "creating media file to write chosen image into");
                    mediaFile = createFile(MEDIA_TYPE_IMAGE);
                    Log.e(TAG, "Is media file null 2: "+mMediaUri.getPath());
                    Log.e(TAG, "media file succesfully written");
                    mProgressDialog.dismiss();


                }else
                    mediaFile = createFile(MEDIA_TYPE_IMAGE);
                mProgressDialog.dismiss();
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
                        //uploadFile(mediaFile); ///////////////////////////////////////////////////////////////
                        Picasso.with(UploadPictureActivity.this)
                                .load(mediaFile)
                                .placeholder(R.drawable.wil_profile)
                                .error(R.drawable.wil_profile)
                                .into(mPhoto);
                    } else {
                        MyToolBox.AlertMessage(this, "Oops", "Network Error. Please check your connection");
                    }
                }



            }
            else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                Log.e(TAG, "From the Else Statement");
                Log.e(TAG, "From the Else Statement");
                Log.e(TAG, "From the Else Statement");

                /*
                 So it begins... ahhhhhhh!
                */
                try {
                    fileBytes = getByteArrayFromFile(this, mMediaUri);
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
                    } catch (OutOfMemoryError memoryError) {
                        Toast.makeText(UploadPictureActivity.this, memoryError.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(UploadPictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } finally {
                    if (MyToolBox.isNetworkAvailable(this)) {
//                    postProfilePhoto();
                        //TODO: do both here - upload file using mediaFile global variable ?
                        Log.e(TAG, "... Captured... Finally... ");
                        //uploadFile(mediaFile);  ///////////////////////////////////////////////////////
                        Picasso.with(UploadPictureActivity.this)
                                .load(mediaFile)
                                .placeholder(R.drawable.wil_profile)
                                .error(R.drawable.wil_profile)
                                .into(mPhoto);
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

    private byte[] getByteArrayFromFile(Context context, Uri uri) {
        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
       // Log.e(TAG, uri.getScheme());

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
            String username = mSharedPref.getCurrentUser().getUsername();


            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path+"IMG_"+username+timestamp+".jpg");

            }
            else if (mediaType == MEDIA_TYPE_VIDEO){
                mediaFile = new File(path+"VID_"+username+timestamp+".mp4");
            }
            else{
                return null;
            }
            Toast.makeText(UploadPictureActivity.this,"File: "+Uri.fromFile(mediaFile),Toast.LENGTH_LONG).show();
            return mediaFile;
        }
        else{
            return null;
        }
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
    public void onDestroy(){
        super.onDestroy();
      //  if(mService != null)
         //   if(mServiceConn != null)
        //    unbindService(mServiceConn);
        Log.e("UploadPictureActivity", "OnDestroy: " +" ");
        Log.e("UploadPictureActivity", "OnDestroy: " +" ");
       // Log.e("UploadPictureActivity", "OnDestroy: " +" ");
       // Log.e("UploadPictureActivity", "OnDestroy: " +" ");

    }

    public void makeLinks(TextView textView, String links, ClickableSpan clickableSpan){
        SpannableString spannableString  = new SpannableString(textView.getText());
        ClickableSpan mClickableSpan = clickableSpan;
        String mLinks = links;
        int startIndexOfLink = textView.getText().toString().indexOf(mLinks);
        spannableString.setSpan(mClickableSpan,startIndexOfLink,startIndexOfLink + mLinks.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString,TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

   /** private class CheckForBilling extends AsyncTask<Void, Integer,String> {
        int response = 0;
        RandomString randomString = new RandomString(36);
        String sku = null;
        static final int REQUEST_CODE = 1001;

**/

      /**  @Override
        protected  String doInBackground(Void... argo) {
            Log.e("UploadPictureActivity", "RandomString>>>>: "+randomString.nextString() +" ");
            String payLoad = randomString.nextString();
            Log.e("UploadPictureActivity", "Random generated Payload >>>>>>>>: "+payLoad +" ");
            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add("com.hollanow.doxa360.services");
            skuList.add("com.android.doxa360.holla.subscription1");
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);


            try {
                Bundle skulDetails = mService.getSkuDetails(3,getPackageName(), "subs", querySkus);
                int response = skulDetails.getInt("RESPONSE_CODE");
                if(response == BILLING_RESPONSE_RESULT_OK){
                    mProgressDialog.dismiss();
                    ArrayList<String> responseList = skulDetails.getStringArrayList("DETAILS_LIST");
                   // Log.e("UploadPictureActivity", "response List size >>>>>>>: "+responseList.size() +" ");
                   // Log.e("UploadPictureActivity", "response List size >>>>>>>: "+responseList.size() +" ");
                  //  Log.e("UploadPictureActivity", "response List size >>>>>>>: "+responseList.size() +" ");
                 //   Log.e("UploadPictureActivity", "response List size >>>>>>>: "+responseList.size() +" ");
                    for(String thisResponse : responseList){
                        JSONObject object = new JSONObject(thisResponse);
                         sku = object.getString("productId");
                        String price = object.getString("price");
                      //  Log.e("UploadPictureActivity", "response List size >>>>>>>: "+sku +" ");
                       // Log.e("UploadPictureActivity", "response List size >>>>>>>: "+sku +" ");
                      //  Log.e("UploadPictureActivity", "response List size >>>>>>>: "+price +" ");
                    }


                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED){
                    Toast.makeText(UploadPictureActivity.this,"Failure to consume since item is not owned",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED){
                    Toast.makeText(UploadPictureActivity.this,"Operation Canceled by You",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE){
                    Toast.makeText(UploadPictureActivity.this,"Network connection is down",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE){
                    Toast.makeText(UploadPictureActivity.this,"Requested product is not available for purchase",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR){
                    Toast.makeText(UploadPictureActivity.this,"Configuration error",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_ERROR){
                    Toast.makeText(UploadPictureActivity.this,"Fatal Error Occured",Toast.LENGTH_LONG).show();
                }else if(response == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
                    Toast.makeText(UploadPictureActivity.this,"Item is already owned",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(UploadPictureActivity.this,"No response from server",Toast.LENGTH_LONG).show();
                }

                Bundle buyIntentBundle = mService.getBuyIntent(3,getPackageName(),sku,"subs",payLoad);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(),REQUEST_CODE,
                        new Intent(),Integer.valueOf(0),Integer.valueOf(0),Integer.valueOf(0));








               // Log.e("UploadPictureActivity", "This is Successful here: "+response +" ");
            }catch (RemoteException ex){
                Log.e("UploadPictureActivity", "Error occured at Remote Exception: "+ex.getMessage() +" ");
            }catch(JSONException a){
                Log.e("UploadPictureActivity", "Error occured at JSON Remote Exception: "+a.getMessage() +" ");
            }catch(IntentSender.SendIntentException e){
                Log.e("UploadPictureActivity", "Error occured at Send Intent Exception: "+e.getMessage() +" ");
            }
            return "finish";
        } **/
/**
        @Override
        public   void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    } **/

    public class RandomString{
        private final Random random = new Random();
        private final char[] buf;
        public RandomString(int length){
            if(length < 1)
                throw new IllegalArgumentException("length < 1: "+length);
            buf = new char[length];
        }

        public String nextString(){
            for(int idx = 0; idx < buf.length;++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }

    public final class SessionIdentifierGenerator{
        private SecureRandom random = new SecureRandom();
        public String nextSessionId(){
            return new BigInteger(130,random).toString(32);
        }
    }


}
