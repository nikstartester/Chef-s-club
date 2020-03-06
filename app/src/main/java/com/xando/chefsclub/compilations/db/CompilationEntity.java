package com.xando.chefsclub.compilations.db;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.xando.chefsclub.compilations.data.CompilationData;

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
