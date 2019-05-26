package com.example.nikis.bludogramfirebase.Compilations.db;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;

@Entity(indices = {@Index(value = "compilationKey", unique = true)})
public class CompilationEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @Embedded
    public CompilationData mCompilationData;

    public CompilationEntity(CompilationData compilationData) {
        this.mCompilationData = compilationData;
    }
}
