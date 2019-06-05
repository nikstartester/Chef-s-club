package com.xando.chefsclub.Recipes.db;

import com.xando.chefsclub.App;

public class Helper {
    public static void deleteAll(App app) {
        new Thread(() -> app.getDatabase().recipeDao().deleteAll()).start();
    }
}
