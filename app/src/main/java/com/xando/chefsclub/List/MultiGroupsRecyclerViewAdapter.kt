package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem


interface MultiGroupsRecyclerViewAdapter{

    /**
     * Group will be added to the end of the groups list
     */
    fun addGroup(groupId: Int)

    /**
     * Groups will be added to the end of the groups list
     */
    fun addGroups(groupsId: List<Int>)

    fun setItemByGroup(position: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int)

    fun addItemByGroup(item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int)

    fun addItemsByGroup(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int)

    fun getItemOfGroup(positionInGroup: Int, groupId: Int): IItem<Any, RecyclerView.ViewHolder>?

    fun getItemsOfGroup(groupId: Int): List<IItem<Any, RecyclerView.ViewHolder>>

    fun getFastAdapter(): FastAdapter<IItem<Any, RecyclerView.ViewHolder>>
}