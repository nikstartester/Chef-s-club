package com.xando.chefsclub.Recipes.ViewRecipes.LocalCookBook.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.ViewRecipes.LocalCookBook.Repository.LocalRecipesRepository;

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
