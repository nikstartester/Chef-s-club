package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.UserCookBook;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.UserRecipesList;


public class RecipesCreatedByUserFragment extends UserRecipesList {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = FirebaseHelper.getUid();
    }
}
