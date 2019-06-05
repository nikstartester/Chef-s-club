package com.xando.chefsclub.Profiles.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xando.chefsclub.DataWorkers.BaseLocalDataSaver;
import com.xando.chefsclub.DataWorkers.BaseRepository;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.Repository.Local.LocalUserProfileSaver;
import com.xando.chefsclub.Profiles.Repository.ProfileRepository;

import static com.xando.chefsclub.Profiles.Repository.ProfileRepository.CHILD_USERS;


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
