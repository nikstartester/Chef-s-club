package com.xando.chefsclub.profiles.upload;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.xando.chefsclub.dataworkers.DataUploader;
import com.xando.chefsclub.dataworkers.DataUploaderService;
import com.xando.chefsclub.profiles.data.ProfileData;

public class ProfileUploaderService extends DataUploaderService<ProfileData> {

    public static Intent getIntent(Context context, ProfileData profileData) {
        Intent intent = new Intent(context, ProfileUploaderService.class);
        return intent.putExtra(EXTRA_DATA, profileData);
    }

    @Override
    public DataUploader<ProfileData> getDataUploader() {
        return new ProfileDataUploader(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
