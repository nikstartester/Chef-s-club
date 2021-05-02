package com.xando.chefsclub.search.recipes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Query
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.xando.chefsclub.App
import com.xando.chefsclub.R
import com.xando.chefsclub.dataworkers.ParcResourceByParc
import com.xando.chefsclub.helper.FirebaseHelper
import com.xando.chefsclub.helper.getHostViewModel
import com.xando.chefsclub.profiles.data.ProfileData
import com.xando.chefsclub.profiles.viewmodel.ProfileViewModel
import com.xando.chefsclub.recipes.data.RecipeData
import com.xando.chefsclub.recipes.db.RecipeToFavoriteEntity
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.ViewRecipeActivity
import com.xando.chefsclub.repository.Favorite
import com.xando.chefsclub.repository.switchFavorite
import com.xando.chefsclub.search.SearchListFragment
import com.xando.chefsclub.search.recipes.filter.RecipeFilterAdapter
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData
import com.xando.chefsclub.search.recipes.filter.dialog.FilterDialog
import com.xando.chefsclub.search.recipes.item.SearchRecipeItem
import com.xando.chefsclub.search.recipes.parser.RecipesResultParser
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*

class SearchRecipesFragment : SearchListFragment<RecipeData, SearchRecipeItem, RecipeFilterData>() {

    private val profileViewModel: ProfileViewModel by lazy { getHostViewModel<ProfileViewModel>() }
    private val disposer = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.resourceLiveData.observe(
            viewLifecycleOwner,
            Observer { res: ParcResourceByParc<ProfileData>? ->
                if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {
                    filterAdapter.data.subscriptions =
                        getSubscriptionsList(res.data!!.subscriptions)
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
        filterDialog.show(parentFragmentManager, "filterDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILTER && resultCode == FilterDialog.RESULT_CODE_FILTER_APPLY) {
            val filterData: RecipeFilterData = data?.getParcelableExtra(FilterDialog.FILTER_DATA)
                    ?: RecipeFilterData()
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
        disposer += Favorite.switchFavorite(
            requireActivity().application as App,
            RecipeToFavoriteEntity(recipeData.recipeKey, recipeData.authorUId)
        )
        disposer += Favorite.updateFavoriteInDB(requireActivity().application as App, recipeData.switchFavorite())

        item.setRecipeData(recipeData).updateFavoriteImage().updateStarCount()
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