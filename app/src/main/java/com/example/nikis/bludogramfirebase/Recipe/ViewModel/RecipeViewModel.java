package com.example.nikis.bludogramfirebase.Recipe.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipe.Repository.RecipeRepository;
import com.example.nikis.bludogramfirebase.Resource;

import static com.example.nikis.bludogramfirebase.Recipe.Repository.RecipeRepository.CHILD_RECIPES;


public class RecipeViewModel extends AndroidViewModel {

    private MutableLiveData<Resource<RecipeData>> mResourceLiveData;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(String recipeKey){
        RecipeRepository.with(getApplication())
                .setFirebaseId(recipeKey)
                .setFirebaseChild(CHILD_RECIPES)
                .setLocalSever(null)
                .to(mResourceLiveData)
                .build()
                .loadDataFromDB();
    }


    public MutableLiveData<Resource<RecipeData>> getResourceLiveData() {
        if(mResourceLiveData == null){
            mResourceLiveData = new MutableLiveData<>();
        }

        return mResourceLiveData;
    }
}
