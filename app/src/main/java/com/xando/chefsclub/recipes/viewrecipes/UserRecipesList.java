package com.xando.chefsclub.recipes.viewrecipes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.RecipesListFragment;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.RecipeItem;


public class UserRecipesList extends RecipesListFragment {

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_SCROLL = "keyScroll";

    protected String userId;
    private boolean nestedScrolling;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString(KEY_USER_ID);
            nestedScrolling = getArguments().getBoolean(KEY_SCROLL, true);
        }
    }


    public static Fragment getInstance(String userId, boolean nestedScrolling) {
        Fragment fragment = new UserRecipesList();

        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        args.putBoolean(KEY_SCROLL, nestedScrolling);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-recipes").
                child(userId);
    }

    @Override
    public boolean isNestedScrolling() {
        return nestedScrolling;
    }

    @Nullable
    @Override
    protected ClickEventHook<RecipeItem> getCustomClickEventHook() {
        return null;
    }
}
