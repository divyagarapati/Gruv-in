package com.tcst.android.gruvin.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.tcst.android.gruvin.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Prasanthi on 15-12-2016.
 */
public class SelectImagesFromGallery extends AppCompatActivity {
    private static final String TAG = "SelectImagesFromGallery";
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 4;
    private ImageView image;
    private VideoView video;
    private Uri selectedImage,selectedVideo;
    private String imagePath, filePathInMobile;
    private Bitmap thumbnail;
    public LinearLayout eImage,eVideo;
    public static final int REQUEST_CAMERA = 2;
    public static final int SELECT_FILE = 3;
    private Button btnAddImage, btnAddVideo;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectfromgallery);
        btnAddImage = (Button) findViewById(R.id.btn_addimages);
        btnAddVideo = (Button) findViewById(R.id.btn_addvideos);
        eImage = (LinearLayout) findViewById(R.id.eImage);
        eVideo = (LinearLayout)findViewById(R.id.eVideo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        video = new VideoView(this);
        video.setLayoutParams(new android.view.ViewGroup.LayoutParams(160,160));
        eVideo.addView(video);
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clcikOnUploadVideo();
            }
        });
        image = new ImageView(this);
        image.setLayoutParams(new android.view.ViewGroup.LayoutParams(160, 160));
        eImage.addView(image);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnUploadImage();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "imageview clicked path:" + view.getId());
            }
        });
        verifyStoragePermissions(this);

    }

    private void clickOnUploadImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  /*  intent.setType("image*//*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);*/
                    startActivityForResult(Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                selectedImage = data.getData();
                thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    Log.d(TAG, "onActivityResult:" + destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(thumbnail);
                image = new ImageView(this);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                        140, 140));
                eImage.addView(image);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(this, thumbnail);
                filePathInMobile = getPath(tempUri);

                Log.d(TAG, "camera image path" + filePathInMobile);
            } else if (requestCode == SELECT_FILE) {
                filePathInMobile = null;
                selectedImage = data.getData();
                filePathInMobile = getPath(selectedImage);
                try {
                    image.setImageBitmap(decodeUri(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                image = new ImageView(this);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                        140, 140));
                eImage.addView(image);
                Log.d(TAG, "gallery image path " + filePathInMobile);
            } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                    filePathInMobile = null;
                    selectedVideo = data.getData();
                    filePathInMobile = getPath(selectedVideo);
                video = new VideoView(this);
                video.setLayoutParams(new android.view.ViewGroup.LayoutParams(140,140));
                eVideo.addView(video);

            }

        }
    }
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    } // getImageUri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);
        return cursor.getString(column_index);
    } // getPath
    private void clcikOnUploadVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
