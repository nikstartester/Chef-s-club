package com.xando.chefsclub.DataWorkers;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.xando.chefsclub.Compilations.db.CompilationDao;
import com.xando.chefsclub.Compilations.db.CompilationEntity;
import com.xando.chefsclub.Profiles.db.ProfileDao;
import com.xando.chefsclub.Profiles.db.ProfileEntity;
import com.xando.chefsclub.Recipes.db.RecipeDao;
import com.xando.chefsclub.Recipes.db.RecipeEntity;
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity;
import com.xando.chefsclub.Recipes.db.RecipesToFavoriteDao;
import com.xando.chefsclub.ShoppingList.db.IngredientEntity;
import com.xando.chefsclub.ShoppingList.db.IngredientsDao;

@Database(entities = {ProfileEntity.class, RecipeEntity.class, RecipeToFavoriteEntity.class,
        IngredientEntity.class, CompilationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProfileDao profileDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipesToFavoriteDao recipesToFavoriteDao();

    public abstract IngredientsDao ingredientsDao();

    public abstract CompilationDao compilationTittleDao();
}
