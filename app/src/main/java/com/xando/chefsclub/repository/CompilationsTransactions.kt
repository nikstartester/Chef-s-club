package com.xando.chefsclub.repository

import com.xando.chefsclub.FirebaseReferences
import com.xando.chefsclub.compilations.data.CompilationData
import com.xando.chefsclub.recipes.data.RecipeData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

// TODO: replace to CompilationRepository
/**
 * NOTE: recipes in 'user-recipes' doesn't include compilation!
 */
object CompilationsTransactions {

    /**
     * Use if you want add recipe to compilation. Update recipe AND compilation data
     */
    fun onAddRecipeToCompilation(compilationKey: String, recipeKey: String) =
        startRecipeCompilationTransaction(
            compilationKey,
            recipeKey,
            { it.addToCompilation(compilationKey).getCompilationChild() },
            { it.addRecipe(recipeKey).getRecipeChild() }
        )

    /**
     * Use if you want remove recipe from compilation. Update recipe AND compilation data
     */
    fun onRemoveRecipeFromCompilation(compilationKey: String, recipeKey: String) =
        startRecipeCompilationTransaction(
            compilationKey,
            recipeKey,
            { it.removeCompilation(compilationKey).getCompilationChild() },
            { it.removeRecipe(recipeKey).getRecipeChild() }
        )

    /**
     * Use if you want remove compilation from recipe. Update ONLY recipe data
     */
    fun removeCompilationFromRecipe(compilationKey: String, recipeKey: String): Disposable {
        val sourceRef = FirebaseReferences.getDataBaseReference().child("recipes/$recipeKey/")

        return FirebaseLoader(sourceRef, RecipeData::class.java)
            .loadOnce()
            .subscribeOn(Schedulers.io())
            .subscribe { sourceRef.updateChildren(it.removeCompilation(compilationKey).getCompilationChild()) }
    }

    /**
     * Use if you want remove recipe from compilation. Update ONLY compilation data
     */
    fun removeRecipeFromCompilation(compilationKey: String, recipeKey: String): Disposable {
        val sourceRef = FirebaseReferences.getDataBaseReference().child("compilations/$compilationKey/")

        return FirebaseLoader(sourceRef, CompilationData::class.java)
            .loadOnce()
            .subscribeOn(Schedulers.io())
            .subscribe { sourceRef.updateChildren(it.removeRecipe(recipeKey).getRecipeChild()) }
    }

    /**
     * Update recipe AND compilation data
     */
    private fun startRecipeCompilationTransaction(
        compilationKey: String,
        recipeKey: String,
        recipeResult: (RecipeData) -> Map<String, Any>,
        compilationResult: (CompilationData) -> Map<String, Any>
    ): Disposable {
        val rootReference = FirebaseReferences.getDataBaseReference()

        val recipeChild = "recipes/$recipeKey/"
        val compilationChild = "compilations/$compilationKey/"

        val recipeRef = rootReference.child(recipeChild)
        val compilationRef = rootReference.child(compilationChild)

        return Observable.zip<RecipeData, CompilationData, Map<String, Any>>(
            FirebaseLoader(recipeRef, RecipeData::class.java).loadOnce(),
            FirebaseLoader(compilationRef, CompilationData::class.java).loadOnce(),
            BiFunction { recipe, compilation ->
                return@BiFunction hashMapOf<String, Any>()
                    .apply {
                        putAll(recipeResult.invoke(recipe).mapKeys { "${recipeChild}${it.key}" })
                        putAll(compilationResult.invoke(compilation).mapKeys { "${compilationChild}${it.key}" })
                    }
            }
        )
            .subscribeOn(Schedulers.io())
            .doOnNext { rootReference.updateChildren(it) }
            .subscribe()
    }

    //region Actions
    private fun RecipeData.addToCompilation(compilationKey: String) = apply {
        inCompilations[compilationKey] = true
    }

    private fun RecipeData.removeCompilation(compilationKey: String) = apply {
        inCompilations.remove(compilationKey)
    }

    private fun CompilationData.addRecipe(recipeKey: String) = apply {
        if (recipesKey.contains(recipeKey).not()) {
            count++
            recipesKey.add(recipeKey)
        }
    }

    private fun CompilationData.removeRecipe(recipeKey: String) = apply {
        if (recipesKey.remove(recipeKey)) count--
    }
    //endregion

    private fun RecipeData.getCompilationChild() = mapOf<String, Any>("inCompilations" to inCompilations)
    private fun CompilationData.getRecipeChild() = mapOf<String, Any>("recipesKey" to recipesKey, "count" to count)
}