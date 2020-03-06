package com.xando.chefsclub.recipes.viewrecipes.localcookbook.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.viewrecipes.localcookbook.repository.LocalRecipesRepository;

import java.util.List;

public class LocalRecipesViewModel extends AndroidViewModel {

    private MutableLiveData<List<RecipeData>> mData = new MutableLiveData<>();

    private final LocalRecipesRepository mRepository = new LocalRecipesRepository(getApplication(), mData);

    public LocalRecipesViewModel(@NonNull Application application) {
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

    public MutableLiveData<List<RecipeData>> getData() {
        if (mData == null) mData = new MutableLiveData<>();
        return mData;
    }
}
