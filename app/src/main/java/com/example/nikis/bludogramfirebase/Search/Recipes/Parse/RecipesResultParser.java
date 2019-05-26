package com.example.nikis.bludogramfirebase.Search.Recipes.Parse;

import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Search.Parse.SearchResultJsonParser;

public class RecipesResultParser extends SearchResultJsonParser<RecipeData> {

    @Override
    protected Class<RecipeData> getDataClass() {
        return RecipeData.class;
    }

}
