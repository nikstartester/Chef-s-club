package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_overview_ingredients_header_item.view.*


class IngredientsHeaderItem(var isEditMode: Boolean = false,
                            val onActionClick: (menuItem: MenuItem) -> Unit)
    : AbstractItem<IngredientsHeaderItem, IngredientsHeaderItem.IngredientsHeaderViewHolder>() {

    private lateinit var viewHolder: IngredientsHeaderViewHolder

    override fun getType() = R.id.overview_recipe_ingredients_header_item
    override fun getViewHolder(v: View) = IngredientsHeaderViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_ingredients_header_item

    override fun bindView(holder: IngredientsHeaderViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        viewHolder.actionView.setOnClickListener { showIngredientsActions() }
    }

    protected fun showIngredientsActions() {
        val popupMenu = PopupMenu(viewHolder.actionView.context, viewHolder.actionView)

        popupMenu.inflate(R.menu.ingredients_menu)

        popupMenu.menu.findItem(R.id.act_edit_available).isVisible = !isEditMode

        popupMenu.setOnMenuItemClickListener { item ->
            onActionClick(item)
            true
        }

        popupMenu.show()
    }

    class IngredientsHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val actionView: View = itemView.imageBtn_ingredients_actions
    }
}