package com.xando.chefsclub.profiles.db;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.xando.chefsclub.profiles.data.ProfileData;

@Entity(indices = {@Index(value = "userUid", unique = true)})
public class ProfileEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Embedded
    public ProfileData profileData;
}
