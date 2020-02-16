package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.ingredient;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.xando.chefsclub.App;
import com.xando.chefsclub.dataworkers.DataBaseLoader;
import com.xando.chefsclub.shoppinglist.db.IngredientEntity;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class IngredientsRepository implements DataBaseLoader<IngredientEntity> {

    private static final String TAG = "IngredientsRepository";

    private final WeakReference<Application> mApplicationWeakReference;

    private MutableLiveData<List<IngredientEntity>> mToLiveData;

    private final CompositeDisposable mCompositeDisposable;

    private String mRecipeId;

    IngredientsRepository(Application application) {
        mApplicationWeakReference = new WeakReference<>(application);

        mCompositeDisposable = new CompositeDisposable();
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public IngredientsRepository setRecipeId(String recipeId) {
        mRecipeId = recipeId;

        return this;
    }

    private void setToLiveData(MutableLiveData<List<IngredientEntity>> toLiveData) {
        mToLiveData = toLiveData;
    }

    void loadDataFromDb(MutableLiveData<List<IngredientEntity>> toLiveData, String recipeId) {
        setRecipeId(recipeId);
        setToLiveData(toLiveData);

        loadDataFromDB();
    }

    @Override
    public void loadDataFromDB() {
        if (mRecipeId == null) throw new NullPointerException("At first set NonNull recipeId");
        if (mToLiveData == null)
            throw new NullPointerException("liveData == null. Where to send data?");

        Application application = mApplicationWeakReference.get();

        if (application != null) {
            Disposable disposable = ((App) application)
                    .getDatabase()
                    .ingredientsDao()
                    .getFlowableAllByRecipeId(mRecipeId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onDataLoadedFromDB);
            mCompositeDisposable.add(disposable);
        }

    }

    @Override
    public void onDataLoadedFromDB(@Nullable List<IngredientEntity> ingrs) {
        if (mToLiveData != null)
            mToLiveData.setValue(ingrs);
        else throw new NullPointerException("liveData == null. Where to send data?");
    }

    public void clear() {
        mApplicationWeakReference.clear();

        mCompositeDisposable.clear();
    }
}
