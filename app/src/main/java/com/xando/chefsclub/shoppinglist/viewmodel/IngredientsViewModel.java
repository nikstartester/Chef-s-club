package com.xando.chefsclub.shoppinglist.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.xando.chefsclub.shoppinglist.db.IngredientEntity;
import com.xando.chefsclub.shoppinglist.repository.ShoppingListRepository;

import java.util.List;


public class IngredientsViewModel extends AndroidViewModel {

    private MutableLiveData<List<IngredientEntity>> mIngredientsLiveData;

    public IngredientsViewModel(@NonNull Application application) {
        super(application);
    }


    public void loadData() {
        new ShoppingListRepository(getApplication(), mIngredientsLiveData).loadDataFromDB();
    }

    public MutableLiveData<List<IngredientEntity>> getData() {
        if (mIngredientsLiveData == null) mIngredientsLiveData = new MutableLiveData<>();

        return mIngredientsLiveData;
    }

    public void setData(List<IngredientEntity> data) {
        if (data == null) mIngredientsLiveData = new MutableLiveData<>();

        mIngredientsLiveData.setValue(data);
    }
}
