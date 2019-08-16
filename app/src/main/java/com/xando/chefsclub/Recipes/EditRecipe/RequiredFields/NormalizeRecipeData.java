package com.xando.chefsclub.Recipes.EditRecipe.RequiredFields;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.Data.StepOfCooking;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NormalizeRecipeData {
    public static final String NAME_TEXT = "Name";
    public static final String INGREDIENTS_TEXT = "At least 2 unique ingredients";
    public static final String STEPS_TEXT = "At least 1 step of cooking";

    public static final int MIN_INGREDIENTS_COUNT = 2;
    public static final int MIN_STEPS_COUNT = 1;

    private RecipeData mRecipeData;

    public static boolean checkRequired(RecipeData recipeData, boolean useNormalizeMethods) {
        if (useNormalizeMethods) {
            return checkText(recipeData.overviewData.name)
                    && checkIngredients(getNormalizeIngredients(recipeData.overviewData.ingredientsList))
                    && checkSteps(getNormalizeSteps(recipeData.stepsData.stepsOfCooking));
        } else return checkText(recipeData.overviewData.name)
                && checkIngredients(recipeData.overviewData.ingredientsList)
                && checkSteps(recipeData.stepsData.stepsOfCooking);
    }

    private static boolean checkText(String text) {
        return text != null && !TextUtils.isEmpty(text);
    }

    private static boolean checkIngredients(List<String> list) {
        int count = 0;
        for (String ingr : list) {
            if (checkText(ingr)) {
                count++;
            }

            if (count >= MIN_INGREDIENTS_COUNT) break;
        }
        return count >= MIN_INGREDIENTS_COUNT;
    }

    private static boolean checkSteps(List<StepOfCooking> steps) {
        int count = 0;
        for (StepOfCooking step : steps) {
            if (checkText(step.text)) {
                count++;
            }

            if (count >= MIN_STEPS_COUNT) break;
        }
        return count >= MIN_STEPS_COUNT;
    }

    public static RecipeData normalizeRecipeData(RecipeData recipeData) {
        recipeData.overviewData.ingredientsList = getNormalizeIngredients(recipeData.overviewData.ingredientsList);

        recipeData.stepsData.stepsOfCooking = getNormalizeSteps(recipeData.stepsData.stepsOfCooking);

        return recipeData;
    }

    public static List<StepOfCooking> getNormalizeSteps(@NonNull List<StepOfCooking> toNormalize) {
        List<StepOfCooking> tmpList = new ArrayList<>();

        for (StepOfCooking step : toNormalize) {
            if (step != null && checkText(step.text)) {
                tmpList.add(step);
            }
        }

        int addCount = MIN_STEPS_COUNT - tmpList.size();

        for (int i = 0; i < addCount; i++) {
            tmpList.add(new StepOfCooking());
        }

        return tmpList;
    }

    public static List<String> getNormalizeIngredients(@NonNull List<String> toNormalize) {
        char multSymb = 'X';

        Map<String, Integer> map = new LinkedHashMap<>();

        for (String ingr : toNormalize) {
            if (ingr == null || TextUtils.isEmpty(ingr)) continue;

            String toAdd = ingr;
            int mult = 1;

            String start = ingr.split(" ")[0];

            if (start.charAt(0) == multSymb) {
                try {
                    mult = Integer.parseInt(start.substring(1));
                    toAdd = ingr.substring(start.length() + 1);
                } catch (NumberFormatException ex) {
                    mult = 1;
                }
            }

            if (map.containsKey(toAdd) && map.get(toAdd) != null) {
                mult += map.get(toAdd);
            }
            map.put(toAdd, mult);

        }

        List<String> tmpList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String s = "";
            if (entry.getValue() > 1) {
                s += multSymb + entry.getValue().toString() + " ";
            }
            s += entry.getKey();

            tmpList.add(s);
        }

        int addCount = MIN_INGREDIENTS_COUNT - tmpList.size();

        for (int i = 0; i < addCount; i++) {
            tmpList.add("");
        }

        return tmpList;
    }

    public RequiredFieldsData getRequiredFields(RecipeData recipeData, boolean useNormalizeMethods) {
        mRecipeData = recipeData;

        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();

        map.put(NAME_TEXT, checkName());

        if (useNormalizeMethods) {
            map.put(INGREDIENTS_TEXT, checkIngredients(getNormalizeIngredients(recipeData.overviewData.ingredientsList)));
            map.put(STEPS_TEXT, checkSteps(getNormalizeSteps(recipeData.stepsData.stepsOfCooking)));
        } else {
            map.put(INGREDIENTS_TEXT, checkIngredients());
            map.put(STEPS_TEXT, checkSteps());
        }

        return new RequiredFieldsData(map);
    }

    private boolean checkName() {
        return checkText(mRecipeData.overviewData.name);
    }

    private boolean checkIngredients() {
        return checkIngredients(mRecipeData.overviewData.ingredientsList);
    }

    private boolean checkSteps() {
        return checkSteps(mRecipeData.stepsData.stepsOfCooking);
    }
}
