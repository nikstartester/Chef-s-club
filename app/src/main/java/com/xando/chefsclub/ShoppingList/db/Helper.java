package com.xando.chefsclub.ShoppingList.db;

import com.xando.chefsclub.App;

import java.util.List;

public class Helper {

    public static void addToDB(App app, final IngredientEntity entity) {
        new Thread(() -> app.getDatabase().ingredientsDao().insert(entity)).start();
    }

    public static void addListToDB(App app, List<IngredientEntity> list) {
        new Thread(() -> app.getDatabase().ingredientsDao().insert(list)).start();
    }

    public static void deleteFromDB(App app, IngredientEntity entity) {
        new Thread(() -> app.getDatabase().ingredientsDao()
                .delete(entity.recipeId, entity.ingredient)).start();
    }

    public static void deleteAllFromDB(App app) {
        new Thread(() -> app.getDatabase().ingredientsDao()
                .deleteAll()).start();
    }

    public static void deleteByRecipeIdFromDB(App app, String recipeId) {
        new Thread(() -> app.getDatabase().ingredientsDao()
                .delete(recipeId)).start();
    }

    public static void changeAvailableFromDB(App app, IngredientEntity entity) {
        new Thread(() -> app.getDatabase().ingredientsDao()
                .changeAvailable(entity.recipeId, entity.ingredient, entity.isAvailable)).start();
    }
}
