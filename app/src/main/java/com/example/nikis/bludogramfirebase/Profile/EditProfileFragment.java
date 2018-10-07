package com.example.nikis.bludogramfirebase.Profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.Helpers.MatisseHelper;
import com.example.nikis.bludogramfirebase.Helpers.PermissionHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Resource;
import com.zhihu.matisse.Matisse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    protected static final String TAG_CREATE_PROFILE = "BG_editProfile";
    protected static final String KEY_GENDER = "gender";
    private static final int PERMISSION_REQUEST_CODE = 5;
    private static final int REQUEST_CODE_CHOOSE = 6;
    private static final String KEY_IMAGE_PATH = "imagePath";

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

    private ProfileViewModel mProfileViewModel;

    private final String[] mPermissions = new String[]{READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};

    private PermissionHelper mPermissionHelper;
    private String mImagePath;

    @OnClick(R.id.btn_update) void test(){
        mProfileViewModel.UpdateData(createProfileData(), mImagePath);
    }



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
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mProfileViewModel.init();
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
            if (isInProgress) {
                showProgress();
            }
        }

        mProfileViewModel.getResourceLiveData().observe(this, (dataResource)->{
            if (dataResource != null && dataResource.status == Resource.Status.SUCCESS) {
                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                hideProgress();
            }else if (dataResource != null && dataResource.status == Resource.Status.LOADING) {
                showProgress();
            }else if (dataResource != null && dataResource.status == Resource.Status.ERROR) {
                hideProgress();
                Toast.makeText(getActivity(), dataResource.exception.getMessage(), Toast.LENGTH_SHORT).show();
            }else {
                hideProgress();
            }
        });

        return v;
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
                imageViewClick();
                break;
        }
    }


    private void showProgress() {
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgress(){
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

    private void imageViewClick() {
        if(mPermissionHelper.isPermissionsGranted()){
            startGallery();
        }else mPermissionHelper.requestPermissionsFragment(PERMISSION_REQUEST_CODE);

    }

    private void startGallery() {
        MatisseHelper.getMatisseBuilder(this, 1)
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);

            mImagePath = MatisseHelper.getRealPathFromURIPath(getActivity(), mSelected.get(0));
            setImage();
        }
    }

    private void setImage() {
        if(mImagePath != null)
        GlideApp.with(this)
                .load(mImagePath)
                .override(1080,1080)
                .apply(RequestOptions.circleCropTransform())
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
            Log.d(TAG_CREATE_PROFILE, "isValidateForm: gender indefinite: " + gender);
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

    }

    private ProfileData createProfileData(){
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String login = edtLogin.getText().toString();

        return new ProfileData(firstName, lastName, login, gender);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IMAGE_PATH, mImagePath);
        outState.putString(KEY_GENDER, gender);

    }
}
