package com.example.nikis.bludogramfirebase.DataWorkers;

public interface OnProcessListener<Data extends BaseData> {
    void onStatusChanged(ParcResourceByParc<Data> resource);
}
