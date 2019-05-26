package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.Data.RecipeIdData;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.Repository.SubscriptionsRecipesRepository;

import java.util.List;


public class SubscriptionsRecipesViewModel extends AndroidViewModel {
    private MutableLiveData<List<RecipeIdData>> data;

    public SubscriptionsRecipesViewModel(@NonNull Application application) {
        super(application);
    }

    public void load() {
        new SubscriptionsRecipesRepository().loadData(data);
    }

    public MutableLiveData<List<RecipeIdData>> getData() {
        if (data == null) data = new MutableLiveData<>();
        return data;
    }

    public void setData(MutableLiveData<List<RecipeIdData>> data) {
        this.data = data;
    }
}
