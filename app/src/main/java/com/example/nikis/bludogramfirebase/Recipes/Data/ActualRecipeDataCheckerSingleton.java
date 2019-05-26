package com.example.nikis.bludogramfirebase.Recipes.Data;

public class ActualRecipeDataCheckerSingleton extends ActualRecipeDataChecker {
    private static final ActualRecipeDataCheckerSingleton ourInstance = new ActualRecipeDataCheckerSingleton();

    public static ActualRecipeDataCheckerSingleton getInstance() {
        return ourInstance;
    }

    private ActualRecipeDataCheckerSingleton() {
    }
}
