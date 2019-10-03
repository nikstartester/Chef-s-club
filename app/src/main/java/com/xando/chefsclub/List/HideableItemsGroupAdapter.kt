package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

class HideableItemsGroupAdapter<ITEM : IItem<out Any, out RecyclerView.ViewHolder>>(private val visibleCount: Int,
                                                                                    groupId: Int,
                                                                                    multiGroupsRecyclerViewAdapter: MultiGroupsRecyclerViewAdapter)
    : GroupAdapter<ITEM>(groupId, multiGroupsRecyclerViewAdapter) {

    private val originalList = mutableListOf<ITEM>()
    var isVisibleAll = false
        private set

    fun updateItemVisibility(isVisibleAll: Boolean) {
        if (this.isVisibleAll == isVisibleAll) return

        this.isVisibleAll = isVisibleAll

        if (originalList.size > visibleCount)
            with(multiGroupsRecyclerViewAdapter) {
                if (isVisibleAll)
                    addItemsByGroup(originalList.subList(visibleCount, originalList.size), groupId)
                else {
                    removeAllItems()
                    addItemsByGroup(originalList.subList(0, visibleCount), groupId)
                }
            }
    }

    override fun addItem(position: Int, item: ITEM) {
        if (position < visibleCount || isVisibleAll)
            super.addItem(position, item)

        originalList.add(position, item)
    }

    override fun addItems(position: Int, items: List<ITEM>) {
        if (visibleCount - originalList.size > 0 || isVisibleAll)
            super.addItems(position, if (items.size + originalList.size <= visibleCount || isVisibleAll)
                items
            else items.subList(0, visibleCount - originalList.size))

        originalList.addAll(position, items)
    }

    override fun getItem(groupPosition: Int): ITEM {
        return originalList[groupPosition]
    }

    override fun getItems(): List<ITEM> {
        return originalList
    }

    override fun getAdapterPosition(groupPosition: Int) =
            if (groupPosition < visibleCount || isVisibleAll)
                super.getAdapterPosition(groupPosition)
            else -1

    override fun move(oldPosition: Int, newPosition: Int) {
        if (newPosition < visibleCount || isVisibleAll)
            super.move(oldPosition, newPosition)

        val item = originalList[oldPosition]
        originalList.removeAt(oldPosition)
        originalList.add(newPosition, item)
    }

    override fun remove(groupPosition: Int) {
        if (groupPosition < visibleCount || isVisibleAll)
            super.remove(groupPosition)

        originalList.removeAt(groupPosition)
    }

    override fun notifyItemChanged(groupPosition: Int) {
        if (groupPosition < visibleCount || isVisibleAll)
            super.notifyItemChanged(groupPosition)
    }
}