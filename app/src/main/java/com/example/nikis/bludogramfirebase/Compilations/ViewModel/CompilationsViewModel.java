package com.example.nikis.bludogramfirebase.Compilations.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.Compilations.Repository.CompilationsRepository;

import java.util.List;


public class CompilationsViewModel extends AndroidViewModel {
    private MutableLiveData<List<CompilationData>> mData = new MutableLiveData<>();

    private final CompilationsRepository mRepository =
            new CompilationsRepository(getApplication(), mData);

    public CompilationsViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData() {
        mRepository.loadDataFromDB();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mRepository.dispose();
    }

    public MutableLiveData<List<CompilationData>> getData() {
        if (mData == null) mData = new MutableLiveData<>();

        return mData;
    }

    public CompilationsRepository getRepository() {
        return mRepository;
    }
}
