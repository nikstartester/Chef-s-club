package com.xando.chefsclub.dataworkers;

import androidx.annotation.Nullable;

import java.util.List;

public interface DataBaseLoader<Data> {

    void loadDataFromDB();

    void onDataLoadedFromDB(@Nullable List<Data> dataList);
}
