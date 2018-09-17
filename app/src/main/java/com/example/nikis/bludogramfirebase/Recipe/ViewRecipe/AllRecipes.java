package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AllRecipes extends RecipesListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("recipes");
    }
}
