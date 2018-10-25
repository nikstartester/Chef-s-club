package com.example.nikis.bludogramfirebase;

import android.app.IntentService;
import android.content.Intent;

public abstract class DataUploaderService<Data extends BaseData> extends IntentService {

    public static final String ACTION_RESPONSE = "com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.action.RESPONSE";

    public static final String EXTRA_DATA = "com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.extra.DATA";

    public static final String EXTRA_RESOURCE = "com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.extra.RESOURCE";

    private Uploader<Data> mUploader;


    public abstract Uploader<Data> getUploader();

    public DataUploaderService() {
        super("DataUploaderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Data data = intent.getParcelableExtra(EXTRA_DATA);

            mUploader = getUploader();

            mUploader.start(data, resource -> sendBroadcast(createResponseIntent(resource)));

        }
    }


    private Intent createResponseIntent(Resource<Data> resource){
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESOURCE, resource);

        return responseIntent;
    }


}
