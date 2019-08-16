package com.xando.chefsclub.Recipes.ViewRecipes.AllRecipes;

import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.RecipesListFragment;

public class BestMarkRecipesFragment extends RecipesListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("recipes").orderByChild("tags/sortByStarTag");
    }

    @Override
    public boolean isNestedScrolling() {
        return true;
    }

    @Nullable
    @Override
    protected ClickEventHook<RecipeItem> getCustomClickEventHook() {
        return null;
    }
}
