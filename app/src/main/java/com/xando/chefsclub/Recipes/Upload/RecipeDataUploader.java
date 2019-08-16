package com.xando.chefsclub.Recipes.Upload;

import android.content.Context;
import android.util.SparseArray;

import com.google.firebase.database.DatabaseReference;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.DataUploader;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.DataWorkers.ParcResourceBySerializable;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Images.ImageUploader;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.Data.StepOfCooking;
import com.xando.chefsclub.Recipes.Repository.RecipeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

        private final SparseArray<String> mUploadedImagesUrlWithoutMain;

        private String mUploadedMainUrl;

        private OverviewImagesUploader(Context context) {
            super(context);

            mUploadedImagesUrlWithoutMain = new SparseArray<>();

            //SparseArray<String> array = new SparseArray<>();
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
                    + "overview_images" + "/" + "m_" + UUID.randomUUID().toString() + ".jpg";

            final ImageUploader imageUploader = ImageUploader.with(context)
                    .setImagePath(mData.overviewData.mainImagePath)
                    .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                    .setFullStoragePath(storageImagePath)
                    .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        protected void addDataOnSuccess(String path, int tag) {
                            mUploadedMainUrl = path;
                        }

                        @Override
                        protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath, int tag) {
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

                String storageImagePath = prefixStorageImagePath + UUID.randomUUID().toString() + ".jpg";

                final ImageUploader imageUploader = ImageUploader.with(context)
                        .setImagePath(imagePaths.get(i))
                        .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                        .setFullStoragePath(storageImagePath)
                        .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                        .setTag(i)
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            protected void addDataOnSuccess(String path, int tag) {
                                mUploadedImagesUrlWithoutMain.put(tag, path);
                            }

                            @Override
                            protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath, int tag) {
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

            //sort(mUploadedImagesUrlWithoutMain);

            mData.overviewData.allImagePathList.clear();
            mData.overviewData.allImagePathList.add(0, mUploadedMainUrl);
            mData.overviewData.allImagePathList.addAll(1, sparseToList(mUploadedImagesUrlWithoutMain));

            mData.overviewData.mainImagePath = mUploadedMainUrl;

            mData.overviewData.imagePathsWithoutMainList.clear();
            mData.overviewData.imagePathsWithoutMainList.addAll(sparseToList(mUploadedImagesUrlWithoutMain));

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

            //sort(mUploadedImagesUrlWithoutMain);

            urls.addAll(1, sparseToList(mUploadedImagesUrlWithoutMain));

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

        private List<String> sparseToList(SparseArray<String> array) {
            List<String> list = new ArrayList<>();

            for (int i = 0; i < array.size(); i++) {
                list.add(array.get(i));
            }

            return list;
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
                            .setFullStoragePath(prefixStorageImagePath + UUID.randomUUID().toString() + ".jpg")
                            .setDirectoryPathForCompress(Constants.Files.getDirectoryForCompressRecipesImages(context))
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                protected void addDataOnSuccess(String path, int tag) {
                                    mData.stepsData
                                            .stepsOfCooking
                                            .get(currStepPos)
                                            .imagePath = path;

                                    mUploadedImagesOfSteps.add(path);
                                }

                                @Override
                                protected boolean isNeedStop(ParcResourceBySerializable<String> resStoragePath, int tag) {
                                    return isNeedStop;
                                }
                            }).build();

                    mImageUploaders.add(imageUploader);

                    imageUploader.startUpload();
                }
            }

            if (mCountStepsWithImage == 0) {
                updateImagesUrlIfAllImagesUploaded();
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
