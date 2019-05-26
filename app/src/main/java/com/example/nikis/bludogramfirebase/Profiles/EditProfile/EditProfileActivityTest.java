package com.example.nikis.bludogramfirebase.Profiles.EditProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.SingleFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditProfileActivityTest extends SingleFragmentActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());

        if (!EditProfileFragment.isProfileAlreadyCreated(this))
            getSupportActionBar().setTitle("Create new profile");
        else getSupportActionBar().setTitle("Edit profile");

    }

    @Override
    protected Fragment createFragment() {
        return new EditProfileFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_fragment;
    }
}
