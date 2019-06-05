package com.example.nikis.bludogramfirebase.Profiles.Upload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.DataUploader;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Images.ImageUploader;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.Upload.Exceptions.ExistLoginException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileDataUploader extends DataUploader<ProfileData> {
    private static final String TAG = "ProfileDataUploader";


    private final ProfileDataUploader.ProfileImageUploader mImageUploader;


    public ProfileDataUploader(Context context) {
        super();
        mImageUploader = new ProfileImageUploader(context);
    }


    @Override
    public void start() {
        updateProfile();
    }

    private void updateProfile() {
        mDataResource = ParcResourceByParc.loading(mData);

        super.updateProgress(mDataResource);

        checkLoginExistAndStart();
    }


    private void checkLoginExistAndStart() {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child("users").orderByChild("login").equalTo(mData.login).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ProfileData> profileDataList = new ArrayList<>(1);

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    profileDataList.add(snap.getValue(ProfileData.class));
                }

                ProfileData profileWithExistLogin = profileDataList.size() > 0 ? profileDataList.get(0) : null;

                if (profileWithExistLogin == null
                        || (FirebaseHelper.getUid() != null
                        && profileWithExistLogin.userUid.equals(FirebaseHelper.getUid()))) {

                    updateChildren();
                } else {
                    mDataResource = ParcResourceByParc.error(new ExistLoginException(), mData);

                    ProfileDataUploader.this.updateProgress(mDataResource);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateChildren() {
        Map<String, Object> childUpdates = createChildUpdates();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();
        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                if (mData.localImagePath != null) {
                    mImageUploader.uploadImage();
                } else {
                    Log.d(TAG, "updateChildren: localImagePath == null");
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mData.userUid = user.getUid();

        //mData.lastTimeUpdate = System.currentTimeMillis();

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + mData.userUid, postValues);

        return childUpdates;
    }

    @Override
    protected boolean checkRelevance(ProfileData data) {
        return false;
    }

    @Override
    protected void cancel() {
        /*
        not canceling
         */
    }

    private class ProfileImageUploader {

        private String mStorageImagePath;

        private final String mDirectoryPathForCompress;

        private final ImageUploader.Builder mImageUpBuilder;

        private ProfileImageUploader(Context context) {
            mImageUpBuilder = ImageUploader.with(context);

            mDirectoryPathForCompress = Constants.Files.getDirectoryForCompressProfilesImages(context);
        }

        private void uploadImage() {

            mStorageImagePath = Constants.ImageConstants.FIREBASE_STORAGE_AT_START + "/profilesImage/"
                    + mData.userUid
                    + ".jpg";

            mImageUpBuilder
                    .setFullStoragePath(mStorageImagePath)
                    .setQuality(ImageUploader.Builder.NORMAL_QUALITY)
                    .setImagePath(mData.localImagePath)
                    .setDirectoryPathForCompress(mDirectoryPathForCompress)
                    .setOnProgressListener(resStoragePath -> {
                        if (resStoragePath.status == ParcResourceByParc.Status.SUCCESS) {
                            mStorageImagePath = resStoragePath.data;

                            updateImagePath();
                        } else if (resStoragePath.status == ParcResourceByParc.Status.ERROR) {
                            mDataResource = ParcResourceByParc.error(resStoragePath.exception, mData);

                            updateProgress(mDataResource);
                        }
                    }).build()
                    .startUpload();
        }

        private void updateImagePath() {
            if (mStorageImagePath != null) {

                Map<String, Object> childUpdates = createImageURLChildUpdates();

                DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mData.imageURL = mStorageImagePath;

                        mDataResource = ParcResourceByParc.success(mData);

                    } else {
                        mDataResource = ParcResourceByParc.error(task.getException(), mData);
                    }

                    updateProgress(mDataResource);
                });
            }
        }

        private Map<String, Object> createImageURLChildUpdates() {
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/users/" + mData.userUid + "/imageURL", mStorageImagePath);

            return childUpdates;
        }
    }
}