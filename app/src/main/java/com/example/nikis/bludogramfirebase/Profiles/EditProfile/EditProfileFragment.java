package com.example.nikis.bludogramfirebase.Profiles.EditProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.BaseFragments.BaseFragmentWithImageChoose;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Helpers.MatisseHelper;
import com.example.nikis.bludogramfirebase.Helpers.NetworkHelper;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.example.nikis.bludogramfirebase.Images.ImageLoaders.GlideImageLoader;
import com.example.nikis.bludogramfirebase.Main.MainActivity;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.Upload.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.Profiles.Upload.ProfileUploaderService;
import com.example.nikis.bludogramfirebase.Profiles.ViewModel.ProfileViewModel;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Settings.SettingsCacheFragment;

import java.io.File;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nikis.bludogramfirebase.Constants.Constants.Login.KEY_IS_ALREADY_REGISTERED;


public class EditProfileFragment extends BaseFragmentWithImageChoose implements View.OnClickListener {
    private static final String KEY_GENDER = "gender";
    private static final String KEY_IMAGE_PATH = "imagePath";
    private static final String KEY_IS_IN_PROGRESS = "isProgress";

    @BindView(R.id.edt_firstName)
    protected EditText edtFirstName;

    @BindView(R.id.edt_lastName)
    protected EditText edtLastName;

    @BindView(R.id.edt_login)
    protected EditText edtLogin;

    @BindView(R.id.radBtn_male)
    protected RadioButton rdbMale;

    @BindView(R.id.radBtn_female)
    protected RadioButton rdnFemale;

    @BindView(R.id.circularImageView)
    protected ImageView circularImageView;

    @BindView(R.id.seekBar_skill)
    protected SeekBar seekBarSkillTroll;

    @BindView(R.id.filter)
    protected RelativeLayout filterForProgress;

    private String gender;

    private boolean isInProgress;

    private ProfileUploaderBroadcastReceiver mBroadcastReceiver;

    private String mImagePath;

    private OnSuccessListener mOnSuccessListener;

    public static boolean isProfileAlreadyCreated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_ALREADY_REGISTERED, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mImagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
            gender = savedInstanceState.getString(KEY_GENDER);
        } else {
            gender = ProfileData.GENDER_NONE;
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ButterKnife.bind(this, v);

        rdbMale.setOnClickListener(this);
        rdnFemale.setOnClickListener(this);
        circularImageView.setOnClickListener(this);

        seekBarSkillTroll.setProgress(new Random().nextInt(98) + 1);

        if (savedInstanceState != null) {
            setImage(mImagePath, Constants.ImageConstants.DEF_TIME);

            isInProgress = savedInstanceState.getBoolean(KEY_IS_IN_PROGRESS);

            if (isInProgress) {
                showProgress();
            }
        }

        IntentFilter intentFilter = new IntentFilter(
                ProfileUploaderService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        getActivity().registerReceiver(mBroadcastReceiver = new ProfileUploaderBroadcastReceiver(),
                intentFilter);

        ProfileViewModel profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        profileViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {
                    onSuccessLoaded(resource);
                }
            }
        });
        if (profileViewModel.getResourceLiveData().getValue() == null)
            profileViewModel.loadDataWithSaver(FirebaseHelper.getUid());

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnSuccessListener) {
            mOnSuccessListener = (OnSuccessListener) context;
        }
    }

    private void onSuccessLoaded(ParcResourceByParc<ProfileData> resource) {
        ProfileData profileData = resource.data;
        if (profileData != null) {
            setData(profileData);
            if (profileData.imageURL != null) {
                setImage(profileData.imageURL, profileData.lastTimeUpdate);

                mImagePath = profileData.imageURL;
            }
        }
    }

    private void setData(ProfileData profileData) {
        edtLogin.setText(profileData.login);
        edtFirstName.setText(profileData.firstName);
        edtLastName.setText(profileData.secondName);

        if (profileData.gender.equals(ProfileData.GENDER_MALE)) {
            radioButtonMaleClick();
        } else {
            radioButtonFemaleClick();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            startEditProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radBtn_male:
                radioButtonMaleClick();
                break;
            case R.id.radBtn_female:
                radioButtonFemaleClick();
                break;
            case R.id.circularImageView:
                //super.startMatisseGallery(1);
                super.showChooseDialog(1);
                break;
        }
    }

    private void showProgress() {
        isInProgress = true;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        isInProgress = false;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void radioButtonMaleClick() {
        rdbMale.setChecked(true);
        rdnFemale.setChecked(false);
        gender = ProfileData.GENDER_MALE;
    }

    private void radioButtonFemaleClick() {
        rdbMale.setChecked(false);
        rdnFemale.setChecked(true);
        gender = ProfileData.GENDER_FEMALE;
    }

    @Override
    protected String getParentDirectoryPath() {
        return Constants.Files.getDirectoryForEditProfileImages(getActivity());
    }

    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        super.addToDeleteIfCapture(mImagePath);

        mImagePath = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(0));

        setImage(mImagePath, Constants.ImageConstants.DEF_TIME);

        super.deleteOldCaptures();
    }

////////////////////////////////////////////////

    private void setImage(String imagePath, long lastTimeUpdate) {
        if (imagePath != null) {
            ImageData imageData = new ImageData(imagePath, lastTimeUpdate);

            GlideImageLoader.getInstance().loadNormalCircularImage(getActivity(),
                    circularImageView,
                    imageData);
        } //else userProfileImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
    }

    private void startEditProfile() {
        if (isValidateForm()) {
            startEdit();
        }
    }

    private boolean isValidateForm() {
        boolean isValidate = false;

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String login = edtLogin.getText().toString();

        String error = getResources().getString(R.string.error_field_required);

        if (TextUtils.isEmpty(firstName)) {
            edtFirstName.setError(error);
        } else {
            edtFirstName.setError(null);
            isValidate = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            edtLastName.setError(error);
            isValidate = false;
        } else {
            edtFirstName.setError(null);
        }

        if (TextUtils.isEmpty(login)) {
            edtLogin.setError(error);
            isValidate = false;
        } else edtLogin.setError(null);

        if (gender.equals(ProfileData.GENDER_NONE)) {
            indicateToChoiceGender();
            isValidate = false;

        } else {
            removeIndicateToChoiceGender();
        }
        return isValidate;
    }

    private void indicateToChoiceGender() {
        Toast.makeText(getActivity(), "Choose gender!", Toast.LENGTH_LONG).show();
    }

    private void removeIndicateToChoiceGender() {

    }

    private void startEdit() {
        if (NetworkHelper.isConnected(getActivity())) {
            getActivity().startService(ProfileUploaderService.getIntent(getActivity(), createProfileData()));
        } else {
            String error = getString(R.string.network_error);
            Snackbar.make(edtLogin, error, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.try_again), (v) -> {
                        startEdit();
                    })
                    .show();
        }

    }

    private ProfileData createProfileData() {
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String login = edtLogin.getText().toString();

        return new ProfileData(firstName, lastName, login, gender, mImagePath);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IMAGE_PATH, mImagePath);
        outState.putString(KEY_GENDER, gender);
        outState.putBoolean(KEY_IS_IN_PROGRESS, isInProgress);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.only_done_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onSuccessUploaded(boolean isAlreadyCreated) {

        SettingsCacheFragment.deleteDir(new File(Constants.Files.getDirectoryForEditProfileImages(getActivity())));

        if (!isAlreadyCreated) {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean(KEY_IS_ALREADY_REGISTERED, true);

            editor.apply();

            startMainActivity();

        } else getActivity().onBackPressed();
    }

    public void onErrorUploaded(ParcResourceByParc<ProfileData> resource) {
        if (resource.exception instanceof ExistLoginException) {
            edtLogin.setError("Login already exist");
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(),
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        getActivity().finish();
    }

    interface OnSuccessListener {
        void onSuccess();
    }

    private class ProfileUploaderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ParcResourceByParc<ProfileData> dataResource = intent.getParcelableExtra(ProfileUploaderService.EXTRA_RESOURCE);
            if (dataResource != null && dataResource.status == ParcResourceByParc.Status.SUCCESS) {
                onSuccess(dataResource);
            } else if (dataResource != null && dataResource.status == ParcResourceByParc.Status.LOADING) {
                showProgress();
            } else if (dataResource != null && dataResource.status == ParcResourceByParc.Status.ERROR) {
                onError(dataResource);
            } else {
                hideProgress();
            }
        }

        private void onError(ParcResourceByParc<ProfileData> dataResource) {
            hideProgress();

            EditProfileFragment.this.onErrorUploaded(dataResource);
        }

        private void onSuccess(ParcResourceByParc<ProfileData> resource) {
            hideProgress();

            if (mOnSuccessListener != null) {
                mOnSuccessListener.onSuccess();
            }
            EditProfileFragment.this.onSuccessUploaded(isProfileAlreadyCreated(getActivity()));
        }
    }
}
