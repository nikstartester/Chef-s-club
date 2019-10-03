package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem

interface MultiGroupsRecyclerViewAdapter : GroupManager {

    fun getFastAdapter(): FastAdapter<IItem<Any, RecyclerView.ViewHolder>>

    fun getStartGroupPosition(groupId: Int): Int

    fun getItemOfGroup(positionInGroup: Int, groupId: Int): IItem<Any, RecyclerView.ViewHolder>?

    fun getItemsOfGroup(groupId: Int): List<IItem<Any, RecyclerView.ViewHolder>>

    fun setItemByGroup(positionInGroup: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int)

    fun addItemByGroup(item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int)

    fun addItemByGroup(positionInGroup: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int)

    fun addItemsByGroup(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int)

    fun addItemsByGroup(positionInGroup: Int, items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int)

    fun moveItemOfGroup(oldPosition: Int, newPosition: Int, groupId: Int)

    fun removeItemOfGroup(positionInGroup: Int, groupId: Int)

    fun removeAllItemsOfGroup(groupId: Int)
}

interface GroupManager {

    fun addGroup(groupId: Int)

    fun addGroups(groupsId: List<Int>)
}