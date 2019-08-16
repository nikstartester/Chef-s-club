package com.xando.chefsclub.Recipes.Upload;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.DataWorkers.ParcResourceBySerializable;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Images.ImageUploader;
import com.xando.chefsclub.Recipes.Repository.RecipeRepository;

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
        public void onStatusChanged(ParcResourceBySerializable<String> resStoragePath, int tag) {
            if (resStoragePath.status == ParcResourceByParc.Status.SUCCESS) {
                if (isNeedStop(resStoragePath, tag)) {
                    RecipeRepository.deleteImage(resStoragePath.data);
                } else {
                    addDataOnSuccess(resStoragePath.data, tag);

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

        protected abstract void addDataOnSuccess(String path, int tag);

        protected abstract boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath, int tag);
    }
}
