package com.xando.chefsclub.DataWorkers;

import android.support.annotation.Nullable;
import android.util.Log;

public abstract class DataUploader<Data extends BaseData> implements ProgressUpdate<Data> {

    private static final String TAG = "DataUploader";

    protected Data mData;

    protected ParcResourceByParc<Data> mDataResource;

    /*
    @Nullable - for compatibility
     */
    @Nullable
    protected String[] mPaths;

    public DataUploader(@Nullable String[] paths) {
        mPaths = paths;
    }

    public DataUploader() {

    }

    @Nullable
    private OnProcessListener<Data> mOnProcessListener;

    public void start(Data data) {
        start(data, null);
    }

    public void start(Data data, OnProcessListener<Data> onProcessListener) {
        mData = data;
        mOnProcessListener = onProcessListener;

        start();
    }

    protected abstract void start();

    protected abstract boolean checkRelevance(Data data);

    protected abstract void cancel();

    @Override
    public void updateProgress(ParcResourceByParc<Data> resource) {
        Log.d(TAG, "updateProgress: " + resource.status);

        if (mOnProcessListener != null) {
            mOnProcessListener.onStatusChanged(resource);
        }
    }

    public static class Cancel extends Exception {
        public Cancel() {
            super("Cancel method called");
        }
    }
}
