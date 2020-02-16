package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.helper.DateTimeHelper
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_steps_all_time_cooking_item.view.*

class AllTimeCookingItem(private val time: Int): AbstractItem<AllTimeCookingItem, AllTimeCookingItem.AllTimeCookingViewHolder>() {

    private lateinit var viewHolder: AllTimeCookingViewHolder

    override fun getType() = R.id.steps_recipe_all_time_cooking_item
    override fun getViewHolder(v: View) = AllTimeCookingViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_steps_all_time_cooking_item

    override fun bindView(holder: AllTimeCookingViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        viewHolder.timeView.text = DateTimeHelper.convertTime(time)
    }

    class AllTimeCookingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val timeView: TextView = itemView.tv_timeMain
    }
}