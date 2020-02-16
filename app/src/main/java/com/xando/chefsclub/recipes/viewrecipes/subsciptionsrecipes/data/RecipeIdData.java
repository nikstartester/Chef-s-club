package com.xando.chefsclub.recipes.viewrecipes.subsciptionsrecipes.data;

import com.xando.chefsclub.constants.Constants;

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
