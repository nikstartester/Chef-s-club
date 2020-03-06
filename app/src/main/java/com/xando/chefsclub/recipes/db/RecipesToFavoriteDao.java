package com.xando.chefsclub.recipes.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

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
