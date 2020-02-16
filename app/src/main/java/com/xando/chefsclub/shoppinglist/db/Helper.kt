package com.xando.chefsclub.shoppinglist.db

import com.xando.chefsclub.App

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

object Helper {

    fun addToDB(app: App, entity: IngredientEntity) {
        doBackgroundAction { app.getDao().insert(entity) }
    }

    fun addListToDB(app: App, list: List<IngredientEntity>) {
        doBackgroundAction { app.getDao().insert(list) }
    }

    fun replace(app: App, list: List<IngredientEntity>) {
        doBackgroundAction { app.getDao().replace(list) }
    }

    fun changeAllAvailable(app: App, recipeId: String, isAvailable: Boolean) {
        doBackgroundAction { app.getDao().changeAllAvailable(recipeId, isAvailable) }
    }

    fun deleteFromDB(app: App, entity: IngredientEntity) {
        doBackgroundAction { app.getDao().delete(entity.recipeId, entity.ingredient) }
    }

    fun deleteAllFromDB(app: App) {
        doBackgroundAction { app.getDao().deleteAll() }
    }

    fun deleteByRecipeIdFromDB(app: App, recipeId: String) {
        doBackgroundAction { app.getDao().delete(recipeId) }
    }

    fun changeAvailableFromDB(app: App, entity: IngredientEntity) {
        doBackgroundAction {
            app.getDao().changeAvailable(entity.recipeId, entity.ingredient, entity.isAvailable)
        }
    }

    fun changeAvailableFromDBNew(app: App, entity: IngredientEntity) {
        doBackgroundAction {
            val list = app.getDao().get(entity.recipeId, entity.ingredient)
            if (list.isNotEmpty())
                app.getDao().changeAvailable(entity.recipeId, entity.ingredient, entity.isAvailable)
            else app.getDao().insert(entity)
        }
    }

    private fun doBackgroundAction(action: () -> Unit) {
        Completable.fromAction { action() }.subscribeOn(Schedulers.io()).subscribe()
    }

    private fun App.getDao() = database.ingredientsDao()
}

