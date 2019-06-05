package com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.OnItemCountChanged;
import com.xando.chefsclub.Recipes.ViewRecipes.UserRecipesList;
import com.xando.chefsclub.SingleFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewProfileFragment extends Fragment implements OnItemCountChanged {
    private static final String KEY_USER_ID = "keyId";
    private static final String KEY_PROFILE_DATA = "profileData";


    @BindView(R.id.profile_name)
    protected TextView profileName;

    @BindView(R.id.profile_login)
    protected TextView profileLogin;

    @BindView(R.id.profile_image)
    protected ImageView profileImage;

    @BindView(R.id.recipes_count)
    protected TextView itemCount;

    @BindView(R.id.btn_subscibe)
    protected Button subscibe;

    @BindView(R.id.subscribers_count)
    protected TextView subscribersCount;

    private String mUserId;

    private ProfileViewModel mProfileViewModel;

    private ProfileData mProfileData;

    public static Fragment getInstance(String userId) {
        Fragment fragment = new ViewProfileFragment();

        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getString(KEY_USER_ID);
        }

        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        ButterKnife.bind(this, view);


        mProfileViewModel.getResourceLiveData().observe(this, res -> {
            if (res != null) {
                switch (res.status) {
                    case SUCCESS:
                        mProfileData = res.data;
                        updateUi();
                        break;
                    case LOADING:
                        break;
                    case ERROR:
                        break;
                }
            }
        });

        if (mProfileViewModel.getResourceLiveData().getValue() == null)
            mProfileViewModel.loadDataAndSync(mUserId);

        if (savedInstanceState == null) {
            addRecipesFragment();
        }

        return view;
    }

    private void addRecipesFragment() {
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_user_recipes);

        if (fragment == null) {
            fragment = UserRecipesList.getInstance(mUserId, false);
            fm.beginTransaction()
                    .add(R.id.container_user_recipes, fragment)
                    .commit();
        }
    }

    private void updateUi() {

        profileName.setText(mProfileData.firstName + " " + mProfileData.secondName);

        profileLogin.setText(mProfileData.login);

        subscribersCount.setText(String.valueOf(mProfileData.subscribersCount));

        if (mProfileData.userUid.equals(FirebaseHelper.getUid())) {
            subscibe.setVisibility(View.GONE);
        } else {
            if (mProfileData.subscribers.containsKey(FirebaseHelper.getUid())) {
                subscibe.setText("Unsubscribe");
            } else subscibe.setText("Subscribe");

            subscibe.setVisibility(View.VISIBLE);
        }

        setTitle();

        setImage(mProfileData);
    }

    private void setImage(ProfileData profileData) {
        if (profileData.imageURL != null) {
            ImageData imageData = new ImageData(profileData.imageURL, profileData.lastTimeUpdate);

            GlideImageLoader.getInstance().loadNormalCircularImage(getActivity(),
                    profileImage,
                    imageData);
        } else profileImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
    }

    private void setTitle() {
        ((SingleFragmentActivity) getActivity()).getSupportActionBar().setTitle(mProfileData.firstName + " " + mProfileData.secondName);
        ((SingleFragmentActivity) getActivity()).getSupportActionBar().setSubtitle((mProfileData.login));
    }

    @OnClick(R.id.btn_subscibe)
    protected void subscribeClick() {
        FirebaseHelper.Subscriptions.updateSubscr(FirebaseHelper.getUid(), mProfileData.userUid);
    }

    @Override
    public void onItemCountChanged(int itemCount) {
        this.itemCount.setText(String.valueOf(itemCount));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_PROFILE_DATA, mProfileData);
    }
}
