package com.example.nikis.bludogramfirebase;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.UserData.UserData;
import com.example.nikis.bludogramfirebase.UserData.UserData.Gender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.RecipesListFragment.getUid;

public class LocalUserData {
    private static final LocalUserData INSTANCE = new LocalUserData();

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String LOGIN = "login";
    private static final String GENDER_STR = "genderStr";
    private static final String USER_UID = "userUid";
    private static final String TIME_LAST_IMAGE_UPDATE = "time";

    private static final String LOCAL_USER = "localUser";

    private static final String DEFAULT = "";

    private UserData userData;
    private String timeLastImageUpdate;

    private DatabaseReference ref;

    private volatile ArrayList<OnUpdateDataListener> onUpdateDataList;

    private LocalUserData() {
        userData = new UserData();
        userData.emptyData();
    }

    public static LocalUserData getInstance() {
        return INSTANCE;
    }

    public String getFirstName() {
        return userData.firstName;
    }

    public LocalUserData setFirstName(String firstName) {
        userData.firstName = firstName;
        return this;
    }

    public String getSecondName() {
        return userData.secondName;
    }

    public LocalUserData setSecondName(String secondName) {
        userData.secondName = secondName;
        return this;
    }

    public String getLogin() {
        return userData.login;
    }

    public LocalUserData setLogin(String login) {
        userData.login = login;
        return this;
    }

    public Gender getGender() {
        return userData.getGender();
    }

    public LocalUserData setGender(String genderStr) {
        userData.setGender(genderStr);
        return this;
    }

    public UserData getUserData(){
        return userData;
    }

    public LocalUserData setUserData(UserData userData){
        this.userData = userData;
        return this;
    }

    public String getTimeLastImageUpdate() {
        return timeLastImageUpdate;
    }

    public LocalUserData setTimeLastImageUpdate(String timeLastImageUpdate) {
        this.timeLastImageUpdate = timeLastImageUpdate;
        return this;
    }

    public void putToPreferences(@NonNull Context context){
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        SharedPreferences preferences = contextWeakReference.get()
                .getSharedPreferences(LOCAL_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        putAllValue(editor);

        editor.commit();
    }

    private void putAllValue(final SharedPreferences.Editor editor){
        editor.putString(FIRST_NAME, userData.firstName);
        editor.putString(LAST_NAME, userData.secondName);
        editor.putString(LOGIN, userData.login);
        editor.putString(GENDER_STR, GenderUtils.genderToString(userData.getGender()));
        editor.putString(USER_UID, userData.userUid);
        editor.putString(TIME_LAST_IMAGE_UPDATE, timeLastImageUpdate);
    }

    public UserData getValueFromPreferences(@NonNull Context context){
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        SharedPreferences preferences = contextWeakReference.get()
                .getSharedPreferences(LOCAL_USER, Context.MODE_PRIVATE);

        userData.firstName = preferences.getString(FIRST_NAME, DEFAULT);
        userData.secondName = preferences.getString(LAST_NAME, DEFAULT);
        userData.login = preferences.getString(LOGIN, DEFAULT);
        userData.setGender(preferences.getString(GENDER_STR, DEFAULT));
        userData.userUid = preferences.getString(USER_UID, DEFAULT);
        timeLastImageUpdate = preferences.getString(TIME_LAST_IMAGE_UPDATE, DEFAULT);
        return userData;
    }

    public boolean isNeedUpdate(){
        return userData == null || userData.firstName.equals("") || userData.secondName.equals("")
                || userData.userUid.equals("") || userData.login.equals("");
    }

    public void updateData(){
        if(ref == null){
            ref = FirebaseReferences.getDataBaseReference();
        }else return;
        ref.child("users").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userData = dataSnapshot.getValue(UserData.class);
               if(onUpdateDataList != null && onUpdateDataList.size() > 0){
                   for (OnUpdateDataListener onUpdateDataListener : onUpdateDataList) {
                       onUpdateDataListener.onUpdate(userData);
                   }
                   onUpdateDataList.clear();
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void addOnUpdateDataListener(OnUpdateDataListener onUpdateData){
        if(onUpdateDataList == null)
            onUpdateDataList = new ArrayList<>();
        onUpdateDataList.add(onUpdateData);
    }

    public LocalUserData clear(){
        userData.emptyData();
        timeLastImageUpdate = "";
        return this;
    }

    public interface OnUpdateDataListener {
        void onUpdate(UserData userData);
    }
}
