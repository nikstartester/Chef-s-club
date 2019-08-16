package com.xando.chefsclub.Recipes.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = "recipeKey", unique = true)})
public class RecipeToFavoriteEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public final String recipeKey;

    public final String authorId;

    public RecipeToFavoriteEntity(String recipeKey, String authorId) {
        this.recipeKey = recipeKey;
        this.authorId = authorId;
    }
}
