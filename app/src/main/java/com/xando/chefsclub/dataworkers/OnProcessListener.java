package com.xando.chefsclub.dataworkers;

public interface OnProcessListener<Data extends BaseData> {

    void onStatusChanged(ParcResourceByParc<Data> resource);
}
