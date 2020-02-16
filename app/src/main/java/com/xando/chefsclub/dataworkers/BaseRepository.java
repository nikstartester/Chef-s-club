package com.xando.chefsclub.dataworkers;

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
import com.xando.chefsclub.profiles.repository.exception.NothingFoundException;

import java.util.List;

public abstract class BaseRepository<Data extends BaseData> implements DataBaseLoader<Data>,
        ProgressUpdate<Data>, ServerLoader, DataBaseSaver<Data>, DeletableData<Data> {

    private static final String TAG = "BaseRepository";

    protected MutableLiveData<ParcResourceByParc<Data>> resData;

    protected String mFirebaseId;

    protected Application mApplication;

    protected BaseLocalDataSaver<Data> mLocalSaver;

    protected String mFirebaseChild;

    private Priority mPriority;

    protected boolean isWithoutStatus = false;

    private boolean isInDb = false;

    @Nullable
    private OnProcessListener<Data> mOnProcessListener;

    protected BaseRepository() {
    }

    protected abstract Class<Data> getDataClass();

    public void loadData() {
        switch (mPriority) {
            case DATABASE_FIRST_AND_SERVER:
                updateProgress(ParcResourceByParc.loading(null));

                loadDataFromDB();
                break;
            case DATABASE_FIRST_OR_SERVER:

                updateProgress(ParcResourceByParc.loading(null));

                loadDataFromDB();

                break;
            case DATABASE_ONLY:
                updateProgress(ParcResourceByParc.loading(null));

                loadDataFromDB();

                break;
        }
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
    public void onDataLoadedFromDB(@Nullable List<Data> list) {
        if (mPriority == Priority.DATABASE_FIRST_AND_SERVER) {

            if (list == null || list.isEmpty()) {
                //empty
            } else {
                updateProgress(ParcResourceByParc.success(list.get(0), ParcResourceByParc.From.DATABASE));

                isInDb = true;
            }

            loadDataFromServer();

            return;
        }

        if (mPriority == Priority.DATABASE_FIRST_OR_SERVER) {
            if ((list == null || list.isEmpty())) {
                loadDataFromServer();
            } else {
                isInDb = true;

                updateProgress(ParcResourceByParc.success(list.get(0), ParcResourceByParc.From.DATABASE));
            }

            return;
        }

        if (mPriority == Priority.DATABASE_ONLY) {
            if (list == null || list.isEmpty()) {
                updateProgress(ParcResourceByParc.error(new NothingFoundFromDbException(), null));
            } else {
                isInDb = true;

                updateProgress(ParcResourceByParc.success(list.get(0), ParcResourceByParc.From.DATABASE));
            }

            return;
        }

        /*if((list == null || list.isEmpty()) && mPriority != Priority.DATABASE_ONLY){
            loadDataFromServer();
        }else {
            isInDb = true;

            updateProgress(ParcResourceByParc.success(list.get(0)));
        }*/
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
                        BaseLocalDataSaver<Data> saver = getDefSaverIfRowExistInDB();
                        if (isInDb && saver != null) {
                            saveOnLocal(saver, data);
                        } else {
                            //if(mPriority != Priority.DATABASE_FIRST_AND_SERVER)
                            updateProgress(ParcResourceByParc.success(data, ParcResourceByParc.From.SERVER));
                        }
                    }

                } else {
                    updateProgress(ParcResourceByParc.error(new NothingFoundFromServerException(), null));

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
        saver.save(data, () -> updateProgress(ParcResourceByParc
                .success(data, ParcResourceByParc.From.SERVER), false));
    }

    public MutableLiveData<ParcResourceByParc<Data>> getResData() {
        return resData;
    }

    //It need only for DATABASE_FIRST_AND_SERVER Priority
    @Deprecated
    protected abstract @Nullable
    BaseLocalDataSaver<Data> getDefSaverIfRowExistInDB();

    public static class NothingFoundFromServerException extends NothingFoundException {
        public NothingFoundFromServerException() {
            super();
        }

        public NothingFoundFromServerException(String message) {
            super(message);
        }
    }

    public static class NothingFoundFromDbException extends NothingFoundException {
        public NothingFoundFromDbException() {
            super();
        }

        public NothingFoundFromDbException(String message) {
            super(message);
        }
    }


    public enum Priority {

        // !It means you never get loading result!.
        //
        //Upload data from server and save in db(if local saver != null)
        //
        // !Use it if you want sync data from server to db!
        //SERVER_ONLY,


        //Get data from db. THEN load data from server
        // and save if local saver != null
        //
        //!Use it if you want get data from db and then sync data from server to db!
        //
        DATABASE_FIRST_AND_SERVER,

        //Get data from db. IF data empty -> load data from server
        // and save if local saver != null
        //
        //!Use it if you want just get data!
        //
        //DEFAULT
        DATABASE_FIRST_OR_SERVER,

        DATABASE_ONLY
    }


    public abstract class Builder {

        //
        //It means that your LiveData will not change
        //
        public Builder isWithoutStatus(boolean isWithoutStatus) {
            BaseRepository.this.isWithoutStatus = isWithoutStatus;

            return this;
        }

        public Builder setPriority(@Nullable Priority priority) {
            BaseRepository.this.mPriority = priority;

            return this;
        }

        public Builder setFirebaseId(@Nullable String firebaseId) {
            BaseRepository.this.mFirebaseId = firebaseId;

            return this;
        }

        public Builder setFirebaseChild(@Nullable String firebaseChild) {
            BaseRepository.this.mFirebaseChild = firebaseChild;

            return this;
        }

        public Builder setLocalSever(@Nullable BaseLocalDataSaver<Data> localSaver) {
            BaseRepository.this.mLocalSaver = localSaver;

            return this;
        }

        @Deprecated
        /*
        Memory leak
         */
        public Builder addOnProgressListener(@Nullable OnProcessListener<Data> onProcessListener) {
            BaseRepository.this.mOnProcessListener = onProcessListener;

            return this;
        }

        public Builder to(MutableLiveData<ParcResourceByParc<Data>> data) {
            BaseRepository.this.resData = data;

            return this;
        }

        protected abstract void checkAndSetStandardValue();


        public BaseRepository build() {

            if (mPriority == null) mPriority = Priority.DATABASE_FIRST_OR_SERVER;

            checkAndSetStandardValue();

            return BaseRepository.this;
        }
    }
}
