package com.example.nikis.bludogramfirebase.Profile.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Resource;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";
    private MutableLiveData<Resource<ProfileData>> mResourceLiveData;

    public ProfileViewModel(@NonNull Application application) {

        super(application);

    }

    public void loadData(String userUid){
        Flowable<List<ProfileData>> flowable = ((App) getApplication())
                .getDatabase()
                .profileDao()
                .getByUid(userUid);
        flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profilesData -> {
                    if(!profilesData.isEmpty())
                    mResourceLiveData.setValue(Resource.success(profilesData.get(0)));

                });
    }

    public LiveData<Resource<ProfileData>> getResourceLiveData() {
        if(mResourceLiveData == null){
            mResourceLiveData = new MutableLiveData<>();

        }
        return mResourceLiveData;

    }



}
