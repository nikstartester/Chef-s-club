package com.example.nikis.bludogramfirebase.Recipes.db;

import com.example.nikis.bludogramfirebase.App;

public class Helper {
    public static void deleteAll(App app) {
        new Thread(() -> app.getDatabase().recipeDao().deleteAll()).start();
    }
}
