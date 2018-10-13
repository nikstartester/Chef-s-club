package com.example.nikis.bludogramfirebase;

public abstract class Uploader<T extends BaseData> {
    public abstract void start(T data, OnProcessListener<T> onProcessListener);
    
    public interface OnProcessListener<Data extends BaseData>{
        public void onStatusChanged(Resource<Data> resource);
    }
}
