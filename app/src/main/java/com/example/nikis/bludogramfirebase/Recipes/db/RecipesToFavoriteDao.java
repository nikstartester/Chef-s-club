package com.example.nikis.bludogramfirebase.Recipes.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipesToFavoriteDao {

    @Query("SELECT * FROM RecipeToFavoriteEntity")
    Flowable<List<RecipeToFavoriteEntity>> getFlowableAll();

    @Query("SELECT * FROM RecipeToFavoriteEntity")
    Single<List<RecipeToFavoriteEntity>> getSingleAll();

    @Insert(onConflict = REPLACE)
    void insert(RecipeToFavoriteEntity entity);

    @Delete
    void delete(RecipeToFavoriteEntity entity);

    @Query("DELETE FROM RecipeToFavoriteEntity WHERE recipeKey = :recipeKey")
    void deleteByKey(String recipeKey);
}
