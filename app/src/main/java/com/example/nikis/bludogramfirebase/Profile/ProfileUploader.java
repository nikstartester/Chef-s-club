package com.example.nikis.bludogramfirebase.Profile;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nikis.bludogramfirebase.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Resource;
import com.example.nikis.bludogramfirebase.Uploader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class ProfileUploader extends Uploader<ProfileData>{
    private static final String TAG = "ProfileUploader";

    private ProfileData mProfileData;

    private ProfileUploader.ProfileImageUploader mImageUploader;

    private Resource<ProfileData> mProfileResource;

    @Nullable
    private OnProcessListener<ProfileData> mOnProcessListener;

    public ProfileUploader(){
        this(null);
    }

    public ProfileUploader(ProfileData profileData) {
        mProfileData = profileData;

        mImageUploader = new ProfileUploader.ProfileImageUploader();
    }

    @Override
    public void start(@NotNull ProfileData data,
                      @Nullable OnProcessListener<ProfileData> onProcessListener) {
        mProfileData = data;
        mOnProcessListener = onProcessListener;

        updateProfile();
    }

    private void updateProfile() {
        if(isLoginExist()){
            mProfileResource = Resource.error(new ExistLoginException(), mProfileData);
        }
        else {
            mProfileResource = Resource.loading(mProfileData);

            updateChildren();
        }
        updateProgress(mProfileResource);
    }

    private void updateProgress(Resource<ProfileData> resource){
        if (mOnProcessListener != null) {
            mOnProcessListener.onStatusChanged(resource);
        }
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
                if(mProfileData.localImagePath != null ) {
                    mImageUploader.uploadImage(mProfileData.localImagePath);
                }else {
                    Log.d(TAG, "updateChildren: localImagePath == null");
                    mProfileResource = Resource.success(mProfileData);

                    updateProgress(mProfileResource);
                }
            }else{
                mProfileResource = Resource.error(databaseError.toException(), mProfileData);

                updateProgress(mProfileResource);
            }
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

        private void uploadImage(String imagePath){
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

                    updateImagePath();

                    //TODO put to another place
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
                }else {
                    mProfileResource = Resource.error(task.getException(), mProfileData);

                    updateProgress(mProfileResource);
                }
            });
        }

        private void updateImagePath(){
            if(mStorageImagePath != null){

                Map<String, Object> childUpdates = createImageURLChildUpdates();

                DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        mProfileData.imageURL = mStorageImagePath;

                        mProfileResource = Resource.success(mProfileData);

                    }else {
                        mProfileResource = Resource.error(task.getException(), mProfileData);
                    }

                    updateProgress(mProfileResource);
                });
            }
        }

        private Map<String, Object> createImageURLChildUpdates(){
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/users/" + mProfileData.userUid + "/imageURL", mStorageImagePath);

            return childUpdates;
        }
    }
}