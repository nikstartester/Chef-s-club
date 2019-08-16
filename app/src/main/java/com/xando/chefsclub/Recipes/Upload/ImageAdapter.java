package com.xando.chefsclub.Recipes.Upload;

import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.Data.StepOfCooking;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter {

    public static RecipeImagesData toImagesData(RecipeData recipeData) {
        RecipeImagesData recipeImagesData = new RecipeImagesData();

        recipeImagesData.recipeKey = recipeData.recipeKey;

        recipeImagesData.overviewImages.allImagePathList = new ArrayList<>(recipeData.overviewData.allImagePathList);
        recipeImagesData.overviewImages.imagePathsWithoutMainList = new ArrayList<>(recipeData.overviewData.imagePathsWithoutMainList);
        recipeImagesData.overviewImages.mainImagePath = recipeData.overviewData.mainImagePath;

        for (StepOfCooking step : recipeData.stepsData.stepsOfCooking) {
            recipeImagesData.stepsImages.add(step.imagePath);
        }

        return recipeImagesData;
    }

    public static List<String> getRemovedImages(RecipeImagesData oldData, RecipeImagesData newData) {
        List<String> list = new ArrayList<>();

        for (String imOld : oldData.overviewImages.allImagePathList) {
            if (imOld != null && imOld.startsWith(Constants.ImageConstants.FIREBASE_STORAGE_AT_START)
                    && !newData.overviewImages.allImagePathList.contains(imOld)) {
                list.add(imOld);
            }
        }

        for (String imOld : oldData.stepsImages) {
            if (imOld != null && imOld.startsWith(Constants.ImageConstants.FIREBASE_STORAGE_AT_START)
                    && !newData.stepsImages.contains(imOld)) {
                list.add(imOld);
            }
        }

        return list;
    }
}
