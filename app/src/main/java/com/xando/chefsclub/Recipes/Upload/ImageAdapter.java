package com.xando.chefsclub.Recipes.Upload;

import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.Data.StepOfCooking;

class ImageAdapter {

    static RecipeImagesData toImagesData(RecipeData recipeData) {
        RecipeImagesData recipeImagesData = new RecipeImagesData();

        recipeImagesData.recipeKey = recipeData.recipeKey;

        recipeImagesData.overviewImages.allImagePathList = recipeData.overviewData.allImagePathList;
        recipeImagesData.overviewImages.imagePathsWithoutMainList = recipeData.overviewData.imagePathsWithoutMainList;
        recipeImagesData.overviewImages.mainImagePath = recipeData.overviewData.mainImagePath;

        for (StepOfCooking step : recipeData.stepsData.stepsOfCooking) {
            recipeImagesData.stepsImages.add(step.imagePath);
        }

        return recipeImagesData;
    }
}
