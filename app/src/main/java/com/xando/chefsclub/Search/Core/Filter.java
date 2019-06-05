package com.xando.chefsclub.Search.Core;

public interface Filter<T extends BaseFilterData> {
    void setFilter(T filter);
}
