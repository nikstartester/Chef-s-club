package com.example.nikis.bludogramfirebase.DataWorkers;

public interface ProgressUpdate<Data extends BaseData> {
    void updateProgress(ParcResourceByParc<Data> resource);
}
