package com.example.nikis.bludogramfirebase.DataWorkers;

interface DataBaseSaver<Data extends BaseData> {
    void saveOnLocal(BaseLocalDataSaver<Data> saver, Data data);
}
