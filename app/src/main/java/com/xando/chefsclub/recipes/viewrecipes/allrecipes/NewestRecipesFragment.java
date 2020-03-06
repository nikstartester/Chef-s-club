package com.xando.chefsclub.recipes.viewrecipes.allrecipes;


import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.RecipesListFragment;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.RecipeItem;

public class NewestRecipesFragment extends RecipesListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("recipes")
                .orderByChild("tags/sortByAllTag")
                .limitToLast(150);
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
