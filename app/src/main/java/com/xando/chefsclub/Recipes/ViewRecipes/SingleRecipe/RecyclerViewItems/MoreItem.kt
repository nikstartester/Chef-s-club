package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_more_item.view.*

class MoreItem(private var text: String) : AbstractItem<MoreItem, MoreItem.MoreViewHolder>() {

    private lateinit var viewHolder: MoreViewHolder

    override fun getViewHolder(v: View) = MoreViewHolder(v)
    override fun getType() = R.id.list_more_item
    override fun getLayoutRes() = R.layout.list_more_item

    override fun bindView(holder: MoreViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        updateMoreText()
    }

    fun setMoreText(text: String){
        this.text = text

        if(::viewHolder.isInitialized)
            updateMoreText()
    }

    private fun updateMoreText(){
        viewHolder.moreView.text = text
    }

    class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val moreView: TextView = itemView.btn_more
    }
}