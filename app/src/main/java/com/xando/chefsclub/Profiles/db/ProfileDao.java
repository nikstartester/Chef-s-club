package com.xando.chefsclub.Profiles.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.xando.chefsclub.Profiles.Data.ProfileData;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profileentity")
    List<ProfileEntity> getAll();

    @Query("SELECT * FROM profileentity WHERE id = :id")
    ProfileEntity getById(long id);

    @Query("SELECT * FROM profileentity WHERE userUid = :userUid")
    Flowable<List<ProfileData>> getFlowableByUid(String userUid);

    @Query("SELECT * FROM profileentity WHERE userUid = :userUid")
    Single<List<ProfileData>> getSingleByUid(String userUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileEntity profile);

    @Update
    void update(ProfileEntity profile);

    @Delete
    void delete(ProfileEntity profile);

    @Query("DELETE FROM profileentity WHERE userUid = :userUid")
    void deleteByUserUid(String userUid);
}
