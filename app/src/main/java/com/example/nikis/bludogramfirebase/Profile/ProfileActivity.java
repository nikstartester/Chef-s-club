package com.example.nikis.bludogramfirebase.Profile;

import android.support.v4.app.Fragment;

import com.example.nikis.bludogramfirebase.SingleFragmentActivity;

public class ProfileActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new EditProfileFragment();
    }
}
