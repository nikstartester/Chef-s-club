package com.xando.chefsclub.Compilations.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.xando.chefsclub.Compilations.Data.CompilationData;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CompilationDao {

    @Query("SELECT * FROM CompilationEntity  ORDER BY id ASC")
    Flowable<List<CompilationData>> getFlowableAll();

    @Query("SELECT * FROM CompilationEntity  ORDER BY id ASC")
    List<CompilationEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CompilationEntity compilationEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CompilationEntity> compilationTittleEntities);

    @Delete
    void delete(List<CompilationEntity> entities);
}
