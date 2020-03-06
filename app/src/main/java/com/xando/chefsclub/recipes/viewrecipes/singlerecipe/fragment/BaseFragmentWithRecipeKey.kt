package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

const val KEY_RECIPE_ID = "recipeId"

abstract class BaseFragmentWithRecipeKey : Fragment() {

    companion object {
        fun Fragment.withRecipeKey(recipeKey: String) = this.apply {
            arguments = Bundle().apply { putString(KEY_RECIPE_ID, recipeKey) }
        }
    }

    protected var recipeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = arguments?.getString(KEY_RECIPE_ID) ?: ""
    }
}
