package com.example.nikis.bludogramfirebase.Profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nikis.bludogramfirebase.Resource;


public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Resource<ProfileData>> mResourceLiveData;

    public void init(){
        if(mResourceLiveData == null)
        mResourceLiveData = new MutableLiveData<>();
    }

    public void UpdateData(ProfileData profileData, String imagePath){
        mResourceLiveData = new ProfileUploader(profileData, imagePath).updateProfile(mResourceLiveData);
    }

    public LiveData<Resource<ProfileData>> getResourceLiveData() {
        return mResourceLiveData;
    }
}
