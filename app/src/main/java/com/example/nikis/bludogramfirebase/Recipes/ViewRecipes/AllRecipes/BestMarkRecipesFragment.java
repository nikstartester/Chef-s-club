package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.AllRecipes;

import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.RecipesListFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

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
