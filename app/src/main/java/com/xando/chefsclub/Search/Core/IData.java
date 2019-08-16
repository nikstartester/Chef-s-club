package com.xando.chefsclub.Search.Core;

import com.xando.chefsclub.DataWorkers.BaseData;

public interface IData<Data extends BaseData> {

    Data getData();

    void setData(Data data);
}
