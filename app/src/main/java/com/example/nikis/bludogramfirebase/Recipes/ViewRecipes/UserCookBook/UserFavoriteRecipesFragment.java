package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.UserCookBook;

import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.RecipesListFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;


public class UserFavoriteRecipesFragment extends RecipesListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("recipes").orderByChild("stars/" + FirebaseHelper.getUid())
                .equalTo(true);
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
