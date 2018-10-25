package com.example.nikis.bludogramfirebase.Profile.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.BaseRepository;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.Repository.Local.LocalUserProfile;
import com.example.nikis.bludogramfirebase.Resource;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProfileRepository extends BaseRepository<ProfileData> {

    public static final String CHILD_USERS = "users";

    private ProfileRepository(){
        super();
    }

    public static Builder with(Application application){
        ProfileRepository repository = new ProfileRepository();
        repository.mApplication = application;

        return repository.new Builder();
    }


    @Override
    public void loadDataFromDB(){

        resData.setValue(Resource.loading(null));

        Flowable<List<ProfileData>> flowable = ((App) mApplication)
                .getDatabase()
                .profileDao()
                .getByUid(mFirebaseId);
        flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profilesData -> {
                    super.onDataLoadedFromDB(profilesData);
                });
    }

    @Override
    public Class<ProfileData> getDataClass() {
        return ProfileData.class;
    }

    public static String getFireBaseAuthUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public class Builder extends BaseRepository<ProfileData>.Builder{

        /*

        if userUid == null -> use Uid from FireBaseAuth
        if resData == null -> use new MutableLiveData()
        if firebaseChild == null -> use "users"
        if mLocalSaver == null -> use new LocalUserProfile(mApplication)
        */
        @Override
        protected void checkAndSetStandardValue() {
            if(resData == null) resData = new MutableLiveData<>();

            if(mFirebaseId == null) mFirebaseId = getFireBaseAuthUid();

            if(mFirebaseChild == null) mFirebaseChild = CHILD_USERS;

            if(mLocalSaver == null) mLocalSaver = new LocalUserProfile(mApplication);
        }

    }
}
