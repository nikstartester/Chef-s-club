package com.xando.chefsclub.Compilations.Repository;

import android.app.Application;

import com.xando.chefsclub.App;
import com.xando.chefsclub.Compilations.Data.CompilationData;
import com.xando.chefsclub.Compilations.db.CompilationEntity;
import com.xando.chefsclub.DataWorkers.BaseLocalDataSaver;


public class LocalCompilationSaver extends BaseLocalDataSaver<CompilationData> {

    protected LocalCompilationSaver(Application application) {
        super(application);
    }

    @Override
    protected void save(CompilationData data) {
        new Thread(() -> {
            ((App) super.mApplication)
                    .getDatabase()
                    .compilationTittleDao()
                    .insert(new CompilationEntity(data));
        }).start();
    }
}
