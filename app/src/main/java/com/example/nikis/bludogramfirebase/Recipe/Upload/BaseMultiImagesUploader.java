package com.example.nikis.bludogramfirebase.Recipe.Upload;

import android.content.Context;

import java.util.Map;

public abstract class BaseMultiImagesUploader {
    protected Context context;

    public BaseMultiImagesUploader(Context context) {
        this.context = context;
    }

    protected abstract void uploadImages();

    protected  abstract void updateImagesUrlIfAllImagesUploaded();

    protected  abstract void updateImagesUrl();

    protected  abstract Map<String, Object> createImageURLChildUpdates();

    protected abstract void onSuccessUpload();

    protected abstract void onErrorUpload(Exception ex);
}
