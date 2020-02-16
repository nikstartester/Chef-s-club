package com.xando.chefsclub.dataworkers;

public interface ProgressUpdate<Data extends BaseData> {

    void updateProgress(ParcResourceByParc<Data> resource);
}
