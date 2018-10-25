package com.example.nikis.bludogramfirebase;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.Profile.Repository.Exceptions.NothingFoundException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class BaseRepository<Data extends BaseData> {

    protected MutableLiveData<Resource<Data>> resData;

    protected String mFirebaseId;

    protected Application mApplication;

    protected BaseLocalDataSaver<Data> mLocalSaver;

    protected String mFirebaseChild;

    protected BaseRepository() {

    }

    public abstract Class<Data> getDataClass();

    public void loadDataFromDB(String firebaseId){
        mFirebaseId = firebaseId;

        loadDataFromDB();
    }

    public abstract void loadDataFromDB();

    protected void onDataLoadedFromDB(@Nullable List<Data> dataList){
        if(dataList == null || dataList.isEmpty()){
            loadDataFromServer();
        }else {
            resData.setValue(Resource.success(dataList.get(0)));
        }
    }

    protected void loadDataFromServer(){
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(mFirebaseChild).child(mFirebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Data data = dataSnapshot.getValue(getDataClass());

                if(data != null){

                    if(mLocalSaver != null) saveOnLocal(data);

                    else {
                        resData.setValue(Resource.success(data));
                    }

                }else {
                    resData.setValue(Resource.error(new NothingFoundException(), null));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                resData.setValue(Resource.error(databaseError.toException(), null));
            }
        });
    }

    protected void saveOnLocal(Data data){
        mLocalSaver.save(data, () -> resData.postValue(Resource.success(data)));
    }

    public MutableLiveData<Resource<Data>> getResData() {
        return resData;
    }


    public abstract class Builder{

        public Builder setFirebaseId(@Nullable String firebaseId){
            BaseRepository.this.mFirebaseId = firebaseId;

            return this;
        }

        public Builder setFirebaseChild(@Nullable String firebaseChild){
            BaseRepository.this.mFirebaseChild = firebaseChild;

            return this;
        }

        public Builder setLocalSever(@Nullable BaseLocalDataSaver<Data> localSaver){
            BaseRepository.this.mLocalSaver = localSaver;

            return this;
        }

        public Builder to(MutableLiveData<Resource<Data>> data){
            BaseRepository.this.resData = data;

            return this;
        }

        protected abstract void checkAndSetStandardValue();

        public BaseRepository build(){

            checkAndSetStandardValue();

            return BaseRepository.this;
        }
    }
}
