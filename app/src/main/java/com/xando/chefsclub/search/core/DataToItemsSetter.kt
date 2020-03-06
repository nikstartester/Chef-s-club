package com.xando.chefsclub.search.core

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.xando.chefsclub.dataworkers.BaseData

interface DataToItemsSetter<Data : BaseData, Item>
        where Item : IItem<out Any, out RecyclerView.ViewHolder>, Item : IData<Data> {
    fun getItems(dataList: List<Data>): List<Item>
}