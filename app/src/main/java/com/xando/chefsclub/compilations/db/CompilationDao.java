package com.xando.chefsclub.compilations.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.xando.chefsclub.compilations.data.CompilationData;

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
