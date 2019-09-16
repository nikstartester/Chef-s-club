package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.xando.chefsclub.App
import com.xando.chefsclub.DataWorkers.ParcResourceByParc
import com.xando.chefsclub.Helpers.FirebaseHelper
import com.xando.chefsclub.Images.ImageData.ImageData
import com.xando.chefsclub.Images.ViewImages.ViewImagesActivity
import com.xando.chefsclub.List.GroupAdapter
import com.xando.chefsclub.List.MultiGroupsRecyclerViewAdapterImpl
import com.xando.chefsclub.List.SingleItemGroupAdapter
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.Data.RecipeData
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.*
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity
import com.xando.chefsclub.ShoppingList.ViewShoppingListActivity
import com.xando.chefsclub.ShoppingList.db.IngredientEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import java.util.*


class NewOverviewRecipeFragment : BaseFragmentWithRecipeKey() {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingFilter: View

    private var recipeData: RecipeData? = null

    private val disposer = CompositeDisposable()

    private val multiGroupsRecyclerViewAdapter = MultiGroupsRecyclerViewAdapterImpl()

    private val photosAdapter = SingleItemGroupAdapter<PhotosItem>(10,
            multiGroupsRecyclerViewAdapter)
    private val propertiesAdapter = SingleItemGroupAdapter<PropertiesItem>(11,
            multiGroupsRecyclerViewAdapter)
    private val categoriesAdapter = SingleItemGroupAdapter<CategoriesItem>(12,
            multiGroupsRecyclerViewAdapter)
    private val descriptionAdapter = SingleItemGroupAdapter<DescriptionItem>(13,
            multiGroupsRecyclerViewAdapter)
    private val ingredientsHeaderAdapter = SingleItemGroupAdapter<IngredientsHeaderItem>(14,
            multiGroupsRecyclerViewAdapter)


    private val ingredientsAdapter = GroupAdapter<IngredientsViewItem>(16,
            multiGroupsRecyclerViewAdapter)

    private var isLoaded = false

    companion object {
        fun getInstance(recipeId: String) = NewOverviewRecipeFragment().withRecipeKey(recipeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeViewModel = ViewModelProviders.of(activity!!).get(RecipeViewModel::class.java)
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        recyclerView.adapter = multiGroupsRecyclerViewAdapter

        recipeViewModel.resourceLiveData.observe(this, Observer {
            if (it != null) {
                when {
                    it.status == ParcResourceByParc.Status.SUCCESS -> {
                        onSuccessLoaded(it)
                        hideProgress()
                    }
                    it.status == ParcResourceByParc.Status.ERROR -> {
                        onErrorLoaded(it)
                        hideProgress()
                    }
                    else -> showProgress()
                }
            }
        })

        if (recipeId.isNotEmpty()) {
            val flowable = (activity!!.application as App)
                    .database
                    .recipeDao()
                    .getFlowableByRecipeKey(recipeId)

            val disposable = flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { recipeEntities ->
                        //
                        // it calls twice if use sync when open recipe(see BaseRepository class)
                        // because use flowable
                        //
                        propertiesAdapter.getItem().apply {
                            if (isSavedLocal != recipeEntities.isNotEmpty()) {
                                isSavedLocal = recipeEntities.isNotEmpty()
                                updateSaveLocalViews()
                            }
                        }
                    }

            disposer.add(disposable)
        }
    }

    private fun initViews(mainView: View) {
        recyclerView = mainView.recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()

        loadingFilter = mainView.findViewById(R.id.filter)

        multiGroupsRecyclerViewAdapter.addGroups(listOf(
                photosAdapter.groupId,
                propertiesAdapter.groupId,
                categoriesAdapter.groupId,
                descriptionAdapter.groupId,
                ingredientsHeaderAdapter.groupId,
                ingredientsAdapter.groupId)
        )

        photosAdapter.setItem(PhotosItem(RecipeData(), profileViewModel, this,
                onImageClick = ::onImageClick, onProfileClick = ::onProfileClick))
        propertiesAdapter.setItem(PropertiesItem(RecipeData(), false))

        propertiesAdapter.getFastAdapter().withEventHook(object : ClickEventHook<PropertiesItem>() {

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
                    if (viewHolder is PropertiesItem.PropertiesViewHolder)
                        viewHolder.imageFavorite
                    else null

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<PropertiesItem>, item: PropertiesItem) {
                if (v.id == R.id.imgFavorite) {
                    onFavoriteClick(item)
                }
            }
        } as ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>)
    }

    override fun onDestroy() {
        disposer.dispose()
        super.onDestroy()
    }

    private fun onSuccessLoaded(res: ParcResourceByParc<RecipeData>) {
        if (isLoaded) return

        isLoaded = true

        recipeData = res.data

        photosAdapter.getItem().apply {
            recipeData = this@NewOverviewRecipeFragment.recipeData!!
            resetView()
            isInit = true
        }

        propertiesAdapter.getItem().apply {
            recipeData = this@NewOverviewRecipeFragment.recipeData!!
            resetView()
            isInit = true
        }

        recipeData!!.overviewData.strCategories
                .filter { it.isNullOrEmpty().not() }
                .let {
                    if (it.isNotEmpty())
                        categoriesAdapter.setItem(CategoriesItem(it))
                }

        recipeData!!.overviewData.description.let {
            if (it.isNotBlank()) descriptionAdapter.setItem(DescriptionItem(it))
        }

        ingredientsHeaderAdapter.setItem(IngredientsHeaderItem(onActionClick = ::onIngredientsActionsClick))

        ingredientsAdapter.addItems(with(recipeData!!) {
            overviewData.ingredientsList.map {
                IngredientsViewItem(IngredientEntity(recipeKey, overviewData.name, it, dateTime))
            }
        })

        if (profileViewModel.resourceLiveData.value == null) {
            profileViewModel.loadDataWithoutSaver(recipeData!!.authorUId)
        }

    }

    private fun onErrorLoaded(res: ParcResourceByParc<RecipeData>) {

    }

    private fun hideProgress() {
        loadingFilter.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showProgress() {
        recyclerView.visibility = View.GONE
        loadingFilter.visibility = View.VISIBLE
    }

    private fun onImageClick(isMain: Boolean, pos: Int) {
        var changesPos = pos
        val dataList = ArrayList<ImageData>()

        for (i in recipeData!!.overviewData.allImagePathList.indices) {
            var imageData: ImageData? = null

            if (i != 0 || recipeData!!.overviewData.allImagePathList[i] != null) {
                imageData = ImageData(recipeData!!.overviewData.allImagePathList[i], recipeData!!.dateTime)
            } else
                changesPos--

            if (imageData != null) {
                dataList.add(imageData)
            }
        }
        if (dataList.isNotEmpty()) {
            startActivity(ViewImagesActivity.getIntent(activity, dataList, if (isMain) 0 else changesPos))
        }
    }

    private fun onProfileClick() {
        startActivity(ViewProfileActivityTest.getIntent(activity, recipeData!!.authorUId))
    }

    private fun onFavoriteClick(item: PropertiesItem) {
        val app = activity!!.application as App
        FirebaseHelper.Favorite.updateFavorite(app, RecipeToFavoriteEntity(recipeId, recipeData!!.authorUId))

        FirebaseHelper.Favorite.updateDBAfterFavoriteChange(app,
                FirebaseHelper.Favorite.updateRecipeDataWithFavoriteChange(recipeData))

        item.updateStarsViews(true)
    }

    private fun onIngredientsActionsClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.act_edit_available -> changeIngredientsEditMode()
            R.id.act_add_all_to_shopping_list -> addAllIngredientsToShoppingList()
            R.id.act_delete_all_from_shopping_list -> deleteAllIngredientsFromShoppingList()
            R.id.act_open_shopping_list -> startActivity(ViewShoppingListActivity.
                    getIntent(activity, recipeData!!.recipeKey)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }

    private fun changeIngredientsEditMode() {

    }

    private fun addAllIngredientsToShoppingList() {

    }

    private fun deleteAllIngredientsFromShoppingList() {

    }
}