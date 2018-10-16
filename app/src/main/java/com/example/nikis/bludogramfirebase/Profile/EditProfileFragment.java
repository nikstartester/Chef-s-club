package com.example.nikis.bludogramfirebase.Profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.BaseFragments.BaseFragmentWithMatisseGallery;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.Helpers.MatisseHelper;
import com.example.nikis.bludogramfirebase.Helpers.PermissionHelper;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.Repository.Local.LocalUserProfile;
import com.example.nikis.bludogramfirebase.Profile.Repository.ProfileRepository;
import com.example.nikis.bludogramfirebase.Profile.Upload.ProfileUploaderService;
import com.example.nikis.bludogramfirebase.Profile.ViewModel.ProfileViewModel;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Resource;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.RecipesListFragment.getUid;


public class EditProfileFragment extends BaseFragmentWithMatisseGallery implements View.OnClickListener {
    protected static final String KEY_GENDER = "gender";
    private static final int PERMISSION_REQUEST_CODE = 5;
    private static final int REQUEST_CODE_CHOOSE = 6;
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

    @BindView(R.id.filter)
    protected RelativeLayout filterForProgress;

    protected String gender;

    private boolean isInProgress;

    private final String[] mPermissions = new String[]{READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};

    private PermissionHelper mPermissionHelper;

    private ProfileUploaderBroadcastReceiver mBroadcastReceiver;

    private ProfileViewModel mProfileViewModel;

    private String mImagePath;

    @OnClick(R.id.btn_update) void test(){
        startEditProfile();
    }

    @OnClick(R.id.btn_deleteFromDisk) void delete(){
        new Thread(() -> ((App)getActivity().getApplication())
                .getDatabase()
                .profileDao()
                .deleteByUserUid(getUid())).start();

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionHelper = new PermissionHelper(this, mPermissions);
        if(savedInstanceState != null){
            mImagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
            gender = savedInstanceState.getString(KEY_GENDER);
        }else {
            gender = ProfileData.GENDER_NONE;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ButterKnife.bind(this, v);

        rdbMale.setOnClickListener(this);
        rdnFemale.setOnClickListener(this);
        circularImageView.setOnClickListener(this);

        if (savedInstanceState != null) {
            setImage();

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

        mProfileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        mProfileViewModel.getResourceLiveData().observe(this, resource -> {
            if(resource != null){
                if(resource.status == Resource.Status.SUCCESS){
                    ProfileData profileData = resource.data;
                    if(profileData != null){
                        setData(profileData);
                        if(profileData.imageURL != null){
                            setImageFromCacheOrFireBaseStorage(profileData.imageURL,
                                    profileData.timeLastImageUpdate);

                            mImagePath = profileData.imageURL;
                        }
                    }
                }
            }
        });
        mProfileViewModel.loadData(ProfileRepository.getFireBaseAuthUid());

        return v;
    }

    private void setData(ProfileData profileData){
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
                super.startMatisseGallery(1);
                break;
        }
    }


    private void showProgress() {
        isInProgress = true;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgress(){
        isInProgress = false;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    protected void radioButtonMaleClick() {
        rdbMale.setChecked(true);
        rdnFemale.setChecked(false);
        gender = ProfileData.GENDER_MALE;
    }

    protected void radioButtonFemaleClick() {
        rdbMale.setChecked(false);
        rdnFemale.setChecked(true);
        gender = ProfileData.GENDER_FEMALE;
    }


    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        mImagePath = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(0));
        setImage();
    }

    private void setImage() {
        if(mImagePath != null)
        GlideApp.with(this)
                .load(mImagePath)
                .override(720,720)
                .apply(RequestOptions.circleCropTransform())
                .into(circularImageView);
    }

    private void setImageFromCacheOrFireBaseStorage(String imageURL, String lastTimeToUpdate){
        StorageReference storageReference = FirebaseReferences.getStorageReference(imageURL);

        GlideApp.with(this)
                .load(storageReference)
                .override(720,720)
                .thumbnail(0.2f)
                .error(R.drawable.ic_add_a_photo_blue_108dp)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.circleCropTransform())
                .signature(new ObjectKey(lastTimeToUpdate))
                .into(circularImageView);
    }

////////////////////////////////////////////////

    public void startEditProfile(){
        if(isValidateForm()){
            startEdit();
        }
    }

    private boolean isValidateForm() {
        boolean isValidate = false;

        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String login = edtLogin.getText().toString();

        String error = getResources().getString(R.string.error_field_required);

        if(TextUtils.isEmpty(firstName)){
            edtFirstName.setError(error);
        } else {
            edtFirstName.setError(null);
            isValidate = true;
        }

        if (TextUtils.isEmpty(lastName)){
            edtLastName.setError(error);
            isValidate = false;
        }else {
            edtFirstName.setError(null);
        }

        if(TextUtils.isEmpty(login)){
            edtLogin.setError(error);
            isValidate = false;
        }else edtLogin.setError(null);

        if(gender.equals(ProfileData.GENDER_NONE)){
            indicateToChoiceGender();
            isValidate = false;

        }else {
            removeIndicateToChoiceGender();
        }
        return isValidate;
    }

    private void indicateToChoiceGender(){
        //TODO написать метод для подсвечивания "RadioGroup", если пол не выбран
        Toast.makeText(getActivity(), "Choose gender!", Toast.LENGTH_LONG).show();
    }
    private void removeIndicateToChoiceGender(){
        //TODO написать метод для снятия подсвечивания "RadioGroup"
    }

    private void startEdit() {
        getActivity().startService(ProfileUploaderService.getIntent(getActivity(), createProfileData()));
    }

    private ProfileData createProfileData(){
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

    private class ProfileUploaderBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Resource<ProfileData> dataResource = intent.getParcelableExtra(ProfileUploaderService.EXTRA_RESOURCE);
            if (dataResource != null && dataResource.status == Resource.Status.SUCCESS) {
                onSuccess(dataResource);
            }else if (dataResource != null && dataResource.status == Resource.Status.LOADING) {
                showProgress();
            }else if (dataResource != null && dataResource.status == Resource.Status.ERROR) {
                hideProgress();
                Toast.makeText(getActivity(), dataResource.exception.getMessage(), Toast.LENGTH_SHORT).show();
            }else {
                hideProgress();
            }
        }

        private void onSuccess(Resource<ProfileData> resource){
            hideProgress();

            if(resource.data != null)
                new LocalUserProfile(getActivity().getApplication()).save(resource.data, null);

        }


    }
}
