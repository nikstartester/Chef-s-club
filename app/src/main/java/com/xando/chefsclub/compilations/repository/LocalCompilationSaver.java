package com.xando.chefsclub.compilations.repository;

import android.app.Application;

import com.xando.chefsclub.App;
import com.xando.chefsclub.compilations.data.CompilationData;
import com.xando.chefsclub.compilations.db.CompilationEntity;
import com.xando.chefsclub.dataworkers.BaseLocalDataSaver;


public class LocalCompilationSaver extends BaseLocalDataSaver<CompilationData> {

    protected LocalCompilationSaver(Application application) {
        super(application);
    }

    @Override
    protected void save(CompilationData data) {
        new Thread(() -> ((App) super.mApplication)
                .getDatabase()
                .compilationTittleDao()
                .insert(new CompilationEntity(data))).start();
    }
}
