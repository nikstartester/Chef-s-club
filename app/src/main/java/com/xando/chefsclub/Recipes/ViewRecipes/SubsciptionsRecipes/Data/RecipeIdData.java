package com.xando.chefsclub.Recipes.ViewRecipes.SubsciptionsRecipes.Data;

import com.xando.chefsclub.Constants.Constants;

public class RecipeIdData {

    public String recipeKey;
    public BaseOverviewData overviewData;
    public String authorUId;

    public final long dateTime = Constants.ImageConstants.DEF_TIME;

    public RecipeIdData() {
    }

    public static class BaseOverviewData {
        public String name;

        public BaseOverviewData() {
        }
    }
}
