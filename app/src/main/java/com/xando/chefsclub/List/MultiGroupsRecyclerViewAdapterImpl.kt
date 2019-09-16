package com.xando.chefsclub.List

import android.support.v7.widget.RecyclerView
import android.util.SparseIntArray
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

private const val NONE_POSITION = -1

open class MultiGroupsRecyclerViewAdapterImpl : FastItemAdapter<IItem<Any, RecyclerView.ViewHolder>>(),
        MultiGroupsRecyclerViewAdapter {

    private val groupsStartPositions = SparseIntArray()
    private val groupItemCounts = SparseIntArray()

    override fun addGroup(groupId: Int) {
        addGroups(listOf(groupId))
    }

    override fun addGroups(groupsId: List<Int>) {
        groupsId.forEach {
            groupsStartPositions.put(it, NONE_POSITION)
            groupItemCounts.put(it, 0)
        }
    }

    override fun setItemByGroup(position: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int) {
        val startPosition = groupsStartPositions.get(groupId)
        if (groupItemCounts.get(groupId) == 0)
            addItemByGroup(item, groupId)
        else set(startPosition + position, item as IItem<Any, RecyclerView.ViewHolder>)
    }

    override fun addItemByGroup(item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int) {
        addItemsByGroup(listOf(item), groupId)
    }

    override fun addItemsByGroup(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        if (groupsStartPositions.get(groupId) != NONE_POSITION && groupsStartPositions.size() > 0)
            addItemsToGroupWithStartPosition(items, groupId)
        else
            addItemsToGroupWithoutStartPosition(items, groupId)

        val groupIndex = groupsStartPositions.indexOfKey(groupId)

        for (i in groupIndex + 1 until groupsStartPositions.size()) {
            val startPosition = groupsStartPositions.valueAt(i)
            if (startPosition != NONE_POSITION)
                groupsStartPositions.put(groupsStartPositions.keyAt(i), startPosition + items.size)
        }

        groupItemCounts.put(groupId, groupItemCounts.get(groupId) + items.size)
    }

    protected fun addItemsToGroupWithStartPosition(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        val startPositionToAdd = groupsStartPositions.get(groupId) + groupItemCounts.get(groupId)
        add(startPositionToAdd, items as List<IItem<Any, RecyclerView.ViewHolder>>)
    }

    protected fun addItemsToGroupWithoutStartPosition(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        val groupIndex = groupsStartPositions.indexOfKey(groupId)

        var prevGroupStartItemsPosition = NONE_POSITION
        var prevGroupIndex = -1

        for (i in groupIndex - 1 downTo 0) {
            prevGroupStartItemsPosition = groupsStartPositions.valueAt(i)

            if (prevGroupStartItemsPosition != NONE_POSITION) {
                prevGroupIndex = i
                break
            }
        }

        var startPositionToAdd = 0
        var currGroupStartItemPosition = 0
        if (prevGroupStartItemsPosition != NONE_POSITION) {
            val preItemGroupCount = groupItemCounts.valueAt(prevGroupIndex)
            currGroupStartItemPosition = prevGroupStartItemsPosition + preItemGroupCount
            startPositionToAdd = currGroupStartItemPosition //+ groupItemCounts.get(groupId)
        }

        add(startPositionToAdd, items as List<IItem<Any, RecyclerView.ViewHolder>>)
        groupsStartPositions.put(groupId, currGroupStartItemPosition)
    }

    override fun getItemOfGroup(positionInGroup: Int, groupId: Int): IItem<Any, RecyclerView.ViewHolder> =
            getItemsOfGroup(groupId)[positionInGroup]

    override fun getItemsOfGroup(groupId: Int): List<IItem<Any, RecyclerView.ViewHolder>> {
        val count = groupItemCounts.get(groupId)
        val startPosition = groupsStartPositions.get(groupId)

        return adapterItems.subList(startPosition, startPosition + count)
    }

    override fun getFastAdapter(): FastAdapter<IItem<Any, RecyclerView.ViewHolder>> = this
}