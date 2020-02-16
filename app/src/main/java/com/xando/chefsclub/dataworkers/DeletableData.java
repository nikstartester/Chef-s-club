package com.xando.chefsclub.dataworkers;

public interface DeletableData<Data extends BaseData> {

    void deleteFromServer(Data data);

    void deleteFromDataBase(Data data);
}
