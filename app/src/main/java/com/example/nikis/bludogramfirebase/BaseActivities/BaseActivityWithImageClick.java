package com.example.nikis.bludogramfirebase.BaseActivities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.GlideEngineV4;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class BaseActivityWithImageClick extends AppCompatActivity{
    public static final String TAG_TEST = "BG_test";
    protected static final String KEY_IMAGE_PATH = "imagePath";
    private static final String KEY_IS_CIRCULAR_IMAGE = "isCircularImage";
    ImageView imageView;
    String imagePath;
    boolean isCircular;
    protected static final int REQUEST_CODE_CHOOSE = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            imagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
            isCircular = savedInstanceState.getBoolean(KEY_IS_CIRCULAR_IMAGE);
        }
    }

    protected void imageViewClick(@NonNull ImageView imageView, boolean isCircular){
        this.imageView = imageView;
        this.isCircular = isCircular;

        ActivityCompat.requestPermissions(this,
                new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},1);
    }

    protected String getSelectedImagePath(){
        return imagePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker();
            }

        }
    }

    private void startImagePicker(){
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.9f)
                .imageEngine(new GlideEngineV4())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);

            imagePath = getRealPathFromURIPath(mSelected.get(0));
            setImage();
        }
    }
    private String getRealPathFromURIPath(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    protected void setImageWithSavedParamIfPathSelected(@NonNull ImageView imageView){
        Log.d(TAG_TEST, "setImageWithSavedParamIfPathSelected: ");
        this.imageView = imageView;
        if(imagePath != null)
            setImage();
    }
    private void setImage(){
        if(isCircular)
            setImageToCircularImageView();
        else setImageToImageView();
    }

    private void setImageToCircularImageView(){
        GlideApp.with(this)
                .load(imagePath)
                //.asBitmap()
                .override(1080,1080)
                .circleCrop()
                .into(imageView);
                /*.into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });*/

    }
    private void setImageToImageView(){
        GlideApp.with(this)
                .load(imagePath)
               // .asBitmap()
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IMAGE_PATH, imagePath);
        outState.putBoolean(KEY_IS_CIRCULAR_IMAGE, isCircular);
    }
}