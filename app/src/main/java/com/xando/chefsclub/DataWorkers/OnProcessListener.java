package com.xando.chefsclub.DataWorkers;

public interface OnProcessListener<Data extends BaseData> {
    void onStatusChanged(ParcResourceByParc<Data> resource);
}
