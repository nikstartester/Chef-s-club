package com.xando.chefsclub.recipes.repository.local;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.StorageReference;
import com.xando.chefsclub.App;
import com.xando.chefsclub.dataworkers.BaseLocalDataSaver;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.GlideApp;
import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.data.StepOfCooking;
import com.xando.chefsclub.recipes.db.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class LocalRecipeSaver extends BaseLocalDataSaver<RecipeData> {

    private final boolean isSaveImage;

    private String mTime;

    public LocalRecipeSaver(Application application) {
        this(application, false);
    }

    public LocalRecipeSaver(Application application, boolean isSaveImage) {
        super(application);
        this.isSaveImage = isSaveImage;
    }

    @Override
    public void save(@NonNull RecipeData data) {
        RecipeEntity entity = new RecipeEntity(data);

        startThreadToInsertData(entity);

        if (isSaveImage) {
            mTime = Long.toString(data.dateTime);

            saveAllImages(getAllImagesPaths(data));
        }
    }

    private void startThreadToInsertData(RecipeEntity recipeEntity) {
        new Thread(() -> {
            ((App) mApplication)
                    .getDatabase()
                    .recipeDao()
                    .insert(recipeEntity);

            if (mOnComplete != null) mOnComplete.onComplete();
        }).start();
    }

    private List<String> getAllImagesPaths(RecipeData recipeData) {

        List<String> allImages = new ArrayList<>(recipeData.overviewData.allImagePathList);

        for (StepOfCooking step : recipeData.stepsData.stepsOfCooking) {
            allImages.add(step.imagePath);
        }

        return allImages;
    }

    private void saveAllImages(List<String> imagePaths) {
        for (String path : imagePaths) {
            if (path != null) {
                saveImageOnDisk(path);
            }
        }
    }

    private void saveImageOnDisk(String imageUrl) {
        StorageReference storageReference = FirebaseReferences.getStorageReference(imageUrl);

        GlideApp.with(mApplication)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(mTime))
                .submit();
    }
}
