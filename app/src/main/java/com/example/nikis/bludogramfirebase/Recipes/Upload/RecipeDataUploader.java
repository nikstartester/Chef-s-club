package com.example.nikis.bludogramfirebase.Recipes.Upload;

import android.content.Context;

import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.DataUploader;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceBySerializable;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Images.ImageUploader;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeDataUploader extends DataUploader<RecipeData> {
    private static final String TAG = "RecipeDataUploader";

    private final BaseMultiImagesUploader mOverviewImagesUploader;
    private final BaseMultiImagesUploader mStepsImagesUploader;

    private ParcResourceByParc.Status mOverviewImagesUploadStatus, mStepsImagesUploadStatus;

    private boolean isNeedStop = false;

    private boolean isUploadImage = true;

    public RecipeDataUploader(Context context, String[] paths) {
        super(paths);

        mOverviewImagesUploader = new OverviewImagesUploader(context);

        mStepsImagesUploader = new StepsImagesUploader(context);
    }

    public boolean isUploadImage() {
        return isUploadImage;
    }

    public RecipeDataUploader setUploadImage(boolean uploadImage) {
        isUploadImage = uploadImage;

        return this;
    }

    @Override
    protected void start() {
        updateRecipe();
    }

    private void updateRecipe() {
        mDataResource = ParcResourceByParc.loading(mData);

        super.updateProgress(mDataResource);

        updateChildren();
    }

    private void updateChildren() {

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();


        if (mData.recipeKey == null) {
            mData.recipeKey = myRef.child("recipes").push().getKey();
        }

        Map<String, Object> childUpdates = createChildUpdates();

        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                if (isUploadImage) {

                    mDataResource = ParcResourceByParc.loading(mData);
                    super.updateProgress(mDataResource);

                    mOverviewImagesUploader.uploadImages();

                    mStepsImagesUploader.uploadImages();
                } else {
                    mDataResource = ParcResourceByParc.success(mData);

                    updateProgress(mDataResource);
                }
            } else {
                mDataResource = ParcResourceByParc.error(databaseError.toException(), mData);

                updateProgress(mDataResource);
            }
        });

    }

    private Map<String, Object> createChildUpdates() {

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        for (String path : super.mPaths) {
            childUpdates.put(path + mData.recipeKey, postValues);
        }

        return childUpdates;
    }

    @Override
    protected boolean checkRelevance(RecipeData data) {
        if (mData.recipeKey == null || data.recipeKey == null)
            return false;

        return mData.recipeKey.equals(data.recipeKey);

    }

    @Override
    protected void cancel() {
        isNeedStop = true;

        if (isUploadImage) {
            mOverviewImagesUploader.cancelUpload();
            mStepsImagesUploader.cancelUpload();
        }

        if (mData.recipeKey != null) {
            RecipeRepository.deleteRecipe(mData);

            RecipeRepository.deleteImages(mOverviewImagesUploader.getUploadedUrls());
            RecipeRepository.deleteImages(mStepsImagesUploader.getUploadedUrls());
        }

        mDataResource = ParcResourceByParc.error(new Cancel(), mData);

        updateProgress(mDataResource);
    }

    @Override
    public void updateProgress(ParcResourceByParc<RecipeData> resource) {
        if (resource.status == ParcResourceByParc.Status.LOADING) {

            if (mOverviewImagesUploadStatus == ParcResourceByParc.Status.SUCCESS
                    && mStepsImagesUploadStatus == ParcResourceByParc.Status.SUCCESS) {

                mDataResource = ParcResourceByParc.success(mData);

                super.updateProgress(mDataResource);


                return;
            }
        }

        super.updateProgress(resource);
    }

    private class OverviewImagesUploader extends BaseMultiImagesUploader {

        private final List<ImageUploader> mImageUploaders = new ArrayList<>();

        private final List<String> mUploadedImagesUrlWithoutMain;

        private String mUploadedMainUrl;

        private OverviewImagesUploader(Context context) {
            super(context);

            mUploadedImagesUrlWithoutMain = new ArrayList<>();
        }

        @Override
        protected void uploadImages() {

            if (mData.overviewData.imagePathsWithoutMainList.size() == 0
                    && mData.overviewData.mainImagePath == null) {

                onSuccessUpload();

                return;
            }

            if (mData.overviewData.mainImagePath != null) {
                uploadMainImages();
            }

            uploadOverviewImages(mData.overviewData.imagePathsWithoutMainList);
        }

        @Override
        protected void onSuccessUpload() {
            mOverviewImagesUploadStatus = ParcResourceByParc.Status.SUCCESS;

            updateProgress(mDataResource);
        }

        private void uploadMainImages() {

            final String storageImagePath = Constants.ImageConstants.FIREBASE_STORAGE_AT_START + "/recipe_images/"
                    + mData.recipeKey + "/"
                    + "overview_images" + "/" + "main.jpg";

            final ImageUploader imageUploader = ImageUploader.with(context)
                    .setImagePath(mData.overviewData.mainImagePath)
                    .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                    .setFullStoragePath(storageImagePath)
                    .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        protected void addDataOnSuccess(String path) {
                            mUploadedMainUrl = path;
                        }

                        @Override
                        protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath) {
                            return isNeedStop;
                        }
                    }).build();

            mImageUploaders.add(imageUploader);

            imageUploader.startUpload();
        }

        @Override
        protected void onErrorUpload(Exception ex) {
            mDataResource = ParcResourceByParc.error(ex, mData);

            mOverviewImagesUploadStatus = ParcResourceByParc.Status.ERROR;

            updateProgress(mDataResource);

            isNeedStop = true;
        }

        private void uploadOverviewImages(List<String> imagePaths) {
            final String prefixStorageImagePath = Constants.ImageConstants.FIREBASE_STORAGE_AT_START + "/recipe_images/" + mData.recipeKey + "/"
                    + "overview_images" + "/";

            for (int i = 0; i < imagePaths.size(); i++) {

                if (isNeedStop) break;

                String storageImagePath = prefixStorageImagePath + i + ".jpg";

                final ImageUploader imageUploader = ImageUploader.with(context)
                        .setImagePath(imagePaths.get(i))
                        .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                        .setFullStoragePath(storageImagePath)
                        .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            protected void addDataOnSuccess(String path) {
                                mUploadedImagesUrlWithoutMain.add(path);
                            }

                            @Override
                            protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath) {
                                return isNeedStop;
                            }
                        }).build();

                mImageUploaders.add(imageUploader);

                imageUploader.startUpload();
            }

        }

        @Override
        protected void updateImagesUrlIfAllImagesUploaded() {
            if (!isNeedStop && (mUploadedMainUrl != null || mData.overviewData.mainImagePath == null)
                    && mUploadedImagesUrlWithoutMain.size() == mData.overviewData.imagePathsWithoutMainList.size())
                super.updateImagesUrl();

        }

        @Override
        protected Map<String, Object> createImageURLChildUpdates() {
            Map<String, Object> childUpdates = new HashMap<>();

            String[] basePrefixes = {"/recipes/", "/user-recipes/" + mData.authorUId + "/"};

            sort(mUploadedImagesUrlWithoutMain);

            mData.overviewData.allImagePathList.clear();
            mData.overviewData.allImagePathList.add(0, mUploadedMainUrl);
            mData.overviewData.allImagePathList.addAll(1, mUploadedImagesUrlWithoutMain);

            mData.overviewData.mainImagePath = mUploadedMainUrl;

            mData.overviewData.imagePathsWithoutMainList.clear();
            mData.overviewData.imagePathsWithoutMainList.addAll(mUploadedImagesUrlWithoutMain);

            for (String basePrefix : basePrefixes) {
                childUpdates.put(basePrefix + mData.recipeKey, mData.toMap());
            }

            String imagePathPref = Constants.ImageConstants.FIREBASE_STORAGE_AT_START
                    + "/recipes_images/" + mData.recipeKey + "/";

            childUpdates.put(imagePathPref, ImageAdapter.toImagesData(mData).toMap());

            return childUpdates;
        }

        @Override
        public List<String> getUploadedUrls() {

            List<String> urls = new ArrayList<>();
            urls.add(0, mUploadedMainUrl);

            sort(mUploadedImagesUrlWithoutMain);

            urls.addAll(1, mUploadedImagesUrlWithoutMain);

            return urls;
        }


        //Sort strings like "sss/jjjjj/.../$INT_CHARS.kkkk..." by $INT_CHARS
        private void sort(List<String> arr) {
            Collections.sort(arr, (o1, o2) -> {
                String[] split1 = o1.split("/");
                int num1 = Integer.valueOf(split1[split1.length - 1].split("\\.")[0]);

                String[] split2 = o2.split("/");
                int num2 = Integer.valueOf(split2[split2.length - 1].split("\\.")[0]);

                return Integer.compare(num1, num2);
            });
        }

        @Override
        protected void cancelUpload() {
            for (ImageUploader imageUploader : mImageUploaders) {
                imageUploader.cancel();
            }
        }

    }

    private class StepsImagesUploader extends BaseMultiImagesUploader {

        private final List<ImageUploader> mImageUploaders = new ArrayList<>();

        private final List<String> mUploadedImagesOfSteps;

        private int mCountStepsWithImage = 0;


        private StepsImagesUploader(Context context) {
            super(context);

            mUploadedImagesOfSteps = new ArrayList<>();
        }

        @Override
        protected void uploadImages() {
            final String prefixStorageImagePath = Constants.ImageConstants.FIREBASE_STORAGE_AT_START + "/recipe_images/" + mData.recipeKey + "/"
                    + "steps_images" + "/";

            for (StepOfCooking step : mData.stepsData.stepsOfCooking) {
                if (step.imagePath != null)
                    mCountStepsWithImage++;
            }

            for (int i = 0; i < mData.stepsData.stepsOfCooking.size(); i++) {

                if (isNeedStop) break;

                final int currStepPos = i;

                String imagePath = mData.stepsData.stepsOfCooking.get(i).imagePath;

                if (imagePath != null) {

                    final ImageUploader imageUploader = ImageUploader.with(context)
                            .setImagePath(imagePath)
                            .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                            .setFullStoragePath(prefixStorageImagePath + i + ".jpg")
                            .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                protected void addDataOnSuccess(String path) {
                                    mData.stepsData
                                            .stepsOfCooking
                                            .get(currStepPos)
                                            .imagePath = path;

                                    mUploadedImagesOfSteps.add(path);
                                }

                                @Override
                                protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath) {
                                    return isNeedStop;
                                }
                            }).build();

                    mImageUploaders.add(imageUploader);

                    imageUploader.startUpload();
                }
            }

            if (mCountStepsWithImage == 0) {
                onSuccessUpload();
            }
        }

        @Override
        protected void onSuccessUpload() {
            mStepsImagesUploadStatus = ParcResourceByParc.Status.SUCCESS;

            updateProgress(mDataResource);
        }

        @Override
        protected void onErrorUpload(Exception ex) {
            mDataResource = ParcResourceByParc.error(ex, mData);

            mStepsImagesUploadStatus = ParcResourceByParc.Status.ERROR;

            updateProgress(mDataResource);
        }

        @Override
        protected void updateImagesUrlIfAllImagesUploaded() {
            if (!isNeedStop && mCountStepsWithImage == mUploadedImagesOfSteps.size()) {
                super.updateImagesUrl();
            }
        }

        @Override
        protected Map<String, Object> createImageURLChildUpdates() {
            Map<String, Object> childUpdates = new HashMap<>();

            String[] basePrefixes = {"/recipes/", "/user-recipes/" + mData.authorUId + "/"};


            for (String basePrefix : basePrefixes) {
                childUpdates.put(basePrefix + mData.recipeKey, mData.toMap());
            }

            String imagePathPref = Constants.ImageConstants.FIREBASE_STORAGE_AT_START
                    + "/recipes_images/" + mData.recipeKey + "/";

            childUpdates.put(imagePathPref, ImageAdapter.toImagesData(mData).toMap());

            return childUpdates;
        }

        @Override
        public List<String> getUploadedUrls() {
            return mUploadedImagesOfSteps;
        }

        @Override
        protected void cancelUpload() {

            for (ImageUploader imageUploader : mImageUploaders) {
                imageUploader.cancel();
            }
        }
    }


}
