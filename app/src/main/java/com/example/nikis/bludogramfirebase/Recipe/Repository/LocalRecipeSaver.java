package com.example.nikis.bludogramfirebase.Recipe.Repository;

import android.app.Application;

import com.example.nikis.bludogramfirebase.BaseLocalDataSaver;
import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;


public class LocalRecipeSaver extends BaseLocalDataSaver<RecipeData> {

    public LocalRecipeSaver(Application application) {
        super(application);
    }

    @Override
    public void save(RecipeData data) {

    }
}
