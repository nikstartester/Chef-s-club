package com.xando.chefsclub.Recipes.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipeentity ORDER BY id ASC")
    Single<List<RecipeEntity>> getSingleAll();

    @Query("SELECT * FROM recipeentity ORDER BY id ASC")
    Flowable<List<RecipeEntity>> getFlowableAll();

    @Query("SELECT recipeKey FROM recipeentity")
    Flowable<List<String>> getFlowableAllRecipeKey();

    @Query("SELECT * FROM recipeentity WHERE isNeedSync = 1")
    Single<List<RecipeEntity>> getNotSyncRecipes();

    @Query("SELECT * FROM recipeentity WHERE id = :id")
    RecipeEntity getById(long id);

    @Query("SELECT * FROM recipeentity WHERE recipeKey = :recipeKey")
    Flowable<List<RecipeEntity>> getFlowableByRecipeKey(String recipeKey);

    @Query("SELECT * FROM recipeentity WHERE recipeKey = :recipeKey")
    Single<List<RecipeEntity>> getSingleByRecipeKey(String recipeKey);

    @Query("SELECT * FROM recipeentity WHERE name = :name")
    Flowable<List<RecipeEntity>> getByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecipeEntity recipeEntity);

    @Query("UPDATE recipeentity SET starCount = :starCount, stars = :stars, isNeedSync = :isNeedSync" +
            " WHERE recipeKey = :recipeKey")
    void updateStar(String recipeKey, int starCount, String stars, boolean isNeedSync);

    @Query("UPDATE recipeentity SET isNeedSync = :isNeedSync WHERE recipeKey = :recipeKey")
    void updateSyncStatus(String recipeKey, boolean isNeedSync);

    @Update
    void update(RecipeEntity recipeEntity);

    @Delete
    void delete(RecipeEntity recipeEntity);

    @Query("DELETE FROM recipeentity")
    void deleteAll();

    @Query("DELETE FROM recipeentity WHERE recipeKey = :recipeKey")
    void deleteByRecipeKey(String recipeKey);

    @Query("UPDATE recipeentity SET inCompilations = '' WHERE recipeKey = :recipekey")
    void deleteInCompilations(String recipekey);
}
