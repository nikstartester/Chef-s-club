package com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.Item;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xando.chefsclub.Recipes.Data.RecipeData;

import java.util.List;

public class RecipeItem extends AbsRecipeItem {

    public RecipeItem(@NonNull RecipeData recipeData, @Nullable LifecycleOwner lifecycleOwner) {
        super(recipeData, lifecycleOwner);
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        super.bindToRecipe();
    }

}
