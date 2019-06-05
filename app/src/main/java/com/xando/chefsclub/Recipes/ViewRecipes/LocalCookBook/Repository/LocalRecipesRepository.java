package com.xando.chefsclub.Recipes.ViewRecipes.LocalCookBook.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.xando.chefsclub.App;
import com.xando.chefsclub.DataWorkers.DataBaseLoader;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.db.RecipeEntity;

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
