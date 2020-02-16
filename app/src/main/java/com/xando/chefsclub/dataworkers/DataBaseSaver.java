package com.xando.chefsclub.dataworkers;

interface DataBaseSaver<Data extends BaseData> {

    void saveOnLocal(BaseLocalDataSaver<Data> saver, Data data);
}
