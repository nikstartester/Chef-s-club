package com.example.nikis.bludogramfirebase.Profile;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.LocalUserData;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.UserData.UserData;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends CreateNewProfile{
    public static final int RESULT_CODE_UPDATE = 89;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUserData();
    }

    private void setUserData() {
        LocalUserData localUserData = LocalUserData.getInstance();
        if(localUserData.isNeedUpdate()){
            localUserData.addOnUpdateDataListener(userData -> showProfileData());
            localUserData.updateData();
        }else {
            showProfileData();
        }
    }

    private void showProfileData(){
        if(!isDestroyed()){
            setProfileImage();

            UserData userData = LocalUserData.getInstance().getUserData();

            super.edtFirstName.setText(userData.firstName);
            super.edtLastName.setText(userData.secondName);
            super.edtLogin.setText(userData.login);

            switch (userData.getGender()){
                case GENDER_MALE:
                    super.radioButtonMaleClick();
                    break;
                case GENDER_FEMALE:
                    super.radioButtonFemaleClick();
                    break;
            }
        }
    }

    private void setProfileImage(){
        StorageReference storageReference = FirebaseReferences.getStorageReference(
                "profilesImage/" + LocalUserData.getInstance().getUserData().userUid + ".jpg");
        GlideApp.with(this)
                .load(storageReference)
                .override(1080,1080)
                .thumbnail(0.2f)
                .error(R.drawable.ic_add_a_photo_blue_108dp)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .signature(new ObjectKey(LocalUserData.getInstance().getTimeLastImageUpdate()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(super.circularImageView);
    }

    @Override
    public void onAllTasksComplete() {
        setResult(RESULT_CODE_UPDATE);
        finish();
    }
}
