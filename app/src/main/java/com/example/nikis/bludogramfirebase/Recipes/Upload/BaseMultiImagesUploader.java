package com.example.nikis.bludogramfirebase.Recipes.Upload;

import android.content.Context;

import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceBySerializable;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Images.ImageUploader;
import com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;

abstract class BaseMultiImagesUploader {
    final Context context;

    BaseMultiImagesUploader(Context context) {
        this.context = context;
    }

    protected abstract void uploadImages();

    protected abstract void updateImagesUrlIfAllImagesUploaded();

    protected void updateImagesUrl() {
        Map<String, Object> childUpdates = createImageURLChildUpdates();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

        myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                onSuccessUpload();
            } else {
                onErrorUpload(task.getException());
            }
        });
    }

    protected abstract Map<String, Object> createImageURLChildUpdates();

    protected abstract void onSuccessUpload();

    protected abstract void onErrorUpload(Exception ex);

    protected abstract void cancelUpload();

    public abstract List<String> getUploadedUrls();

    abstract class OnProgressListener implements ImageUploader.OnProgressListener<String> {

        @Override
        public void onStatusChanged(ParcResourceBySerializable<String> resStoragePath) {
            if (resStoragePath.status == ParcResourceByParc.Status.SUCCESS) {
                if (isNeedStop(resStoragePath)) {
                    RecipeRepository.deleteImage(resStoragePath.data);
                } else {
                    addDataOnSuccess(resStoragePath.data);

                    updateImagesUrlIfAllImagesUploaded();
                }

            } else if (resStoragePath.status == ParcResourceByParc.Status.ERROR) {
                if (resStoragePath.exception instanceof ImageUploader.CancelUploadImage) {
                               /*
                               nothing
                                */
                } else {
                    onErrorUpload(resStoragePath.exception);
                }
            }
        }

        protected abstract void addDataOnSuccess(String path);

        protected abstract boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath);
    }
}
