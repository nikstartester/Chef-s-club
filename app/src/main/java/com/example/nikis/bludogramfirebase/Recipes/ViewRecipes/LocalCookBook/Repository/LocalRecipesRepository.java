package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.LocalCookBook.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.DataWorkers.DataBaseLoader;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalRecipesRepository implements DataBaseLoader<RecipeData> {

    private final Application mApplication;

    private final MutableLiveData<List<RecipeData>> resData;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LocalRecipesRepository(Application application, MutableLiveData<List<RecipeData>> toData) {
        mApplication = application;
        this.resData = toData;
    }

    @Override
    public void loadDataFromDB() {
        Disposable disposable = ((App) mApplication)
                .getDatabase()
                .recipeDao()
                .getFlowableAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(recipeEntities -> {
                    List<RecipeData> recipeDataList = new ArrayList<>();

                    for (RecipeEntity entity : recipeEntities) {
                        recipeDataList.add(entity.toRecipeData());
                    }

                    onDataLoadedFromDB(recipeDataList);
                });

        mCompositeDisposable.add(disposable);
    }

    public void dispose() {
        mCompositeDisposable.dispose();
    }

    @Override
    public void onDataLoadedFromDB(@Nullable List<RecipeData> recipeData) {
        resData.setValue(recipeData);
    }
}
