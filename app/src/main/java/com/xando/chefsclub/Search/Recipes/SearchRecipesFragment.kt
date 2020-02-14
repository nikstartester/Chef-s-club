package com.xando.chefsclub.Search.Recipes

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import com.algolia.search.saas.Query
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.xando.chefsclub.App
import com.xando.chefsclub.DataWorkers.ParcResourceByParc
import com.xando.chefsclub.Helpers.FirebaseHelper
import com.xando.chefsclub.Helpers.getHostViewModel
import com.xando.chefsclub.Profiles.Data.ProfileData
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.Data.RecipeData
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity
import com.xando.chefsclub.Search.Recipes.Filter.FilterDialog.FilterDialog
import com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterAdapter
import com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData
import com.xando.chefsclub.Search.Recipes.Item.SearchRecipeItem
import com.xando.chefsclub.Search.Recipes.Parse.RecipesResultParser
import com.xando.chefsclub.Search.SearchListFragment
import java.util.*

class SearchRecipesFragment : SearchListFragment<RecipeData, SearchRecipeItem, RecipeFilterData>() {

    private val profileViewModel: ProfileViewModel by lazy { getHostViewModel<ProfileViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel.resourceLiveData.observe(this,
                Observer { res: ParcResourceByParc<ProfileData>? ->
                    if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {
                        filterAdapter.data.subscriptions = getSubscriptionsList(res.data!!.subscriptions)
                        updateFilter()
                    }
                })
    }

    private fun getSubscriptionsList(subscrMap: Map<String, Boolean>): List<String> {
        val subscrList = mutableListOf<String>()
        for ((key, value) in subscrMap) {
            if (value) {
                subscrList.add(key)
            }
        }
        return subscrList
    }

    override fun showFilter(filterBtn: View) {
        val filterDialog = FilterDialog()
        filterDialog.setTargetFragment(this, REQUEST_CODE_FILTER)
        filterDialog.arguments = FilterDialog.getArgs(filterAdapter.data)
        filterDialog.show(fragmentManager, "filterDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILTER && resultCode == FilterDialog.RESULT_CODE_FILTER_APPLY) {
            val filterData: RecipeFilterData = data.getParcelableExtra(FilterDialog.FILTER_DATA)
            updateFilterData(filterData)
            emptySearch()
        }
    }

    override fun getIndexName() = ALGOLIA_INDEX_NAME

    override fun getFilterAdapterInstance() = RecipeFilterAdapter()

    override fun getBaseQuery(): Query = Query().setRestrictSearchableAttributes("overviewData.name")

    override val searchResultJsonParser = RecipesResultParser()

    override fun getItems(dataList: List<RecipeData>) =
            dataList.map { SearchRecipeItem(it) }

    override val clickEventHook =
            object : ClickEventHook<IItem<out Any, out RecyclerView.ViewHolder>>() {

                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    return if (viewHolder is SearchRecipeItem.ViewHolder) {
                        val views: MutableList<View> = ArrayList()
                        views.add(viewHolder.itemView.findViewById(R.id.imageBtn_star))
                        views
                    } else null
                }

                override fun onClick(v: View, position: Int,
                                     fastAdapter: FastAdapter<IItem<out Any, out RecyclerView.ViewHolder>>,
                                     item: IItem<out Any, out RecyclerView.ViewHolder>) {
                    when (v.id) {
                        R.id.imageBtn_star -> onStarClicked(item as SearchRecipeItem)
                    }
                }
            }

    private fun onStarClicked(item: SearchRecipeItem) {
        val recipeData = item.recipeData
        FirebaseHelper.Favorite.updateFavorite(activity!!.application as App,
                RecipeToFavoriteEntity(recipeData.recipeKey, recipeData.authorUId))
        FirebaseHelper.Favorite.updateRecipeDataAndDBAfterFavoriteChange(
                activity!!.application as App,
                recipeData)
        item.setRecipeData(recipeData)
                .updateFavoriteImage()
                .updateStarCount()
    }

    override val clickItemListener =
            OnClickListener { _, _, item: IItem<out Any, out RecyclerView.ViewHolder>, _ ->
                if (item.type == R.id.search_recipe_item)
                    onItemClick(item as SearchRecipeItem)

                true
            }

    private fun onItemClick(item: SearchRecipeItem) {
        startActivity(ViewRecipeActivity.getIntent(
                activity,
                item.recipeData.recipeKey, FirebaseHelper.getUid() == item.recipeData.authorUId)
        )
    }

    companion object {

        private const val ALGOLIA_INDEX_NAME = "recipes"
        private const val REQUEST_CODE_FILTER = 7

        @JvmStatic
        fun getInstance(filterData: RecipeFilterData?): Fragment {
            val fragment: Fragment = SearchRecipesFragment()
            fragment.arguments = getArgs(filterData)
            return fragment
        }
    }
}