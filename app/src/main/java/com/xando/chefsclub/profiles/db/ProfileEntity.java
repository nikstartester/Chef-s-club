package com.xando.chefsclub.profiles.db;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.xando.chefsclub.profiles.data.ProfileData;

@Entity(indices = {@Index(value = "userUid", unique = true)})
public class ProfileEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Embedded
    public ProfileData profileData;
}
