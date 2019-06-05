package com.xando.chefsclub.Search.Recipes.Parse;

import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Search.Parse.SearchResultJsonParser;

public class RecipesResultParser extends SearchResultJsonParser<RecipeData> {

    @Override
    protected Class<RecipeData> getDataClass() {
        return RecipeData.class;
    }

}
