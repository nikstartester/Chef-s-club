package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_overview_description_item.view.*


class DescriptionItem(var description: String): AbstractItem<DescriptionItem, DescriptionItem.DescriptionViewHolder>() {

    private lateinit var viewHolder: DescriptionViewHolder

    override fun getType() = R.id.overview_recipe_description_item
    override fun getViewHolder(v: View) = DescriptionViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_description_item

    override fun bindView(holder: DescriptionViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        viewHolder.descriptionView.text = description
    }

    class DescriptionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val descriptionView: TextView = itemView.tv_description
    }
}