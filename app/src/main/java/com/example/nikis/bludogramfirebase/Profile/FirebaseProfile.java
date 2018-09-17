package com.example.nikis.bludogramfirebase.Profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.BaseActivities.BaseActivityWithImageClick;
import com.example.nikis.bludogramfirebase.Exceptions.ExistLoginException;
import com.example.nikis.bludogramfirebase.Exceptions.NetworkException;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.LocalUserData;
import com.example.nikis.bludogramfirebase.UserData.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public abstract class FirebaseProfile extends BaseActivityWithImageClick {
    protected static final String TAG_CREATE_PROFILE = "BG_editProfile";
    private final String KEY_IS_IN_PROGRESS = "1341";

    private UserData userData;

    private boolean isUploadImageTaskComplete;
    private boolean isCreateProfileTaskCompleteWithoutImage;
    protected boolean isInProgress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);

        if(savedInstanceState != null){
            isInProgress = savedInstanceState.getBoolean(KEY_IS_IN_PROGRESS);
        }
    }

    protected void createProfileWithImage(UserData userData)throws ExistLoginException, NetworkException{
        isInProgress = true;
        this.userData = userData;
        createProfile();

        String imagePath = super.getSelectedImagePath();
        if(imagePath != null)
            uploadImage(imagePath);
    }



    private void createProfile() throws ExistLoginException, NetworkException {
        if(!isOnline())
            throw new NetworkException("No network connection");
        if(isLoginExist())
            throw new ExistLoginException("Login already exists");
        updateChildren();
    }



    private boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isLoginExist(){
        //TODO написать проверку на существование вобранного логина
        return false;
    }

    private void updateChildren() {
        Map<String, Object> childUpdates = createChildUpdates();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

        myRef.updateChildren(childUpdates)
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG_CREATE_PROFILE, "writeNewUser success");
                isCreateProfileTaskCompleteWithoutImage = true;
                if(isUploadImageTaskComplete || getSelectedImagePath() == null)
                {
                    complete();
                }
            }else{
                Exception ex = task.getException();
                if (ex != null)
                {
                    Log.d(TAG_CREATE_PROFILE, "writeNewUser failure", task.getException());
                    Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Map<String, Object> createChildUpdates(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userData.userUid = user.getUid();

        Map<String, Object> postValues = userData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users/" + userData.userUid, postValues);

        return childUpdates;
    }

    private void uploadImage(String imagePath){
        File file = new File(imagePath);
        Uri fileUri = Uri.fromFile(file);

        String newImageName = userData.userUid + ".jpg" ;

        StorageReference storageRef = FirebaseReferences.getStorageReference();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image")
                .build();

        UploadTask uploadTask = storageRef.child("profilesImage/" + newImageName)
                .putFile(fileUri, metadata);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            System.out.println("Upload is " + progress + "% done");
            if(progress == 100D)
            {
                isUploadImageTaskComplete = true;
                if(isCreateProfileTaskCompleteWithoutImage){

                    LocalUserData.getInstance().setTimeLastImageUpdate(Long.toString(System.currentTimeMillis()))
                            .putToPreferences(this);

                    StorageReference storageReference = FirebaseReferences.getStorageReference(
                            "profilesImage/" + userData.userUid + ".jpg");

                    complete();

                    GlideApp.with(getApplication())
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(LocalUserData.getInstance().getTimeLastImageUpdate()))
                            .submit();


                }
            }
        });
    }
    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
    private void complete(){

        isCreateProfileTaskCompleteWithoutImage = false;
        isUploadImageTaskComplete = false;
        isInProgress = false;

        LocalUserData.getInstance()
                .setUserData(userData)
                .putToPreferences(this);

        onAllTasksComplete();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_IN_PROGRESS, isInProgress);
    }


    public abstract void onAllTasksComplete();

}
