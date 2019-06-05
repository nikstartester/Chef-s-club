package com.xando.chefsclub.Search.Core;

import android.support.annotation.NonNull;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.DataWorkers.BaseData;

import java.util.List;

public interface DataToItemsSetter<Data extends BaseData, Item extends AbstractItem & IData<Data>> {
    @NonNull
    Item[] getItems(@NonNull List<Data> dataList);
}
