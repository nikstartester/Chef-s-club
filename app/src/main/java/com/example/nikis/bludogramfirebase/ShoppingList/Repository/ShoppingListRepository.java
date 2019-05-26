package com.example.nikis.bludogramfirebase.ShoppingList.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.DataWorkers.DataBaseLoader;
import com.example.nikis.bludogramfirebase.ShoppingList.db.IngredientEntity;

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
