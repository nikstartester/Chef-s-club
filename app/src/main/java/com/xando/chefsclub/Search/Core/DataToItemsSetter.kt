package com.xando.chefsclub.Search.Core

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.xando.chefsclub.DataWorkers.BaseData

interface DataToItemsSetter<Data : BaseData, Item>
        where Item : IItem<out Any, out RecyclerView.ViewHolder>, Item : IData<Data> {
    fun getItems(dataList: List<Data>): List<Item>
}