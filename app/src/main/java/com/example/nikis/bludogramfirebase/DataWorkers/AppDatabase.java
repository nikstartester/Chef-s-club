package com.example.nikis.bludogramfirebase.DataWorkers;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.nikis.bludogramfirebase.Compilations.db.CompilationDao;
import com.example.nikis.bludogramfirebase.Compilations.db.CompilationEntity;
import com.example.nikis.bludogramfirebase.Profiles.db.ProfileDao;
import com.example.nikis.bludogramfirebase.Profiles.db.ProfileEntity;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeDao;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeEntity;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeToFavoriteEntity;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipesToFavoriteDao;
import com.example.nikis.bludogramfirebase.ShoppingList.db.IngredientEntity;
import com.example.nikis.bludogramfirebase.ShoppingList.db.IngredientsDao;

@Database(entities = {ProfileEntity.class, RecipeEntity.class, RecipeToFavoriteEntity.class,
        IngredientEntity.class, CompilationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProfileDao profileDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipesToFavoriteDao recipesToFavoriteDao();

    public abstract IngredientsDao ingredientsDao();

    public abstract CompilationDao compilationTittleDao();
}
