package com.xando.chefsclub.search.recipes.parser;

import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.search.parser.SearchResultJsonParser;

public class RecipesResultParser extends SearchResultJsonParser<RecipeData> {

    @Override
    protected Class<RecipeData> getDataClass() {
        return RecipeData.class;
    }

}
