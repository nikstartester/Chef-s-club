package com.example.nikis.bludogramfirebase.Profile.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profile.Repository.Local.LocalUserProfile;
import com.example.nikis.bludogramfirebase.Profile.Repository.Exceptions.NothingFoundException;
import com.example.nikis.bludogramfirebase.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ProfileRepository {

    @Nullable
    private MutableLiveData<Resource<ProfileData>> data;

    private String mUserUid;

    private Application mApplication;

    private ProfileRepository(){

    }

    public static Builder with(Application application){
        ProfileRepository repository = new ProfileRepository();
        repository.mApplication = application;

        return repository.new Builder();
    }

    public void loadData(){

        data.setValue(Resource.loading(null));

        Flowable<List<ProfileData>> flowable = ((App) mApplication)
                .getDatabase()
                .profileDao()
                .getByUid(mUserUid);
        flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profilesData -> {
                    if(!profilesData.isEmpty())
                        data.setValue(Resource.success(profilesData.get(0)));
                    else {
                        loadDataFromServer();
                    }
                });

    }

    private void loadDataFromServer(){
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child("users").child(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ProfileData profileData = dataSnapshot.getValue(ProfileData.class);
                if(profileData !=null){

                    new LocalUserProfile(mApplication).save(profileData, () -> {
                        data.postValue(Resource.success(profileData));
                    });

                }else {
                    data.setValue(Resource.error(new NothingFoundException(), null));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                data.setValue(Resource.error(databaseError.toException(), null));
            }
        });
    }

    @Nullable
    public MutableLiveData<Resource<ProfileData>> getData() {
        return data;
    }

    public static String getFireBaseAuthUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public class Builder{

        public Builder setUserUid(@Nullable String userUid){
            ProfileRepository.this.mUserUid = userUid;

            return this;
        }

        public Builder to(MutableLiveData<Resource<ProfileData>> data){
            ProfileRepository.this.data = data;

            return this;
        }

        /*
        if userUid == null -> use Uid from FireBaseAuth
        if data == null -> use new MutableLiveData()
        */
        public ProfileRepository build(){
            if(data == null) data = new MutableLiveData<>();

            if(mUserUid == null) mUserUid = getFireBaseAuthUid();

            return ProfileRepository.this;
        }

    }
}
