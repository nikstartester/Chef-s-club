package com.example.nikis.bludogramfirebase;

import android.support.annotation.Nullable;

public abstract class Uploader<Data extends BaseData> extends Object{

    protected Data mData;

    protected Resource<Data> mDataResource;

    @Nullable
    private OnProcessListener<Data> mOnProcessListener;

    public  void start(Data data, OnProcessListener<Data> onProcessListener){
        mData = data;
        mOnProcessListener = onProcessListener;

        start();
    }

    protected abstract void start();


    protected void updateProgress(Resource<Data> resource){
        if (mOnProcessListener != null) {
            mOnProcessListener.onStatusChanged(resource);
        }
    }

    public interface OnProcessListener<Data extends BaseData>{
        public void onStatusChanged(Resource<Data> resource);
    }
}
