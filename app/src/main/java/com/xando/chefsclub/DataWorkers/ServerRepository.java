package com.xando.chefsclub.DataWorkers;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Profiles.Repository.Exceptions.NothingFoundException;

public abstract class ServerRepository<Data extends BaseData> implements
        ProgressUpdate<Data>, ServerLoader, DataBaseSaver<Data> {

    private static final String TAG = "ServerRepository";

    private MutableLiveData<ParcResourceByParc<Data>> resData;

    private String mFirebaseId;

    protected Application mApplication;

    private BaseLocalDataSaver<Data> mLocalSaver;

    private String mFirebaseChild;


    private boolean isWithoutStatus = false;

    private boolean isInDb = false;

    @Nullable
    private OnProcessListener<Data> mOnProcessListener;

    protected ServerRepository() {
    }

    public abstract Class<Data> getDataClass();

    private void loadData() {
        loadDataFromServer();
    }

    public void loadData(String firebaseId) {
        mFirebaseId = firebaseId;

        loadData();
    }

    @Override
    public void updateProgress(ParcResourceByParc<Data> resource) {
        updateProgress(resource, false);
    }

    private void updateProgress(ParcResourceByParc<Data> result, boolean isMainThread) {
        if (!isWithoutStatus) {
            Log.d(TAG, "updateProgress: " + result.status);
            if (isMainThread) {
                resData.setValue(result);
            } else {
                resData.postValue(result);
            }

            if (mOnProcessListener != null) {
                mOnProcessListener.onStatusChanged(result);
            }
        }
    }

    @Override
    public void loadDataFromServer() {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(mFirebaseChild).child(mFirebaseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Data data = dataSnapshot.getValue(getDataClass());

                if (data != null) {

                    if (mLocalSaver != null) {
                        saveOnLocal(data);
                    } else {
                        updateProgress(ParcResourceByParc.success(data));
                    }

                } else {
                    updateProgress(ParcResourceByParc.error(new NothingFoundException(), null));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                updateProgress(ParcResourceByParc.error(databaseError.toException(), null));

            }
        });
    }

    private void saveOnLocal(Data data) {
        saveOnLocal(mLocalSaver, data);
    }

    @Override
    public void saveOnLocal(BaseLocalDataSaver<Data> saver, Data data) {
        saver.save(data, () -> updateProgress(ParcResourceByParc.success(data), false));
    }

    public MutableLiveData<ParcResourceByParc<Data>> getResData() {
        return resData;
    }

    private enum EventListenerStrategy {

    }

    public abstract class Builder {

        //
        //It means that your LiveData will not change
        //
        public ServerRepository.Builder isWithoutStatus(boolean isWithoutStatus) {
            ServerRepository.this.isWithoutStatus = isWithoutStatus;

            return this;
        }

        public ServerRepository.Builder setFirebaseId(@Nullable String firebaseId) {
            ServerRepository.this.mFirebaseId = firebaseId;

            return this;
        }

        public ServerRepository.Builder setFirebaseChild(@Nullable String firebaseChild) {
            ServerRepository.this.mFirebaseChild = firebaseChild;

            return this;
        }

        public ServerRepository.Builder setLocalSever(@Nullable BaseLocalDataSaver<Data> localSaver) {
            ServerRepository.this.mLocalSaver = localSaver;

            return this;
        }

        @Deprecated
        /*
        Memory leak
         */
        public ServerRepository.Builder addOnProgressListener(@Nullable OnProcessListener<Data> onProcessListener) {
            ServerRepository.this.mOnProcessListener = onProcessListener;

            return this;
        }

        public ServerRepository.Builder to(MutableLiveData<ParcResourceByParc<Data>> data) {
            ServerRepository.this.resData = data;

            return this;
        }

        protected abstract void checkAndSetStandardValue();


        public ServerRepository build() {

            checkAndSetStandardValue();

            return ServerRepository.this;
        }
    }
}
