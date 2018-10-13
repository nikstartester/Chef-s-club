package com.example.nikis.bludogramfirebase.Profile.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.nikis.bludogramfirebase.Profile.Data.ProfileData;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ProfileDao{

    @Query("SELECT * FROM profileentity")
    List<ProfileEntity> getAll();

    @Query("SELECT * FROM profileentity WHERE id = :id")
    ProfileEntity getById(long id);

    @Query("SELECT * FROM profileentity WHERE userUid = :userUid")
    Flowable<List<ProfileData>> getByUid(String userUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileEntity profile);

    @Update
    void update(ProfileEntity profile);

    @Delete
    void delete(ProfileEntity profile);
}
