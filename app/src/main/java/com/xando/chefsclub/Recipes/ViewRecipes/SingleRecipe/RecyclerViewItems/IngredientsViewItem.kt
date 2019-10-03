package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.daimajia.androidanimations.library.YoYo
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.xando.chefsclub.Helpers.UiHelper
import com.xando.chefsclub.R
import com.xando.chefsclub.ShoppingList.db.IngredientEntity
import kotlinx.android.synthetic.main.list_ingredients_view_item.view.*
import java.util.*


class IngredientsViewItem(var data: IngredientEntity)
    : AbstractItem<IngredientsViewItem, IngredientsViewItem.IngredientViewItemViewHolder>() {

    private val animations = ArrayList<YoYo.YoYoString?>()
    private var isEditAvailableMode: Boolean = false
    private lateinit var viewHolder: IngredientViewItemViewHolder

    override fun getViewHolder(v: View) = IngredientViewItemViewHolder(v)
    override fun getType() = R.id.ingredients_item_view_new
    override fun getLayoutRes() = R.layout.list_ingredients_view_item

    override fun bindView(holder: IngredientViewItemViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        updateEnabledAvailableCheckBox()

        updateAvailable()

        viewHolder.checkBox.isChecked = isSelected

        viewHolder.ingredientView.text = data.ingredient
    }

    override fun unbindView(holder: IngredientViewItemViewHolder) {
        animations.forEach { it?.stop() }
        animations.clear()

        super.unbindView(holder)
    }

    fun changeEditMode(isEdit: Boolean) {
        isEditAvailableMode = isEdit

        if (::viewHolder.isInitialized) updateEnabledAvailableCheckBox()
    }

    private fun updateEnabledAvailableCheckBox() {
        viewHolder.availableCheckBox.isEnabled = isEditAvailableMode
    }

    //Change selected without notify selections callbacks
    fun changeSelected(isSelected: Boolean) {
        withSetSelected(isSelected)

        if (::viewHolder.isInitialized) viewHolder.checkBox.isChecked = isSelected

        if (!isSelected) setUnavailable()
    }

    fun setUnavailable() {
        data.isAvailable = false

        if (::viewHolder.isInitialized.not()) return

        viewHolder.availableCheckBox.isChecked = false
        setAvailableViewVisibility()
    }

    fun updateAvailable() {
        if (::viewHolder.isInitialized.not()) return

        viewHolder.availableCheckBox.isChecked = data.isAvailable
        setAvailableViewVisibility()
    }

    private fun setAvailableViewVisibility() {
        setAvailableViewVisibility(false)
    }

    private fun setAvailableViewVisibility(isAnimated: Boolean) {
        val isToVisible = data.isAvailable || isEditAvailableMode

        val visibility = if (isToVisible) View.VISIBLE else View.GONE

        if (isAnimated)
            animations.add(UiHelper.Other.showFadeAnim(viewHolder.availableCheckBox, visibility))
        else viewHolder.availableCheckBox.visibility = visibility
    }

    class IngredientViewItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkBox: CheckBox = itemView.checkBox
        var availableCheckBox: CheckBox = itemView.checkBox_available
        val ingredientView: TextView = itemView.tv_ingredient
    }

    class CheckBoxClickEvent : ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>() {

        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is IngredientViewItemViewHolder) {
                viewHolder.checkBox
            } else null
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<IItem<Any, RecyclerView.ViewHolder>>, item: IItem<Any, RecyclerView.ViewHolder>) {
            if (v.id == R.id.checkBox)
                fastAdapter.getExtension<SelectExtension<IItem<Any,
                        RecyclerView.ViewHolder>>>(SelectExtension::class.java)?.toggleSelection(position)
        }
    }
}