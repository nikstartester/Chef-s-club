package com.xando.chefsclub.profiles.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.xando.chefsclub.profiles.data.ProfileData;

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
