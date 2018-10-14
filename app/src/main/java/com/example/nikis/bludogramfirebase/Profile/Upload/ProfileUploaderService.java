package com.example.nikis.bludogramfirebase.Profile.Upload;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Resource;

public class ProfileUploaderService extends IntentService {

    public static final String ACTION_RESPONSE = "com.example.nikis.bludogramfirebase.action.RESPONSE";

    private static final String EXTRA_PROFILE_DATA = "com.example.nikis.bludogramfirebase.extra.PROFILE_DATA";
    public static final String EXTRA_RESOURCE = "com.example.nikis.bludogramfirebase.extra.EXTRA_RESOURCE";

    public ProfileUploaderService() {
        super("ProfileUploaderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ProfileData profileData = intent.getParcelableExtra(EXTRA_PROFILE_DATA);

            new ProfileUploader().start(profileData, resource -> sendBroadcast(createResponseIntent(resource)));
        }
    }

    public static Intent getIntent(Context context, ProfileData profileData){
        Intent intent = new Intent(context, ProfileUploaderService.class);
        return intent.putExtra(EXTRA_PROFILE_DATA, profileData);
    }

    private Intent createResponseIntent(Resource<ProfileData> resource){
        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESOURCE, resource);

        return responseIntent;
    }
}
