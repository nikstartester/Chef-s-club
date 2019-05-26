package com.example.nikis.bludogramfirebase.DataWorkers;

public interface DeletableData<Data extends BaseData> {
    void deleteFromServer(Data data);

    void deleteFromDataBase(Data data);
}
