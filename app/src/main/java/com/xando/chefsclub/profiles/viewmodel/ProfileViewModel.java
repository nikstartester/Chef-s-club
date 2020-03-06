package com.xando.chefsclub.profiles.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.xando.chefsclub.dataworkers.BaseLocalDataSaver;
import com.xando.chefsclub.dataworkers.BaseRepository;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.repository.ProfileRepository;
import com.xando.chefsclub.profiles.repository.local.LocalUserProfileSaver;

import static com.xando.chefsclub.profiles.repository.ProfileRepository.CHILD_USERS;


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
