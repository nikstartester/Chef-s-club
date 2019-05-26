package com.example.nikis.bludogramfirebase.Search.Core;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;

public interface IData<Data extends BaseData> {
    Data getData();

    void setData(Data data);
}
