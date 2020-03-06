package com.xando.chefsclub.dataworkers;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.xando.chefsclub.compilations.db.CompilationDao;
import com.xando.chefsclub.compilations.db.CompilationEntity;
import com.xando.chefsclub.profiles.db.ProfileDao;
import com.xando.chefsclub.profiles.db.ProfileEntity;
import com.xando.chefsclub.recipes.db.RecipeDao;
import com.xando.chefsclub.recipes.db.RecipeEntity;
import com.xando.chefsclub.recipes.db.RecipeToFavoriteEntity;
import com.xando.chefsclub.recipes.db.RecipesToFavoriteDao;
import com.xando.chefsclub.shoppinglist.db.IngredientEntity;
import com.xando.chefsclub.shoppinglist.db.IngredientsDao;

@Database(entities = {ProfileEntity.class, RecipeEntity.class, RecipeToFavoriteEntity.class,
        IngredientEntity.class, CompilationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProfileDao profileDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipesToFavoriteDao recipesToFavoriteDao();

    public abstract IngredientsDao ingredientsDao();

    public abstract CompilationDao compilationTittleDao();
}
