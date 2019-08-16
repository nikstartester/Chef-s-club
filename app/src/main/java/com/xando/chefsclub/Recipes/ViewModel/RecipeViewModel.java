package com.xando.chefsclub.Recipes.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xando.chefsclub.DataWorkers.BaseLocalDataSaver;
import com.xando.chefsclub.DataWorkers.BaseRepository;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.Local.LocalRecipeSaver;
import com.xando.chefsclub.Recipes.Repository.RecipeRepository;

import static com.xando.chefsclub.Recipes.Repository.RecipeRepository.CHILD_RECIPES;


public class RecipeViewModel extends AndroidViewModel {

    private MutableLiveData<ParcResourceByParc<RecipeData>> mResourceLiveData;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(String recipeKey) {
        loadData(recipeKey, null);
    }

    public void loadData(String recipeKey, BaseRepository.Priority priority) {
        loadData(recipeKey, null, priority, false);
    }

    public void loadData(String recipeKey, boolean isSaveLocal, boolean isSaveImage,
                         BaseRepository.Priority priority, boolean isWithoutStatus) {
        if (isSaveLocal) {
            loadData(recipeKey, new LocalRecipeSaver(getApplication(), isSaveImage), priority, isWithoutStatus);
        } else {
            loadData(recipeKey, null, priority, isWithoutStatus);
        }
    }

    private void loadData(String recipeKey, BaseLocalDataSaver<RecipeData> localDataSaver,
                          BaseRepository.Priority priority, boolean isWithoutStatus) {
        RecipeRepository.with(getApplication())
                .setFirebaseId(recipeKey)
                .setFirebaseChild(CHILD_RECIPES)
                .setLocalSever(localDataSaver)
                .to(mResourceLiveData)
                .setPriority(priority)
                .isWithoutStatus(isWithoutStatus)
                .build()
                .loadData();
    }

    public MutableLiveData<ParcResourceByParc<RecipeData>> getResourceLiveData() {
        if (mResourceLiveData == null) {
            mResourceLiveData = new MutableLiveData<>();
        }

        return mResourceLiveData;
    }

    public void setResourceData(ParcResourceByParc<RecipeData> data) {
        if (mResourceLiveData == null) {
            mResourceLiveData = new MutableLiveData<>();
        }

        mResourceLiveData.setValue(data);
    }
}
