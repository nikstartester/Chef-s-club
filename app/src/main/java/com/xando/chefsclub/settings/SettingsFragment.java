package com.xando.chefsclub.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.login.LoginActivity;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.editprofile.EditProfileFragment;
import com.xando.chefsclub.profiles.viewmodel.ProfileViewModel;
import com.xando.chefsclub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingsFragment extends Fragment {

    @BindView(R.id.profile_image)
    protected ImageView profileImage;
    @BindView(R.id.profile_name)
    protected TextView profileName;
    @BindView(R.id.profile_login)
    protected TextView profileLogin;

    private ProfileViewModel mProfileViewModel;
    @Nullable
    private ChangeFragment mChangeFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, view);

        mProfileViewModel.getResourceLiveData().observe(this, res -> {
            if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {
                ProfileData profileData = res.data;
                if (profileData != null) {
                    setProfileData(profileData);
                }
            }
        });

        if (mProfileViewModel.getResourceLiveData().getValue() == null)
            mProfileViewModel.loadDataAndSync(FirebaseHelper.getUid());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ChangeFragment) {
            mChangeFragment = (ChangeFragment) context;
        }
    }

    private void setProfileData(@NonNull ProfileData profileData) {
        profileLogin.setText(profileData.login);
        profileName.setText(profileData.firstName + " " + profileData.secondName);

        if (profileData.imageURL != null) {
            ImageData imageData = new ImageData(profileData.imageURL, profileData.lastTimeUpdate);

            GlideImageLoader.getInstance().loadSmallCircularImage(getActivity(),
                    profileImage,
                    imageData);
        } else {
            profileImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
        }
    }


    @OnClick(R.id.btn_edit_profile)
    protected void startEditProfile() {
        changeSettingsFragment(new EditProfileFragment(), "Edit profile");
    }

    @OnClick(R.id.btn_local_cookbook)
    protected void startEditLocalCookBookSetting() {
        changeSettingsFragment(new SettingsLoacalFragment(), "Local cookbook");
    }

    @OnClick(R.id.btn_cache)
    protected void startEditCacheSettings() {
        changeSettingsFragment(new SettingsCacheFragment(), "Cache");
    }

    private void changeSettingsFragment(@NonNull Fragment fragment, String title) {
        if (mChangeFragment != null) {
            mChangeFragment.toChangeFragment(fragment, title);
        }
    }

    @OnClick(R.id.btn_sign_out)
    protected void signOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    public interface ChangeFragment {
        void toChangeFragment(@NonNull Fragment fragment, String title);
    }
}