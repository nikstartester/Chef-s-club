package com.example.nikis.bludogramfirebase.Profile.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.Repository.ProfileRepository;
import com.example.nikis.bludogramfirebase.Resource;


public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";
    private MutableLiveData<Resource<ProfileData>> mResourceLiveData;

    public ProfileViewModel(@NonNull Application application) {

        super(application);

    }

    public void loadData(String userUid){
         ProfileRepository.with(getApplication())
                 .setUserUid(userUid)
                 .to(mResourceLiveData)
                 .build()
                 .loadData();
    }

    public LiveData<Resource<ProfileData>> getResourceLiveData() {
        if(mResourceLiveData == null){
            mResourceLiveData = new MutableLiveData<>();
        }
        return mResourceLiveData;

    }



}
