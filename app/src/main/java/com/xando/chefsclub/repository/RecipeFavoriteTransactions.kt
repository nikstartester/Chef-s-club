package com.xando.chefsclub.repository

import com.google.firebase.database.DatabaseError
import com.xando.chefsclub.App
import com.xando.chefsclub.FirebaseReferences
import com.xando.chefsclub.helper.FirebaseHelper
import com.xando.chefsclub.recipes.data.RecipeData
import com.xando.chefsclub.recipes.db.RecipeToFavoriteEntity
import com.xando.chefsclub.recipes.db.RecipesToFavoriteDao
import com.xando.chefsclub.recipes.db.converter.MapBoolConverter
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

// TODO: replace to RecipeRepository
object Favorite {
    private lateinit var recipesToFavoriteDao: RecipesToFavoriteDao

    /**
     * Main method to switch favorite of [model]. Add [model] to cache for sync if transaction aborted (for example: no network)
     * Use [syncFavorite] to sync aborted transaction when app restart. Not necessary in another time because firebase has own runtime cache
     */
    fun switchFavorite(app: App, model: RecipeToFavoriteEntity): Disposable {
        val disposer = CompositeDisposable()
        disposer += insertFavoriteEntity(app, model)
        disposer += switchFavoriteOnServer(app, model)
        return disposer
    }

    /**
     * Update favorite to local data
     */
    fun updateFavoriteInDB(app: App, recipeData: RecipeData): Disposable {
        val recipeDao = app.database.recipeDao()
        return Single.fromCallable {
            recipeDao.updateStar(
                recipeData.recipeKey,
                recipeData.starCount,
                MapBoolConverter.fromStars(recipeData.stars),
                true
            )
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    /**
     * Sync aborted favorite transaction. Call when app restart.
     * Not necessary in another time because firebase has own runtime cache
     */
    fun syncFavorite(app: App): Disposable {
        if (::recipesToFavoriteDao.isInitialized.not()) recipesToFavoriteDao = app.database.recipesToFavoriteDao()

        return recipesToFavoriteDao.singleAll
            .subscribeOn(Schedulers.io())
            .subscribe { entities: List<RecipeToFavoriteEntity> ->
                for (entity in entities) switchFavoriteOnServer(app, entity)
            }
    }

    /**
     * Insert [model] to cache to sync aborted transaction when app restart
     */
    private fun insertFavoriteEntity(app: App, model: RecipeToFavoriteEntity): Disposable {
        if (::recipesToFavoriteDao.isInitialized.not()) recipesToFavoriteDao = app.database.recipesToFavoriteDao()

        return Single.fromCallable {
            recipesToFavoriteDao.insert(
                RecipeToFavoriteEntity(
                    model.recipeKey,
                    model.authorId
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    /**
     * Switch favorite without cache
     */
    private fun switchFavoriteOnServer(app: App, model: RecipeToFavoriteEntity): Disposable {
        if (::recipesToFavoriteDao.isInitialized.not()) recipesToFavoriteDao = app.database.recipesToFavoriteDao()

        return startRecipeTransaction(
            model.recipeKey,
            model.authorId,
            { recipe -> recipe.switchFavorite().getFavoriteChildren() },
            onSuccess = {
                Thread(Runnable { recipesToFavoriteDao.deleteByKey(it.recipeKey) }).start()
            }
        )
    }

    /**
     * Load recipe end put [recipeResult] to global child and user child
     */
    private fun startRecipeTransaction(
        recipeKey: String,
        authorId: String,
        recipeResult: (recipe: RecipeData) -> Map<String, Any>,
        onLoadedError: (ex: Throwable) -> Unit = {},
        onUploadError: (error: DatabaseError) -> Unit = {},
        onSuccess: (recipe: RecipeData) -> Unit = {}
    ): Disposable {
        val rootReference = FirebaseReferences.getDataBaseReference()

        val sourceRef = rootReference
            .child("recipes")
            .child(recipeKey)

        return FirebaseLoader(sourceRef, RecipeData::class.java)
            .loadOnce()
            .subscribeOn(Schedulers.io())
            .subscribe({
                           val globalChild = "recipes/$recipeKey/"
                           val userChild = "user-recipes/${authorId}/$recipeKey/"

                           val result = recipeResult.invoke(it)

                           val allResult = hashMapOf<String, Any>().apply {
                               putAll(result.mapKeys { entry -> "${globalChild}${entry.key}" })
                               putAll(result.mapKeys { entry -> "${userChild}${entry.key}" })
                           }

                           rootReference.updateChildren(allResult) { error, _ ->
                               if (error == null) onSuccess.invoke(it)
                               else onUploadError.invoke(error)
                           }
                       }, { onLoadedError.invoke(it) })
    }

    /**
     * @return map of favorite data to update child
     */
    private fun RecipeData.getFavoriteChildren(): Map<String, Any> =
        mapOf("stars" to stars, "starCount" to starCount)
}

fun RecipeData.switchFavorite() = apply {
    if (stars.containsKey(FirebaseHelper.getUid())) {
        starCount--
        stars.remove(FirebaseHelper.getUid())
    } else {
        starCount++
        stars[FirebaseHelper.getUid()] = true
    }
}