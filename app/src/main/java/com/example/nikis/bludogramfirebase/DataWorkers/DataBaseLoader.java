package com.example.nikis.bludogramfirebase.DataWorkers;

import android.support.annotation.Nullable;

import java.util.List;

public interface DataBaseLoader<Data> {
    void loadDataFromDB();

    void onDataLoadedFromDB(@Nullable List<Data> dataList);
}
