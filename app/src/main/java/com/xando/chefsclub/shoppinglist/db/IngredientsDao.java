package com.xando.chefsclub.shoppinglist.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface IngredientsDao {

    @Query("SELECT * FROM ingrediententity WHERE recipeId = :recipeId AND ingredient = :ingredient")
    List<IngredientEntity> get(String recipeId, String ingredient);

    @Query("SELECT * FROM ingrediententity ORDER BY recipeId, ingredient")
    Single<List<IngredientEntity>> getSingleAll();

    @Query("SELECT * FROM ingrediententity ORDER BY recipeId, ingredient")
    Flowable<List<IngredientEntity>> getFlowableAll();

    @Query("SELECT * FROM ingrediententity WHERE recipeId = :recipeId")
    Single<List<IngredientEntity>> getSingleAllByRecipeId(String recipeId);

    @Query("SELECT id FROM ingrediententity WHERE recipeId = :recipeId")
    Single<List<Long>> getSingleAllIdsByRecipeId(String recipeId);

    @Query("SELECT * FROM ingrediententity WHERE id = :id")
    Flowable<List<IngredientEntity>> getFlowableById(Long id);

    @Query("SELECT * FROM ingrediententity WHERE recipeId = :recipeId")
    Flowable<List<IngredientEntity>> getFlowableAllByRecipeId(String recipeId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(IngredientEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<IngredientEntity> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void replace(List<IngredientEntity> list);

    @Query("UPDATE ingrediententity SET isAvailable = :isAvailable WHERE recipeId = :recipeId")
    void changeAllAvailable(String recipeId, boolean isAvailable);

    @Query("DELETE FROM ingrediententity WHERE recipeId = :recipeId " +
            "AND ingredient = :ingredient")
    void delete(String recipeId, String ingredient);

    @Query("DELETE FROM ingrediententity WHERE recipeId = :recipeId")
    void delete(String recipeId);

    @Query("DELETE FROM ingrediententity")
    void deleteAll();

    @Query("UPDATE ingrediententity SET isAvailable = :isAvailable WHERE (recipeId = :recipeId " +
            "AND ingredient = :ingredient)")
    void changeAvailable(String recipeId, String ingredient, boolean isAvailable);
}
