package com.example.nikis.bludogramfirebase.Compilations.Repository;

import android.app.Application;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.Compilations.db.CompilationEntity;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseLocalDataSaver;


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
