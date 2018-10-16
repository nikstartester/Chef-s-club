package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.BaseFragments.BaseFragmentWithMatisseGallery;


public abstract class BaseEditRecipeFragment extends BaseFragmentWithMatisseGallery {
    protected static final String KEY_RECIPE_ID = "recipeId";

    @Nullable
    private String recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            recipeId = getArguments().getString(KEY_RECIPE_ID);
        }
    }
}
