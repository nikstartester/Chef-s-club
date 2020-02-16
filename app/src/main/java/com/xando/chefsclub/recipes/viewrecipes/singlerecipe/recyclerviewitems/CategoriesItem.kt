package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems

import android.support.v7.widget.RecyclerView
import android.view.View
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_overview_categories_item.view.*


class CategoriesItem(val categoriesList: List<String>): AbstractItem<CategoriesItem, CategoriesItem.CategoriesViewHolder>() {

    private lateinit var viewHolder: CategoriesViewHolder

    private val categoriesAdapter: FastItemAdapter<ChipCategoryItem> = FastItemAdapter()

    override fun getType() = R.id.overview_recipe_categories_item
    override fun getViewHolder(v: View) = CategoriesViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_categories_item

    override fun bindView(holder: CategoriesViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        viewHolder.categories.apply {
            layoutManager = ChipsLayoutManager
                    .newBuilder(viewHolder.categories.context)
                    .build()
            adapter = categoriesAdapter
        }

        resetView()
    }

    fun resetView(){
        if(::viewHolder.isInitialized.not()) return

        categoriesAdapter.clear()

        categoriesList.forEach { categoriesAdapter.add(ChipCategoryItem(it)) }
    }

    class CategoriesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val categories: RecyclerView = itemView.rv_selectedCategories
    }
}