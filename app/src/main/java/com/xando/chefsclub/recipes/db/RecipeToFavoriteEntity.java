package com.xando.chefsclub.recipes.db;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
