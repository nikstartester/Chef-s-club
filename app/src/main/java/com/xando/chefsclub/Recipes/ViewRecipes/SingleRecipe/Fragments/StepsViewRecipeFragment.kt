package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.xando.chefsclub.DataWorkers.ParcResourceByParc
import com.xando.chefsclub.Helpers.getHostViewModel
import com.xando.chefsclub.Images.ImageData.ImageData
import com.xando.chefsclub.Images.ViewImages.ViewImagesActivity
import com.xando.chefsclub.List.GroupAdapter
import com.xando.chefsclub.List.MultiGroupsRecyclerViewAdapterImpl
import com.xando.chefsclub.List.SingleItemGroupAdapter
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.Data.RecipeData
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.AllTimeCookingItem
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.StepViewItem
import kotlinx.android.synthetic.main.fragment_view_recipe_steps.view.*
import java.util.*

private const val ALL_TIME_ADAPTER_ID = 10
private const val STEPS_ADAPTER_ID = 11

class StepsViewRecipeFragment : BaseFragmentWithRecipeKey() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var loadingFilterView: View

    private var recipeData: RecipeData? = null

    private val mRecipeViewModel: RecipeViewModel by lazy { getHostViewModel<RecipeViewModel>() }

    private val multiGroupsRecyclerViewAdapter = MultiGroupsRecyclerViewAdapterImpl()
    private val fastAdapter = multiGroupsRecyclerViewAdapter.getFastAdapter()

    private val allTimeAdapter = SingleItemGroupAdapter<AllTimeCookingItem>(ALL_TIME_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)

    private val stepsAdapter = GroupAdapter<StepViewItem>(STEPS_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)

    private var isLoaded = false

    companion object {
        fun getInstance(recipeId: String) = StepsViewRecipeFragment().withRecipeKey(recipeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_view_recipe_steps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        recyclerView.adapter = multiGroupsRecyclerViewAdapter

        mRecipeViewModel.resourceLiveData.observe(this, Observer { resource ->
            if (resource != null) {
                when {
                    resource.status == ParcResourceByParc.Status.SUCCESS -> {
                        onSuccessLoaded(resource)
                        hideProgress()
                    }
                    resource.status == ParcResourceByParc.Status.ERROR -> hideProgress()
                    else -> showProgress()
                }
            }
        })
    }

    private fun onSuccessLoaded(resource: ParcResourceByParc<RecipeData>) {
        if (isLoaded) return
        isLoaded = true

        recipeData = resource.data

        setDataToViews()
    }

    private fun initViews(mainView: View) {
        initRecyclerView(mainView)

        loadingFilterView = mainView.findViewById(R.id.filter)

        setOnClickListeners()

        multiGroupsRecyclerViewAdapter.addGroups(listOf(
                allTimeAdapter.groupId,
                stepsAdapter.groupId)
        )
    }

    private fun initRecyclerView(mainView: View) {
        recyclerView = mainView.rv_steps.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
        }
    }

    private fun setDataToViews() {
        recipeData!!.stepsData.timeMainNum.let {
            if (it > 0) allTimeAdapter.setItem(AllTimeCookingItem(it))
        }
        setStepsToAdapter()
    }

    @MainThread
    private fun showProgress() {
        loadingFilterView.visibility = View.VISIBLE
    }

    @MainThread
    private fun hideProgress() {
        loadingFilterView.visibility = View.GONE
    }

    private fun setOnClickListeners() {
        fastAdapter.withEventHook(initClickEventHook())
    }

    private fun initClickEventHook() =
            object : ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>() {

                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    val views = ArrayList<View>()
                    if (viewHolder is StepViewItem.ViewHolder)
                        views.add(viewHolder.itemView.findViewById(R.id.img_image))

                    return views
                }

                override fun onClick(v: View,
                                     position: Int,
                                     fastAdapter: FastAdapter<IItem<Any, RecyclerView.ViewHolder>>,
                                     item: IItem<Any, RecyclerView.ViewHolder>) {
                    when (v.id) {
                        R.id.img_image -> imageViewClick(stepsAdapter.getGroupPosition(position))
                    }
                }
            }

    private fun setStepsToAdapter() {
        stepsAdapter.addItems(recipeData!!.stepsData.stepsOfCooking
                .map { StepViewItem(it, ImageData(it.imagePath, recipeData!!.dateTime)) })
    }

    private fun imageViewClick(groupPosition: Int) {
        var changesPos = 0
        val dataList = ArrayList<ImageData>()
        var count = 0

        recipeData!!.stepsData.stepsOfCooking.forEachIndexed { index, step ->
            if (step.imagePath != null) {
                dataList.add(ImageData(step.imagePath, recipeData!!.dateTime))

                if (index == groupPosition) changesPos = count

                count++
            }
        }

        if (dataList.isNotEmpty()) {
            startActivity(ViewImagesActivity.getIntent(activity, dataList, changesPos))
        }
    }
}
