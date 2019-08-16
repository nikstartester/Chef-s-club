package com.xando.chefsclub.Profiles.Repository.Local;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.StorageReference;
import com.xando.chefsclub.App;
import com.xando.chefsclub.DataWorkers.BaseLocalDataSaver;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.GlideApp;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.db.ProfileEntity;

public class LocalUserProfileSaver extends BaseLocalDataSaver<ProfileData> {

    public LocalUserProfileSaver(Application application) {
        super(application);
    }

    @Override
    public void save(@NonNull ProfileData profileData) {

        //String time = Long.toString(System.currentTimeMillis());

        final ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.profileData = profileData;
        // profileEntity.profileData.lastTimeUpdate = time;

        startThreadToInsertData(profileEntity);

        String imageUrl = profileData.imageURL;

        if (imageUrl != null) saveImageOnDisk(imageUrl, profileData.lastTimeUpdate);

    }

    private void startThreadToInsertData(ProfileEntity profileEntity) {

        new Thread(() -> {
            ((App) mApplication)
                    .getDatabase()
                    .profileDao()
                    .insert(profileEntity);

            if (mOnComplete != null) mOnComplete.onComplete();
        }).start();
    }

    private void saveImageOnDisk(String imageUrl, long time) {
        StorageReference storageReference = FirebaseReferences.getStorageReference(imageUrl);

        GlideApp.with(mApplication)
                .load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(time))
                .submit();
    }
}
