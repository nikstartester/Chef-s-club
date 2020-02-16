package com.xando.chefsclub.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.concurrent.HandlerExecutor;
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
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.helper.Keyboard;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.main.MainActivity;
import com.xando.chefsclub.profiles.editprofile.EditProfileActivityTest;
import com.xando.chefsclub.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xando.chefsclub.constants.Constants.Login.KEY_IS_ALREADY_REGISTERED;
import static com.xando.chefsclub.helper.FirebaseHelper.getUid;
import static com.xando.chefsclub.login.LoginActivity.State.STATE_IN_PROGRESS;
import static com.xando.chefsclub.login.LoginActivity.State.STATE_START_VERIFICATION;
import static com.xando.chefsclub.login.LoginActivity.State.STATE_START_VERIFICATION_WITH_CODE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "9671";

    @BindView(R.id.edt_phone)
    protected EditText edtPhone;
    @BindView(R.id.edt_code)
    protected EditText edtCode;
    @BindView(R.id.progressBar_loading)
    protected ProgressBar progressBar;

    private boolean isToShowProgress = false;

    private boolean isCanSendRequest = true;

    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private OnVerificationStateChangedCallbacks mCallbacks;

    private ValueEventListener mListenerIsUserExist;

    private String mPhoneNumber;

    private FirebaseAuth mAuth;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnStartVerification = findViewById(R.id.btn_startVerification);
        ImageButton btnStartVerificationWithCode = findViewById(R.id.btn_startVerificationWithCode);
        Button btnResend = findViewById(R.id.btn_resend);
        ImageButton btnBackToStartVerification = findViewById(R.id.btn_back);
        ImageButton btnInfo = findViewById(R.id.login_info);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        initCallbacks();

        edtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnStartVerificationWithCode.setOnClickListener(this);
        btnStartVerification.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        btnBackToStartVerification.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
    }

    private void initCallbacks() {
        mCallbacks = new VerificationCallbacks();

        mListenerIsUserExist = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    boolean isAlreadyRegistered = dataSnapshot.getValue() != null;

                    SharedPreferences sharedPreferences = getSharedPreferences(
                            Constants.Settings.APP_PREFERENCES, MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (!isAlreadyRegistered) {
                        editor.putBoolean(KEY_IS_ALREADY_REGISTERED, false);

                        updateUi(STATE_START_VERIFICATION_WITH_CODE);

                        startFirstEditProfile();

                    } else {
                        editor.putBoolean(KEY_IS_ALREADY_REGISTERED, true);

                        startMainActivity();
                    }

                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                updateUi(STATE_START_VERIFICATION_WITH_CODE);

                Toast.makeText(LoginActivity.this, databaseError.getMessage(),
                        LENGTH_SHORT).show();
                //Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new HandlerExecutor(getMainLooper()), task -> {
                    if (task.isSuccessful()) {
                        //Log.d(TAG, "signInWithCredential: success");

                        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

                        myRef.child("users/" + getUid())
                                .addListenerForSingleValueEvent(mListenerIsUserExist);

                    } else {
                        updateUi(STATE_START_VERIFICATION_WITH_CODE);

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            edtCode.setError(getString(R.string.invalid_code));
                        else if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(LoginActivity.this
                                    , getString(R.string.network_error),
                                    LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showError(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String buttonStringAccept = getString(R.string.button_ok);
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(buttonStringAccept, null);
        alertDialog.show();
    }

    private void startNext() {
        SharedPreferences preferences = getSharedPreferences(Constants.Settings.APP_PREFERENCES, MODE_PRIVATE);

        boolean isAlreadyRegistered = preferences.getBoolean(KEY_IS_ALREADY_REGISTERED, false);

        if (isAlreadyRegistered) {
            startMainActivity();
        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) edtPhone.setText(user.getPhoneNumber());
        }
    }

    private void startFirstEditProfile() {
        startActivity(new Intent(this, EditProfileActivityTest.class));
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startNext();
        } else if (isToShowProgress && isValidateForm(STATE_START_VERIFICATION)) {
            startPhoneNumberVerification(edtPhone.getText().toString());

            updateUi(STATE_IN_PROGRESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isToShowProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, isToShowProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startVerification:
                if (isValidateForm(STATE_START_VERIFICATION)) {

                    Keyboard.hideKeyboardFrom(this, edtPhone);

                    String currPhoneNumber = edtPhone.getText().toString();

                    if (mPhoneNumber == null || !mPhoneNumber.equals(currPhoneNumber))
                        isCanSendRequest = true;

                    if (isCanSendRequest) {
                        mPhoneNumber = currPhoneNumber;

                        if (NetworkHelper.isConnected(this))
                            updateUi(STATE_IN_PROGRESS);
                        else showNetworkError();

                        startPhoneNumberVerification(mPhoneNumber);
                    } else {
                        updateUi(STATE_START_VERIFICATION_WITH_CODE);
                    }
                }
                break;
            case R.id.btn_startVerificationWithCode:
                if (isValidateForm(STATE_START_VERIFICATION_WITH_CODE)) {

                    Keyboard.hideKeyboardFrom(this, edtCode);

                    if (NetworkHelper.isConnected(this))
                        updateUi(STATE_IN_PROGRESS);
                    else showNetworkError();

                    verifyPhoneNumberWithCode(mVerificationId, edtCode.getText().toString());
                }
                break;
            case R.id.btn_resend:
                if (NetworkHelper.isConnected(this)) {
                    resendVerificationCode(mPhoneNumber, mResendToken);
                    updateUi(STATE_IN_PROGRESS);
                } else showNetworkError();
                break;
            case R.id.btn_back:
                updateUi(STATE_START_VERIFICATION);
                break;
            case R.id.login_info:
                Keyboard.hideKeyboardFrom(this, edtPhone);

                String text = "Enter your phone number and we'll send an SMS verification code.";

                Snackbar.make(edtPhone, text, Snackbar.LENGTH_LONG).show();

                break;
        }
    }

    private boolean isValidateForm(State state) {
        boolean isValidate;
        if (state == STATE_START_VERIFICATION) {
            isValidate = isValidateStartVerification();
        } else {
            isValidate = isValidateStartVerificationWithCode();
        }
        return isValidate;
    }

    private boolean isValidateStartVerification() {
        boolean isValidate = false;
        String phone = edtPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError(getResources().getString(R.string.error_field_required));
        } else {
            if (phone.length() < 6) {
                edtPhone.setError(getString(R.string.invalid_phone));
            } else {
                edtPhone.setError(null);
                isValidate = true;
            }
        }
        return isValidate;
    }

    private boolean isValidateStartVerificationWithCode() {
        boolean isValidate = false;
        String code = edtCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            edtCode.setError(getResources().getString(R.string.error_field_required));
        } else {
            edtCode.setError(null);
            isValidate = true;
        }
        return isValidate;
    }

    private void showNetworkError() {
        Snackbar.make(edtPhone, getString(R.string.network_error), Snackbar.LENGTH_SHORT).show();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                new HandlerExecutor(getMainLooper()),
                mCallbacks);

        isToShowProgress = true;
        //Log.d(TAG, "startPhoneNumberVerification: " + phoneNumber);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
        //Log.d(TAG, "verifyPhoneNumberWithCode: ");
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                new HandlerExecutor(getMainLooper()),
                mCallbacks,
                token);

        //Log.d(TAG, "resendVerificationCode: " + phoneNumber);
    }

    private void startTimer() {
        setStandardStatusWithText(getString(R.string.status_request_after_while));

        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerTick(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    timerFinish();
                }
            };
        } else {
            mCountDownTimer.cancel();
        }
        mCountDownTimer.start();

        //Log.d(TAG, "startTimer: ");
    }

    private void setStandardStatusWithText(String text) {
        TextView status = findViewById(R.id.tv_status);
        status.setText(getResources().getString(R.string.status)
                + " " + text);
    }

    private void timerTick(long millisUntilFinished) {
        setTimerText(getString(R.string.wait) + " " + millisUntilFinished / 1000);
    }

    private void setTimerText(String text) {
        TextView mTimer = findViewById(R.id.tv_timer);
        mTimer.setText(text);
    }

    private void timerFinish() {
        setButtonResendEnabled(true);

        setTimerText("");

        setStandardStatusWithText(getString(R.string.status_request_new_code_available));

        isCanSendRequest = true;
    }

    private void setButtonResendEnabled(boolean isEnabled) {
        Button btnResend = findViewById(R.id.btn_resend);
        btnResend.setEnabled(isEnabled);
    }

    private void updateUi(State toState) {
        switch (toState) {
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

    private void updateUiToStartVerification() {
        if (!isToShowProgress)
            progressBar.setVisibility(View.INVISIBLE);

        findViewById(R.id.enter_phone).setVisibility(View.VISIBLE);
        findViewById(R.id.other_sign_in).setVisibility(View.VISIBLE);
        findViewById(R.id.verification_fields).setVisibility(View.GONE);

        //Log.d(TAG, "updateAllUiViews: To state StartVerification");
    }

    private void updateUiToStartVerificationWithCode() {
        if (!isToShowProgress)
            progressBar.setVisibility(View.INVISIBLE);

        findViewById(R.id.enter_phone).setVisibility(View.GONE);
        findViewById(R.id.verification_fields).setVisibility(View.VISIBLE);

        //Log.d(TAG, "updateAllUiViews: To state StartVerificationWithCode");
    }

    private void updateUiToShowProgress() {
        findViewById(R.id.enter_phone).setVisibility(View.GONE);
        findViewById(R.id.verification_fields).setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            setTimerText("");
            setStandardStatusWithText("");
        }
        //Log.d(TAG, "stopTimer: ");
    }

    enum State {
        STATE_START_VERIFICATION,
        STATE_START_VERIFICATION_WITH_CODE,
        STATE_IN_PROGRESS
    }

    private class VerificationCallbacks extends OnVerificationStateChangedCallbacks {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            //Log.d(TAG, "onVerificationCompleted: ");

            edtCode.setText(credential.getSmsCode());

            signInWithPhoneAuthCredential(credential);
            updateUi(STATE_IN_PROGRESS);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //Log.w(TAG, "onVerificationFailed", e);

            isToShowProgress = false;

            updateUi(STATE_START_VERIFICATION);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                edtPhone.setError(getString(R.string.invalid_phone));
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                showError(getString(R.string.sms_quota));
            } else if (e instanceof FirebaseNetworkException) {
                Toast.makeText(LoginActivity.this
                        , getString(R.string.network_error),
                        LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            //Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            //TODO hasChanged this string
            isToShowProgress = false;

            updateUi(STATE_START_VERIFICATION_WITH_CODE);

            if (isCanSendRequest) {
                startTimer();
                setButtonResendEnabled(false);
            }

            isCanSendRequest = false;
        }
    }
}
