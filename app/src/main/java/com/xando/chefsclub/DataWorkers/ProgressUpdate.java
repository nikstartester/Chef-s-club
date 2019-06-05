package com.xando.chefsclub.DataWorkers;

public interface ProgressUpdate<Data extends BaseData> {
    void updateProgress(ParcResourceByParc<Data> resource);
}
