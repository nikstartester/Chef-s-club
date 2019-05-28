package com.example.nikis.bludogramfirebase.Profiles.db;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;

@Entity(indices = {@Index(value = "userUid", unique = true)})
public class ProfileEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Embedded
    public ProfileData profileData;
}