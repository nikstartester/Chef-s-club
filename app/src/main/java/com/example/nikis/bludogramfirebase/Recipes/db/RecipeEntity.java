package com.example.nikis.bludogramfirebase.Recipes.db;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.example.nikis.bludogramfirebase.Recipes.db.Converters.AllImagePathConverter;
import com.example.nikis.bludogramfirebase.Recipes.db.Converters.ImagePathsWithoutMainConverter;
import com.example.nikis.bludogramfirebase.Recipes.db.Converters.IngredientsConverter;
import com.example.nikis.bludogramfirebase.Recipes.db.Converters.StepsOfCookingConverter;

import java.util.List;

@Entity(indices = {@Index(value = "recipeKey", unique = true), @Index(value = "name")})
public class RecipeEntity {

    public static final String EMPTY_FIELD = "null";

    public RecipeEntity(RecipeData recipeData) {
        updateData(recipeData);

    }

    private void updateData(RecipeData recipeData) {
        this.recipeData = recipeData;

        name = recipeData.overviewData.name;
        mainImagePath = recipeData.overviewData.mainImagePath;
        ingredientsList = recipeData.overviewData.ingredientsList;
        imagePathsWithoutMainList = recipeData.overviewData.imagePathsWithoutMainList;
        description = recipeData.overviewData.description;
        categories = recipeData.overviewData.strCategories;
        allImagePathList = recipeData.overviewData.allImagePathList;


        timeMainNum = recipeData.stepsData.timeMainNum;
        stepsOfCooking = recipeData.stepsData.stepsOfCooking;
    }

    public RecipeData toRecipeData() {

        recipeData.overviewData.name = name;
        recipeData.overviewData.mainImagePath = mainImagePath;
        recipeData.overviewData.ingredientsList = ingredientsList;
        recipeData.overviewData.imagePathsWithoutMainList = imagePathsWithoutMainList;
        recipeData.overviewData.description = description;
        recipeData.overviewData.strCategories = categories;
        recipeData.overviewData.allImagePathList = allImagePathList;


        recipeData.stepsData.timeMainNum = timeMainNum;
        recipeData.stepsData.stepsOfCooking = stepsOfCooking;

        return recipeData;
    }


    @PrimaryKey(autoGenerate = true)
    public long id;

    public boolean isNeedSync;


    @Embedded
    public RecipeData recipeData;

    //////////////////////////////

    //OverviewData_START

    public String name;

    public String description;

    @TypeConverters(IngredientsConverter.class)
    public List<String> ingredientsList;

    public String mainImagePath;

    @TypeConverters(ImagePathsWithoutMainConverter.class)
    public List<String> imagePathsWithoutMainList;

    @TypeConverters(AllImagePathConverter.class)
    public List<String> allImagePathList;

    @TypeConverters(AllImagePathConverter.class)
    public List<String> categories;

    //OverviewData_END


///////////////////////////////////


    //StepsData_START

    public int timeMainNum;

    @TypeConverters(StepsOfCookingConverter.class)
    public List<StepOfCooking> stepsOfCooking;

    //StepsData_END


    /////////////////////
}
