package com.example.nikis.bludogramfirebase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.nikis.bludogramfirebase.Profile.db.ProfileDao;
import com.example.nikis.bludogramfirebase.Profile.db.ProfileEntity;

@Database(entities = {ProfileEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProfileDao profileDao();

}
