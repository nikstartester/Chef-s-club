package com.xando.chefsclub.search.core;

import com.xando.chefsclub.dataworkers.BaseData;

public interface IData<Data extends BaseData> {

    Data getData();

    void setData(Data data);
}
