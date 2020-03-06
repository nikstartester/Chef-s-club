package com.xando.chefsclub.list

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

open class GroupAdapter<ITEM : IItem<out Any, out RecyclerView.ViewHolder>>(val groupId: Int, protected val multiGroupsRecyclerViewAdapter: MultiGroupsRecyclerViewAdapter) {

    var count = 0

    open fun addItem(item: ITEM) {
        addItem(0, item)
    }

    open fun addItem(position: Int, item: ITEM) {
        multiGroupsRecyclerViewAdapter.addItemByGroup(position, item, groupId)
        count++
    }

    open fun addItems(items: List<ITEM>) {
        addItems(0, items)
    }

    open fun addItems(position: Int, items: List<ITEM>) {
        multiGroupsRecyclerViewAdapter.addItemsByGroup(position, items, groupId)
        count += items.size
    }

    open fun getItem(groupPosition: Int) = multiGroupsRecyclerViewAdapter.getItemOfGroup(groupPosition, groupId) as ITEM

    open fun getItems() = multiGroupsRecyclerViewAdapter.getItemsOfGroup(groupId) as List<ITEM>

    open fun getFastAdapter() = multiGroupsRecyclerViewAdapter.getFastAdapter()

    open fun getAdapterPosition(item: ITEM) = getFastAdapter().getPosition(item as IItem<Any, RecyclerView.ViewHolder>)

    open fun getAdapterStartPosition() = multiGroupsRecyclerViewAdapter.getStartGroupPosition(groupId)

    open fun getAdapterPosition(groupPosition: Int) = getAdapterStartPosition() + groupPosition

    open fun getGroupPosition(adapterPosition: Int) =
            if (getAdapterStartPosition() >= 0) adapterPosition - getAdapterStartPosition()
            else -1

    open fun notifyDataSetChanged() {
        getFastAdapter().notifyItemRangeChanged(getAdapterStartPosition(), count)
    }

    open fun notifyItemChanged(groupPosition: Int) {
        getFastAdapter().notifyItemChanged(getAdapterStartPosition() + groupPosition)
    }

    open fun move(oldPosition: Int, newPosition: Int) {
        multiGroupsRecyclerViewAdapter.moveItemOfGroup(oldPosition, newPosition, groupId)
    }

    open fun remove(groupPosition: Int) {
        multiGroupsRecyclerViewAdapter.removeItemOfGroup(groupPosition, groupId)
        count--
    }

    open fun removeAllItems() {
        multiGroupsRecyclerViewAdapter.removeAllItemsOfGroup(groupId)
        count = 0
    }
}

fun <ITEM : IItem<out Any, out RecyclerView.ViewHolder>> GroupAdapter<ITEM>.isEmpty() = count == 0

fun <ITEM : IItem<out Any, out RecyclerView.ViewHolder>> GroupAdapter<ITEM>.isNotEmpty() = count > 0