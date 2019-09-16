package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.IItem


class SingleItemGroupAdapter<ITEM: IItem<out Any, out RecyclerView.ViewHolder>>(val groupId: Int,
                                                                                private val multiGroupsRecyclerViewAdapter: MultiGroupsRecyclerViewAdapter) {

    var count = 0

    fun setItem(item: ITEM){
        multiGroupsRecyclerViewAdapter.setItemByGroup(0, item, groupId)
        count = 1
    }

    fun getItem() = multiGroupsRecyclerViewAdapter.getItemOfGroup(0, groupId) as ITEM

    fun getFastAdapter() = multiGroupsRecyclerViewAdapter.getFastAdapter()
}