package com.example.nikis.bludogramfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.Profile.CreateNewProfile;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.nikis.bludogramfirebase.LoginActivity.State.STATE_IN_PROGRESS;
import static com.example.nikis.bludogramfirebase.LoginActivity.State.STATE_START_VERIFICATION;
import static com.example.nikis.bludogramfirebase.LoginActivity.State.STATE_START_VERIFICATION_WITH_CODE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = "BG_verification";
    private static final String KEY_VERIFY_IN_PROGRESS = "9671";
    private EditText edtPhone, edtCode;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private OnVerificationStateChangedCallbacks mCallbacks;
    private ValueEventListener mListenerIsUserExist;
    private String phoneNumber;

    private FirebaseAuth mAuth;

    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageButton btnStartVerification = findViewById(R.id.btn_startVerification);
        ImageButton btnStartVerificationWithCode = findViewById(R.id.btn_startVerificationWithCode);
        Button btnResend = findViewById(R.id.btn_resend);
        ImageButton btnBackToStartVerification = findViewById(R.id.btn_back);

        edtPhone = findViewById(R.id.edt_phone);
        edtCode = findViewById(R.id.edt_code);

        progressBar = findViewById(R.id.progressBar_loading);

        mAuth = FirebaseAuth.getInstance();

        initCallbacks();

        btnStartVerificationWithCode.setOnClickListener(this);
        btnStartVerification.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        btnBackToStartVerification.setOnClickListener(this);
    }

    private void initCallbacks(){
        mCallbacks = new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted: ");

                edtCode.setText(credential.getSmsCode());
                mVerificationInProgress = false;

                signInWithPhoneAuthCredential(credential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    edtPhone.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    showError(e.getMessage());
                }else if(e instanceof FirebaseNetworkException){
                    Toast.makeText(LoginActivity.this
                            ,"An error occurred. Check the Internet" +
                                    " connection and try again",
                            LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                //TODO check this string
                mVerificationInProgress = false;

                updateUi(STATE_START_VERIFICATION_WITH_CODE);
                startTimer();
            }
        };

        mListenerIsUserExist = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    boolean isAlreadyRegistered = dataSnapshot.hasChild("users/" + user.getUid());
                    Log.d(TAG, "onDataChange: " + isAlreadyRegistered);
                    if (!isAlreadyRegistered) {
                        startFirstEditProfile();
                    } else {
                        startMainActivity();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, databaseError.getMessage(),
                        LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential: success");

                        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                        myRef.addListenerForSingleValueEvent(mListenerIsUserExist);

                    } else {
                        Log.w(TAG, "signInWithCredential: failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            edtCode.setError("Invalid code");
                        else if(task.getException() instanceof FirebaseNetworkException){
                            Log.d(TAG, "onLoadFinished:2) " + task.getException().getMessage());

                            Toast.makeText(LoginActivity.this
                                    ,"An error occurred. Check the Internet" +
                                            " connection and try again",
                                    LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showError(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String buttonStringAccept = "Ok";
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(buttonStringAccept, null);
        alertDialog.show();
    }

    private void startFirstEditProfile(){
        Intent intent = new Intent(LoginActivity.this,
                CreateNewProfile.class);
        startActivity(intent);
        finish();
    }
    private void startMainActivity(){
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            startMainActivity();
        }
        if (mVerificationInProgress && isValidateForm(STATE_START_VERIFICATION)) {
            startPhoneNumberVerification(edtPhone.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_startVerification:
                if(isValidateForm(STATE_START_VERIFICATION)){
                    phoneNumber = edtPhone.getText().toString();
                    startPhoneNumberVerification(phoneNumber);

                    updateUi(STATE_IN_PROGRESS);
                }
                break;
            case R.id.btn_startVerificationWithCode:
                if(isValidateForm(STATE_START_VERIFICATION_WITH_CODE))
                    verifyPhoneNumberWithCode(mVerificationId, edtCode.getText().toString());
                break;
            case R.id.btn_resend:
                resendVerificationCode(phoneNumber, mResendToken);
                updateUi(STATE_IN_PROGRESS);
                break;
            case R.id.btn_back:
                updateUi(STATE_START_VERIFICATION);
                stopTimer();
                break;
        }
    }

    private boolean isValidateForm(State state){
        boolean isValidate;
        if(state == STATE_START_VERIFICATION){
            isValidate = isValidateStartVerification();
        }else {
            isValidate = isValidateStartVerificationWithCode();
        }
        return isValidate;
    }

    private boolean isValidateStartVerification(){
        boolean isValidate = false;
        String phone = edtPhone.getText().toString();
        if(TextUtils.isEmpty(phone)){
            edtPhone.setError(getResources().getString(R.string.error_field_required));
        }else {
            if(phone.length() < 6){
                edtPhone.setError("Incorrectly entered phone number");
            }else {
                edtPhone.setError(null);
                isValidate = true;
            }
        }
        return isValidate;
    }
    private boolean isValidateStartVerificationWithCode(){
        boolean isValidate = false;
        String code = edtCode.getText().toString();
        if(TextUtils.isEmpty(code)){
            edtCode.setError(getResources().getString(R.string.error_field_required));
        }else {
            edtCode.setError(null);
            isValidate = true;
        }
        return isValidate;
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mVerificationInProgress = true;
        Log.d(TAG, "startPhoneNumberVerification: " + phoneNumber);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
        Log.d(TAG, "verifyPhoneNumberWithCode: ");
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);

        Log.d(TAG, "resendVerificationCode: " + phoneNumber);
    }

    public void startTimer(){
        setStandardStatusWithText(getString(R.string.status_request_after_while));

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTick(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                timerFinish();
            }
        }.start();
        Log.d(TAG, "startTimer: ");
    }


    private void setStandardStatusWithText(String text){
        TextView status = findViewById(R.id.tv_status);
        status.setText(getResources().getString(R.string.status)
                + " " + text);
    }

    private void timerTick(long millisUntilFinished){
        setTimerText("Wait " + millisUntilFinished / 1000);
    }

    private void setTimerText(String text){
        TextView mTimer = findViewById(R.id.tv_timer);
        mTimer.setText(text);
    }

    private void timerFinish(){
        setButtonResendEnabled(true);

        setTimerText("");

        setStandardStatusWithText(getString(R.string.status_request_new_code_available));
    }

    private void setButtonResendEnabled(boolean isEnabled){
        Button btnResend = findViewById(R.id.btn_resend);
        btnResend.setEnabled(isEnabled);

        if(isEnabled)
            btnResend.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        else
            btnResend.setBackgroundColor(getResources().getColor(R.color.grey_300));
    }

    private void updateUi(State toState){
        switch (toState){
            case STATE_START_VERIFICATION:
                updateUiToStartVerification();
                break;
            case STATE_START_VERIFICATION_WITH_CODE:
                updateUiToStartVerificationWithCode();
                break;
            case STATE_IN_PROGRESS:
                updateUiToShowProgress();
                break;
        }
        edtPhone.setError(null);
        edtCode.setError(null);
    }

    private void updateUiToStartVerification(){
        findViewById(R.id.enter_phone).setVisibility(View.VISIBLE);
        findViewById(R.id.other_sign_in).setVisibility(View.VISIBLE);
        findViewById(R.id.verification_fields).setVisibility(View.GONE);
        Log.d(TAG, "updateUi: To state StartVerification");
    }
    private void updateUiToStartVerificationWithCode(){
        if(!mVerificationInProgress)
            progressBar.setVisibility(View.GONE);

        findViewById(R.id.enter_phone).setVisibility(View.GONE);
        //findViewById(R.id.other_sign_in).setVisibility(View.INVISIBLE);
        findViewById(R.id.verification_fields).setVisibility(View.VISIBLE);

        setButtonResendEnabled(false);

        startTimer();
        Log.d(TAG, "updateUi: To state StartVerificationWithCode");
    }
    private void updateUiToShowProgress(){
        findViewById(R.id.enter_phone).setVisibility(View.GONE);
        //findViewById(R.id.other_sign_in).setVisibility(View.GONE);
        findViewById(R.id.verification_fields).setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            setTimerText("");
            setStandardStatusWithText("");
        }
        Log.d(TAG, "stopTimer: ");
    }

    enum State{
        STATE_START_VERIFICATION,
        STATE_START_VERIFICATION_WITH_CODE,
        STATE_IN_PROGRESS
    }
}
