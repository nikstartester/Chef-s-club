package com.xando.chefsclub.Recipes.ViewRecipes.UserCookBook;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Recipes.ViewRecipes.UserRecipesList;


public class RecipesCreatedByUserFragment extends UserRecipesList {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = FirebaseHelper.getUid();
    }
}
