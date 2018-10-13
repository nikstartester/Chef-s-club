package com.example.nikis.bludogramfirebase;

import android.os.Parcelable;

import java.util.Map;

public abstract class BaseData implements Parcelable {
    public abstract Map<String, Object> toMap();
}
