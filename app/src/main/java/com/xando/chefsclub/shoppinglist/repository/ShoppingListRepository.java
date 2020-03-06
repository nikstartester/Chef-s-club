package com.xando.chefsclub.shoppinglist.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.xando.chefsclub.App;
import com.xando.chefsclub.dataworkers.DataBaseLoader;
import com.xando.chefsclub.shoppinglist.db.IngredientEntity;

import java.util.List;

import io.reactivex.schedulers.Schedulers;


public class ShoppingListRepository implements DataBaseLoader<IngredientEntity> {

    private final Application mApplication;

    private final MutableLiveData<List<IngredientEntity>> mLiveData;

    public ShoppingListRepository(Application application, MutableLiveData<List<IngredientEntity>> liveData) {
        mApplication = application;
        mLiveData = liveData;
    }

    @Override
    public void loadDataFromDB() {
        ((App) mApplication)
                .getDatabase()
                .ingredientsDao()
                .getSingleAll()
                .subscribeOn(Schedulers.io())
                .subscribe(list -> onDataLoadedFromDB(list));
    }

    @Override
    public void onDataLoadedFromDB(@Nullable List<IngredientEntity> ingredientEntities) {
        if (mLiveData != null)
            mLiveData.postValue(ingredientEntities);

    }
}
