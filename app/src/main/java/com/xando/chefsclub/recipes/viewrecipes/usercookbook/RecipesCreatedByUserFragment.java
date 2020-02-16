package com.xando.chefsclub.recipes.viewrecipes.usercookbook;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.recipes.viewrecipes.UserRecipesList;


public class RecipesCreatedByUserFragment extends UserRecipesList {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = FirebaseHelper.getUid();
    }
}
