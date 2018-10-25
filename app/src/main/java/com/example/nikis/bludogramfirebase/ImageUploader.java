package com.example.nikis.bludogramfirebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class ImageUploader {
    private static final String TAG = "ImageUploader";

    private Context mContext;

    private String mImagePath;

    private String mFullStoragePath;

    private int mQuality = 0;

    private OnProgressListener<String> mOnProgressListener;

    private ImageUploader(){

    }

    public static Builder with(Context context){
        ImageUploader imageUploader = new ImageUploader();

        imageUploader.mContext = context;

        return imageUploader.new Builder();
    }

    public void startUpload(){
        if(mImagePath.equals(mFullStoragePath)){
            onProgress(ParcelableResource.success(mFullStoragePath));

            return;
        }

        Log.d(TAG, "startUpload: " + mImagePath + "  " + mFullStoragePath);

        onProgress(ParcelableResource.loading(mFullStoragePath));

        if(mQuality != 100){
            startUploadImageTask(compressImage());
        }else {
            startUploadImageTask(new File(mImagePath));
        }
    }


    private File compressImage(){
        File compressedFile = null;
        try {
            compressedFile = new Compressor(mContext)
                    .setQuality(mQuality)
                    .compressToFile(new File(mImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedFile;
    }

    private void startUploadImageTask(File file){
        StorageReference storageReference = FirebaseReferences.getStorageReference();

        Uri fileUri = Uri.fromFile(file);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image")
                .build();

        storageReference.child(mFullStoragePath)
                .putFile(fileUri, metadata).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                onProgress(ParcelableResource.success(mFullStoragePath));
            }else {
                onProgress(ParcelableResource.error(task.getException(), mFullStoragePath));
            }
        });
    }

    private void onProgress(ParcelableResource<String> resStoragePath){
        if(mOnProgressListener != null)
            mOnProgressListener.onStatusChanged(resStoragePath);
    }

    public interface OnProgressListener<T extends Serializable> {
        void onStatusChanged(ParcelableResource<T> resStoragePath);
    }


    public class Builder{
        public static final int NORMAL_QUALITY = 75;

        private String mStoragePath;

        private String mImageName;

        private Builder(){

        }

        public Builder setImagePath(String imagePath) {
            mImagePath = imagePath;

            return this;
        }

        public Builder setStoragePath(String storagePath) {
            mStoragePath = storagePath;

            return this;
        }

        public Builder setFullStoragePath(String fullStoragePath){
            mFullStoragePath = fullStoragePath;

            return this;
        }

        public Builder setImageName(String imageName) {
            mImageName = imageName;

            return this;
        }

        public Builder setQuality(int quality) {
            mQuality = quality;

            return this;
        }

        public Builder setOnProgressListener(OnProgressListener<String> onProgressListener){
            mOnProgressListener = onProgressListener;

            return this;
        }

        public ImageUploader build(){
            if(mQuality > 100 || mQuality < 1) throw new IllegalArgumentException("Quality must be between 1 to 100");

            if (mImagePath == null) throw new NullPointerException("ImagePath must not be null!");

            if(mFullStoragePath == null){

                if (mStoragePath == null) throw  new NullPointerException("StoragePath must not be null if FullStoragePath is null!");

                if(mImageName == null) setImageName(UUID.randomUUID().toString() + "jpg");

                mFullStoragePath = mStoragePath + mImageName;
            }

            if(mQuality == 0) setQuality(NORMAL_QUALITY);

            return ImageUploader.this;
        }

    }

}
