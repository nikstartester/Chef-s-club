package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment

const val KEY_RECIPE_ID = "recipeId"

abstract class BaseFragmentWithRecipeKey : Fragment() {

    protected var recipeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            recipeId = arguments!!.getString(KEY_RECIPE_ID)
        }
    }

companion object{
    fun Fragment.withRecipeKey(recipeKey: String) = this.apply {
        arguments = Bundle().apply { putString(KEY_RECIPE_ID, recipeKey) }
    }
}
}
