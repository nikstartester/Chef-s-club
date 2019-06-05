package com.xando.chefsclub.Search.Core;

import com.algolia.search.saas.Query;

public abstract class FilterAdapter<T extends BaseFilterData> {
    protected T data;

    protected FilterAdapter() {

    }

    public FilterAdapter(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public FilterAdapter<T> setData(T data) {
        this.data = data;

        return this;
    }

    public abstract FilterAdapter<T> setEmptyData();

    public Query addToQuery(T data, Query query) {
        this.data = data;

        return addToQuery(query);
    }

    public abstract Query addToQuery(Query query);
}
