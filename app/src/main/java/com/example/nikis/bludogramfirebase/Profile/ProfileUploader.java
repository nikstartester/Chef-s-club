package com.example.nikis.bludogramfirebase.Profile;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class ProfileUploader {
    private static final String TAG = "ProfileUploader";
    private ProfileData mProfileData;
    private String mImagePath;
    private ProfileImageUploader mImageUploader;

    public ProfileUploader(ProfileData profileData, @Nullable String imagePath) {
        mProfileData = profileData;
        mImagePath = imagePath;
        mImageUploader = new ProfileImageUploader();
    }

    public MutableLiveData<Resource<ProfileData>> updateProfile(MutableLiveData<Resource<ProfileData>> data) {
        if(isLoginExist()){
         data.setValue(Resource.error(new ExistLoginException(), mProfileData));
        }
        else {
            data.setValue(Resource.loading(mProfileData));
            updateChildren(data);
        }
        return data;
    }

    private boolean isLoginExist(){
        //TODO написать проверку существования выбранного логина
        return false;
    }

    private void updateChildren(MutableLiveData<Resource<ProfileData>> data) {
        Map<String, Object> childUpdates = createChildUpdates();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();
        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if(databaseError == null){
                if(mImagePath != null ) {
                    mImageUploader.uploadImage(mImagePath, data);
                }else {
                    data.setValue(Resource.success(mProfileData));
                }
            }else data.setValue(Resource.error(databaseError.toException(), mProfileData));
        });

    }

    private Map<String, Object> createChildUpdates(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileData.userUid = user.getUid();

        Map<String, Object> postValues = mProfileData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + mProfileData.userUid, postValues);

        return childUpdates;
    }

    private class ProfileImageUploader{

        private String mStorageImagePath;

        private void uploadImage(String imagePath,
                                 MutableLiveData<Resource<ProfileData>> data){
            File file = new File(imagePath);
            Uri fileUri = Uri.fromFile(file);

            String newImageName = mProfileData.userUid + ".jpg";

            StorageReference storageRef = FirebaseReferences.getStorageReference();

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image")
                    .build();

            mStorageImagePath = "profilesImage/" + newImageName;

            UploadTask uploadTask = storageRef.child(mStorageImagePath)
                    .putFile(fileUri, metadata);

            uploadTask.addOnCompleteListener(task -> {
                if(task.isSuccessful()) {

                    //data.setValue(Resource.loading(mProfileData));

                    updateImagePath(data);

                    /*LocalUserData.getInstance().setTimeLastImageUpdate(Long.toString(System.currentTimeMillis()))
                            .putToPreferences(this);*/

                    /*StorageReference storageReference = FirebaseReferences.getStorageReference(
                            mStorageImagePath);

                    GlideApp.with(context.getApplicationContext())
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(LocalUserData.getInstance().getTimeLastImageUpdate()))
                            .submit();
                    */
                }else data.setValue(Resource.error(task.getException(), mProfileData));
            });
        }

        private void updateImagePath(MutableLiveData<Resource<ProfileData>> data){
            if(mStorageImagePath != null){


                Map<String, Object> childUpdates = createImageURLChildUpdates();

                DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        mProfileData.imageURL = mStorageImagePath;

                        data.setValue(Resource.success(mProfileData));
                    }else {
                        data.setValue(Resource.error(task.getException(), mProfileData));
                    }
                });
            }
        }

        private Map<String, Object> createImageURLChildUpdates(){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/users/" + mProfileData.userUid + "/imageURL", mStorageImagePath);

            return childUpdates;
        }
    }



}
