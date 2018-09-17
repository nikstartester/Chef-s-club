package com.example.nikis.bludogramfirebase.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.Exceptions.NetworkException;
import com.example.nikis.bludogramfirebase.MainActivity;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.UserData.UserData.Gender;
import com.example.nikis.bludogramfirebase.UserData.UserData;

import static com.example.nikis.bludogramfirebase.GenderUtils.genderToString;
import static com.example.nikis.bludogramfirebase.GenderUtils.stringToGender;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.GENDER_FEMALE;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.GENDER_MALE;
import static com.example.nikis.bludogramfirebase.UserData.UserData.Gender.INDEFINITE;

public class CreateNewProfile extends FirebaseProfile implements View.OnClickListener{
    protected static final String TAG_CREATE_PROFILE = "BG_editProfile";
    protected static final String KEY_GENDER = "gender";

    protected EditText edtFirstName, edtLastName, edtLogin;
    protected RadioButton rdbMale;
    protected RadioButton rdnFemale;
    protected ImageView circularImageView;

    private RelativeLayout filterForProgress;

    protected Gender gender;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtFirstName = findViewById(R.id.edt_firstName);
        edtLastName = findViewById(R.id.edt_lastName);
        edtLogin = findViewById(R.id.edt_login);

        rdbMale = findViewById(R.id.radBtn_male);
        rdnFemale = findViewById(R.id.radBtn_female);
        circularImageView = findViewById(R.id.circularImageView);

        filterForProgress = findViewById(R.id.filter);

        rdbMale.setOnClickListener(this);
        rdnFemale.setOnClickListener(this);
        circularImageView.setOnClickListener(this);

        if(savedInstanceState != null){
            gender = stringToGender(savedInstanceState.getString(KEY_GENDER));

            super.setImageWithSavedParamIfPathSelected(circularImageView);

            if(super.isInProgress){
                showProgress();
            }
        }else {
            gender = INDEFINITE;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.radBtn_male:
                radioButtonMaleClick();
                break;
            case R.id.radBtn_female:
                radioButtonFemaleClick();
                break;
            case R.id.circularImageView:
                super.imageViewClick(circularImageView, true);
                break;
        }
    }
    protected void radioButtonMaleClick(){
        rdbMale.setChecked(true);
        rdnFemale.setChecked(false);
        gender = GENDER_MALE;
    }
    protected void radioButtonFemaleClick(){
        rdbMale.setChecked(false);
        rdnFemale.setChecked(true);
        gender = GENDER_FEMALE;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.only_done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_done){
            startCreateProfileIfValidateForm();
        }
        return super.onOptionsItemSelected(item);
    }
    private void startCreateProfileIfValidateForm(){
        if(isValidateForm()){

            showProgress();

            UserData userData = new UserData(edtFirstName.getText().toString(),
                    edtLastName.getText().toString(), edtLogin.getText().toString(), gender);
            try {
                super.createProfileWithImage(userData);
            } catch (NetworkException e) {
                hideProgress();

                Snackbar.make(edtLastName, e.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("retry", v -> startCreateProfileIfValidateForm())
                        .show();

            } catch (ExistLoginException e) {
                hideProgress();

                edtLogin.setError(e.getMessage());
            }
        }
    }

    private boolean isValidateForm(){
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

        if(gender == INDEFINITE){
            indicateToChoiceGender();
            isValidate = false;
            Log.d(TAG_CREATE_PROFILE, "isValidateForm: gender indefinite: " + gender.name());
        }else {
            removeIndicateToChoiceGender();
        }
        return isValidate;
    }
    private void indicateToChoiceGender(){
        //TODO написать метод для подсвечивания "RadioGroup", если пол не выбран
        Toast.makeText(this, "Choose gender!", Toast.LENGTH_LONG).show();
    }
    private void removeIndicateToChoiceGender(){
        //TODO написать метод для снятия подсвечивания "RadioGroup"
    }
    private void showProgress(){
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgress(){
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String genderString = genderToString(gender);
        outState.putString(KEY_GENDER, genderString);
    }

    @Override
    public void onAllTasksComplete() {

        hideProgress();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
