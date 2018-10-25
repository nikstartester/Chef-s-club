package com.example.nikis.bludogramfirebase.Profile.Upload;

import android.content.Context;
import android.content.Intent;

import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.DataUploaderService;
import com.example.nikis.bludogramfirebase.Uploader;

public class ProfileUploaderService extends DataUploaderService<ProfileData> {


    public static Intent getIntent(Context context, ProfileData profileData){
        Intent intent = new Intent(context, ProfileUploaderService.class);
        return intent.putExtra(EXTRA_DATA, profileData);
    }

    @Override
    public Uploader<ProfileData> getUploader() {
        return new ProfileUploader(getApplicationContext());
    }
}
