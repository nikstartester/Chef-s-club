package com.xando.chefsclub.DataWorkers;

import android.os.Parcelable;

import java.util.Map;

public abstract class BaseData implements Parcelable {
    public abstract Map<String, Object> toMap();
}
