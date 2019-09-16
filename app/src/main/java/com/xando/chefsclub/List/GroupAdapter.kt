package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.IItem


class GroupAdapter<ITEM: IItem<out Any, out RecyclerView.ViewHolder>>(val groupId: Int, private val multiGroupsRecyclerViewAdapter: MultiGroupsRecyclerViewAdapter) {

    var count = 0

    fun addItem(item: ITEM){
        multiGroupsRecyclerViewAdapter.addItemByGroup(item, groupId)
        count ++
    }

    fun addItems(items: List<ITEM>){
        multiGroupsRecyclerViewAdapter.addItemsByGroup(items, groupId)
        count += items.size
    }

    fun getItem(position: Int) = multiGroupsRecyclerViewAdapter.getItemOfGroup(position, groupId) as ITEM

    fun getItems() = multiGroupsRecyclerViewAdapter.getItemsOfGroup(groupId) as List<ITEM>
}