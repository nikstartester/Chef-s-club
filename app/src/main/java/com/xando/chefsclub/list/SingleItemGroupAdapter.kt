package com.xando.chefsclub.list

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

class SingleItemGroupAdapter<ITEM : IItem<out Any,
        out RecyclerView.ViewHolder>>(val groupId: Int,
                                      private val multiGroupsRecyclerViewAdapter: MultiGroupsRecyclerViewAdapter) {

    var count = 0

    fun setItem(item: ITEM) {
        multiGroupsRecyclerViewAdapter.setItemByGroup(0, item, groupId)
        count = 1
    }

    @Suppress("UNCHECKED_CAST")
    fun getItem() = multiGroupsRecyclerViewAdapter.getItemOfGroup(0, groupId) as ITEM

    fun removeItem() {
        multiGroupsRecyclerViewAdapter.removeAllItemsOfGroup(groupId)
        count = 0
    }

    fun getFastAdapter() = multiGroupsRecyclerViewAdapter.getFastAdapter()
}