package com.xando.chefsclub.DataWorkers;

interface DataBaseSaver<Data extends BaseData> {

    void saveOnLocal(BaseLocalDataSaver<Data> saver, Data data);
}
