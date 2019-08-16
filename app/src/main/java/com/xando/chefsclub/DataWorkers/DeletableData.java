package com.xando.chefsclub.DataWorkers;

public interface DeletableData<Data extends BaseData> {

    void deleteFromServer(Data data);

    void deleteFromDataBase(Data data);
}
