package com.xando.chefsclub.profiles.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.xando.chefsclub.App;
import com.xando.chefsclub.dataworkers.BaseLocalDataSaver;
import com.xando.chefsclub.dataworkers.BaseRepository;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.repository.local.LocalUserProfileSaver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileRepository extends BaseRepository<ProfileData> {

    public static final String CHILD_USERS = "users";

    private ProfileRepository() {
        super();
    }

    public static Builder with(Application application) {
        ProfileRepository repository = new ProfileRepository();
        repository.mApplication = application;

        return repository.new Builder();
    }

    @Override
    public void loadDataFromDB() {

        resData.setValue(ParcResourceByParc.loading(null));

        ((App) mApplication)
                .getDatabase()
                .profileDao()
                .getSingleByUid(mFirebaseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(super::onDataLoadedFromDB);
    }

    @Override
    public void deleteFromServer(ProfileData data) {

    }

    @Override
    public void deleteFromDataBase(ProfileData data) {

    }

    @Override
    public Class<ProfileData> getDataClass() {
        return ProfileData.class;
    }

    @Nullable
    @Override
    protected BaseLocalDataSaver<ProfileData> getDefSaverIfRowExistInDB() {
        return new LocalUserProfileSaver(mApplication);
    }

    public class Builder extends BaseRepository<ProfileData>.Builder {

        /*

        if userUid == null -> use Uid from FireBaseAuth
        if resData == null -> use new MutableLiveData()
        if firebaseChild == null -> use "users"
        if mLocalSaver == null -> use new LocalUserProfileSaver(mApplication)
        */
        @Override
        protected void checkAndSetStandardValue() {
            if (resData == null) resData = new MutableLiveData<>();

            if (mFirebaseId == null) mFirebaseId = FirebaseHelper.getUid();

            if (mFirebaseChild == null) mFirebaseChild = CHILD_USERS;

            //if(mLocalSaver == null) mLocalSaver = new LocalUserProfileSaver(mApplication);
        }

    }
}
