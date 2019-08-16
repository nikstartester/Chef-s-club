package com.xando.chefsclub.Images;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.ParcResourceBySerializable;
import com.xando.chefsclub.FirebaseReferences;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static com.xando.chefsclub.Images.ImageUploader.Builder.DEF_TAG;

public class ImageUploader {

    private static final String TAG = "ImageUploader";

    public int tag = DEF_TAG;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;
    private Context mContext;
    private String mImagePath;
    private String mFullStoragePath;
    private int mQuality = 0;
    private OnProgressListener<String> mOnProgressListener;
    private String mDirectoryPathForCompress;
    private boolean isCanceled;
    private boolean isSuccessful;

    private ImageUploader() {

    }

    public static Builder with(Context context) {
        ImageUploader imageUploader = new ImageUploader();

        imageUploader.mContext = context;

        return imageUploader.new Builder();
    }

    public void startUpload() {
        if (mImagePath.equals(mFullStoragePath) ||
                mImagePath.startsWith(Constants.ImageConstants.FIREBASE_STORAGE_AT_START)) {

            onProgress(ParcResourceBySerializable.success(mImagePath));

            return;
        }

        //Log.d(TAG, "startUpload: " + mImagePath + "  " + mFullStoragePath);

        onProgress(ParcResourceBySerializable.loading(mFullStoragePath));

        if (mQuality != 100) {
            File compressedFile;
            try {
                compressedFile = compressImage();
            } catch (IOException e) {
                onProgress(ParcResourceBySerializable.error(e, mFullStoragePath));

                return;
            }

            startUploadImageTask(compressedFile);

        } else {
            startUploadImageTask(new File(mImagePath));
        }
    }

    private File compressImage() throws IOException {

        Compressor compressor = new Compressor(mContext);

        if (mDirectoryPathForCompress != null) {
            compressor.setDestinationDirectoryPath(mDirectoryPathForCompress);
        }

        String name = "CMPS_IMG_" + UUID.randomUUID().toString() + ".jpg";

        return compressor.setQuality(mQuality).compressToFile(new File(mImagePath), name);
    }

    private void startUploadImageTask(File file) {

        StorageReference storageReference = FirebaseReferences.getStorageReference();

        final Uri fileUri = Uri.fromFile(file);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image")
                .build();

        mUploadTask = storageReference.child(mFullStoragePath)
                .putFile(fileUri, metadata).addOnCompleteListener(task -> {
                    isSuccessful = task.isSuccessful();
                    if (task.isSuccessful()) {
                        onProgress(ParcResourceBySerializable.success(mFullStoragePath));
                    } else if (!task.isCanceled()) {
                        onProgress(ParcResourceBySerializable.error(task.getException(), mFullStoragePath));
                    }
                }).addOnCanceledListener(() -> {
                    onProgress(ParcResourceBySerializable.error(new CancelUploadImage(), mFullStoragePath));
                });
    }

    private void onProgress(ParcResourceBySerializable<String> resStoragePath) {
        if (mOnProgressListener != null)
            mOnProgressListener.onStatusChanged(resStoragePath, tag);
    }

    public void cancel() {
        isCanceled = true;
        mUploadTask.cancel();
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public interface OnProgressListener<T extends Serializable> {
        void onStatusChanged(ParcResourceBySerializable<T> resStoragePath, int tag);
    }

    public static class CancelUploadImage extends Exception {
        public CancelUploadImage() {
            super("Cancel upload image");
        }
    }

    public class Builder {
        public static final int NORMAL_QUALITY = 75;
        public static final int MAX_QUALITY = 100;
        public static final int DEF_TAG = -9999;

        private String mStoragePath;

        private String mImageName;

        private Builder() {

        }

        public Builder setImagePath(String imagePath) {
            mImagePath = imagePath;

            return this;
        }

        public Builder setStoragePath(String storagePath) {
            mStoragePath = storagePath;

            return this;
        }

        public Builder setFullStoragePath(String fullStoragePath) {
            mFullStoragePath = fullStoragePath;

            return this;
        }

        public Builder setDirectoryPathForCompress(String directoryPathForCompress) {
            mDirectoryPathForCompress = directoryPathForCompress;

            return this;
        }

        Builder setImageName(String imageName) {
            mImageName = imageName;

            return this;
        }

        public Builder setQuality(int quality) {
            mQuality = quality;

            return this;
        }

        public Builder setTag(int tag) {
            ImageUploader.this.tag = tag;

            return this;
        }

        public Builder setOnProgressListener(OnProgressListener<String> onProgressListener) {
            mOnProgressListener = onProgressListener;

            return this;
        }

        public ImageUploader build() {
            if (mQuality > 100 || mQuality < 1) {
                throw new IllegalArgumentException("Quality must be between from 1 to 100");
            }

            if (mImagePath == null) {
                throw new NullPointerException("ImagePath must not be null!");
            }

            if (mFullStoragePath == null) {

                if (mStoragePath == null) {
                    throw new NullPointerException("StoragePath must not be null if" +
                            " FullStoragePath is null!");
                }

                if (mImageName == null) {
                    setImageName(UUID.randomUUID().toString() + ".jpg");
                }

                mFullStoragePath = mStoragePath + mImageName;
            }

            if (mQuality == 0) setQuality(MAX_QUALITY);

            if (mDirectoryPathForCompress == null)
                mDirectoryPathForCompress = Constants.Files.getDirectoryForCompressFiles(mContext);

            return ImageUploader.this;
        }

    }

}
