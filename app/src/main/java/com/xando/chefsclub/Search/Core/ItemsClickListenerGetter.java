package com.xando.chefsclub.Search.Core;

import android.support.annotation.Nullable;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;

public interface ItemsClickListenerGetter<Item extends AbstractItem> {

    @Nullable
    ClickEventHook<Item> getClickEventHookInstance();

    @Nullable
    OnClickListener<Item> getClickItemListenerInstance();
}
