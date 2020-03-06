package com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.xando.chefsclub.recipes.data.RecipeData;

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
