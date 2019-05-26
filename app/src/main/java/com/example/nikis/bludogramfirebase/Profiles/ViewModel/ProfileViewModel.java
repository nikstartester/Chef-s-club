package com.example.nikis.bludogramfirebase.Profiles.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseLocalDataSaver;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseRepository;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.Repository.Local.LocalUserProfileSaver;
import com.example.nikis.bludogramfirebase.Profiles.Repository.ProfileRepository;

import static com.example.nikis.bludogramfirebase.Profiles.Repository.ProfileRepository.CHILD_USERS;


public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private MutableLiveData<ParcResourceByParc<ProfileData>> mResourceLiveData;

    public ProfileViewModel(@NonNull Application application) {

        super(application);

    }

    public void loadDataWithoutSaver(String userId) {
        createRepositoryBuilder(userId, null)
                .build()
                .loadData();
    }

    public void loadDataWithSaver(String userId) {
        createRepositoryBuilder(userId, new LocalUserProfileSaver(getApplication()))
                .build()
                .loadData();
    }

    public void loadDataAndSync(String userId) {
        createRepositoryBuilder(userId, new LocalUserProfileSaver(getApplication()))
                .setPriority(BaseRepository.Priority.DATABASE_FIRST_AND_SERVER)
                .build()
                .loadData();
    }

    private BaseRepository.Builder createRepositoryBuilder(String userId, @Nullable BaseLocalDataSaver<ProfileData> saver) {
        return ProfileRepository.with(getApplication())
                .setFirebaseId(userId)
                .setFirebaseChild(CHILD_USERS)
                .setLocalSever(saver)
                .to(mResourceLiveData);
    }

    public LiveData<ParcResourceByParc<ProfileData>> getResourceLiveData() {
        if (mResourceLiveData == null) {
            mResourceLiveData = new MutableLiveData<>();
        }
        return mResourceLiveData;

    }
}
