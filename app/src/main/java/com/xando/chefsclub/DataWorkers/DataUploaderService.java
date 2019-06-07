package com.xando.chefsclub.DataWorkers;

import android.app.Service;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public abstract class DataUploaderService<Data extends BaseData> extends Service {

    public static final String ACTION_RESPONSE = "com.xando.chefsclub.DataWorkers.action.RESPONSE";
    public static final String EXTRA_RESOURCE = "com.xando.chefsclub.DataWorkers.extra.RESOURCE";
    protected static final String EXTRA_DATA = "com.xando.chefsclub.DataWorkers.extra.DATA";
    protected static final String EXTRA_ACTIONS = "com.xando.chefsclub.DataWorkers.extra.Action";

    private List<DataUploader<Data>> mDataUploaders = new ArrayList<>();

    protected abstract DataUploader<Data> getDataUploader();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Action action = (Action) intent.getSerializableExtra(EXTRA_ACTIONS);

            Data data = intent.getParcelableExtra(EXTRA_DATA);

            if (action == null || action == Action.START_UPLOAD) {
                startUpload(data);
            } else if (action == Action.CANCEL_UPLOAD) {
                cancelUpload(data);
            }

        }
    }

    private void startUpload(Data data) {
        DataUploader<Data> dataUploader = getDataUploader();

        mDataUploaders.add(dataUploader);

        dataUploader.start(data, resource -> {
            sendBroadcast(createResponseIntent(resource));

            checkResult(resource);
        });
    }

    private void cancelUpload(Data data) {
        for (DataUploader<Data> uploader : mDataUploaders) {
            if (uploader.checkRelevance(data)) {
                uploader.cancel();
            }
        }
        removeAllFinishedUploaders();

        checkToStop();
    }

    private void checkResult(ParcResourceByParc<Data> resource) {
        if (resource.status != ParcResourceByParc.Status.LOADING) {

            removeAllFinishedUploaders();

            checkToStop();

        }
    }

    private void checkToStop() {
        if (mDataUploaders.size() == 0) {
            stopSelf();
        }
    }

    private void removeAllFinishedUploaders() {
        List<Integer> indexesToRemove = new ArrayList<>();

        for (int i = 0; i < mDataUploaders.size(); i++) {
            DataUploader<Data> uploader = mDataUploaders.get(i);

            if (uploader.mDataResource != null
                    && uploader.mDataResource.status != ParcResourceByParc.Status.LOADING) {

                indexesToRemove.add(i);
            }
        }
        for (int index : indexesToRemove) {
            mDataUploaders.remove(index);
        }
    }


    private Intent createResponseIntent(ParcResourceByParc<Data> resource) {
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESOURCE, resource);

        return responseIntent;
    }

    public enum Action {
        START_UPLOAD, CANCEL_UPLOAD
    }

}
