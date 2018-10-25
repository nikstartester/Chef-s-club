package com.example.nikis.bludogramfirebase.Profile.Repository.Local;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.BaseLocalDataSaver;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.db.ProfileEntity;
import com.google.firebase.storage.StorageReference;

public class LocalUserProfile extends BaseLocalDataSaver<ProfileData> {

    public LocalUserProfile(Application application) {
        super(application);
    }

    @Override
    public void save(@NonNull ProfileData profileData){

        String time = Long.toString(System.currentTimeMillis());

        final ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.profileData = profileData;
        profileEntity.profileData.timeLastImageUpdate = time;

        startThreadToInsertData(profileEntity);

        String imageUrl = profileData.imageURL;

        if(imageUrl != null) saveImageOnDisk(imageUrl, time);
    }

    private void startThreadToInsertData(ProfileEntity profileEntity){

        new Thread(() -> {
            ((App)mApplication)
                    .getDatabase()
                    .profileDao()
                    .insert(profileEntity);

            if(mOnComplete != null) mOnComplete.onComplete();
        }).start();
    }

    private void saveImageOnDisk(String imageUrl, String time) {
        StorageReference storageReference = FirebaseReferences.getStorageReference(imageUrl);

        GlideApp.with(mApplication)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(time))
                .submit();
    }


}
