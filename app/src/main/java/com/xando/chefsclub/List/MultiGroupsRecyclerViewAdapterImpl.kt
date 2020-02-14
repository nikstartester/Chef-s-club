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

    override fun getFastAdapter(): FastAdapter<IItem<Any, RecyclerView.ViewHolder>> = this

    override fun getStartGroupPosition(groupId: Int) = groupsStartPositions.get(groupId).let {
        if (it == NONE_POSITION)
            getPrevGroupIndexWithStartPosition(groupId).let { prevGroupIndexWithStartPos ->
                if (prevGroupIndexWithStartPos != -1)
                    (groupsStartPositions.valueAt(prevGroupIndexWithStartPos)
                            + groupItemCounts.valueAt(prevGroupIndexWithStartPos))
                else 0
            }
        else it
    }

    override fun getItemOfGroup(positionInGroup: Int, groupId: Int): IItem<Any, RecyclerView.ViewHolder> =
            getItemsOfGroup(groupId)[positionInGroup]

    override fun getItemsOfGroup(groupId: Int): List<IItem<Any, RecyclerView.ViewHolder>> {
        val count = groupItemCounts.get(groupId)
        val startPosition = groupsStartPositions.get(groupId)

        return adapterItems.subList(startPosition, startPosition + count)
    }

    override fun setItemByGroup(positionInGroup: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int) {
        val startPosition = groupsStartPositions.get(groupId)
        if (groupItemCounts.get(groupId) == 0)
            addItemByGroup(item, groupId)
        else set(startPosition + positionInGroup, item as IItem<Any, RecyclerView.ViewHolder>)
    }

    override fun addItemByGroup(item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int) {
        addItemsByGroup(listOf(item), groupId)
    }

    override fun addItemByGroup(positionInGroup: Int, item: IItem<out Any, out RecyclerView.ViewHolder>, groupId: Int) {
        addItemsByGroup(0, listOf(item), groupId)
    }

    override fun addItemsByGroup(items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        addItemsByGroup(0, items, groupId)
    }

    override fun addItemsByGroup(positionInGroup: Int, items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        if (groupsStartPositions.get(groupId) != NONE_POSITION && groupsStartPositions.size() > 0)
            addItemsToGroupWithStartPosition(positionInGroup, items, groupId)
        else
            addItemsToGroupWithoutStartPosition(positionInGroup, items, groupId)

        val groupIndex = groupsStartPositions.indexOfKey(groupId)

        for (i in groupIndex + 1 until groupsStartPositions.size()) {
            val startPosition = groupsStartPositions.valueAt(i)
            if (startPosition != NONE_POSITION)
                groupsStartPositions.put(groupsStartPositions.keyAt(i), startPosition + items.size)
        }

        groupItemCounts.put(groupId, groupItemCounts.get(groupId) + items.size)
    }

    protected fun addItemsToGroupWithStartPosition(position: Int, items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        val startGroupPosition = groupsStartPositions.get(groupId) + groupItemCounts.get(groupId)
        add(startGroupPosition + position, items as List<IItem<Any, RecyclerView.ViewHolder>>)
    }

    protected fun addItemsToGroupWithoutStartPosition(position: Int, items: List<IItem<out Any, out RecyclerView.ViewHolder>>, groupId: Int) {
        val prevGroupIndex = getPrevGroupIndexWithStartPosition(groupId)
        val prevGroupStartItemsPosition = if (prevGroupIndex == -1)
            NONE_POSITION
        else groupsStartPositions.valueAt(prevGroupIndex)

        var startGroupPosition = 0
        if (prevGroupStartItemsPosition != NONE_POSITION) {
            val preItemGroupCount = groupItemCounts.valueAt(prevGroupIndex)
            startGroupPosition = prevGroupStartItemsPosition + preItemGroupCount
        }

        add(startGroupPosition + position, items as List<IItem<Any, RecyclerView.ViewHolder>>)
        groupsStartPositions.put(groupId, startGroupPosition)
    }

    override fun moveItemOfGroup(oldPosition: Int, newPosition: Int, groupId: Int) {
        val startPosition = groupsStartPositions.get(groupId)
        if (startPosition != NONE_POSITION) {
            move(oldPosition + startPosition, newPosition + startPosition)
        }
    }

    override fun removeItemOfGroup(positionInGroup: Int, groupId: Int) {
        val startPosition = groupsStartPositions.get(groupId)
        if (startPosition != NONE_POSITION) {
            remove(startPosition + positionInGroup)
            groupItemCounts.put(groupId, groupItemCounts.get(groupId) - 1)
            changeStartPositionOnNextGroups(groupId, -1)
        }
    }

    protected fun getPrevGroupIndexWithStartPosition(groupId: Int): Int {
        val groupIndex = groupsStartPositions.indexOfKey(groupId)
        var prevGroupIndex = -1

        for (i in groupIndex - 1 downTo 0) {
            val prevGroupStartItemsPosition = groupsStartPositions.valueAt(i)

            if (prevGroupStartItemsPosition != NONE_POSITION) {
                prevGroupIndex = i
                break
            }
        }

        return prevGroupIndex
    }

    protected fun changeStartPositionOnNextGroups(groupId: Int, count: Int) {
        val groupIndex = groupsStartPositions.indexOfKey(groupId)

        for (i in groupIndex + 1 until groupsStartPositions.size()) {
            val startPosition = groupsStartPositions.valueAt(i)
            if (startPosition != NONE_POSITION)
                groupsStartPositions.put(groupsStartPositions.keyAt(i), startPosition + count)
        }

    }

    override fun removeAllItemsOfGroup(groupId: Int) {
        val startPosition = groupsStartPositions.get(groupId)
        if (startPosition != NONE_POSITION) {
            val itemCount = groupItemCounts.get(groupId)
            removeItemRange(startPosition, itemCount)
            groupItemCounts.put(groupId, 0)
            changeStartPositionOnNextGroups(groupId, -itemCount)
        }
    }

    override fun clear(): FastItemAdapter<IItem<Any, RecyclerView.ViewHolder>> {
        for (i in 0 until groupItemCounts.size()) {
            groupItemCounts.put(groupItemCounts.keyAt(i), 0)
        }

        for (i in 0 until groupsStartPositions.size()) {
            groupsStartPositions.put(groupsStartPositions.keyAt(i), NONE_POSITION)
        }

        return super.clear()
    }
}