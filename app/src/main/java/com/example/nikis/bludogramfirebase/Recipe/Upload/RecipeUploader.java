package com.example.nikis.bludogramfirebase.Recipe.Upload;

import android.content.Context;
import android.util.Log;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.ImageUploader;
import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Resource;
import com.example.nikis.bludogramfirebase.Uploader;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeUploader extends Uploader<RecipeData> {
    private static final String TAG = "RecipeUploader";

    private BaseMultiImagesUploader mOverviewImagesUploader, mStepsImagesUploader;

    private Resource.Status mOverviewImagesUploadStatus, mStepsImagesUploadStatus;

    private boolean isNeedStop = false;

    public RecipeUploader(Context context){
        super();

        mOverviewImagesUploader = new OverviewImagesUploader(context);

        mStepsImagesUploader = new StepsImagesUploader(context);
    }

    @Override
    protected void start() {
        updateRecipe();
    }

    private void updateRecipe(){
        mDataResource = Resource.loading(mData);

        updateChildren();

        super.updateProgress(mDataResource);
    }

    private void updateChildren() {

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();


        if(mData.recipeKey == null){
            mData.recipeKey = myRef.child("recipes").push().getKey();

        }

        Map<String, Object> childUpdates = createChildUpdates();

        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if(databaseError == null){

                mOverviewImagesUploader.uploadImages();

                mStepsImagesUploader.uploadImages();
            }else{
                mDataResource = Resource.error(databaseError.toException(), mData);

                updateProgress(mDataResource);
            }
        });

    }

    private Map<String, Object> createChildUpdates(){

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();


        childUpdates.put("/recipes/" + mData.recipeKey, postValues);
        childUpdates.put("/user-recipes/" + mData.authorUId + "/" + mData.recipeKey, postValues);

        return childUpdates;
    }


    @Override
    protected void updateProgress(Resource<RecipeData> resource) {
        if (resource.status == Resource.Status.LOADING){

            if(mOverviewImagesUploadStatus == Resource.Status.SUCCESS
                    && mStepsImagesUploadStatus == Resource.Status.SUCCESS)
            {
                super.updateProgress(Resource.success(mData));

                return;
            }
        }

        super.updateProgress(resource);
    }

    private class OverviewImagesUploader extends BaseMultiImagesUploader{

        private List<String> mUploadedImagesUrlWithoutMain;

        private String mUploadedMainUrl;

        private OverviewImagesUploader(Context context) {
            super(context);

            mUploadedImagesUrlWithoutMain = new ArrayList<>();
        }

        @Override
        protected void uploadImages(){

            if( mData.overviewData.imagePathsWithoutMainList.size() == 0
                    && mData.overviewData.mainImagePath == null) {

                onSuccessUpload();

                return;
            }

            if(mData.overviewData.mainImagePath != null)
            {
                Log.d(TAG, "uploadImages: mainImagePath != null!");

                uploadMainImages();
            }
            else {
                Log.d(TAG, "uploadImages: mainImagePath == null!");
            }

            Log.d(TAG, "uploadImages: Start upload images! count = " + mData.overviewData.imagePathsWithoutMainList.size());

            uploadOverviewImages(mData.overviewData.imagePathsWithoutMainList);
        }

        @Override
        protected void onSuccessUpload() {
            mOverviewImagesUploadStatus = Resource.Status.SUCCESS;

            updateProgress(mDataResource);
        }

        private void uploadMainImages(){

            final String storageImagePath = "recipe_images/" + mData.recipeKey + "/"
                    + "overview_images" + "/" + "main.jpg";

            ImageUploader.with(context)
                    .setImagePath(mData.overviewData.mainImagePath)
                    .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                    .setFullStoragePath(storageImagePath)
                    .setOnProgressListener(resStoragePath -> {
                        if(resStoragePath.status == Resource.Status.SUCCESS){
                            Log.d(TAG, "uploadMainImages: Success mainImage!");

                            mUploadedMainUrl = resStoragePath.data;

                            updateImagesUrlIfAllImagesUploaded();

                        }else if(resStoragePath.status == Resource.Status.ERROR){

                            onErrorUpload(resStoragePath.exception);

                            Log.d(TAG, "uploadMainImages: Fail mainImage! " + resStoragePath.exception.getMessage());
                        }
                    }).build()
            .startUpload();
        }

        @Override
        protected void onErrorUpload(Exception ex) {
            mDataResource = Resource.error(ex, mData);

            mOverviewImagesUploadStatus = Resource.Status.ERROR;

            updateProgress(mDataResource);

            isNeedStop = true;
        }

        private void uploadOverviewImages(List<String> imagePaths){
            final String prefixStorageImagePath = "recipe_images/" + mData.recipeKey + "/"
                    + "overview_images" + "/";

            for (int i = 0; i < imagePaths.size(); i++) {

                if(isNeedStop) break;

                String storageImagePath = prefixStorageImagePath + i + ".jpg";


                ImageUploader.with(context)
                        .setImagePath(imagePaths.get(i))
                        .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                        .setFullStoragePath(storageImagePath)
                        .setOnProgressListener(resStoragePath -> {
                            if(resStoragePath.status == Resource.Status.SUCCESS){

                                Log.d(TAG, "uploadMainImages: Success image:" + resStoragePath.data+  "!");

                                mUploadedImagesUrlWithoutMain.add(resStoragePath.data);

                                updateImagesUrlIfAllImagesUploaded();

                            }else if(resStoragePath.status == Resource.Status.ERROR){
                                Log.d(TAG, "uploadMainImages: Fail image:" + resStoragePath.data + "! " + resStoragePath.exception.getLocalizedMessage());

                                onErrorUpload(resStoragePath.exception);
                            }
                        }).build()
                    .startUpload();
            }

        }

        @Override
        protected void updateImagesUrlIfAllImagesUploaded(){

            if((mUploadedMainUrl != null || mData.overviewData.mainImagePath == null)
                    && mUploadedImagesUrlWithoutMain.size() == mData.overviewData.imagePathsWithoutMainList.size())
                updateImagesUrl();

        }

        @Override
        protected void updateImagesUrl(){
            Map<String, Object> childUpdates = createImageURLChildUpdates();

            DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

            myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    onSuccessUpload();

                }else {
                    onErrorUpload(task.getException());
                }
            });
        }

        @Override
        protected Map<String, Object> createImageURLChildUpdates(){
            Map<String, Object> childUpdates = new HashMap<>();

            String[] basePrefixes = {"/recipes/", "/user-recipes/"};

            sort(mUploadedImagesUrlWithoutMain);

            mData.overviewData.allImagePathList.clear();
            mData.overviewData.allImagePathList.add(0, mUploadedMainUrl);
            mData.overviewData.allImagePathList.addAll(1, mUploadedImagesUrlWithoutMain);


            mData.overviewData.mainImagePath = mUploadedMainUrl;

            mData.overviewData.imagePathsWithoutMainList.clear();
            mData.overviewData.imagePathsWithoutMainList.addAll(mUploadedImagesUrlWithoutMain);

            for (String basePrefix : basePrefixes){
                childUpdates.put(basePrefix + mData.recipeKey, mData.toMap());
            }

            return childUpdates;
        }


        //Sort strings like "sss/jjjjj/.../$INT_CHARS.kkkk..." by $INT_CHARS
        private void sort(List<String> arr){
            Collections.sort(arr, (o1, o2) -> {
                String[] split1 = o1.split("/");
                int num1 = Integer.valueOf(split1[split1.length - 1].split("\\.")[0]);

                String[] split2 = o2.split("/");
                int num2 = Integer.valueOf(split2[split2.length - 1].split("\\.")[0]);

                return Integer.compare(num1, num2);
            });
        }
    }

    private class StepsImagesUploader extends BaseMultiImagesUploader{

        private List<String> mUploadedImagesOfSteps;

        private int mCountStepsWithImage;


        private StepsImagesUploader(Context context) {
            super(context);

            mUploadedImagesOfSteps = new ArrayList<>();
        }

        @Override
        protected void uploadImages(){
            final String prefixStorageImagePath = "recipe_images/" + mData.recipeKey + "/"
                    + "steps_images" + "/";

            for (int i = 0; i < mData.stepsData.stepsOfCooking.size(); i++) {

                if(isNeedStop) break;

                final int currStepPos = i;

                String imagePath = mData.stepsData.stepsOfCooking.get(i).imagePath;

                if(imagePath != null){

                    mCountStepsWithImage++;

                    ImageUploader.with(context)
                            .setImagePath(imagePath)
                            .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                            .setFullStoragePath(prefixStorageImagePath + i +".jpg")
                            .setOnProgressListener(resStoragePath -> {
                                if(resStoragePath.status == Resource.Status.SUCCESS){

                                    mData.stepsData
                                            .stepsOfCooking
                                            .get(currStepPos)
                                            .imagePath = resStoragePath.data;

                                    mUploadedImagesOfSteps.add(resStoragePath.data);

                                    updateImagesUrlIfAllImagesUploaded();

                                }else if(resStoragePath.status == Resource.Status.ERROR){

                                    onErrorUpload(resStoragePath.exception);
                                }
                            }).build()
                            .startUpload();
                }
            }

            if (mCountStepsWithImage == 0){
                onSuccessUpload();
            }
        }

        @Override
        protected void onSuccessUpload() {
            mStepsImagesUploadStatus = Resource.Status.SUCCESS;

            updateProgress(mDataResource);
        }

        @Override
        protected void onErrorUpload(Exception ex) {
            mDataResource = Resource.error(ex, mData);

            mStepsImagesUploadStatus = Resource.Status.ERROR;

            updateProgress(mDataResource);
        }

        @Override
        protected void updateImagesUrlIfAllImagesUploaded(){
            if(mCountStepsWithImage == mUploadedImagesOfSteps.size()){
                updateImagesUrl();
            }
        }

        @Override
        protected void updateImagesUrl() {
            Map<String, Object> childUpdates = createImageURLChildUpdates();

            DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

            myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    onSuccessUpload();
                }else {
                    onErrorUpload(task.getException());
                }
            });
        }

        @Override
        protected Map<String, Object> createImageURLChildUpdates(){
            Map<String, Object> childUpdates = new HashMap<>();

            String[] basePrefixes = {"/recipes/", "/user-recipes/"};


            for (String basePrefix : basePrefixes){
                childUpdates.put(basePrefix + mData.recipeKey, mData.toMap());
            }

            return childUpdates;
        }

    }


}
