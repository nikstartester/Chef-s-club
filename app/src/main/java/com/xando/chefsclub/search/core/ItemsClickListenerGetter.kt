package com.xando.chefsclub.search.core

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnClickListener

interface ItemsClickListenerGetter<Item : IItem<out Any, out RecyclerView.ViewHolder>> {

    val clickEventHook: ClickEventHook<Item>?
    val clickItemListener: OnClickListener<Item>
}