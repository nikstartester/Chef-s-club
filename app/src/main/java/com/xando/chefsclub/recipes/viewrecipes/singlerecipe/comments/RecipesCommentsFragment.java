package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecipesCommentsFragment extends CommentsListFragment {

    private static final String KEY_RECIPE_ID = "keyRecipeId";

    public static Fragment getInstance(String recipeId) {
        Fragment fragment = new RecipesCommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("comments").child("recipes").
                child(getRecipeId()).orderByChild("date");
    }

    @NonNull
    @Override
    public String getRecipeId() {
        String recipeId = null;

        if (getArguments() != null)
            recipeId = getArguments().getString(KEY_RECIPE_ID, null);

        if (recipeId == null)
            throw new NullPointerException("You must pass nonnull recipeId (use getInstance(String recipeId) method)");
        else return recipeId;
    }
}
