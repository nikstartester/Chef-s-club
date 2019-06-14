package com.xando.chefsclub.DataWorkers;

import android.app.Application;

import com.xando.chefsclub.Profiles.Repository.Local.LocalUserProfileSaver;

public abstract class BaseLocalDataSaver<Data extends BaseData> {
    protected Application mApplication;

    protected LocalUserProfileSaver.OnComplete mOnComplete;

    private BaseLocalDataSaver() {
    }

    protected BaseLocalDataSaver(Application application) {
        mApplication = application;
    }

    public void save(Data data, LocalUserProfileSaver.OnComplete onComplete) {
        mOnComplete = onComplete;

        save(data);
    }

    protected abstract void save(Data data);


    public interface OnComplete {
        void onComplete();
    }
}