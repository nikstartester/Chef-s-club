package com.example.nikis.bludogramfirebase.Profile.Upload;

import android.content.Context;
import android.util.Log;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.ImageUploader;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.Upload.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.Resource;
import com.example.nikis.bludogramfirebase.Uploader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;



public class ProfileUploader extends Uploader<ProfileData>{
    private static final String TAG = "ProfileUploader";


    private ProfileUploader.ProfileImageUploader mImageUploader;


    public ProfileUploader(Context context){
        super();
        mImageUploader = new ProfileImageUploader(context);
    }


    @Override
    public void start() {
        updateProfile();
    }

    private void updateProfile() {
        if(isLoginExist()){
            mDataResource = Resource.error(new ExistLoginException(), mData);
        }
        else {
            mDataResource = Resource.loading(mData);

            updateChildren();
        }
        super.updateProgress(mDataResource);
    }



    private boolean isLoginExist(){
        //TODO написать проверку существования выбранного логина
        return false;
    }

    private void updateChildren() {
        Map<String, Object> childUpdates = createChildUpdates();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();
        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if(databaseError == null){
                if(mData.localImagePath != null ) {
                    mImageUploader.uploadImage();
                }else {
                    Log.d(TAG, "updateChildren: localImagePath == null");
                    mDataResource = Resource.success(mData);

                    updateProgress(mDataResource);
                }
            }else{
                mDataResource = Resource.error(databaseError.toException(), mData);

                updateProgress(mDataResource);
            }
        });

    }

    private Map<String, Object> createChildUpdates(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mData.userUid = user.getUid();

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + mData.userUid, postValues);

        return childUpdates;
    }



    private class ProfileImageUploader{

        private String mStorageImagePath;

        private ImageUploader.Builder  mImageUpBuilder;

        private ProfileImageUploader(Context context){
            mImageUpBuilder = ImageUploader.with(context);
        }

        private void uploadImage() {

            mStorageImagePath = "profilesImage/"
                    + mData.userUid
                    + ".jpg";

            mImageUpBuilder
                    .setFullStoragePath(mStorageImagePath)
                    .setQuality(80)
                    .setImagePath(mData.localImagePath)
                    .setOnProgressListener(resStoragePath -> {
                        if(resStoragePath.status == Resource.Status.SUCCESS){
                            mStorageImagePath = resStoragePath.data;

                            updateImagePath();
                        }else if(resStoragePath.status == Resource.Status.ERROR){
                            mDataResource = Resource.error(resStoragePath.exception, mData);

                            updateProgress(mDataResource);
                        }
                    }).build()
            .startUpload();
        }

        private void updateImagePath(){
            if(mStorageImagePath != null){

                Map<String, Object> childUpdates = createImageURLChildUpdates();

                DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        mData.imageURL = mStorageImagePath;

                        mDataResource = Resource.success(mData);

                    }else {
                        mDataResource = Resource.error(task.getException(), mData);
                    }

                    updateProgress(mDataResource);
                });
            }
        }

        private Map<String, Object> createImageURLChildUpdates(){
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/users/" + mData.userUid + "/imageURL", mStorageImagePath);

            return childUpdates;
        }
    }
}