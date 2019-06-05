package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class BaseFragmentWithRecipeKey extends Fragment {

    protected static final String KEY_RECIPE_ID = "recipeId";

    @Nullable
    protected String recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recipeId = getArguments().getString(KEY_RECIPE_ID);
        }
    }

}
