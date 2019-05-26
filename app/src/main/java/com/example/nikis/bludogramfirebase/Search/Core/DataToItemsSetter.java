package com.example.nikis.bludogramfirebase.Search.Core;

import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public interface DataToItemsSetter<Data extends BaseData, Item extends AbstractItem & IData<Data>> {
    @NonNull
    Item[] getItems(@NonNull List<Data> dataList);
}
