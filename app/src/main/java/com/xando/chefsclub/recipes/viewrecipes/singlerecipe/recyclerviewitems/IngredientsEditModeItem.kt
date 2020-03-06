package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_overview_ingredients_edit_mode_item.view.*

class IngredientsEditModeItem(private val onAllToShoppingListClick: (isChecked: Boolean) -> Unit,
                              private val onAllToAvailabilityClick: (isChecked: Boolean) -> Unit)
    : AbstractItem<IngredientsEditModeItem, IngredientsEditModeItem.IngredientsEditModeViewHolder>() {

    private lateinit var viewHolder: IngredientsEditModeViewHolder

    var isAllToShoppingListChecked = false
    var isAllToAvailabilityChecked = false


    override fun getType() = R.id.overview_recipe_ingredients_edit_mode_item
    override fun getViewHolder(v: View) = IngredientsEditModeViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_ingredients_edit_mode_item

    override fun bindView(holder: IngredientsEditModeViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        viewHolder.toShoppingListCheckBox.isChecked = isAllToShoppingListChecked
        viewHolder.toAvailabilityCheckBox.isChecked = isAllToAvailabilityChecked

        viewHolder.toShoppingListCheckBox.setOnCheckedChangeListener { _, isChecked ->
            run {
                onAllToShoppingListClick(isChecked)
                isAllToShoppingListChecked = isChecked
            }
        }
        viewHolder.toAvailabilityCheckBox.setOnCheckedChangeListener { _, isChecked ->
            run {
                onAllToAvailabilityClick(isChecked)
                isAllToAvailabilityChecked = isChecked
            }
        }
    }

    override fun unbindView(holder: IngredientsEditModeViewHolder) {
        viewHolder.toShoppingListCheckBox.setOnCheckedChangeListener(null)
        viewHolder.toAvailabilityCheckBox.setOnCheckedChangeListener(null)

        super.unbindView(holder)
    }

    class IngredientsEditModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val toShoppingListCheckBox: CheckBox = itemView.checkBox_all_to_shoppingList
        val toAvailabilityCheckBox: CheckBox = itemView.checkBox_all_available
        val closeView: View = itemView.imgBtn_close_edit_mode
    }
}