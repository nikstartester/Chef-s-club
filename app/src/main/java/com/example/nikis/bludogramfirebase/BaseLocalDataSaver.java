package com.example.nikis.bludogramfirebase;

import android.app.Application;

import com.example.nikis.bludogramfirebase.Profile.Repository.Local.LocalUserProfile;

public abstract class BaseLocalDataSaver<Data extends BaseData> {
    protected Application mApplication;

    protected LocalUserProfile.OnComplete mOnComplete;

    private BaseLocalDataSaver(){}

    public BaseLocalDataSaver(Application application){
        mApplication = application;
    }

    public void save(Data data, LocalUserProfile.OnComplete onComplete){
        mOnComplete = onComplete;

        save(data);
    }

    public abstract void save(Data data);


    public interface OnComplete{
        public void onComplete();
    }
}
